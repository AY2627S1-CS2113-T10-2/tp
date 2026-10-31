package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ApplicationTable}.
 */
public class ApplicationTableTest {

    @Test
    public void format_emptyIndices_displaysHeadersAndSeparators() {
        ApplicationList list = new ApplicationList();
        String output = ApplicationTable.format(list, Collections.emptyList(), "Applications View");

        assertTrue(output.contains("Applications View"));
        assertTrue(output.contains("No. | Completed | Company | Role"));
        assertTrue(output.contains("---+-----------+---------+-----"));
    }

    @Test
    public void format_populatedList_formatsColumnsAndIcons() {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Alphabet Inc.", "Software Engineer", false,
                RecruitmentStage.APPLIED, null, "", "", ""));
        list.add(new Application("Meta Platforms", "Production Engineer", true,
                RecruitmentStage.ACCEPTED, null, "", "", ""));

        String output = ApplicationTable.format(list, List.of(0, 1), "Test Heading");

        assertTrue(output.contains("Test Heading"));
        assertTrue(output.contains("1   | [ ]"));
        assertTrue(output.contains("Alphabet Inc."));
        assertTrue(output.contains("Software Engineer"));
        assertTrue(output.contains("2   | [X]"));
        assertTrue(output.contains("Meta Platforms"));
        assertTrue(output.contains("Production Engineer"));
    }
}
