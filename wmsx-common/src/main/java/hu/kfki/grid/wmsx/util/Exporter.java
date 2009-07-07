/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

/* $Id$ */

package hu.kfki.grid.wmsx.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.InvocationLayerFactory;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;

/**
 * Helper class to create remote proxies.
 * 
 * @version $Date$
 */
public final class Exporter {

    private static final Exporter INSTANCE = new Exporter();

    private final InvocationLayerFactory invocationLayerFactory = new BasicILFactory();

    private final List<net.jini.export.Exporter> exporters = new ArrayList<net.jini.export.Exporter>();

    private Exporter() {
        // empty on purpose.
    }

    /**
     * @return the singleton instance.
     */
    public static Exporter getInstance() {
        return Exporter.INSTANCE;
    }

    private static InetAddress getLocalHost() throws UnknownHostException {
        final Set<InetAddress> addresses = new HashSet<InetAddress>();
        try {
            final Enumeration<NetworkInterface> ifaces = NetworkInterface
                    .getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                final NetworkInterface ni = ifaces.nextElement();
                final Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    final InetAddress ia = addrs.nextElement();
                    if (!ia.isAnyLocalAddress() && !ia.isLoopbackAddress()
                            && !ia.isSiteLocalAddress()
                            && ia instanceof Inet4Address) {
                        addresses.add(ia);
                    }
                }
            }
        } catch (final IOException e) {
            // ignore
        }

        if (addresses.isEmpty()) {
            return InetAddress.getLocalHost();
        } else {
            return addresses.iterator().next();
        }
    }

    /**
     * Export an object through RMI in the allowed Globus port ranges.
     * 
     * @param impl
     *            the actual implementation.
     * @return a remote proxy.
     */
    public Remote export(final Remote impl) {
        String hostname;
        try {
            hostname = Exporter.getLocalHost().getHostAddress();
        } catch (final UnknownHostException ue) {
            return null;
        }

        Remote stub = null;
        int port = GlobusTcp.getInstance().getMinTcp();
        final int max = GlobusTcp.getInstance().getMaxTcp();
        synchronized (this.exporters) {
            while (stub == null && port <= max) {
                try {
                    final ServerEndpoint endpoint = TcpServerEndpoint
                            .getInstance(hostname, port);
                    final net.jini.export.Exporter ex = new BasicJeriExporter(
                            endpoint, this.invocationLayerFactory, false, true);
                    stub = ex.export(impl);
                    this.exporters.add(ex);
                } catch (final ExportException e) {
                    port++;
                    stub = null;
                }
            }
        }
        return stub;
    }

    /**
     * Remove all exporters.
     */
    public void unexportAll() {
        synchronized (this.exporters) {
            for (final net.jini.export.Exporter e : this.exporters) {
                e.unexport(true);
            }
            this.exporters.clear();
        }
    }
}
