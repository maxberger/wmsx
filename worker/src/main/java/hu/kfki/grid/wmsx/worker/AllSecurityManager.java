package hu.kfki.grid.wmsx.worker;

import java.security.Permission;

public class AllSecurityManager extends SecurityManager {

    public void checkPermission(final Permission perm, final Object context) {
        // Do nothing
    }

    public void checkPermission(final Permission perm) {
        // Do nothing
    }

}
