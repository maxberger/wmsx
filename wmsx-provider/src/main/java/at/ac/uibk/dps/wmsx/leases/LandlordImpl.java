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

package at.ac.uibk.dps.wmsx.leases;

import hu.kfki.grid.wmsx.util.Exporter;

import java.util.HashMap;
import java.util.Map;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import com.sun.jini.landlord.Landlord;
import com.sun.jini.landlord.LandlordUtil;
import com.sun.jini.landlord.LeaseFactory;
import com.sun.jini.landlord.LocalLandlord;

/**
 * Implements the Landlord protocol for remote event listeners.
 * 
 * @version $Date: 1/1/2000$
 */
public class LandlordImpl implements Landlord, LocalLandlord, ReferentUuid {

    private static final long MAX_LEASE_TIME = 120 * 60 * 1000;

    private static final long MIN_LEASE_TIME = 60 * 1000;

    private final Uuid uuid = UuidFactory.generate();

    private final Map<Uuid, Long> expires = new HashMap<Uuid, Long>();

    private final LeaseFactory leaseFactory;

    private static final class SingletonHolder {
        private static final LandlordImpl INSTANCE = new LandlordImpl();

        private SingletonHolder() {
        }
    }

    private LandlordImpl() {
        final Landlord stub = (Landlord) Exporter.getInstance().export(this);
        this.leaseFactory = new LeaseFactory(stub, this.uuid);
    }

    /** {@inheritDoc} */
    public void cancel(final Uuid cookie) throws UnknownLeaseException {
        synchronized (this.expires) {
            this.expires.remove(cookie);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Map cancelAll(final Uuid[] cookies) {
        return LandlordUtil.cancelAll(this, cookies);
    }

    /** {@inheritDoc} */
    public long renew(final Uuid cookie, long duration)
            throws LeaseDeniedException, UnknownLeaseException {
        if (duration > LandlordImpl.MAX_LEASE_TIME) {
            duration = LandlordImpl.MAX_LEASE_TIME;
        }
        if (duration < LandlordImpl.MIN_LEASE_TIME) {
            duration = LandlordImpl.MIN_LEASE_TIME;
        }
        synchronized (this.expires) {
            this.expires.put(cookie, System.currentTimeMillis() + duration);
        }
        return duration;
    }

    /** {@inheritDoc} */
    public RenewResults renewAll(final Uuid[] cookies, final long[] durations) {
        return LandlordUtil.renewAll(this, cookies, durations);
    }

    /** {@inheritDoc} */
    public Uuid getReferentUuid() {
        return this.uuid;
    }

    /**
     * @return Singleton instance.
     */
    public static LandlordImpl getInstance() {
        return LandlordImpl.SingletonHolder.INSTANCE;
    }

    /**
     * @return the leaseFactory
     */
    public LeaseFactory getLeaseFactory() {
        return this.leaseFactory;
    }

}
