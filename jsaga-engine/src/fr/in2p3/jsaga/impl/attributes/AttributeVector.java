package fr.in2p3.jsaga.impl.attributes;

import org.ogf.saga.error.IncorrectStateException;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   AttributeVector
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   12 juin 2007
* ***************************************************
* Description:                                      */
/**
 * This class is a friend of AbstractAttributesImpl
 */
public class AttributeVector extends AttributeAbstract {
    private String[] m_values;

    /** constructor for friend classes */
    AttributeVector(String key, boolean isSupported, boolean isReadOnly, String[] values) {
        super(key, isSupported, isReadOnly);
        m_values = values;
    }
    /** constructor */
    public AttributeVector(String key, String[] values) {
        super(key);
        m_values = values;
    }
    /** constructor */
    public AttributeVector(String key, String value) {
        super(key);
        m_values = toVector(value);
    }

    /** clone */
    public AttributeVector clone() throws CloneNotSupportedException {
        AttributeVector clone = (AttributeVector) super.clone();
        clone.m_values = new String[m_values.length];
        System.arraycopy(m_values, 0, clone.m_values, 0, m_values.length);
        return clone;
    }

    /** override Attribute.setValue() */
    public void setValue(String value) throws IncorrectStateException {
        throw new IncorrectStateException("Attribute "+super.getKey()+" not scalar");
    }

    /** override Attribute.getValue() */
    public String getValue() throws IncorrectStateException {
        throw new IncorrectStateException("Attribute "+super.getKey()+" not scalar");
    }

    public void _setValue(String value) {
        m_values = toVector(value);
    }

    public String _getValue() {
        return toScalar(m_values);
    }

    public void _setValues(String[] values) {
        m_values = values;
    }

    public String[] _getValues() {
        return m_values;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AttributeVector)) {
            return false;
        }
        AttributeVector attribute = (AttributeVector) o;
        if (!super.getKey().equals(attribute.getKey())) {
            return false;
        }
        if (m_values==null) {
            return (attribute.m_values==null);
        }
        if (attribute.m_values==null) {
            return (m_values==null);
        }
        if (m_values.length != attribute.m_values.length) {
            return false;
        }
        for (int i=0; i<m_values.length; i++) {
            String v1 = m_values[i];
            String v2 = attribute.m_values[i];
            if (v1==null) {
                if (v2 != null) {
                    return false;
                }
            } else {
                if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
