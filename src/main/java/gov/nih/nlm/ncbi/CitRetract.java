//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;sequence>
 *         &lt;element name="CitRetract_type">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="retracted"/>
 *                       &lt;enumeration value="notice"/>
 *                       &lt;enumeration value="in-error"/>
 *                       &lt;enumeration value="erratum"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CitRetract_exp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "citRetractType",
    "citRetractExp"
})
@XmlRootElement(name = "CitRetract")
public class CitRetract {

    @XmlElement(name = "CitRetract_type", required = true)
    protected CitRetract.CitRetractType citRetractType;
    @XmlElement(name = "CitRetract_exp")
    protected String citRetractExp;

    /**
     * Gets the value of the citRetractType property.
     * 
     * @return
     *     possible object is
     *     {@link CitRetract.CitRetractType }
     *     
     */
    public CitRetract.CitRetractType getCitRetractType() {
        return citRetractType;
    }

    /**
     * Sets the value of the citRetractType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitRetract.CitRetractType }
     *     
     */
    public void setCitRetractType(CitRetract.CitRetractType value) {
        this.citRetractType = value;
    }

    /**
     * Gets the value of the citRetractExp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitRetractExp() {
        return citRetractExp;
    }

    /**
     * Sets the value of the citRetractExp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitRetractExp(String value) {
        this.citRetractExp = value;
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
     *       &lt;attribute name="value" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="retracted"/>
     *             &lt;enumeration value="notice"/>
     *             &lt;enumeration value="in-error"/>
     *             &lt;enumeration value="erratum"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class CitRetractType {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}