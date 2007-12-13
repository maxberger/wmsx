package hu.kfki.grid.wmsx.job;

public final class JobState {

    private JobState() {
    };

    public static final JobState STARTUP = new JobState();

    public static final JobState RUNNING = new JobState();

    public static final JobState SUCCESS = new JobState();

    public static final JobState FAILED = new JobState();

    public static final JobState NONE = new JobState();

}
