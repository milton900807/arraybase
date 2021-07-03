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
 *       &lt;sequence>
 *         &lt;element name="Patent-priority_country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Patent-priority_number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Patent-priority_date">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Date"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "patentPriorityCountry",
    "patentPriorityNumber",
    "patentPriorityDate"
})
@XmlRootElement(name = "Patent-priority")
public class PatentPriority {

    @XmlElement(name = "Patent-priority_country", required = true)
    protected String patentPriorityCountry;
    @XmlElement(name = "Patent-priority_number", required = true)
    protected String patentPriorityNumber;
    @XmlElement(name = "Patent-priority_date", required = true)
    protected PatentPriority.PatentPriorityDate patentPriorityDate;

    /**
     * Gets the value of the patentPriorityCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatentPriorityCountry() {
        return patentPriorityCountry;
    }

    /**
     * Sets the value of the patentPriorityCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatentPriorityCountry(String value) {
        this.patentPriorityCountry = value;
    }

    /**
     * Gets the value of the patentPriorityNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatentPriorityNumber() {
        return patentPriorityNumber;
    }

    /**
     * Sets the value of the patentPriorityNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatentPriorityNumber(String value) {
        this.patentPriorityNumber = value;
    }

    /**
     * Gets the value of the patentPriorityDate property.
     * 
     * @return
     *     possible object is
     *     {@link PatentPriority.PatentPriorityDate }
     *     
     */
    public PatentPriority.PatentPriorityDate getPatentPriorityDate() {
        return patentPriorityDate;
    }

    /**
     * Sets the value of the patentPriorityDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link PatentPriority.PatentPriorityDate }
     *     
     */
    public void setPatentPriorityDate(PatentPriority.PatentPriorityDate value) {
        this.patentPriorityDate = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Date"/>
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
        "date"
    })
    public static class PatentPriorityDate {

        @XmlElement(name = "Date", required = true)
        protected Date date;

        /**
         * Gets the value of the date property.
         * 
         * @return
         *     possible object is
         *     {@link Date }
         *     
         */
        public Date getDate() {
            return date;
        }

        /**
         * Sets the value of the date property.
         * 
         * @param value
         *     allowed object is
         *     {@link Date }
         *     
         */
        public void setDate(Date value) {
            this.date = value;
        }

    }

}
