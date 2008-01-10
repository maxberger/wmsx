package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.JobState;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class ControllerImpl implements Controller {

    LinkedList<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    Map<Object, ControllerWorkDescription> running = new HashMap<Object, ControllerWorkDescription>();

    Set<Object> success = new HashSet<Object>();

    Set<Object> failed = new HashSet<Object>();

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    ControllerImpl() {
    }

    public WorkDescription retrieveWork() {
        synchronized (this.pending) {
            if (this.pending.isEmpty()) {
                return null;
            }
            final ControllerWorkDescription cwd = this.pending.removeFirst();
            this.running.put(cwd.getWorkDescription().getId(), cwd);
            return cwd.getWorkDescription();
        }
    }

    public void addWork(final ControllerWorkDescription newWork) {
        synchronized (this.pending) {
            this.pending.add(newWork);
        }
    }

    public void doneWith(final Object id, final ResultDescription result)
            throws RemoteException {
        synchronized (this.pending) {
            this.running.remove(id);
            this.success.add(id);
        }
        ControllerImpl.LOGGER.info("Done with worker Job " + id);
    }

    public JobState getState(final Object id) {
        synchronized (this.pending) {
            if (this.success.contains(id)) {
                return JobState.SUCCESS;
            }
            if (this.failed.contains(id)) {
                return JobState.FAILED;
            }
            if (this.running.keySet().contains(id)) {
                return JobState.RUNNING;
            }
            for (final ControllerWorkDescription cwd : this.pending) {
                if (cwd.getWorkDescription().getId().equals(id)) {
                    return JobState.STARTUP;
                }
            }
        }
        return JobState.NONE;
    }
}
