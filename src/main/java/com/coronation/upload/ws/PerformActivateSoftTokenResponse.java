
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for performActivateSoftTokenResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="performActivateSoftTokenResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.entrustplugin.expertedge.com/}activateSoftTokenResponseDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "performActivateSoftTokenResponse", propOrder = {
    "_return"
})
public class PerformActivateSoftTokenResponse {

    @XmlElement(name = "return")
    protected ActivateSoftTokenResponseDTO _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ActivateSoftTokenResponseDTO }
     *     
     */
    public ActivateSoftTokenResponseDTO getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivateSoftTokenResponseDTO }
     *     
     */
    public void setReturn(ActivateSoftTokenResponseDTO value) {
        this._return = value;
    }

}
