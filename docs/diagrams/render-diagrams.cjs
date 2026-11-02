/*
 * Renders docs/diagrams/sources/*.mmd to docs/diagrams/*.png.
 *
 * Mermaid cannot draw a few things the course's UML reference sheet asks for, so sequence diagrams are
 * post-processed before they are turned into a PNG:
 *   - the condition of an alt / loop / opt frame is written right beside the frame keyword
 *   - notes are redrawn with a folded corner, sized to fit their text, placed beside the diagram
 *     and joined to the lifeline they describe with a dashed line (so they never cover other lines)
 *   - message and condition text gets a white background so that lifelines never strike through the words
 *
 * Usage (from docs/diagrams):
 *   npm install @mermaid-js/mermaid-cli          # once; provides mmdc and puppeteer
 *   set PUPPETEER_EXECUTABLE_PATH=<path to Chrome or Edge>   # if no browser was downloaded
 *   node render-diagrams.cjs                      # all diagrams
 *   node render-diagrams.cjs edit-application-sequence search-sequence   # selected diagrams
 *
 * Class, object and activity diagrams are rendered straight to PNG by mmdc.
 */
const fs = require('fs');
const path = require('path');
const os = require('os');
const { execFileSync } = require('child_process');
const puppeteer = require('puppeteer');

const SOURCES = path.join(__dirname, 'sources');
const SKIP = new Set(['architecture-overview', 'architecture-command-flow', 'application-model-class']);
const SCALE = 2;

const executablePath = process.env.PUPPETEER_EXECUTABLE_PATH || undefined;
const mmdc = path.join(path.dirname(require.resolve('@mermaid-js/mermaid-cli')), 'cli.js');
const puppeteerConfig = path.join(os.tmpdir(), 'jobseek-puppeteer.json');
fs.writeFileSync(puppeteerConfig, JSON.stringify({ executablePath, args: ['--no-sandbox'] }));

