package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit and workflow tests for the Applications page and Add Application feature in {@link JobSeek}.
 */
public class JobSeekTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        JobSeek.clearApplications();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        JobSeek.clearApplications();
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void run_addApplication_success() {
        String input = "1\nGoogle\nSoftware Engineer Intern\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        JobSeek.run(scanner);

        assertEquals(1, JobSeek.getApplications().size());
        assertEquals("Google", JobSeek.getApplications().get(0).getCompany());
        assertEquals("Software Engineer Intern", JobSeek.getApplications().get(0).getRole());

        String output = outputStream.toString();
        assertTrue(output.contains("Application added successfully."));
        assertTrue(output.contains("Company: Google"));
        assertTrue(output.contains("Role: Software Engineer Intern"));
        assertTrue(output.contains("1. Google | Software Engineer Intern"));
    }

    @Test
    public void run_addApplicationWithBlankValues_rejectsAndPromptsAgain() {
        String input = "1\n\n   \nGoogle\n\n   \nSoftware Engineer Intern\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        JobSeek.run(scanner);

        assertEquals(1, JobSeek.getApplications().size());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Company cannot be empty."));
        assertTrue(output.contains("Error: Role cannot be empty."));
    }
}
