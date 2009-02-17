/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.gridlab.gat.GATContext;

/**
 * Provides Data for known VOs.
 * 
 * @version $Revision$
 */
public final class VoData {

    private static final String VOMS_HOST_DN = "vomsHostDN";

    private static final String VOMS_SERVER_PORT = "vomsServerPort";

    private static final String VOMS_SERVER_URL = "vomsServerURL";

    private static final Logger LOGGER = Logger.getLogger(VoData.class
            .toString());

    private final Map<String, Map<String, String>> voMap = new HashMap<String, Map<String, String>>();

    private static final class SingletonHolder {
        private static final VoData INSTANCE = new VoData();

        private SingletonHolder() {
        }
    }

    private VoData() {
        final Map<String, String> compchem = new HashMap<String, String>();
        compchem.put(VoData.VOMS_SERVER_URL, "voms.cnaf.infn.it");
        compchem.put(VoData.VOMS_SERVER_PORT, "15003");
        compchem.put(VoData.VOMS_HOST_DN,
                "/C=IT/O=INFN/OU=Host/L=CNAF/CN=voms.cnaf.infn.it");
        this.voMap.put("compchem", compchem);

        final Map<String, String> voce = new HashMap<String, String>();
        voce.put(VoData.VOMS_SERVER_URL, "skurut19.cesnet.cz");
        voce.put(VoData.VOMS_SERVER_PORT, "7001");
        voce.put(VoData.VOMS_HOST_DN,
                "/DC=cz/DC=cesnet-ca/O=CESNET/CN=skurut19.cesnet.cz");
        this.voMap.put("voce", voce);
    }

    /**
     * @return the Singleton Instance.
     */
    public static VoData getInstance() {
        return VoData.SingletonHolder.INSTANCE;
    }

    /**
     * Add the necessary preferences for the given VO.
     * 
     * @param vo
     *            VO to look up.
     * @param context
     *            {@link GATContext} to modify.
     */
    public void addToContext(final String vo, final GATContext context) {

        final String vol = vo.toLowerCase(Locale.ENGLISH);
        context.addPreference("VirtualOrganisation", vol);
        final Map<String, String> voData = this.voMap.get(vol);
        if (voData == null) {
            VoData.LOGGER.warning("No information for VO: " + vo);
        } else {
            for (final Map.Entry<String, String> e : voData.entrySet()) {
                context.addPreference(e.getKey(), e.getValue());
            }
        }
    }
}
