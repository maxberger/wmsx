/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.JobChangeEvent;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobState;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

/**
 * 
 * @version $Date: 1/1/2000$
 */
public class JobChangeEventEmitter implements JobListener {

    private static final Logger LOGGER = Logger
            .getLogger(JobChangeEventEmitter.class.toString());

    private final RemoteEventListener listener;

    private int snum;

    /**
     * @param r
     */
    public JobChangeEventEmitter(final RemoteEventListener r) {
        this.listener = r;
    }

    /** {@inheritDoc} */
    public void done(final JobUid id, final boolean success) {
        final RemoteEvent event;
        this.snum++;
        if (success) {
            event = new JobChangeEvent(id.toTransportJobUid(),
                    JobState.SUCCESS, this.snum);
        } else {
            event = new JobChangeEvent(id.toTransportJobUid(), JobState.FAILED,
                    this.snum);
        }
        this.sendEvent(event);
    }

    /** {@inheritDoc} */
    public void running(final JobUid id) {
        this.snum++;
        this.sendEvent(new JobChangeEvent(id.toTransportJobUid(),
                JobState.RUNNING, this.snum));
    }

    /** {@inheritDoc} */
    public void startup(final JobUid id) {
        this.snum++;
        this.sendEvent(new JobChangeEvent(id.toTransportJobUid(),
                JobState.STARTUP, this.snum));
    }

    /**
     * @param event
     */
    private void sendEvent(final RemoteEvent event) {
        try {
            this.listener.notify(event);
        } catch (final RemoteException e) {
            JobChangeEventEmitter.LOGGER.info(e.toString());
        } catch (final UnknownEventException e) {
            JobChangeEventEmitter.LOGGER.info(e.toString());
        } catch (final Exception e) {
            JobChangeEventEmitter.LOGGER.warning(e.toString());
        }
    }

}
