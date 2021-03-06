package org.ogf.saga.job.run;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogf.saga.error.*;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.job.StartJob;
import org.ogf.saga.job.base.JobBaseTest;
import org.ogf.saga.task.*;

import java.util.List;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   OptionalTest
* Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
* Date:   5 NOV 2013
* ***************************************************
* Description: 
* This test suite is made to be test advanced functions
*/
public abstract class OptionalTest extends JobBaseTest {
    
    protected OptionalTest(String jobprotocol) throws Exception {
        super(jobprotocol);
    }

    /**
     * Runs a long job, waits for RUNNING_QUEUED state and suspends it
     */
    @Test
    public void test_suspend_queued() throws Exception {
        
        // prepare
        JobDescription desc = createLongJob();
        
        // submit
        Job job = runJob(desc);
        
        if (! super.waitForSubState(job, MODEL+":RUNNING_QUEUED")) {
            fail("Job did not enter RUNNING_QUEUED state within "+MAX_QUEUING_TIME+" seconds");
        }
        
        job.suspend();
        
        if (! super.waitForSubState(job, MODEL+":SUSPENDED_ACTIVE")) {
          fail("Job did not enter SUSPENDED_ACTIVE state within "+MAX_QUEUING_TIME+" seconds");
        }
        checkStatus(job.getState(), State.SUSPENDED);

        // resume job
        job.resume();
        
        if (! super.waitForSubState(job, MODEL+":RUNNING_ACTIVE")) {
          fail("Job did not enter RUNNING_ACTIVE state within "+MAX_QUEUING_TIME+" seconds");
        }
        checkStatus(job.getState(), State.RUNNING);
    }
    
    /**
     * Runs a long job, waits for running state and suspends it
     */
    @Test
    public void test_suspend_running() throws Exception {
        
        // prepare
        JobDescription desc = createLongJob();
        
        // submit
        Job job = runJob(desc);
        
        // wait for RUNNING state (deviation from SAGA specification)
        if (! super.waitForSubState(job, MODEL+":RUNNING_ACTIVE")) {
            Assert.fail("Job did not enter RUNNING_ACTIVE state within "+MAX_QUEUING_TIME+" seconds");
        }
        
        // suspend job
        job.suspend();
        
        if (! super.waitForSubState(job, MODEL+":SUSPENDED_ACTIVE")) {
            fail("Job did not enter SUSPENDED_ACTIVE state within "+MAX_QUEUING_TIME+" seconds");
        }
        checkStatus(job.getState(), State.SUSPENDED);

        // resume job
        job.resume();
        
        if (! super.waitForSubState(job, MODEL+":RUNNING_ACTIVE")) {
            fail("Job did not enter RUNNING_ACTIVE state within "+MAX_QUEUING_TIME+" seconds");
        }
        checkStatus(job.getState(), State.RUNNING);
    }
    
    /**
     * Runs a long job, waits for running state and suspends it
     */
    @Test(expected=IncorrectStateException.class)
    public void test_suspend_done() throws Exception {
        
        // prepare
        JobDescription desc = createSimpleJob();
        
        // submit
        Job job = runJob(desc);
        
        // wait for the END
        job.waitFor();
        
        // check job for DONE status
        checkStatus(job.getState(), State.DONE);
        
        // suspend job
        job.suspend();
    }
    
    /**
     * Runs a long job, waits for done state and resumes it
     */
    @Test(expected=IncorrectStateException.class)
    public void test_resume_done() throws Exception {
        
        // prepare
        JobDescription desc = createSimpleJob();
        
        // submit
        Job job = runJob(desc);
        
        // wait the end
        job.waitFor();
        
        // check job for DONE status
        checkStatus(job.getState(), State.DONE);
        
        // resume job
        job.resume();
    }
    
    /**
     * Runs a long job, list available jobs and check if the running job is in the resource manager list
     */
    @Test
    public void test_listJob() throws Exception {
        
        // prepare
        JobDescription desc = createLongJob();
        
        // submit
        Job job = runJob(desc);

        // get jobs
        try {
            JobService service = JobFactory.createJobService(m_session, m_jobservice);
            List<String> jobList = service.list();
            // test if the running job is in the job list
            boolean jobIsInList = false;
            if(jobList.contains(job.getAttribute(Job.JOBID)))
                jobIsInList = true;
            
            Assert.assertEquals(
                    true,
                    jobIsInList);
        }
        finally {
            job.waitFor(Float.valueOf(FINALY_TIMEOUT));
        }
    }
    
