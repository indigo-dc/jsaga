package fr.in2p3.jsaga.impl.file.copy;

import fr.in2p3.jsaga.impl.monitoring.*;
import fr.in2p3.jsaga.impl.task.AbstractTaskImplWithAsyncAttributes;
import org.ogf.saga.attributes.AsyncAttributes;
import org.ogf.saga.error.*;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.url.URL;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   AbstractCopyTask
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   21 f�vr. 2009
* ***************************************************
* Description:                                      */
/**
 *
 */
public abstract class AbstractCopyTask<T,E> extends AbstractTaskImplWithAsyncAttributes<T,E,T> implements Task<T,E>, AsyncAttributes<T> {
    public static final String FILE_COPY_PROGRESS = "file.copy.progress";
    // internal
    private URL m_target;
    private int m_flags;
    // metrics
    private long m_totalWrittenBytes;
    private MetricImpl<Long> m_metric_Progress;

    /** constructor */
    public AbstractCopyTask(Session session, URL target, int flags) throws NotImplementedException {
        super(session, true);
        // internal
        m_target = target;
        m_flags = flags;
        // metrics
        m_totalWrittenBytes = 0L;
        m_metric_Progress = new MetricFactoryImpl<Long>(this).createAndRegister(
                FILE_COPY_PROGRESS,
                "this metric gives the state of ongoing file transfer as number of bytes transfered.",
                MetricMode.ReadOnly,
                "bytes",
                MetricType.Int,
                0L);
    }

    void increment(long writtenBytes) {
        m_totalWrittenBytes += writtenBytes;
        m_metric_Progress.setValue(m_totalWrittenBytes);
    }

    public abstract void doCopy(URL target, int flags) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, IncorrectStateException, DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException, IncorrectURLException;

    ////////////////////////////////////// implementation of AbstractTaskImpl //////////////////////////////////////

    private Thread m_thread;
    public void doSubmit() throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException {
        m_thread = new Thread(new Runnable(){
            public void run() {
                AbstractCopyTask.super.setState(State.RUNNING);
                try {
                    AbstractCopyTask.this.doCopy(m_target, m_flags);
                    AbstractCopyTask.super.setState(State.DONE);
                } catch (Exception e) {
                    AbstractCopyTask.super.setException(new NoSuccessException(e));
                    AbstractCopyTask.super.setState(State.FAILED);
                }
            }
        });
        m_thread.start();
    }

    protected void doCancel() {
        try {
            if (m_thread != null) {
                m_thread.interrupt();
            }
            this.setState(State.CANCELED);
        } catch(SecurityException e) {
            // do nothing (failed to cancel task)
        }
    }

    protected State queryState() {
        return null;    // FileCopyTask does not support queryState
    }

    public boolean startListening() {
        return true;    // FileCopyTask is always listening anyway...
    }

    public void stopListening() {
        // do nothing
    }
}
