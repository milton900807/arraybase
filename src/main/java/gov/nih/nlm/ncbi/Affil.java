//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Affil_str" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Affil_std">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Affil_std_affil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_div" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_sub" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Affil_std_postal-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "affilStr",
    "affilStd"
})
@XmlRootElement(name = "Affil")
public class Affil {

    @XmlElement(name = "Affil_str")
    protected String affilStr;
    @XmlElement(name = "Affil_std")
    protected Affil.AffilStd affilStd;

    /**
     * Gets the value of the affilStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAffilStr() {
        return affilStr;
    }

    /**
     * Sets the value of the affilStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAffilStr(String value) {
        this.affilStr = value;
    }

    /**
     * Gets the value of the affilStd property.
     * 
     * @return
     *     possible object is
     *     {@link Affil.AffilStd }
     *     
     */
    public Affil.AffilStd getAffilStd() {
        return affilStd;
    }

    /**
     * Sets the value of the affilStd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Affil.AffilStd }
     *     
     */
    public void setAffilStd(Affil.AffilStd value) {
        this.affilStd = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Affil_std_affil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_div" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_sub" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Affil_std_postal-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "affilStdAffil",
        "affilStdDiv",
        "affilStdCity",
        "affilStdSub",
        "affilStdCountry",
        "affilStdStreet",
        "affilStdEmail",
        "affilStdFax",
        "affilStdPhone",
        "affilStdPostalCode"
    })
    public static class AffilStd {

        @XmlElement(name = "Affil_std_affil")
        protected String affilStdAffil;
        @XmlElement(name = "Affil_std_div")
        protected String affilStdDiv;
        @XmlElement(name = "Affil_std_city")
        protected String affilStdCity;
        @XmlElement(name = "Affil_std_sub")
        protected String affilStdSub;
        @XmlElement(name = "Affil_std_country")
        protected String affilStdCountry;
        @XmlElement(name = "Affil_std_street")
        protected String affilStdStreet;
        @XmlElement(name = "Affil_std_email")
        protected String affilStdEmail;
        @XmlElement(name = "Affil_std_fax")
        protected String affilStdFax;
        @XmlElement(name = "Affil_std_phone")
        protected String affilStdPhone;
        @XmlElement(name = "Affil_std_postal-code")
        protected String affilStdPostalCode;

        /**
         * Gets the value of the affilStdAffil property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdAffil() {
            return affilStdAffil;
        }

        /**
         * Sets the value of the affilStdAffil property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdAffil(String value) {
            this.affilStdAffil = value;
        }

        /**
         * Gets the value of the affilStdDiv property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdDiv() {
            return affilStdDiv;
        }

        /**
         * Sets the value of the affilStdDiv property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdDiv(String value) {
            this.affilStdDiv = value;
        }

        /**
         * Gets the value of the affilStdCity property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdCity() {
            return affilStdCity;
        }

        /**
         * Sets the value of the affilStdCity property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdCity(String value) {
            this.affilStdCity = value;
        }

        /**
         * Gets the value of the affilStdSub property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdSub() {
            return affilStdSub;
        }

        /**
         * Sets the value of the affilStdSub property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdSub(String value) {
            this.affilStdSub = value;
        }

        /**
         * Gets the value of the affilStdCountry property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdCountry() {
            return affilStdCountry;
        }

        /**
         * Sets the value of the affilStdCountry property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdCountry(String value) {
            this.affilStdCountry = value;
        }

        /**
         * Gets the value of the affilStdStreet property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdStreet() {
            return affilStdStreet;
        }

        /**
         * Sets the value of the affilStdStreet property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdStreet(String value) {
            this.affilStdStreet = value;
        }

        /**
         * Gets the value of the affilStdEmail property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdEmail() {
            return affilStdEmail;
        }

        /**
         * Sets the value of the affilStdEmail property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdEmail(String value) {
            this.affilStdEmail = value;
        }

        /**
         * Gets the value of the affilStdFax property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdFax() {
            return affilStdFax;
        }

        /**
         * Sets the value of the affilStdFax property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdFax(String value) {
            this.affilStdFax = value;
        }

        /**
         * Gets the value of the affilStdPhone property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdPhone() {
            return affilStdPhone;
        }

        /**
         * Sets the value of the affilStdPhone property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdPhone(String value) {
            this.affilStdPhone = value;
        }

        /**
         * Gets the value of the affilStdPostalCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffilStdPostalCode() {
            return affilStdPostalCode;
        }

        /**
         * Sets the value of the affilStdPostalCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffilStdPostalCode(String value) {
            this.affilStdPostalCode = value;
        }

    }

}