    /**
     * Runs long jobs on the same time with one job service
     */
    @Test
    public void test_simultaneousLongJob() throws Exception {
        
        int numberOfJobs = Integer.parseInt(SIMULTANEOUS_JOB_NUMBER);

        // jobs
        StartJob[] newJob = new StartJob[numberOfJobs];
        
        // create and start jobs
        JobService m_service = JobFactory.createJobService(m_session, m_jobservice); 
        for (int i = 0; i < numberOfJobs; i++) {
            newJob[i] = new StartJob(m_service, i, true);
            newJob[i].start();
        }
    
        for (int i = 0; i < numberOfJobs; i++) {
            newJob[i].join();
        }
        
        // get job exception
        int numberOfFailed = 0;
        for (int i = 0; i < numberOfJobs; i++) {
            if(newJob[i].getException() != null) {
                numberOfFailed ++;
            }
        }
        if(numberOfFailed > 1) 
            throw new NoSuccessException(numberOfFailed + " jobs of "+numberOfJobs+" are failed.");
        if(numberOfFailed > 0) 
            throw new NoSuccessException(numberOfFailed + " job of "+numberOfJobs+" is failed.");

    }

    /**
     * Runs short jobs on the same time with one job service
     */
    @Test
    public void test_simultaneousShortJob() throws Exception {
        
        int numberOfJobs = Integer.parseInt(SIMULTANEOUS_JOB_NUMBER);

        // jobs
        StartJob[] newJob = new StartJob[numberOfJobs];
        
        // create and start jobs
        JobService m_service = JobFactory.createJobService(m_session, m_jobservice); 
        for (int i = 0; i < numberOfJobs; i++) {
            newJob[i] = new StartJob(m_service, i, false);
            newJob[i].start();
        }
    
        for (int i = 0; i < numberOfJobs; i++) {
            newJob[i].join();
        }
        
        // get job exception
        int numberOfFailed = 0;
        SagaException lastException = null;
        for (int i = 0; i < numberOfJobs; i++) {
            if(newJob[i].getException() != null) {
                numberOfFailed ++;
                lastException = newJob[i].getException();
            }
        }
        if(numberOfFailed > 0) {
            throw new NoSuccessException("Failed job(s): "+numberOfFailed+"/"+numberOfJobs, lastException);
        }

        Assert.assertEquals(
                    0,
                    numberOfFailed);
    }
    
    /**
     * Runs short jobs on the same time with one task container
     */
    @Test
    public void test_TaskContainer_ShortJob() throws Exception {
        
        int numberOfJobs = Integer.parseInt(SIMULTANEOUS_JOB_NUMBER);

        TaskContainer taskContainer = TaskFactory.createTaskContainer();
                
        // create and start jobs
        JobService m_service = JobFactory.createJobService(m_session, m_jobservice);
        Job[] jobs = new Job[numberOfJobs];        
        for (int index = 0; index < numberOfJobs; index++) {
            // create description
            JobDescription desc = JobFactory.createJobDescription();
            desc.setAttribute(JobDescription.EXECUTABLE, "/bin/date");
            desc.setAttribute(JobDescription.OUTPUT, index+"-stdout.txt");
            desc.setAttribute(JobDescription.ERROR, index+"-stderr.txt");
            // add job to task
            jobs[index] = m_service.createJob(desc);
            taskContainer.add(jobs[index]);
        }
    
        // run
        taskContainer.run();
        
        // wait the end 
        taskContainer.waitFor(WaitMode.ALL);
        
        // get failed jobs
        int numberOfFailed = 0;
        for (int index = 0; index < numberOfJobs; index++) {
            if(jobs[index].getState().getValue() == State.FAILED.getValue()) {
                numberOfFailed ++;
            }
        }
        
        if(numberOfFailed > 1) 
            throw new NoSuccessException(numberOfFailed + " jobs of "+numberOfJobs+" are failed.");
        if(numberOfFailed > 0) 
            throw new NoSuccessException(numberOfFailed + " job of "+numberOfJobs+" is failed.");
        
        Assert.assertEquals(
                    0,
                    numberOfFailed);
    }
}