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
 *         &lt;element name="Rsite-ref_str" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Rsite-ref_db">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Dbtag"/>
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
    "rsiteRefStr",
    "rsiteRefDb"
})
@XmlRootElement(name = "Rsite-ref")
public class RsiteRef {

    @XmlElement(name = "Rsite-ref_str")
    protected String rsiteRefStr;
    @XmlElement(name = "Rsite-ref_db")
    protected RsiteRef.RsiteRefDb rsiteRefDb;

    /**
     * Gets the value of the rsiteRefStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRsiteRefStr() {
        return rsiteRefStr;
    }

    /**
     * Sets the value of the rsiteRefStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRsiteRefStr(String value) {
        this.rsiteRefStr = value;
    }

    /**
     * Gets the value of the rsiteRefDb property.
     * 
     * @return
     *     possible object is
     *     {@link RsiteRef.RsiteRefDb }
     *     
     */
    public RsiteRef.RsiteRefDb getRsiteRefDb() {
        return rsiteRefDb;
    }

    /**
     * Sets the value of the rsiteRefDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link RsiteRef.RsiteRefDb }
     *     
     */
    public void setRsiteRefDb(RsiteRef.RsiteRefDb value) {
        this.rsiteRefDb = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Dbtag"/>
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
        "dbtag"
    })
    public static class RsiteRefDb {

        @XmlElement(name = "Dbtag", required = true)
        protected Dbtag dbtag;

        /**
         * Gets the value of the dbtag property.
         * 
         * @return
         *     possible object is
         *     {@link Dbtag }
         *     
         */
        public Dbtag getDbtag() {
            return dbtag;
        }

        /**
         * Sets the value of the dbtag property.
         * 
         * @param value
         *     allowed object is
         *     {@link Dbtag }
         *     
         */
        public void setDbtag(Dbtag value) {
            this.dbtag = value;
        }

    }

}
