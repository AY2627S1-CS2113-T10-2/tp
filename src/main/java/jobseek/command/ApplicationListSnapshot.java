package jobseek.command;

import java.util.ArrayList;
import java.util.List;

import jobseek.model.Application;
import jobseek.model.ApplicationList;

/**
 * Captures list order and field values before undo so a failed save can restore them.
 * Original objects are retained because earlier commands in history refer to those objects.
 */
final class ApplicationListSnapshot {
    private final List<Application> originals;
    private final List<Application> values = new ArrayList<>();

    /** Captures every mutable field without replacing the objects held by command history. */
    ApplicationListSnapshot(ApplicationList applications) {
        originals = new ArrayList<>(applications.getAll());
        for (Application application : originals) {
            values.add(new Application(application.getCompany(), application.getRole(), application.isCompleted(),
                    application.getRecruitmentStage(), application.getDeadline(), application.getPortalLink(),
                    application.getEmail(), application.getNotes()));
        }
    }

    /** Restores the same application objects, their fields and their original ordering. */
    void restore(ApplicationList applications) {
        for (int i = 0; i < originals.size(); i++) {
            Application original = originals.get(i);
            Application saved = values.get(i);
            original.setCompany(saved.getCompany());
            original.setRole(saved.getRole());
            original.setCompleted(saved.isCompleted());
            original.setRecruitmentStage(saved.getRecruitmentStage());
            original.setDeadline(saved.getDeadline());
            original.setPortalLink(saved.getPortalLink());
            original.setEmail(saved.getEmail());
            original.setNotes(saved.getNotes());
        }
        applications.getAll().clear();
        applications.getAll().addAll(originals);
    }
}
