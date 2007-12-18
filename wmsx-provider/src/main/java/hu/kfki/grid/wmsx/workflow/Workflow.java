package hu.kfki.grid.wmsx.workflow;

import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

public class Workflow {

    final File directory;

    final Map nextNodes;

    final Map prevNodes;

    final Set potentialTodo;

    final Set done;

    private static final Logger LOGGER = Logger.getLogger(Workflow.class
            .toString());

    public Workflow(final File dir) {
        this.directory = dir;
        this.nextNodes = new HashMap();
        this.prevNodes = new HashMap();
        this.potentialTodo = new HashSet();
        this.done = new HashSet();
    }

    public synchronized void isDone(final JdlJob jdlJob) {
        final String name = jdlJob.getName();
        System.out.println("Done with: " + name);
        this.done.add(name);
        synchronized (this.nextNodes) {
            final List next = (List) this.nextNodes.get(name);
            if (next != null) {
                this.potentialTodo.addAll(next);
            }
        }
        final Iterator it = this.potentialTodo.iterator();
        final List toExecute = new Vector();
        while (it.hasNext()) {
            final String node = (String) it.next();
            final List prevs = (List) this.prevNodes.get(node);
            boolean execute = true;
            if (prevs != null) {
                final Iterator i2 = prevs.iterator();
                while (i2.hasNext() && execute) {
                    final String prevNode = (String) i2.next();
                    if (!this.done.contains(prevNode)) {
                        execute = false;
                    }
                }
            }
            if (execute) {
                toExecute.add(node);
            }
        }

        final Iterator i3 = toExecute.iterator();
        while (i3.hasNext()) {
            final String node = (String) i3.next();
            this.executeNode(node);
        }
    }

    private void executeNode(final String node) {
        System.out.println("Executing " + node);
        this.potentialTodo.remove(node);
        WmsxProviderImpl.getInstance().addJobFactory(
                new WorkflowNodeJobFactory(this, node));
    }

    public void setNextNodes(final String name, final List listEntry) {
        synchronized (this.nextNodes) {
            this.nextNodes.put(name, listEntry);
            this.parsePrev(listEntry);
        }
    }

    private void parsePrev(final List listEntry) {
        final Iterator it = listEntry.iterator();
        while (it.hasNext()) {
            final String nodeName = (String) it.next();
            if (!this.prevNodes.containsKey(nodeName)) {
                try {
                    final JobDescription nodeJob = new JDLJobDescription(
                            new File(this.directory, nodeName)
                                    .getAbsolutePath());
                    final List prevs = nodeJob.getListEntry("Prev");
                    this.prevNodes.put(nodeName, prevs);
                } catch (final IOException e) {
                    Workflow.LOGGER.warning(e.getMessage());
                }

            }
        }
    }

    public File getDirectory() {
        return this.directory;
    }
}
