package hu.kfki.grid.wmsx.job.result;

import java.io.File;
import java.util.logging.Logger;

public class ResultMover implements Runnable {

    private final Process process;

    private final File dir;

    private static final Logger LOGGER = Logger.getLogger(ResultMover.class
            .toString());

    public ResultMover(final Process p, final File d) {
        this.process = p;
        this.dir = d;
    }

    public void run() {
        try {
            this.process.waitFor();
            final File[] dirContent = this.dir.listFiles();
            for (int i = 0; i < dirContent.length; i++) {
                final File subdir = dirContent[i];
                if (subdir.isDirectory()) {
                    final File[] toMoves = subdir.listFiles();
                    for (int j = 0; j < toMoves.length; j++) {
                        final File toMove = toMoves[i];
                        toMove.renameTo(new File(this.dir, toMove.getName()));
                    }
                    subdir.delete();
                }
            }

        } catch (final InterruptedException e) {
            ResultMover.LOGGER.warning(e.getMessage());
        }

    }

}
