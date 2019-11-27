
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for softTokenOnBoardingProcessResponseDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="softTokenOnBoardingProcessResponseDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="offline" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="online" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="respCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="respMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="respMessageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="successful" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "softTokenOnBoardingProcessResponseDTO", propOrder = {
    "offline",
    "online",
    "respCode",
    "respMessage",
    "respMessageCode",
    "successful"
})
public class SoftTokenOnBoardingProcessResponseDTO {

    protected boolean offline;
    protected boolean online;
    protected Integer respCode;
    protected String respMessage;
    protected String respMessageCode;
    protected boolean successful;

    /**
     * Gets the value of the offline property.
     * 
     */
    public boolean isOffline() {
        return offline;
    }

    /**
     * Sets the value of the offline property.
     * 
     */
    public void setOffline(boolean value) {
        this.offline = value;
    }

    /**
     * Gets the value of the online property.
     * 
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Sets the value of the online property.
     * 
     */
    public void setOnline(boolean value) {
        this.online = value;
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

    /**
     * Gets the value of the successful property.
     * 
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets the value of the successful property.
     * 
     */
    public void setSuccessful(boolean value) {
        this.successful = value;
    }

}