/** Runs inside the browser page: edits the Mermaid SVG of a sequence diagram. */
function postProcessSequenceDiagram() {
  const NS = 'http://www.w3.org/2000/svg';
  const svg = document.querySelector('svg');
  const num = (el, a) => parseFloat(el.getAttribute(a));

  const style = document.createElementNS(NS, 'style');
  style.textContent = '.messageText,.loopText,.sectionTitle{paint-order:stroke !important;stroke:#fff !important;stroke-width:6px !important;stroke-linejoin:round !important;}';
  svg.insertBefore(style, svg.firstChild);

  // ---- frames: condition beside the keyword, frame wide enough for it
  const items = [...svg.querySelectorAll('line.loopLine, polygon.labelBox, text.labelText, text.loopText, text.sectionTitle')];
  let batch = [];
  let box = null;
  let labelY = 0;
  let firstCondition = true;
  let lastTextEl = null;
  let needRight = 0;
  const flushFrame = () => {
    if (!batch.length || !needRight) return;
    const vertical = batch.filter((l) => num(l, 'x1') === num(l, 'x2'));
    const right = Math.max(...vertical.map((l) => num(l, 'x1')));
    if (needRight > right) {
      batch.forEach((l) => {
        if (num(l, 'x1') === right) l.setAttribute('x1', needRight);
        if (num(l, 'x2') === right) l.setAttribute('x2', needRight);
      });
    }
  };
  let prevKind = '';
  for (const el of items) {
    const kind = el.tagName === 'line' ? 'line' : el.classList.contains('labelBox') ? 'box'
      : el.classList.contains('labelText') ? 'label' : el.classList.contains('sectionTitle') ? 'section' : 'text';
    if (kind === 'line') {
      if (prevKind === 'text' || prevKind === 'section') { flushFrame(); batch = []; needRight = 0; }
      batch.push(el);
    } else if (kind === 'box') {
      box = el.getBBox();
      firstCondition = true;
      lastTextEl = null;
    } else if (kind === 'label') {
      labelY = num(el, 'y');
    } else if (kind === 'section') {
      // condition of an else branch: left aligned just under its divider line
      el.setAttribute('text-anchor', 'start');
      el.setAttribute('x', box.x + 10);
      const bb = el.getBBox();
      needRight = Math.max(needRight, bb.x + bb.width + 14);
      lastTextEl = null;
    } else {
      const y = num(el, 'y');
      // a long condition is wrapped by Mermaid into several text elements; join them again
      const continuation = lastTextEl && Math.abs(y - num(lastTextEl, 'y')) <= 26;
      if (continuation) {
        const tspan = lastTextEl.querySelector('tspan') || lastTextEl;
        tspan.textContent = (tspan.textContent + ' ' + el.textContent).trim();
        el.remove();
      } else {
        lastTextEl = el;
        el.setAttribute('text-anchor', 'start');
        const x = firstCondition ? box.x + box.width + 10 : box.x + 10;
        el.setAttribute('x', x);
        if (firstCondition) el.setAttribute('y', labelY);
        el.querySelectorAll('tspan').forEach((t) => t.setAttribute('x', x));
      }
      const bb = lastTextEl.getBBox();
      needRight = Math.max(needRight, bb.x + bb.width + 14);
      firstCondition = false;
    }
    prevKind = kind;
  }
  flushFrame();

  // ---- draw every label on top of lifelines and activation bars, so its white halo hides them
  // The halo from the stylesheet is not reliable in every renderer, so each label also gets an explicit
  // white rectangle behind it, sized from its bounding box.
  svg.querySelectorAll('text.messageText, text.loopText, text.sectionTitle').forEach((t) => {
    const bb = t.getBBox();
    const bg = document.createElementNS(NS, 'rect');
    bg.setAttribute('x', bb.x - 3);
    bg.setAttribute('y', bb.y);
    bg.setAttribute('width', bb.width + 6);
    bg.setAttribute('height', bb.height);
    bg.setAttribute('fill', '#fff');
    svg.appendChild(bg);
    svg.appendChild(t);
  });

  // ---- notes: fitted, folded corner, beside the diagram, dashed pointer to the lifeline
  const lifelines = [...svg.querySelectorAll('line.actor-line')].map((l) => num(l, 'x1'));
  const contentRight = Math.max(...[...svg.querySelectorAll('line, rect')]
    .map((e) => Math.max(num(e, 'x2') || 0, (num(e, 'x') || 0) + (num(e, 'width') || 0))));
  let noteX = contentRight + 45;
  let maxRight = noteX;
  const notes = [...svg.querySelectorAll('rect.note')];
  for (const rect of notes) {
    const rx = num(rect, 'x');
    const ry = num(rect, 'y');
    const rh = num(rect, 'height');
    const texts = [];
    for (let n = rect.nextElementSibling; n && n.classList && n.classList.contains('noteText'); n = n.nextElementSibling) {
      texts.push(n);
    }
    const lines = texts.map((t) => t.textContent.trim());
    texts.forEach((t) => t.remove());

    const FONT = 16;
    const pad = 8;
    const fold = 12;
    const measure = document.createElementNS(NS, 'text');
    measure.setAttribute('style', 'font-size:' + FONT + 'px');
    svg.appendChild(measure);
    let width = 0;
    lines.forEach((ln) => { measure.textContent = ln; width = Math.max(width, measure.getBBox().width); });
    measure.remove();
    const lineH = FONT + 5;
    const w = width + pad * 2 + 4;
    const h = lines.length * lineH + pad * 2 - 4;
    const x = noteX;
    const y = ry + (rh - h) / 2;

    const shape = document.createElementNS(NS, 'path');
    shape.setAttribute('d', `M${x} ${y} H${x + w - fold} L${x + w} ${y + fold} V${y + h} H${x} Z M${x + w - fold} ${y} V${y + fold} H${x + w}`);
    shape.setAttribute('fill', '#efe6fb');
    shape.setAttribute('stroke', '#7e57b3');
    shape.setAttribute('stroke-width', '1.5');
    shape.setAttribute('stroke-linejoin', 'round');
    rect.replaceWith(shape);

    lines.forEach((ln, i) => {
      const t = document.createElementNS(NS, 'text');
      t.setAttribute('x', x + pad);
      t.setAttribute('y', y + pad + FONT * 0.85 + i * lineH - 2);
      t.setAttribute('style', 'font-size:' + FONT + 'px;fill:#000');
      t.textContent = ln;
      svg.appendChild(t);
    });

    // dashed pointer to the lifeline nearest the original note position
    const anchor = lifelines.reduce((best, lx) => (Math.abs(lx - rx) < Math.abs(best - rx) ? lx : best), lifelines[0]);
    const pointer = document.createElementNS(NS, 'line');
    pointer.setAttribute('x1', x);
    pointer.setAttribute('y1', y + h / 2);
    pointer.setAttribute('x2', anchor);
    pointer.setAttribute('y2', y + h / 2);
    pointer.setAttribute('stroke', '#4a72b0');
    pointer.setAttribute('stroke-width', '1.5');
    pointer.setAttribute('stroke-dasharray', '6 5');
    svg.appendChild(pointer);
    const dot = document.createElementNS(NS, 'circle');
    dot.setAttribute('cx', anchor);
    dot.setAttribute('cy', y + h / 2);
    dot.setAttribute('r', 3);
    dot.setAttribute('fill', '#4a72b0');
    svg.appendChild(dot);
    maxRight = Math.max(maxRight, x + w);
  }

  // ---- make the canvas wide enough, then report the size
  const vb = svg.getAttribute('viewBox').split(/\s+/).map(Number);
  const right = Math.max(vb[0] + vb[2], maxRight + 40, contentRight + 60);
  vb[2] = right - vb[0];
  svg.setAttribute('viewBox', vb.join(' '));
  svg.setAttribute('width', vb[2]);
  svg.setAttribute('height', vb[3]);
  svg.style.maxWidth = 'none';
  return { width: Math.ceil(vb[2]), height: Math.ceil(vb[3]) };
}

