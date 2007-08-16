package renewer;

public class VOMS extends Renewer {

    public VOMS(final String password) {
        super(password);
    }

    protected boolean exec(final String password) {
        return PasswordAppLauncher.getInstance().launch(
                new String[] { "voms-proxy-init", "-pwstdin" }, password);

    }

}
