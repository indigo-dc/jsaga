package fr.in2p3.jsaga.adaptor.ssh3;

import fr.in2p3.jsaga.adaptor.ClientAdaptor;
import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.UOptional;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.ssh3.data.SFTPFileAttributes;
import fr.in2p3.jsaga.adaptor.ssh3.job.SSHJobProcess;
import fr.in2p3.jsaga.adaptor.security.SecurityCredential;
import fr.in2p3.jsaga.adaptor.security.impl.SSHSecurityCredential;
import fr.in2p3.jsaga.adaptor.security.impl.UserPassSecurityCredential;
import fr.in2p3.jsaga.adaptor.security.impl.UserPassStoreSecurityCredential;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   SSHAdaptorAbstract
* Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
* Date:   16 juillet 2013
* ***************************************************/

public abstract class SSHAdaptorAbstract implements ClientAdaptor {
    
    protected static final String KNOWN_HOSTS = "KnownHosts";
    protected final static int IO_BUFFER_LEN = 32768; // max number of bytes to write to SFTP outputstream
    protected static Map sessionMap = new HashMap();
    protected SecurityCredential credential;
    protected Connection m_conn;
    
    public Class[] getSupportedSecurityCredentialClasses() {
        return new Class[]{UserPassSecurityCredential.class, UserPassStoreSecurityCredential.class, SSHSecurityCredential.class};
    }

    public void setSecurityCredential(SecurityCredential credential) {
        this.credential = credential;
    } 
    
    public int getDefaultPort() {
        return 22;
    }
    
    public Usage getUsage() {
        return new UOptional(KNOWN_HOSTS);
    }

    public Default[] getDefaults(Map map) throws IncorrectStateException {
        return new Default[]{
                new Default(KNOWN_HOSTS, new File[]{
                        new File(System.getProperty("user.home")+"/.ssh/known_hosts")})
           };
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) 
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, 
            BadParameterException, TimeoutException, NoSuccessException {
            
        try {

            boolean knownHostsUsed = (attributes.containsKey(KNOWN_HOSTS) && attributes.get(KNOWN_HOSTS) != null && ((String)attributes.get(KNOWN_HOSTS)).length()>0);
            // set known hosts file : checking will be done
            ServerHostKeyVerifier verifier = null;
            if (knownHostsUsed) {
                File knownHostsFile = new File((String) attributes.get(KNOWN_HOSTS)); 
                if(!knownHostsFile.exists())
                    throw new BadParameterException("Unable to find the selected known host file:" + knownHostsFile.toString());
                verifier = new SSHHostKeyVerifier(knownHostsFile);
            }
            m_conn = new Connection(host, port);
            m_conn.connect(verifier);
            boolean isAuthenticated = false;
            if(credential instanceof UserPassSecurityCredential) {
                String userId = ((UserPassSecurityCredential) credential).getUserID();
                String password = ((UserPassSecurityCredential) credential).getUserPass();
                isAuthenticated = m_conn.authenticateWithPassword(userId, password);
            } else if(credential instanceof SSHSecurityCredential) {
                String userId = ((SSHSecurityCredential) credential).getUserID();
                String passPhrase = ((SSHSecurityCredential) credential).getUserPass();
                // clone private key because the object will be reset
                byte[] privateKey = ((SSHSecurityCredential) credential).getPrivateKey().clone();
                char[] pemPrivateKey = new String(privateKey).toCharArray();
                isAuthenticated = m_conn.authenticateWithPublicKey(userId, pemPrivateKey, passPhrase);
            } else if (credential instanceof UserPassStoreSecurityCredential) {
                try {
                    String userId = ((UserPassStoreSecurityCredential) credential).getUserID(host);
                    String password = ((UserPassStoreSecurityCredential) credential).getUserPass(host);
                    isAuthenticated = m_conn.authenticateWithPassword(userId, password);
                } catch (Exception e) {
                    m_conn.close();
                    throw new AuthenticationFailedException(e);
                }
            } else {
                m_conn.close();
                throw new AuthenticationFailedException("Invalid security instance.");
            }
            if (!isAuthenticated) {
                m_conn.close();
                throw new AuthenticationFailedException("Auth fail");
            }
        } catch (Exception e) {
            if (m_conn != null)
                m_conn.close();
            if("Auth fail".equals(e.getMessage()) )
                throw new AuthenticationFailedException(e);
            throw new NoSuccessException("Unable to connect to server", e);
        }
    }

    public void disconnect() throws NoSuccessException {
        if (m_conn != null) {
            m_conn.close();
            m_conn = null;
        }
    }
    
    public  void store(SSHJobProcess p, String nativeJobId) throws SFTPException, IOException, InterruptedException {
        byte[] buf = serialize(p);
        SFTPv3Client sftp = new SFTPv3Client(m_conn);
        SFTPv3FileHandle f = sftp.createFileTruncate(p.getSerializeFile());
        sftp.write(f, 0, buf, 0, buf.length);
        sftp.closeFile(f);
        sftp.close();
    }
    
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(obj);
        oos.close();
        return buffer.toByteArray();
    }

    public SSHJobProcess restore(String nativeJobId) throws SFTPException, IOException, ClassNotFoundException,  InterruptedException {
        String processFile = SSHJobProcess.getRootDir() + "/" + nativeJobId + ".process";
        SFTPv3Client sftp = new SFTPv3Client(m_conn);
        SFTPv3FileAttributes attrs = sftp.lstat(processFile);
        byte[] buf = new byte[attrs.size.intValue()];
        SFTPv3FileHandle f = sftp.openFileRO(processFile);
        int len = sftp.read(f, 0, buf,0, buf.length);
        if (len != buf.length) {
            throw new IOException("Read " + len + " + characters out of " + buf.length);
        }
        sftp.closeFile(f);
        sftp.close();
        return (SSHJobProcess)deserialize(buf);
    }
    
    private static Object deserialize(byte[] bytes)
            throws ClassNotFoundException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(input);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error reading from byte-array!");
        }
    }
    
    protected SFTPFileAttributes getFileAttributes(String filename) throws IOException {
        SFTPv3Client sftp = new SFTPv3Client(m_conn);
        SFTPv3FileAttributes attrs = sftp.stat(filename);
        sftp.close();
        return new SFTPFileAttributes(filename, attrs);
    }
    
}