async function renderSequence(browser, name, pngPath) {
  const svgPath = path.join(os.tmpdir(), name + '.svg');
  execFileSync('node', [mmdc, '-i', path.join(SOURCES, name + '.mmd'), '-o', svgPath, '-p', puppeteerConfig],
    { stdio: ['ignore', 'ignore', 'inherit'] });
  const page = await browser.newPage();
  await page.setContent('<body style="margin:0;background:#fff">' + fs.readFileSync(svgPath, 'utf8') + '</body>');
  const size = await page.evaluate(postProcessSequenceDiagram);
  await page.setViewport({ width: size.width, height: size.height, deviceScaleFactor: SCALE });
  const element = await page.$('svg');
  // render to memory first, then write: an editor that has the old PNG open can lock the file briefly
  const image = await element.screenshot();
  for (let attempt = 1; ; attempt++) {
    try {
      fs.writeFileSync(pngPath, image);
      break;
    } catch (error) {
      if (attempt === 5) throw new Error('Cannot write ' + pngPath + ' (is it open in another program?): ' + error.message);
      await new Promise((resolve) => setTimeout(resolve, 1500));
    }
  }
  await page.close();
}

(async () => {
  const wanted = process.argv.slice(2);
  const names = fs.readdirSync(SOURCES)
    .filter((f) => f.endsWith('.mmd'))
    .map((f) => f.replace(/\.mmd$/, ''))
    .filter((n) => !SKIP.has(n) && (!wanted.length || wanted.includes(n)));
  const browser = await puppeteer.launch({ executablePath, args: ['--no-sandbox'], headless: true });
  for (const name of names) {
    const png = path.join(__dirname, name + '.png');
    const isSequence = fs.readFileSync(path.join(SOURCES, name + '.mmd'), 'utf8').includes('sequenceDiagram');
    if (isSequence) {
      await renderSequence(browser, name, png);
    } else {
      execFileSync('node', [mmdc, '-i', path.join(SOURCES, name + '.mmd'), '-o', png, '-p', puppeteerConfig,
        '-s', String(SCALE), '-b', 'white'], { stdio: ['ignore', 'ignore', 'inherit'] });
    }
    console.log('rendered', name);
  }
  await browser.close();
})();
