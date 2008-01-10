package hu.kfki.grid.wmsx.worker;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.logging.Logger;

public class ControllerImpl implements Controller {

    LinkedList<WorkDescription> work = new LinkedList<WorkDescription>();

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    ControllerImpl() {
        this.addWork(new WorkDescription("eins"));
        this.addWork(new WorkDescription("zwei"));
        this.addWork(new WorkDescription("drei"));
    }

    public WorkDescription retrieveWork() {
        synchronized (this.work) {
            if (this.work.isEmpty()) {
                return null;
            }
            return this.work.removeFirst();
        }
    }

    public void addWork(final WorkDescription newWork) {
        synchronized (this.work) {
            this.work.add(newWork);
        }
    }

    public void doneWith(final String id, final ResultDescription result)
            throws RemoteException {
        ControllerImpl.LOGGER.info("Done with worker Job " + id);
    }

}
