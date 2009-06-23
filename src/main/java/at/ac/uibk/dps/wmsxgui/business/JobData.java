/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.TransportJobUID;

/**
 * 
 * @author bafu
 */
public class JobData {
    private static final long     serialVersionUID = -1595944205656281484L;

    private final TransportJobUID transportJobUID;
    private JobInfo               jobinfo;

    public JobData(final TransportJobUID transportJobUID, final JobInfo jobinfo) {
        this.transportJobUID = transportJobUID;
        this.jobinfo = jobinfo;
    }

    public void setJobinfo(final JobInfo jobinfo) {
        this.jobinfo = jobinfo;
    }

    public JobInfo getJobinfo() {
        return this.jobinfo;
    }

    public TransportJobUID getTransportJobUID() {
        return this.transportJobUID;
    }

    @Override
    public String toString() {
        return this.transportJobUID.toString();
    }

}
