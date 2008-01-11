package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ControllerImpl implements Controller {

    LinkedList<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    Map<Object, ControllerWorkDescription> running = new TreeMap<Object, ControllerWorkDescription>();

    Map<Object, Map<String, byte[]>> success = new TreeMap<Object, Map<String, byte[]>>();

    Set<Object> failed = new TreeSet<Object>();

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

    @SuppressWarnings("unchecked")
    public void doneWith(final Object id, final ResultDescription result)
            throws RemoteException {
        synchronized (this.pending) {
            this.running.remove(id);
            this.success.put(id, result.getOutputSandbox());
        }
        ControllerImpl.LOGGER.info("Done with worker Job " + id);
    }

    public JobState getState(final Object id) {
        synchronized (this.pending) {
            if (this.failed.contains(id)) {
                return JobState.FAILED;
            }
            if (this.success.keySet().contains(id)) {
                return JobState.SUCCESS;
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

    public void retrieveSandbox(final Object id, final File dir) {
        Map<String, byte[]> sandbox;
        synchronized (this.pending) {
            sandbox = this.success.get(id);
        }
        FileUtil.retrieveSandbox(sandbox, dir);
    }
}
