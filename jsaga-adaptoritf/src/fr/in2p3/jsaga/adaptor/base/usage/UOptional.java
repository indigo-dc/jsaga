package fr.in2p3.jsaga.adaptor.base.usage;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;

import java.io.FileNotFoundException;
import java.util.Map;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   UOptional
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   8 aout 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class UOptional extends U {
    public UOptional(String name) {
        super(name);
    }

    public String toString() {
        return "["+m_name+"]";
    }

    @Override
    public int getFirstMatchingUsage(Map attributes) throws DoesNotExistException, BadParameterException {
        try {
            super.getFirstMatchingUsage(attributes);
        } catch (DoesNotExistException e) {
        } catch (FileNotFoundException e) {
        }
        return m_id;
    }
    
    @Override
    protected Object throwExceptionIfInvalid(Object value) throws Exception {
        return value;   // value is always valid (even if null)
    }
}
