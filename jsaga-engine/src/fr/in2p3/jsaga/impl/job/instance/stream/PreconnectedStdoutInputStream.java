package fr.in2p3.jsaga.impl.job.instance.stream;

import fr.in2p3.jsaga.impl.job.instance.AbstractSyncJobImpl;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;

import java.io.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   PreconnectedStdoutInputStream
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   14 mai 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class PreconnectedStdoutInputStream extends JobStdoutInputStream {
    private OutputStreamContainer m_out;

    /** constructor for StreamableJobInteractiveSet */
    public PreconnectedStdoutInputStream(AbstractSyncJobImpl job) {
        super(job);
        m_out = new OutputStreamContainer();
    }

    public OutputStream getOutputStreamContainer() {
        return m_out;
    }

    /////////////////////////////////// interface InputStream ///////////////////////////////////

    public int read() throws IOException {return this.stream().read();}
    public int read(byte[] b) throws IOException {return this.stream().read(b);}
    public int read(byte[] b, int off, int len) throws IOException {return this.stream().read(b, off, len);}
    public long skip(long n) throws IOException {return this.stream().skip(n);}
    public void close() throws IOException {this.stream().close();}

    /////////////////////////////////////// private method ///////////////////////////////////////

    private InputStream stream() throws IOException {
        try {
            switch(m_job.getState()) {
                case DONE:
                case CANCELED:
                case FAILED:
                    return m_out.getInputStream();
                case RUNNING:
                    throw new NoSuccessException("Not supported yet...");
                default:
                    throw new DoesNotExistException("Stdout is not available because job is neither finished nor running");
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
