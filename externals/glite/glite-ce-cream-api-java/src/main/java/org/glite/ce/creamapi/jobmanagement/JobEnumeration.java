/*
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */

package org.glite.ce.creamapi.jobmanagement;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.ce.common.db.DatabaseException;
import org.glite.ce.creamapi.jobmanagement.db.JobDBInterface;
import org.glite.ce.creamapi.jobmanagement.JobManagementException;

public class JobEnumeration implements Enumeration {
    private static Logger logger = Logger.getLogger(JobEnumeration.class.getName());

    private List<String> jobIdList = null;

    private JobDBInterface db = null;

    public JobEnumeration(JobDBInterface db) {
        this.jobIdList = new ArrayList<String>(0);
        this.db = db;
    }

    public JobEnumeration(final List<String> jobIdList, JobDBInterface db) {
        this.jobIdList = (jobIdList == null ? new ArrayList<String>(0) : jobIdList);
        this.db = db;
    }

    public List<String> getJobIdList() {
        return jobIdList;
    }

    protected void addAll(JobEnumeration jobEnum) {
        if (jobEnum != null) {
            jobIdList.addAll(jobEnum.getJobIdList());
        }
    }

    public int size() {
        return jobIdList.size();
    }

    public boolean hasMoreElements() {
        return !jobIdList.isEmpty();
    }

    public boolean hasMoreJobs() {
        return hasMoreElements();
    }

    public Object nextElement() {
        if (jobIdList.size() == 0) {
            return null;
        }

        return jobIdList.remove(0);
    }

    public String nextJobId() {
        return (String) nextElement();
    }

//    public Job nextJob() {
//        return nextJob(false);
//    }
    
    public Job nextJob() throws JobManagementException{
    	Job job = null;
        String jobId = (String) nextElement();
        if (jobId == null || db == null) {
            return null;
        }
        
        try {
          job = (Job) db.retrieveJob(jobId, null);
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: " + e.getMessage());
            throw new JobManagementException(e.getMessage());
        } catch (DatabaseException e) {
        	logger.error("DatabaseException: " + e.getMessage());
            throw new JobManagementException(e.getMessage());
        }
        
        return job;
    }
    
//    public Job nextJob(boolean full) {
//        String jobId = (String) nextElement();
//        if (jobId == null || db == null) {
//            return null;
//        }
//        try {
//            Job job = (Job) db.retrieveJob(jobId, null);
//            if(full) {
//                job.setCommandHistory(db.retrieveCommands(jobId));
//            }
//            return job;
//        } catch (DatabaseException e) {
//            return null;
//        }
//    }
    
    
    public JobArrayList getJobArrayList() {
        return new JobArrayList(jobIdList, db);
    }

}
