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

package at.ac.uibk.dps.wmsx.gat.guid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;

import at.ac.uibk.dps.wmsx.backends.guid.GuidBackend;
import at.ac.uibk.dps.wmsx.gat.GatCommon;

/**
 * GAT implementation of {@link GuidBackend}.
 * 
 * @version $Date$
 */
public class GatGuidBackend implements GuidBackend {
    private static final String GUID_PREFIX = "guid:///";

    private static final Logger LOGGER = Logger.getLogger(GatGuidBackend.class
            .toString());

    private final GatCommon gatCommon = GatCommon.getInstance();

    /**
     * Default constructor.
     */
    public GatGuidBackend() {
        // nothing to do.
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return this.gatCommon.isAvailable("File", "GliteGuidFileAdaptor");
    }

    /** {@inheritDoc} */
    public String upload(final java.io.File localFile) {
        String filename = null;
        try {
            final File gatFile = GAT.createFile(this.gatCommon.getGatContext(),
                    new URI(GatGuidBackend.GUID_PREFIX));
            gatFile.createNewFile();
            GatGuidBackend.LOGGER.fine("Created file: " + gatFile.toGATURI());
            final File localGatFile = GAT.createFile(this.gatCommon
                    .getGatContext(), localFile.getAbsolutePath());
            localGatFile.copy(gatFile.toGATURI());
            filename = gatFile.getPath();
            gatFile.deleteOnExit();
        } catch (final IOException e) {
            GatGuidBackend.LOGGER.warning(e.toString());
        } catch (final GATObjectCreationException e) {
            GatGuidBackend.LOGGER.warning(e.toString());
        } catch (final URISyntaxException e) {
            GatGuidBackend.LOGGER.warning(e.getMessage());
        } catch (final GATInvocationException e) {
            GatGuidBackend.LOGGER.warning(e.getMessage());
        }
        return filename;
    }

    /** {@inheritDoc} */
    public void download(final String guid, final java.io.File localFile) {
        try {
            final File gatFile = GAT.createFile(this.gatCommon.getGatContext(),
                    new URI(GatGuidBackend.GUID_PREFIX + guid));
            final File localGatFile = GAT.createFile(this.gatCommon
                    .getGatContext(), localFile.getAbsolutePath());
            gatFile.copy(localGatFile.toGATURI());
        } catch (final GATObjectCreationException e) {
            GatGuidBackend.LOGGER.warning(e.getMessage());
        } catch (final URISyntaxException e) {
            GatGuidBackend.LOGGER.warning(e.getMessage());
        } catch (final GATInvocationException e) {
            GatGuidBackend.LOGGER.warning(e.getMessage());
        }
    }
}
