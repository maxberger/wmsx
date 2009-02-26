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

package at.ac.uibk.dps.wmsx.gat;

import hu.kfki.grid.wmsx.util.LogUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.gridlab.gat.AdaptorInfo;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.security.CertificateSecurityContext;

/**
 * Common functionality for GAT Adaptors.
 * 
 * @version $Date$
 */
public final class GatCommon {
    private static final Logger LOGGER = Logger.getLogger(GatCommon.class
            .toString());

    private static final String GAT_BACKEND_NAME = "Glite";

    private final GATContext context;

    private CertificateSecurityContext secContext;

    private static final class SingletonHolder {
        private static final GatCommon INSTANCE = new GatCommon();

        private SingletonHolder() {
            // do not instantiate.
        }
    }

    private GatCommon() {
        this.context = GAT.getDefaultGATContext();
        final Preferences globalPrefs = new Preferences();

        globalPrefs.put("ResourceBroker.adaptor.name",
                GatCommon.GAT_BACKEND_NAME);
        globalPrefs.put("AdvertService.adaptor.name",
                GatCommon.GAT_BACKEND_NAME);

        // System.setProperty("gat.debug", "true");
        // System.setProperty("gat.verbose", "true");
        globalPrefs.put("gat.verbose", "false");

        globalPrefs.put("glite.deleteJDL", "true");
        globalPrefs.put("glite.pollIntervalSecs", "30");

        globalPrefs.put("File.adaptor.name",
                "Local,GliteGuid,GliteSrm,GridFTP,!sftp");

        try {
            final String globusDir = System.getProperty("user.home")
                    + File.separatorChar + ".globus" + File.separatorChar;
            this.secContext = new CertificateSecurityContext(new URI(globusDir
                    + "userkey.pem"), new URI(globusDir + "usercert.pem"), "");
        } catch (final URISyntaxException e) {
            GatCommon.LOGGER.warning(LogUtil.logException(e));
            throw new RuntimeException(e);
        }
        this.context.addSecurityContext(this.secContext);
        this.context.addPreferences(globalPrefs);
        final String envVo = System.getenv("LCG_GFAL_VO");
        if (envVo != null) {
            this.setVo(envVo);
        }
    }

    /**
     * @return the Singleton Instance.
     */
    public static GatCommon getInstance() {
        return GatCommon.SingletonHolder.INSTANCE;
    }

    /**
     * Checks if the given Gat adapter is available.
     * 
     * @param type
     *            adator type
     * @param name
     *            Adaptor Name.
     * @return true if the adaptor is available.
     */
    public boolean isAvailable(final String type, final String name) {
        try {
            for (final AdaptorInfo ai : GAT.getAdaptorInfos(type)) {
                if (name.equalsIgnoreCase(ai.getShortName())) {
                    return true;
                }
            }
        } catch (final GATInvocationException e) {
            GatCommon.LOGGER.warning(e.getMessage());
        }
        return false;
    }

    /**
     * Retrieve the current {@link GATContext}.
     * 
     * @return the {@link GATContext}
     */
    public GATContext getGatContext() {
        return this.context;
    }

    /**
     * Set the new password.
     * 
     * @param pass
     *            password
     */
    public void setPassword(final String pass) {
        this.secContext.setPassword(pass);
    }

    /**
     * Set the VO for the common context.
     * 
     * @param vo
     *            new VO
     */
    public void setVo(final String vo) {
        VoData.getInstance().addToContext(vo, this.context);
    }

}
