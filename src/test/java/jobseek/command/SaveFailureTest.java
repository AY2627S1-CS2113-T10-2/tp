package jobseek.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import jobseek.Main;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression tests for save failures during changes and during undo. */
public class SaveFailureTest {
    @TempDir
    Path directory;

    /** Every modifying command must restore fields, order and identity after a failed save. */
    @Test
    public void execute_saveFails_restoresStateWithoutSuccessOrHistoryEntry() throws Exception {
        for (int kind = 0; kind < 6; kind++) {
            ApplicationList applications = initialList();
            Application original = applications.get(0);
            original.setCompleted(kind == 5);
            List<String> before = state(applications);
            ControlledStorage storage = new ControlledStorage(directory.resolve("forward" + kind));
            storage.save(applications);
            byte[] saved = Files.readAllBytes(Path.of(storage.getFilePath()));
            storage.fail = true;
            RecordingUi ui = new RecordingUi();
            UndoableCommand command = command(kind);

            assertThrows(IOException.class, () -> command.execute(applications, ui, storage));

            assertEquals(before, state(applications));
            assertSame(original, applications.get(0));
            assertFalse(command.hasChanged());
            CommandHistory history = new CommandHistory();
            history.record(command);
            assertTrue(history.isEmpty());
            assertEquals(0, ui.messages);
            assertArrayEquals(saved, Files.readAllBytes(Path.of(storage.getFilePath())));
        }
    }

    /** A failed undo retains both the applied state and the command so undo can be retried. */
    @Test
    public void undo_saveFails_restoresAppliedStateAndAllowsRetry() throws Exception {
        for (int kind = 0; kind < 6; kind++) {
            ApplicationList applications = initialList();
            Application original = applications.get(0);
            original.setCompleted(kind == 5);
            List<String> initial = state(applications);
            ControlledStorage storage = new ControlledStorage(directory.resolve("undo" + kind));
            RecordingUi ui = new RecordingUi();
            UndoableCommand command = command(kind);
            command.execute(applications, ui, storage);
            CommandHistory history = new CommandHistory();
            history.record(command);
            List<String> applied = state(applications);
            byte[] saved = Files.readAllBytes(Path.of(storage.getFilePath()));
            ui.messages = 0;
            storage.fail = true;
            UndoCommand undo = new UndoCommand(history);

            assertThrows(IOException.class, () -> undo.execute(applications, ui, storage));

            assertEquals(applied, state(applications));
            assertSame(command, history.peek());
            assertEquals(0, ui.messages);
            assertArrayEquals(saved, Files.readAllBytes(Path.of(storage.getFilePath())));
            storage.fail = false;
            undo.execute(applications, ui, storage);
            assertEquals(initial, state(applications));
            assertSame(original, applications.get(0));
            assertTrue(history.isEmpty());
            assertEquals(initial, state(storage.load()));
        }
    }

    /** The main loop must preserve an older undo entry after a newer deletion fails to save. */
    @Test
    public void run_failedDelete_preservesEarlierUndoHistory() throws Exception {
        ApplicationList applications = initialList();
        List<String> initial = state(applications);
        Storage storage = new Storage(directory.resolve("main").toString()) {
            private int saves;

            @Override
            public void save(ApplicationList list) throws IOException {
                saves++;
                if (saves == 2) {
                    throw new IOException("Simulated write failure");
                }
                super.save(list);
            }
        };
        Ui ui = new Ui(new Scanner("edit 1 c/Changed\ndelete 1\ny\nundo\nexit\n"));

        new Main(ui, storage, applications).run();

        assertEquals(initial, state(applications));
        assertEquals(initial, state(storage.load()));
    }

    /** Creates records with optional values so rollback cannot accidentally clear them. */
    private ApplicationList initialList() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "Intern", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 11, 3), "https://acme.example", "jobs@acme.example", "Original notes"));
        applications.add(new Application("OtherCo", "Developer"));
        return applications;
    }

    /** Covers every modifying command, including both completion flag directions. */
    private UndoableCommand command(int kind) {
        return switch (kind) {
        case 0 -> new AddCommand("NewCo", "Analyst");
        case 1 -> new DeleteCommand(0);
        case 2 -> EditCommand.editAll(0, "Changed", "Changed role", RecruitmentStage.OFFER,
                null, "", "", "Updated notes");
        case 3 -> new RecruitmentStageCommand(0, RecruitmentStage.INTERVIEW);
        case 4 -> new MarkCommand(0, true);
        default -> new MarkCommand(0, false);
        };
    }

    /** Compares all persisted fields in list order. */
    private List<String> state(ApplicationList applications) {
        return applications.getAll().stream().map(app -> String.join("|", app.getCompany(), app.getRole(),
                Boolean.toString(app.isCompleted()), app.getRecruitmentStage().name(),
                String.valueOf(app.getDeadline()), app.getPortalLink(), app.getEmail(), app.getNotes())).toList();
    }

    /** Allows tests to inject a write error, then retry using real local storage. */
    private static class ControlledStorage extends Storage {
        private boolean fail;

        ControlledStorage(Path path) {
            super(path.toString());
        }

        @Override
        public void save(ApplicationList applications) throws IOException {
            if (fail) {
                throw new IOException("Simulated write failure");
            }
            super.save(applications);
        }
    }

    /** Confirms deletion prompts while recording only success messages. */
    private static class RecordingUi extends Ui {
        private int messages;

        RecordingUi() {
            super(new Scanner("y\n"));
        }

        @Override
        public void showMessage(String message) {
            messages++;
        }
    }
}
