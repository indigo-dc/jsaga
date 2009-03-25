package fr.in2p3.jsaga.impl.file.copy;

import fr.in2p3.jsaga.impl.file.FileImpl;
import org.ogf.saga.error.*;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   FileCopyTask
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   9 juil. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class FileCopyTask<T,E> extends AbstractCopyTask<T,E> {
    private FileImpl m_sourceFile;

    /** constructor */
    public FileCopyTask(Session session, FileImpl sourceFile, URL target, int flags) throws NotImplementedException {
        super(session, target, flags);
        m_sourceFile = sourceFile;
    }

    public void doCopy(URL target, int flags) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, IncorrectStateException, DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException, IncorrectURLException {
        m_sourceFile._copyAndMonitor(target, flags, this);
    }
}
