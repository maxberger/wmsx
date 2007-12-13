package hu.kfki.grid.wmsx.backends;

import hu.kfki.grid.wmsx.backends.lcg.EDGBackend;
import hu.kfki.grid.wmsx.backends.lcg.GLiteBackend;
import hu.kfki.grid.wmsx.backends.local.FakeBackend;
import hu.kfki.grid.wmsx.backends.local.LocalBackend;

public final class Backends {

    public static final Backend EDG = EDGBackend.getInstance();

    public static final Backend GLITE = GLiteBackend.getInstance();

    public static final Backend FAKE = FakeBackend.getInstance();

    public static final Backend LOCAL = LocalBackend.getInstance();
}
