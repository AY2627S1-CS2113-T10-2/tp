package jobseek.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ApplicationFilters}.
 */
public class ApplicationFiltersTest {

    @Test
    public void companyContains_caseInsensitiveSubstringMatching() {
        Application app = new Application("Google Asia", "SWE Intern", false,
                RecruitmentStage.APPLIED, null, "", "", "");

        Predicate<Application> filter = ApplicationFilters.companyContains("google");
        assertTrue(filter.test(app));

        Predicate<Application> mismatch = ApplicationFilters.companyContains("Meta");
        assertFalse(mismatch.test(app));
    }

    @Test
    public void roleContains_caseInsensitiveSubstringMatching() {
        Application app = new Application("Grab", "Backend Developer Intern", false,
                RecruitmentStage.APPLIED, null, "", "", "");

        Predicate<Application> filter = ApplicationFilters.roleContains("DEVELOPER");
        assertTrue(filter.test(app));

        Predicate<Application> mismatch = ApplicationFilters.roleContains("Frontend");
        assertFalse(mismatch.test(app));
    }

    @Test
    public void inStage_matchesExactStage() {
        Application interviewApp = new Application("Shopee", "QA Engineer", false,
                RecruitmentStage.INTERVIEW, null, "", "", "");

        assertTrue(ApplicationFilters.inStage(RecruitmentStage.INTERVIEW).test(interviewApp));
        assertFalse(ApplicationFilters.inStage(RecruitmentStage.OFFER).test(interviewApp));
    }

    @Test
    public void deadlineOnOrBefore_correctDateComparisons() {
        LocalDate date = LocalDate.of(2026, 11, 15);
        Application earlier = new Application("A", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 10), "", "", "");
        Application sameDay = new Application("B", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 15), "", "", "");
        Application later = new Application("C", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 20), "", "", "");
        Application undated = new Application("D", "R", false,
                RecruitmentStage.APPLIED, null, "", "", "");

        Predicate<Application> filter = ApplicationFilters.deadlineOnOrBefore(date);
        assertTrue(filter.test(earlier));
        assertTrue(filter.test(sameDay));
        assertFalse(filter.test(later));
        assertFalse(filter.test(undated));
    }

    @Test
    public void deadlineOnOrAfter_correctDateComparisons() {
        LocalDate date = LocalDate.of(2026, 11, 15);
        Application earlier = new Application("A", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 10), "", "", "");
        Application sameDay = new Application("B", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 15), "", "", "");
        Application later = new Application("C", "R", false,
                RecruitmentStage.APPLIED, LocalDate.of(2026, 11, 20), "", "", "");
        Application undated = new Application("D", "R", false,
                RecruitmentStage.APPLIED, null, "", "", "");

        Predicate<Application> filter = ApplicationFilters.deadlineOnOrAfter(date);
        assertFalse(filter.test(earlier));
        assertTrue(filter.test(sameDay));
        assertTrue(filter.test(later));
        assertFalse(filter.test(undated));
    }

    @Test
    public void isCompleted_matchesBooleanCompletionState() {
        Application active = new Application("A", "R", false,
                RecruitmentStage.APPLIED, null, "", "", "");
        Application done = new Application("B", "R", true,
                RecruitmentStage.ACCEPTED, null, "", "", "");

        assertTrue(ApplicationFilters.isCompleted(false).test(active));
        assertFalse(ApplicationFilters.isCompleted(true).test(active));
        assertTrue(ApplicationFilters.isCompleted(true).test(done));
        assertFalse(ApplicationFilters.isCompleted(false).test(done));
    }
}
