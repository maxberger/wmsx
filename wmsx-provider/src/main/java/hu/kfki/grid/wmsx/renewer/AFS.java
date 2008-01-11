package hu.kfki.grid.wmsx.renewer;

public class AFS extends Renewer {

    public AFS(final String password) {
        super(password);
    }

    @Override
    protected boolean exec(final String password) {
        return PasswordAppLauncher.getInstance().launch(
                new String[] { "kinit" }, password);
    }

    @Override
    protected void postexec() {
        PasswordAppLauncher.getInstance()
                .launch(new String[] { "aklog" }, null);
    }

}
