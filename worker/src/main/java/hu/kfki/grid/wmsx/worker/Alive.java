package hu.kfki.grid.wmsx.worker;

import java.rmi.RemoteException;

import net.jini.id.Uuid;

public class Alive implements Runnable {

    private final Controller controller;

    private final Uuid uuid;

    private boolean started;

    private boolean shutdown;

    public Alive(final Controller cont, final Uuid id) {
        this.controller = cont;
        this.uuid = id;
        this.started = false;
        this.shutdown = false;
    }

    public void run() {
        boolean goon = true;

        while (goon) {
            try {
                Thread.sleep(30 * 1000);
            } catch (final InterruptedException e) {
                // ignore
            }
            try {
                this.controller.ping(this.uuid);
            } catch (final RemoteException e) {
                this.shutdown = true;
            }
            synchronized (this) {
                if (this.shutdown) {
                    this.started = false;
                    this.shutdown = false;
                    goon = false;
                }
            }
        }
    }

    public synchronized void start() {
        if (!this.started) {
            new Thread(this).start();
            this.started = true;
        }

    }

    public synchronized void stop() {
        this.shutdown = true;
    }
}
