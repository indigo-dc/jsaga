package fr.in2p3.jsaga.impl.context.attrs;

import fr.in2p3.jsaga.impl.context.ContextImpl;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   DataServiceConfigAttribute
 * Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
 * ***************************************************
 * Description:                                      */

public class DataServiceConfigAttribute extends ServiceConfigAttribute {

    public String getKey() {
        return ContextImpl.DATA_SERVICE_ATTRIBUTES;
    }

    public String getDescription() {
        return "array of attributes for data service used with this context";
    }


}
