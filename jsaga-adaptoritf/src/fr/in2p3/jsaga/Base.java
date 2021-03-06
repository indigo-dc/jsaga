package fr.in2p3.jsaga;

import java.io.File;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   Base
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   22 aout 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class Base {
    private static String s_sagaFactory = "fr.in2p3.jsaga.impl.SagaFactoryImpl";
    private static boolean s_used = false;
    public static String getSagaFactory() {
        if(!s_used) s_used=true;
        return s_sagaFactory;
    }
    public static void setSagaFactory(String sagaFactoryClassName) {
        if(s_used) throw new RuntimeException("Can not change JSAGA factory name because a SAGA object was already created");
        s_sagaFactory = sagaFactoryClassName;
    }

    public static final File JSAGA_HOME =
            System.getProperty("JSAGA_HOME")!=null
                    ? new File(System.getProperty("JSAGA_HOME"))
                    : new File("jsaga-engine/config").exists()
                        ? new File("jsaga-engine/config")
                        : (new File("config").exists()
                            ? new File("config")
                            : new File("."));

    public static final File JSAGA_USER = new File(System.getProperty("user.home"), ".jsaga/");
    public static final File JSAGA_VAR =
            System.getProperty("JSAGA_VAR")!=null
                    ? new File(System.getProperty("JSAGA_VAR"))
                    : new File(JSAGA_USER, "var/");
    static {
        if(!JSAGA_USER.exists() && !JSAGA_USER.mkdir()) {
            throw new RuntimeException("Failed to create directory: "+JSAGA_USER);
        }
        if(!JSAGA_VAR.exists() && !JSAGA_VAR.mkdir()) {
            throw new RuntimeException("Failed to create directory: "+JSAGA_VAR);
        }
    }

    public static final boolean DEBUG =
            System.getProperty("debug")!=null && !System.getProperty("debug").equalsIgnoreCase("false");
}
