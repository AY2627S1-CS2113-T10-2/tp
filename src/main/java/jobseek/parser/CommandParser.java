package jobseek.parser;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.FindCommand;
import jobseek.command.HelpCommand;
import jobseek.command.MarkCommand;
import jobseek.command.SearchCommand;
import jobseek.command.SortCommand;
import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationFilters;
import jobseek.model.RecruitmentStage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Turns one line of typed text into a {@link Command}, so users can work without the numbered menu.
 *
 * <p>Supported formats (a field prefix is a single letter followed by a slash, e.g. {@code c/}):
 * <ul>
 * <li>{@code add c/COMPANY r/ROLE [d/YYYY-MM-DD]}</li>
 * <li>{@code edit INDEX [c/COMPANY] [r/ROLE] [s/STAGE]}</li>
 * <li>{@code delete INDEX}, {@code mark INDEX}, {@code unmark INDEX}</li>
 * <li>{@code find [c/TEXT] [r/TEXT] [s/STAGE] [b/DATE] [a/DATE] [m/yes|no]}, which combines filters</li>
 * <li>{@code search KEYWORD}, {@code sort}, {@code help}, {@code exit}</li>
 * </ul>
 * Input that does not start with one of these words is not handled here, so the caller can fall back
 * to the numbered menu. Parsing is kept separate from {@code Main} so it can be tested on its own.
 */
public class CommandParser {
    public static final String ADD_USAGE = "Usage: add c/COMPANY r/ROLE [d/YYYY-MM-DD]";
    public static final String EDIT_USAGE = "Usage: edit INDEX [c/COMPANY] [r/ROLE] [s/STAGE] "
            + "(at least one field)";
    public static final String DELETE_USAGE = "Usage: delete INDEX";
    public static final String MARK_USAGE = "Usage: mark INDEX";
    public static final String UNMARK_USAGE = "Usage: unmark INDEX";
    public static final String SEARCH_USAGE = "Usage: search KEYWORD";
    public static final String FIND_USAGE = "Usage: find [c/COMPANY] [r/ROLE] [s/STAGE] [b/DATE] [a/DATE] "
            + "[m/yes|no] (at least one filter; b/ = deadline on or before, a/ = on or after, "
            + "m/ = marked completed)";

    /** A field prefix is a lowercase letter and a slash at the start of the text or after a space. */
    private static final Pattern FIELD_PREFIX = Pattern.compile("(?:^|\\s)([a-z])/");

    private CommandParser() {
        // Utility class: all methods are static.
    }

    /**
     * Parses a typed command.
     *
     * @param input Full line entered by the user.
     * @return The matching command, or null when the first word is not a typed command
     *         (for example a menu number), so the caller can try other ways to interpret it.
     * @throws JobSeekException If the first word is a typed command but its arguments are invalid.
     */
    public static Command parse(String input) throws JobSeekException {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String[] parts = input.trim().split("\\s+", 2);
        String word = parts[0].toLowerCase(Locale.ROOT);
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        switch (word) {
        case "add":
            return parseAdd(arguments);
        case "edit":
            return parseEdit(arguments);
        case "delete":
            return new DeleteCommand(parseIndex(arguments, DELETE_USAGE));
        case "mark":
            return new MarkCommand(parseIndex(arguments, MARK_USAGE), true);
        case "unmark":
            return new MarkCommand(parseIndex(arguments, UNMARK_USAGE), false);
        case "find":
            return parseFind(arguments);
        case "search":
            if (arguments.isEmpty()) {
                throw new JobSeekException("Search term cannot be empty. " + SEARCH_USAGE);
            }
            return new SearchCommand(arguments);
        case "sort":
            return new SortCommand();
        case "help":
            return new HelpCommand();
        case "exit":
            return new ExitCommand();
        default:
            return null;
        }
    }

    private static Command parseAdd(String arguments) throws JobSeekException {
        Fields fields = parseFields(arguments, "crd", ADD_USAGE);
        if (!fields.leadingText.isEmpty()) {
            throw new JobSeekException("Unexpected text '" + fields.leadingText + "'. " + ADD_USAGE);
        }
        String company = fields.values.get('c');
        String role = fields.values.get('r');
        if (company == null || role == null) {
            throw new JobSeekException("Both company (c/) and role (r/) are required. " + ADD_USAGE);
        }
        return new AddCommand(company, role, parseDeadline(fields.values.get('d')));
    }

    private static Command parseEdit(String arguments) throws JobSeekException {
        Fields fields = parseFields(arguments, "crs", EDIT_USAGE);
        int index = parseIndex(fields.leadingText, EDIT_USAGE);
        String company = fields.values.get('c');
        String role = fields.values.get('r');
        String stage = fields.values.get('s');
        if (company == null && role == null && stage == null) {
            throw new JobSeekException("Nothing to edit. " + EDIT_USAGE);
        }
        return new EditCommand(index, company, role, stage == null ? null : parseStage(stage));
    }

