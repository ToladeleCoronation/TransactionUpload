
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for performActivateCompleteSoftTokenResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="performActivateCompleteSoftTokenResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.entrustplugin.expertedge.com/}activateCompleteSoftTokenResponseDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "performActivateCompleteSoftTokenResponse", propOrder = {
    "_return"
})
public class PerformActivateCompleteSoftTokenResponse {

    @XmlElement(name = "return")
    protected ActivateCompleteSoftTokenResponseDTO _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ActivateCompleteSoftTokenResponseDTO }
     *     
     */
    public ActivateCompleteSoftTokenResponseDTO getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivateCompleteSoftTokenResponseDTO }
     *     
     */
    public void setReturn(ActivateCompleteSoftTokenResponseDTO value) {
        this._return = value;
    }

}
