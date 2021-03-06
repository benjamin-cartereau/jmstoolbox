
package org.titou10.jtb.config.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}qManagerDef" maxOccurs="unbounded"/>
 *         &lt;element ref="{}sessionDef" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "qManagerDef",
    "sessionDef"
})
@XmlRootElement(name = "config")
public class Config {

    @XmlElement(required = true)
    protected List<QManagerDef> qManagerDef;
    @XmlElement(required = true)
    protected List<SessionDef> sessionDef;

    /**
     * Gets the value of the qManagerDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qManagerDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQManagerDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QManagerDef }
     * 
     * 
     */
    public List<QManagerDef> getQManagerDef() {
        if (qManagerDef == null) {
            qManagerDef = new ArrayList<QManagerDef>();
        }
        return this.qManagerDef;
    }

    /**
     * Gets the value of the sessionDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sessionDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSessionDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SessionDef }
     * 
     * 
     */
    public List<SessionDef> getSessionDef() {
        if (sessionDef == null) {
            sessionDef = new ArrayList<SessionDef>();
        }
        return this.sessionDef;
    }

}
