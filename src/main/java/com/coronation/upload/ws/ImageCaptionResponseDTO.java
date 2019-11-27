
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for imageCaptionResponseDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="imageCaptionResponseDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="captionSecret" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="imageSecret" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "imageCaptionResponseDTO", propOrder = {
    "captionSecret",
    "imageSecret",
    "respCode",
    "respMessage",
    "respMessageCode",
    "successful"
})
public class ImageCaptionResponseDTO {

    @XmlElement(nillable = true)
    protected List<String> captionSecret;
    @XmlElement(nillable = true)
    protected List<String> imageSecret;
    protected Integer respCode;
    protected String respMessage;
    protected String respMessageCode;
    protected boolean successful;

    /**
     * Gets the value of the captionSecret property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the captionSecret property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCaptionSecret().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCaptionSecret() {
        if (captionSecret == null) {
            captionSecret = new ArrayList<String>();
        }
        return this.captionSecret;
    }

    /**
     * Gets the value of the imageSecret property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageSecret property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageSecret().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getImageSecret() {
        if (imageSecret == null) {
            imageSecret = new ArrayList<String>();
        }
        return this.imageSecret;
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
