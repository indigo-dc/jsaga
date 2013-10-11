/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package it.infn.ct.jsaga.adaptor.rocci;

import fr.in2p3.jsaga.adaptor.ClientAdaptor;
import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.security.SecurityCredential;
import fr.in2p3.jsaga.adaptor.security.impl.GSSCredentialSecurityCredential;

import fr.in2p3.jsaga.adaptor.security.impl.SSHSecurityCredential;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

import org.ogf.saga.error.*;

import java.util.Map;
       
/* *********************************************
 * *** Istituto Nazionale di Fisica Nucleare ***
 * ***      Sezione di Catania (Italy)       ***
 * ***        http://www.ct.infn.it/         ***
 * *********************************************
 * File:    rOCCIJobControlAdaptor.java
 * Authors: Giuseppe LA ROCCA, Diego SCARDACI
 * Email:   <giuseppe.larocca, diego.scardaci>@ct.infn.it
 * Ver.:    1.0.3
 * Date:    27 September 2013
 * *********************************************/

public class rOCCIAdaptorCommon extends Object implements ClientAdaptor {
            
  protected SecurityCredential credential = null;
  protected SecurityCredential sshCredential = null;  
  protected String sshHost = null;
  protected String user_cred = "";
  protected String ca_path = "";
  
  public Class[] getSupportedSecurityCredentialClasses() 
  {    
      return new Class[] {         
          GSSCredentialSecurityCredential.class,
          SSHSecurityCredential.class
      };
  }

  public void setSecurityCredential(SecurityCredential sc) 
  { 
      credential = sc;
      
      Session session = null;
      Context context = null;
      
      try {
    	  // FIXME
            session = SessionFactory.createSession(false);
            context = ContextFactory.createContext("VOMS"); 
            
            //Put here your proxy file path
            user_cred = context.getAttribute(Context.USERPROXY);            
            
            //Put here your CRL directory path
            ca_path = context.getAttribute(Context.CERTREPOSITORY);                       
            
        } catch (NotImplementedException ex) {            
            ex.printStackTrace(System.out);
        } 
        catch (AuthenticationFailedException ex) {             
            ex.printStackTrace(System.out);
        }
        catch (AuthorizationFailedException ex) {             
            ex.printStackTrace(System.out);
        } 
        catch (PermissionDeniedException ex) {             
            ex.printStackTrace(System.out);
        }           
        catch (DoesNotExistException ex) {             
            ex.printStackTrace(System.out);
        } 
        catch (IncorrectStateException ex) {             
            ex.printStackTrace(System.out);
        } 
        catch (TimeoutException ex) {             
            ex.printStackTrace(System.out);
        } 
        catch (NoSuccessException ex) {             
            ex.printStackTrace(System.out);
        }
  }
  
  public String getType() { return "rocci"; }

  public int getDefaultPort() { return 11443; }
  
  public void connect (String userInfo, String host, int port, String basePath, Map attributes) 
         throws NotImplementedException, 
                AuthenticationFailedException, 
                AuthorizationFailedException, 
                IncorrectURLException, 
                BadParameterException, 
                TimeoutException, 
                NoSuccessException 
  { }
  
  public void disconnect() throws NoSuccessException {  } 

  public Usage getUsage() { return null; }

  public Default[] getDefaults(Map map) throws IncorrectStateException 
  {
    return null;
  }
}
        