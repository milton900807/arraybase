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
 *         &lt;element name="Cit-jour_title">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-jour_imp">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Imprint"/>
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
    "citJourTitle",
    "citJourImp"
})
@XmlRootElement(name = "Cit-jour")
public class CitJour {

    @XmlElement(name = "Cit-jour_title", required = true)
    protected CitJour.CitJourTitle citJourTitle;
    @XmlElement(name = "Cit-jour_imp", required = true)
    protected CitJour.CitJourImp citJourImp;

    /**
     * Gets the value of the citJourTitle property.
     * 
     * @return
     *     possible object is
     *     {@link CitJour.CitJourTitle }
     *     
     */
    public CitJour.CitJourTitle getCitJourTitle() {
        return citJourTitle;
    }

    /**
     * Sets the value of the citJourTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitJour.CitJourTitle }
     *     
     */
    public void setCitJourTitle(CitJour.CitJourTitle value) {
        this.citJourTitle = value;
    }

    /**
     * Gets the value of the citJourImp property.
     * 
     * @return
     *     possible object is
     *     {@link CitJour.CitJourImp }
     *     
     */
    public CitJour.CitJourImp getCitJourImp() {
        return citJourImp;
    }

    /**
     * Sets the value of the citJourImp property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitJour.CitJourImp }
     *     
     */
    public void setCitJourImp(CitJour.CitJourImp value) {
        this.citJourImp = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Imprint"/>
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
        "imprint"
    })
    public static class CitJourImp {

        @XmlElement(name = "Imprint", required = true)
        protected Imprint imprint;

        /**
         * Gets the value of the imprint property.
         * 
         * @return
         *     possible object is
         *     {@link Imprint }
         *     
         */
        public Imprint getImprint() {
            return imprint;
        }

        /**
         * Sets the value of the imprint property.
         * 
         * @param value
         *     allowed object is
         *     {@link Imprint }
         *     
         */
        public void setImprint(Imprint value) {
            this.imprint = value;
        }

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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
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
        "title"
    })
    public static class CitJourTitle {

        @XmlElement(name = "Title", required = true)
        protected Title title;

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link Title }
         *     
         */
        public Title getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link Title }
         *     
         */
        public void setTitle(Title value) {
            this.title = value;
        }

    }

}
