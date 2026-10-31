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
    public void fromChoice_validOptionsAndLabels_resolvesCorrectly() {
        assertEquals(RecruitmentStage.APPLIED, RecruitmentStage.fromChoice("1"));
        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, RecruitmentStage.fromChoice("2"));
        assertEquals(RecruitmentStage.INTERVIEW, RecruitmentStage.fromChoice("3"));
        assertEquals(RecruitmentStage.OFFER, RecruitmentStage.fromChoice("4"));
        assertEquals(RecruitmentStage.ACCEPTED, RecruitmentStage.fromChoice("5"));
        assertEquals(RecruitmentStage.REJECTED, RecruitmentStage.fromChoice("6"));
        assertEquals(RecruitmentStage.WITHDRAWN, RecruitmentStage.fromChoice("7"));
        assertEquals(RecruitmentStage.INTERVIEW, RecruitmentStage.fromChoice("Interview"));
    }

    @Test
    public void fromChoice_invalidOptions_throws() {
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice("0"));
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice("8"));
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice(""));
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice(null));
    }

    @Test
    public void fromLabel_rejectsUnknownStages() {
        assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromLabel("Screening"));
    }
}
