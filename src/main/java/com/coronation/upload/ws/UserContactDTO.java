
package com.coronation.upload.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userContactDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userContactDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="appCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="appDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailAddressDeliveryLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailAddressdefault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNoDeliveryLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNodefault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userContactDTO", propOrder = {
    "appCode",
    "appDesc",
    "emailAddress",
    "emailAddressDeliveryLabel",
    "emailAddressdefault",
    "group",
    "phoneNo",
    "phoneNoDeliveryLabel",
    "phoneNodefault",
    "userName"
})
public class UserContactDTO {

    protected String appCode;
    protected String appDesc;
    protected String emailAddress;
    protected String emailAddressDeliveryLabel;
    protected boolean emailAddressdefault;
    protected String group;
    protected String phoneNo;
    protected String phoneNoDeliveryLabel;
    protected boolean phoneNodefault;
    protected String userName;

    /**
     * Gets the value of the appCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppCode() {
        return appCode;
    }

    /**
     * Sets the value of the appCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppCode(String value) {
        this.appCode = value;
    }

    /**
     * Gets the value of the appDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppDesc() {
        return appDesc;
    }

    /**
     * Sets the value of the appDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppDesc(String value) {
        this.appDesc = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the emailAddressDeliveryLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddressDeliveryLabel() {
        return emailAddressDeliveryLabel;
    }

    /**
     * Sets the value of the emailAddressDeliveryLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddressDeliveryLabel(String value) {
        this.emailAddressDeliveryLabel = value;
    }

    /**
     * Gets the value of the emailAddressdefault property.
     * 
     */
    public boolean isEmailAddressdefault() {
        return emailAddressdefault;
    }

    /**
     * Sets the value of the emailAddressdefault property.
     * 
     */
    public void setEmailAddressdefault(boolean value) {
        this.emailAddressdefault = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

    /**
     * Gets the value of the phoneNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * Sets the value of the phoneNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNo(String value) {
        this.phoneNo = value;
    }

    /**
     * Gets the value of the phoneNoDeliveryLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNoDeliveryLabel() {
        return phoneNoDeliveryLabel;
    }

    /**
     * Sets the value of the phoneNoDeliveryLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNoDeliveryLabel(String value) {
        this.phoneNoDeliveryLabel = value;
    }

    /**
     * Gets the value of the phoneNodefault property.
     * 
     */
    public boolean isPhoneNodefault() {
        return phoneNodefault;
    }

    /**
     * Sets the value of the phoneNodefault property.
     * 
     */
    public void setPhoneNodefault(boolean value) {
        this.phoneNodefault = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

}
