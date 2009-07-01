/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.TransportJobUID;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class JobData {
    private static final long serialVersionUID = -1595944205656281484L;

    private final TransportJobUID transportJobUID;
    private JobInfo jobinfo;

    private static final int HASHINT = 97;
    private static final int HASHINTINIT = 5;

    /**
     * JobData is the constructor, which creates an JobData object.
     * 
     * @param ptransportJobUID
     *            TransportJobUID object, which will be set to the JobData
     *            object
     * @param pjobinfo
     *            JobInfo object, which will be set to the JobData object
     */
    public JobData(final TransportJobUID ptransportJobUID, final JobInfo pjobinfo) {
        this.transportJobUID = ptransportJobUID;
        this.jobinfo = pjobinfo;
    }

    /**
     * setJobinfo is a Setter for the Jobinfo Object wrapped by JobData.
     * 
     * @param pjobinfo
     *            JobInfo object, which will be set to the JobData object
     */
    public void setJobinfo(final JobInfo pjobinfo) {
        this.jobinfo = pjobinfo;
    }

    /**
     * getJobinfo is a Getter for the Jobinfo Object wrapped by JobData.
     * 
     * @return Jobinfo of the given JobData
     */
    public JobInfo getJobinfo() {
        return this.jobinfo;
    }

    /**
     * getTransportJobUID is a Getter for the TransportJobUID Object wrapped by
     * JobData.
     * 
     * @return TransportJobUID of the given JobData
     */
    public TransportJobUID getTransportJobUID() {
        return this.transportJobUID;
    }

    /**
     * toString overrides the default toString and returns the JobUID.
     * 
     * @return JobUID of the given JobData
     */
    @Override
    public String toString() {
        return this.transportJobUID.toString();
    }

    /**
     * equals compares to objects, which have the type JobData.
     * 
     * @param obj
     *            Object which should be compared
     * @return True, if the objects a equal, otherwise false
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final JobData other = (JobData) obj;
        if (!this.transportJobUID.getLocalId().equals(
                                                      other.transportJobUID
                                                              .getLocalId())) {
            return false;
        }
        return true;
    }

    /**
     * hashCode calculates the hashCode for the given object.
     * 
     * @return hashCode for the given object
     */
    @Override
    public int hashCode() {
        int hash = HASHINTINIT;
        hash = HASHINT
                * hash
                + (this.transportJobUID != null ? this.transportJobUID
                        .hashCode() : 0);
        hash = HASHINT * hash + (this.jobinfo != null ? this.jobinfo.hashCode() : 0);
        return hash;
    }

}
