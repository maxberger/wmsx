package hu.kfki.grid.wmsx.renewer;

import java.util.List;
import java.util.Vector;

public class VOMS extends Renewer {

    private final String voms;

    public VOMS(final String password, final String vo) {
        super(password);
        this.voms = vo;
    }

    @Override
    protected boolean exec(final String password) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("voms-proxy-init");
        if (this.voms != null) {
            commandLine.add("-voms");
            commandLine.add(this.voms);
        }
        commandLine.add("-pwstdin");
        return PasswordAppLauncher.getInstance().launch(
                commandLine.toArray(new String[commandLine.size()]), password);

    }

}
