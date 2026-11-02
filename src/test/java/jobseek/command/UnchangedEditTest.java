package jobseek.command;

import jobseek.TestStorage;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that edits which leave every value unchanged are not saved or recorded for undo.
 */
public class UnchangedEditTest {

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }

    private static ApplicationList listWithAcme() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "Intern", false, RecruitmentStage.INTERVIEW,
                LocalDate.of(2026, 12, 1), "http://acme", "hr@acme.com", "note"));
        return applications;
    }

    private static EditCommand run(EditCommand command, ApplicationList applications, RecordingUi ui)
            throws Exception {
        command.execute(applications, ui, TestStorage.create());
        return command;
    }

    @Test
    public void execute_sameCompany_isNotRecorded() throws Exception {
        RecordingUi ui = new RecordingUi();

        EditCommand command = run(EditCommand.editCompany(0, "Acme"), listWithAcme(), ui);

        assertFalse(command.hasChanged());
        assertTrue(ui.lastMessage.startsWith("No changes made"));
    }

    @Test
    public void execute_allFieldsEqualToCurrentValues_isNotRecorded() throws Exception {
        RecordingUi ui = new RecordingUi();
        EditCommand all = EditCommand.editAll(0, "Acme", "Intern", RecruitmentStage.INTERVIEW,
                LocalDate.of(2026, 12, 1), "http://acme", "hr@acme.com", "note");

        run(all, listWithAcme(), ui);

        assertFalse(all.hasChanged());
    }

    @Test
    public void execute_sameDeadlineOrClearingAnAlreadyEmptyField_isNotRecorded() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "Intern", false));

        EditCommand clearDeadline = run(EditCommand.editDeadline(0, null), applications, new RecordingUi());
        EditCommand clearNotes = run(EditCommand.editNotes(0, ""), applications, new RecordingUi());

        assertFalse(clearDeadline.hasChanged());
        assertFalse(clearNotes.hasChanged());
    }

    @Test
    public void execute_realChange_isStillRecordedAndUndoable() throws Exception {
        ApplicationList applications = listWithAcme();
        EditCommand command = run(EditCommand.editCompany(0, "Beta"), applications, new RecordingUi());

        assertTrue(command.hasChanged());
        assertEquals("Beta", applications.get(0).getCompany());
        command.undo(applications);
        assertEquals("Acme", applications.get(0).getCompany());
    }

    @Test
    public void execute_oneFieldDiffersAmongSeveral_isRecorded() throws Exception {
        ApplicationList applications = listWithAcme();
        EditCommand all = EditCommand.editAll(0, "Acme", "Intern", RecruitmentStage.OFFER,
                LocalDate.of(2026, 12, 1), "http://acme", "hr@acme.com", "note");

        run(all, applications, new RecordingUi());

        assertTrue(all.hasChanged());
        assertEquals(RecruitmentStage.OFFER, applications.get(0).getRecruitmentStage());
    }

    @Test
    public void execute_sameStage_isNotRecorded() throws Exception {
        ApplicationList applications = listWithAcme();
        RecordingUi ui = new RecordingUi();
        RecruitmentStageCommand command = new RecruitmentStageCommand(0, RecruitmentStage.INTERVIEW);

        command.execute(applications, ui, TestStorage.create());

        assertFalse(command.hasChanged());
        assertTrue(ui.lastMessage.startsWith("No changes made"));
    }

    @Test
    public void execute_differentStage_isRecorded() throws Exception {
        ApplicationList applications = listWithAcme();
        RecruitmentStageCommand command = new RecruitmentStageCommand(0, RecruitmentStage.OFFER);

        command.execute(applications, new RecordingUi(), TestStorage.create());

        assertTrue(command.hasChanged());
    }

    @Test
    public void history_unchangedEditThenUndo_undoesThePreviousRealChange() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();
        AddCommand add = new AddCommand("Acme", "Intern");
        add.execute(applications, new RecordingUi(), TestStorage.create());
        history.record(add);
        EditCommand unchanged = EditCommand.editCompany(0, "Acme");
        unchanged.execute(applications, new RecordingUi(), TestStorage.create());
        history.record(unchanged);

        new UndoCommand(history).execute(applications, new RecordingUi(), TestStorage.create());

        // The undo reversed the add, not a no-op edit.
        assertTrue(applications.isEmpty());
    }
}
