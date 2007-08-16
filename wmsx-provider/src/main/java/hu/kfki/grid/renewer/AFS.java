package hu.kfki.grid.renewer;

public class AFS extends Renewer {

    public AFS(final String password) {
        super(password);
    }

    protected boolean exec(final String password) {
        return PasswordAppLauncher.getInstance().launch(
                new String[] { "kinit" }, password);
    }

    protected void postexec() {
        PasswordAppLauncher.getInstance()
                .launch(new String[] { "aklog" }, null);
    }

}
