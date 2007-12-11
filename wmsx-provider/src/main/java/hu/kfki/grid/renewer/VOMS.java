package hu.kfki.grid.renewer;

import java.util.List;
import java.util.Vector;

public class VOMS extends Renewer {

    private final String voms;

    public VOMS(final String password, String vo) {
        super(password);
        this.voms = vo;
    }

    protected boolean exec(final String password) {
        final List commandLine = new Vector();
        commandLine.add("voms-proxy-init");
        if (voms != null) {
            commandLine.add("-voms");
            commandLine.add(this.voms);
        }
        commandLine.add("-pwstdin");
        return PasswordAppLauncher.getInstance().launch(
                (String[]) commandLine.toArray(new String[commandLine.size()]),
                password);

    }

}
