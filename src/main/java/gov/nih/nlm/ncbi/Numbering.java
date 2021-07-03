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
 *         &lt;element name="Numbering_cont">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-cont"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Numbering_enum">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-enum"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Numbering_ref">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-ref"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Numbering_real">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-real"/>
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
    "numberingCont",
    "numberingEnum",
    "numberingRef",
    "numberingReal"
})
@XmlRootElement(name = "Numbering")
public class Numbering {

    @XmlElement(name = "Numbering_cont")
    protected Numbering.NumberingCont numberingCont;
    @XmlElement(name = "Numbering_enum")
    protected Numbering.NumberingEnum numberingEnum;
    @XmlElement(name = "Numbering_ref")
    protected Numbering.NumberingRef numberingRef;
    @XmlElement(name = "Numbering_real")
    protected Numbering.NumberingReal numberingReal;

    /**
     * Gets the value of the numberingCont property.
     * 
     * @return
     *     possible object is
     *     {@link Numbering.NumberingCont }
     *     
     */
    public Numbering.NumberingCont getNumberingCont() {
        return numberingCont;
    }

    /**
     * Sets the value of the numberingCont property.
     * 
     * @param value
     *     allowed object is
     *     {@link Numbering.NumberingCont }
     *     
     */
    public void setNumberingCont(Numbering.NumberingCont value) {
        this.numberingCont = value;
    }

    /**
     * Gets the value of the numberingEnum property.
     * 
     * @return
     *     possible object is
     *     {@link Numbering.NumberingEnum }
     *     
     */
    public Numbering.NumberingEnum getNumberingEnum() {
        return numberingEnum;
    }

    /**
     * Sets the value of the numberingEnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Numbering.NumberingEnum }
     *     
     */
    public void setNumberingEnum(Numbering.NumberingEnum value) {
        this.numberingEnum = value;
    }

    /**
     * Gets the value of the numberingRef property.
     * 
     * @return
     *     possible object is
     *     {@link Numbering.NumberingRef }
     *     
     */
    public Numbering.NumberingRef getNumberingRef() {
        return numberingRef;
    }

    /**
     * Sets the value of the numberingRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Numbering.NumberingRef }
     *     
     */
    public void setNumberingRef(Numbering.NumberingRef value) {
        this.numberingRef = value;
    }

    /**
     * Gets the value of the numberingReal property.
     * 
     * @return
     *     possible object is
     *     {@link Numbering.NumberingReal }
     *     
     */
    public Numbering.NumberingReal getNumberingReal() {
        return numberingReal;
    }

    /**
     * Sets the value of the numberingReal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Numbering.NumberingReal }
     *     
     */
    public void setNumberingReal(Numbering.NumberingReal value) {
        this.numberingReal = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-cont"/>
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
        "numCont"
    })
    public static class NumberingCont {

        @XmlElement(name = "Num-cont", required = true)
        protected NumCont numCont;

        /**
         * Gets the value of the numCont property.
         * 
         * @return
         *     possible object is
         *     {@link NumCont }
         *     
         */
        public NumCont getNumCont() {
            return numCont;
        }

        /**
         * Sets the value of the numCont property.
         * 
         * @param value
         *     allowed object is
         *     {@link NumCont }
         *     
         */
        public void setNumCont(NumCont value) {
            this.numCont = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-enum"/>
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
        "numEnum"
    })
    public static class NumberingEnum {

        @XmlElement(name = "Num-enum", required = true)
        protected NumEnum numEnum;

        /**
         * Gets the value of the numEnum property.
         * 
         * @return
         *     possible object is
         *     {@link NumEnum }
         *     
         */
        public NumEnum getNumEnum() {
            return numEnum;
        }

        /**
         * Sets the value of the numEnum property.
         * 
         * @param value
         *     allowed object is
         *     {@link NumEnum }
         *     
         */
        public void setNumEnum(NumEnum value) {
            this.numEnum = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-real"/>
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
        "numReal"
    })
    public static class NumberingReal {

        @XmlElement(name = "Num-real", required = true)
        protected NumReal numReal;

        /**
         * Gets the value of the numReal property.
         * 
         * @return
         *     possible object is
         *     {@link NumReal }
         *     
         */
        public NumReal getNumReal() {
            return numReal;
        }

        /**
         * Sets the value of the numReal property.
         * 
         * @param value
         *     allowed object is
         *     {@link NumReal }
         *     
         */
        public void setNumReal(NumReal value) {
            this.numReal = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Num-ref"/>
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
        "numRef"
    })
    public static class NumberingRef {

        @XmlElement(name = "Num-ref", required = true)
        protected NumRef numRef;

        /**
         * Gets the value of the numRef property.
         * 
         * @return
         *     possible object is
         *     {@link NumRef }
         *     
         */
        public NumRef getNumRef() {
            return numRef;
        }

        /**
         * Sets the value of the numRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link NumRef }
         *     
         */
        public void setNumRef(NumRef value) {
            this.numRef = value;
        }

    }

}