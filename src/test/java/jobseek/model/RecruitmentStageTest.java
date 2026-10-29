package jobseek.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit tests for {@link RecruitmentStage}. */
public class RecruitmentStageTest {

    @Test
    public void fromLabel_acceptsStageNamesAndShortcuts() {
        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, RecruitmentStage.fromLabel("oa"));
        assertEquals(RecruitmentStage.INTERVIEW, RecruitmentStage.fromLabel(" interview "));
        assertEquals(RecruitmentStage.APPLIED, RecruitmentStage.fromLabel("Pending"));
    }

    @Test
    public void isTerminal_marksOnlyOutcomeStagesAsTerminal() {
        assertFalse(RecruitmentStage.INTERVIEW.isTerminal());
        assertTrue(RecruitmentStage.ACCEPTED.isTerminal());
        assertTrue(RecruitmentStage.REJECTED.isTerminal());
        assertTrue(RecruitmentStage.WITHDRAWN.isTerminal());
    }

    @Test
    public void fromLabel_rejectsUnknownStages() {
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromLabel("Screening"));
    }
}
