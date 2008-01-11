package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

public class ControllerWorkDescription {

    private final WorkDescription workDescription;

    private static final Logger LOGGER = Logger
            .getLogger(ControllerWorkDescription.class.toString());

    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        final Map<String, byte[]> inputSandbox = new TreeMap<String, byte[]>();

        for (final String fileName : jobDesc
                .getListEntry(JobDescription.INPUTSANDBOX)) {
            try {
                final File f = FileUtil.resolveFile(jobDesc.getBaseDir(),
                        fileName);
                inputSandbox.put(f.getName(), this.loadFile(f));
            } catch (final IOException io) {
                ControllerWorkDescription.LOGGER.warning(io.getMessage());
            }
        }

        this.workDescription = new WorkDescription(uid, inputSandbox, jobDesc
                .getStringEntry(JobDescription.EXECUTABLE), jobDesc
                .getStringEntry(JobDescription.STDOUTPUT), jobDesc
                .getStringEntry(JobDescription.STDERROR));
    }

    private byte[] loadFile(final File f) throws IOException {
        final int len = (int) f.length();
        final byte[] buf = new byte[len];
        final FileInputStream fis = new FileInputStream(f);
        fis.read(buf);
        fis.close();
        return buf;
    }

    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }
}
