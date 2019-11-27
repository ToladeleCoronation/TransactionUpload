
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for performSetMutualAuthX complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="performSetMutualAuthX">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://ws.entrustplugin.expertedge.com/}setMutualSecretXDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "performSetMutualAuthX", propOrder = {
    "arg0"
})
public class PerformSetMutualAuthX {

    protected SetMutualSecretXDTO arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link SetMutualSecretXDTO }
     *     
     */
    public SetMutualSecretXDTO getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetMutualSecretXDTO }
     *     
     */
    public void setArg0(SetMutualSecretXDTO value) {
        this.arg0 = value;
    }

}