    /**
     * Builds a find command. Each filter given becomes one {@link Predicate}; they are joined with
     * {@code and}, so an application must satisfy all of them to be shown.
     */
    private static Command parseFind(String arguments) throws JobSeekException {
        Fields fields = parseFields(arguments, "crsbam", FIND_USAGE);
        if (!fields.leadingText.isEmpty()) {
            throw new JobSeekException("Unexpected text '" + fields.leadingText + "'. " + FIND_USAGE);
        }
        if (fields.values.isEmpty()) {
            throw new JobSeekException("Please give at least one filter. " + FIND_USAGE);
        }

        Predicate<Application> filter = application -> true;
        List<String> conditions = new ArrayList<>();
        for (Map.Entry<Character, String> entry : fields.values.entrySet()) {
            String value = entry.getValue();
            switch (entry.getKey()) {
            case 'c':
                filter = filter.and(ApplicationFilters.companyContains(value));
                conditions.add("company contains '" + value + "'");
                break;
            case 'r':
                filter = filter.and(ApplicationFilters.roleContains(value));
                conditions.add("role contains '" + value + "'");
                break;
            case 's':
                String stage = parseStage(value);
                filter = filter.and(ApplicationFilters.inStage(RecruitmentStage.fromLabel(stage)));
                conditions.add("stage is " + stage);
                break;
            case 'b':
                LocalDate before = parseDeadline(value);
                filter = filter.and(ApplicationFilters.deadlineOnOrBefore(before));
                conditions.add("deadline on or before " + before);
                break;
            case 'a':
                LocalDate after = parseDeadline(value);
                filter = filter.and(ApplicationFilters.deadlineOnOrAfter(after));
                conditions.add("deadline on or after " + after);
                break;
            default:
                boolean completed = parseYesNo(value);
                filter = filter.and(ApplicationFilters.isCompleted(completed));
                conditions.add(completed ? "marked completed" : "not marked completed");
                break;
            }
        }
        return new FindCommand(filter, String.join(", ", conditions));
    }

    private static boolean parseYesNo(String text) throws JobSeekException {
        switch (text.toLowerCase(Locale.ROOT)) {
        case "yes":
        case "y":
        case "true":
            return true;
        case "no":
        case "n":
        case "false":
            return false;
        default:
            throw new JobSeekException("Invalid value for m/: '" + text + "'. Use yes or no.");
        }
    }

    /** Converts a one-based index typed by the user into the zero-based index commands expect. */
    private static int parseIndex(String text, String usage) throws JobSeekException {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new JobSeekException("Please specify an application index. " + usage);
        }
        try {
            int oneBased = Integer.parseInt(trimmed);
            if (oneBased < 1) {
                throw new JobSeekException("Application index must be 1 or more. " + usage);
            }
            return oneBased - 1;
        } catch (NumberFormatException e) {
            throw new JobSeekException("Invalid application index: '" + trimmed + "'. " + usage);
        }
    }

    private static LocalDate parseDeadline(String text) throws JobSeekException {
        if (text == null) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new JobSeekException("Invalid deadline: '" + text + "'. Use YYYY-MM-DD, e.g. 2026-11-30.");
        }
    }

    /** Validates the stage here so a typo is reported as a normal error instead of failing later. */
    private static String parseStage(String text) throws JobSeekException {
        try {
            return RecruitmentStage.fromLabel(text).getLabel();
        } catch (IllegalArgumentException e) {
            throw new JobSeekException("Invalid stage: '" + text + "'. Valid stages: Applied, "
                    + "Online Assessment (OA), Interview, Offer, Accepted, Rejected, Withdrawn.");
        }
    }

    /**
     * Splits text such as {@code "2 c/Google r/SWE Intern"} into the leading text before the first
     * prefix ("2") and a map from prefix letter to value.
     *
     * @param arguments Text after the command word.
     * @param allowedPrefixes Prefix letters this command accepts.
     * @param usage Usage message appended to errors.
     */
    private static Fields parseFields(String arguments, String allowedPrefixes, String usage)
            throws JobSeekException {
        List<MatchResult> prefixes = FIELD_PREFIX.matcher(arguments).results().toList();
        Fields fields = new Fields();
        if (prefixes.isEmpty()) {
            fields.leadingText = arguments.trim();
            return fields;
        }

        fields.leadingText = arguments.substring(0, prefixes.get(0).start()).trim();
        for (int i = 0; i < prefixes.size(); i++) {
            MatchResult prefix = prefixes.get(i);
            char key = arguments.charAt(prefix.start(1));
            int valueEnd = i + 1 < prefixes.size() ? prefixes.get(i + 1).start() : arguments.length();
            String value = arguments.substring(prefix.end(), valueEnd).trim();

            if (allowedPrefixes.indexOf(key) < 0) {
                throw new JobSeekException("Unknown field '" + key + "/'. " + usage);
            }
            if (fields.values.containsKey(key)) {
                throw new JobSeekException("Field '" + key + "/' was given more than once. " + usage);
            }
            if (value.isEmpty()) {
                throw new JobSeekException("Field '" + key + "/' cannot be empty. " + usage);
            }
            fields.values.put(key, value);
        }
        return fields;
    }

    /** The result of {@link #parseFields}: free text before the first prefix, then prefix values. */
    private static class Fields {
        private String leadingText = "";
        private final Map<Character, String> values = new LinkedHashMap<>();
    }
}
