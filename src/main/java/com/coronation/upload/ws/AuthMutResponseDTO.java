
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for authMutResponseDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="authMutResponseDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authenticationSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="respCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="respMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="respMessageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authMutResponseDTO", propOrder = {
    "authenticationSuccessful",
    "respCode",
    "respMessage",
    "respMessageCode"
})
public class AuthMutResponseDTO {

    protected boolean authenticationSuccessful;
    protected Integer respCode;
    protected String respMessage;
    protected String respMessageCode;

    /**
     * Gets the value of the authenticationSuccessful property.
     * 
     */
    public boolean isAuthenticationSuccessful() {
        return authenticationSuccessful;
    }

    /**
     * Sets the value of the authenticationSuccessful property.
     * 
     */
    public void setAuthenticationSuccessful(boolean value) {
        this.authenticationSuccessful = value;
    }

    /**
     * Gets the value of the respCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRespCode() {
        return respCode;
    }

    /**
     * Sets the value of the respCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRespCode(Integer value) {
        this.respCode = value;
    }

    /**
     * Gets the value of the respMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespMessage() {
        return respMessage;
    }

    /**
     * Sets the value of the respMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespMessage(String value) {
        this.respMessage = value;
    }

    /**
     * Gets the value of the respMessageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespMessageCode() {
        return respMessageCode;
    }

    /**
     * Sets the value of the respMessageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespMessageCode(String value) {
        this.respMessageCode = value;
    }

}
