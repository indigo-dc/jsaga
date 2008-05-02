package fr.in2p3.jsaga.engine.workflow;

import fr.in2p3.jsaga.engine.schema.status.Task;
import fr.in2p3.jsaga.engine.schema.status.types.TaskStateType;
import fr.in2p3.jsaga.engine.schema.status.types.TaskTypeType;
import fr.in2p3.jsaga.impl.task.AbstractTaskImpl;
import fr.in2p3.jsaga.workflow.WorkflowTask;
import org.apache.log4j.Logger;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.*;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;

import java.lang.Exception;
import java.util.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   AbstractWorkflowTaskImpl
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   24 avr. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public abstract class AbstractWorkflowTaskImpl extends AbstractTaskImpl implements WorkflowTask {
    private static Logger s_logger = Logger.getLogger(AbstractWorkflowTaskImpl.class);
    private String m_name;
    private final Map<String,WorkflowTask> m_predecessors;
    private final Map<String,WorkflowTask> m_successors;

    /** constructor */
    public AbstractWorkflowTaskImpl(Session session, String name) throws NotImplemented, BadParameter, Timeout, NoSuccess {
        super(session, null, true);
        m_name = name;
        m_predecessors = new HashMap<String,WorkflowTask>();
        m_successors = new HashMap<String,WorkflowTask>();
    }

    /** clone */
    public SagaObject clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning workflow tasks is not supported yet");
    }

    //////////////////////////////////////////// abstract methods ////////////////////////////////////////////

    protected abstract void doSubmit() throws NotImplemented, IncorrectState, Timeout, NoSuccess;
    protected abstract void doCancel();
    protected abstract State queryState() throws NotImplemented, Timeout, NoSuccess;
    public abstract boolean startListening(Metric metric) throws NotImplemented, IncorrectState, Timeout, NoSuccess;
    public abstract void stopListening(Metric metric) throws NotImplemented, Timeout, NoSuccess;

    //////////////////////////////////////////////// interface Task ////////////////////////////////////////////////

    public void run() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        // run current task if predecessors are all DONE
        boolean areAllDone = (m_predecessors.size()>0);
        for (Iterator<WorkflowTask> it=m_predecessors.values().iterator(); areAllDone && it.hasNext();) {
            areAllDone &= State.DONE.equals(it.next().getState());
        }
        if (areAllDone) {
            super.run();
        }
    }

    //////////////////////////////////////////// interface TaskCallback ////////////////////////////////////////////

    /** override super.setState() */
    public synchronized void setState(State state) {
        // update state
        super.setState(state);

        // run successors if state of current task is DONE
        if (State.DONE.equals(state)) {
            for (Iterator<WorkflowTask> it=m_successors.values().iterator(); it.hasNext(); ) {
                WorkflowTask successor = it.next();
                try {
                    successor.run();
                } catch (Exception e) {
                    s_logger.warn("Failed to run task: "+successor.getName(), e);
                }
            }
        }
    }
    
    ///////////////////////////////////////// interface WorkflowTask /////////////////////////////////////////

    public String getName() {
        return m_name;
    }

    public synchronized void addPredecessor(WorkflowTask predecessor) {
        if (! m_predecessors.containsKey(predecessor.getName())) {
            m_predecessors.put(predecessor.getName(), predecessor);
        }
    }

    public synchronized void addSuccessor(WorkflowTask successor) {
        if (! m_successors.containsKey(successor.getName())) {
            m_successors.put(successor.getName(), successor);
        }
    }

    public Task getStateAsXML() throws NotImplemented, Timeout, NoSuccess {
        Task task = new Task();
        task.setName(task.getName());
        task.setState(this._getStateAsXML());
        if (this instanceof StartTask) {
            task.setType(TaskTypeType.START);
        }
        return task;
    }
    private TaskStateType _getStateAsXML() throws NotImplemented, Timeout, NoSuccess {
        switch(this.getState()) {
            case NEW:
                return TaskStateType.NEW;
            case RUNNING:
                return TaskStateType.RUNNING;
            case DONE:
                return TaskStateType.DONE;
            case CANCELED:
                return TaskStateType.CANCELED;
            case FAILED:
                return TaskStateType.FAILED;
            case SUSPENDED:
                return TaskStateType.SUSPENDED;
            default:
                return null;
        }
    }
}
