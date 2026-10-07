package jobseek.model;

import java.util.ArrayList;

/**
 * Manages an in-memory list of job applications.
 * Provides safe collection operations and index boundary validations.
 */
public class ApplicationList {
    private final ArrayList<Application> applications;

    /**
     * Constructs an empty ApplicationList.
     */
    public ApplicationList() {
        this.applications = new ArrayList<>();
    }

    /**
     * Constructs an ApplicationList pre-populated with existing applications.
     *
     * @param applications Initial list of applications.
     */
    public ApplicationList(ArrayList<Application> applications) {
        this.applications = applications != null ? applications : new ArrayList<>();
    }

    /**
     * Adds an application to the list.
     *
     * @param app Application to be added.
     */
    public void add(Application app) {
        if (app != null) {
            applications.add(app);
        }
    }

    /**
     * Removes and returns the application at the specified 0-based index.
     *
     * @param index 0-based index of the application to delete.
     * @return The removed Application.
     * @throws IndexOutOfBoundsException If index is out of bounds (< 0 or >= size).
     */
    public Application delete(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + ". Must be between 0 and "
                    + (applications.size() - 1) + ".");
        }
        return applications.remove(index);
    }

    /**
     * Retrieves the application at the specified 0-based index.
     *
     * @param index 0-based index of the application to retrieve.
     * @return The Application at the given index.
     * @throws IndexOutOfBoundsException If index is out of bounds (< 0 or >= size).
     */
    public Application get(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + ". Must be between 0 and "
                    + (applications.size() - 1) + ".");
        }
        return applications.get(index);
    }

    /**
     * Returns the total number of applications currently stored.
     *
     * @return Total count of applications.
     */
    public int size() {
        return applications.size();
    }

    /**
     * Returns the underlying list of applications.
     *
     * @return ArrayList of all applications.
     */
    public ArrayList<Application> getAll() {
        return applications;
    }

    /**
     * Checks if the specified 0-based index is valid within the list.
     *
     * @param index The 0-based index to check.
     * @return True if the index is non-negative and less than list size, false otherwise.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < applications.size();
    }

    /**
     * Checks whether the list contains zero applications.
     *
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return applications.isEmpty();
    }
}
