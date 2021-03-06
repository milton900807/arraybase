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
 *         &lt;element name="PCRReaction_forward" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PCRPrimerSet"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PCRReaction_reverse" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PCRPrimerSet"/>
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
    "pcrReactionForward",
    "pcrReactionReverse"
})
@XmlRootElement(name = "PCRReaction")
public class PCRReaction {

    @XmlElement(name = "PCRReaction_forward")
    protected PCRReaction.PCRReactionForward pcrReactionForward;
    @XmlElement(name = "PCRReaction_reverse")
    protected PCRReaction.PCRReactionReverse pcrReactionReverse;

    /**
     * Gets the value of the pcrReactionForward property.
     * 
     * @return
     *     possible object is
     *     {@link PCRReaction.PCRReactionForward }
     *     
     */
    public PCRReaction.PCRReactionForward getPCRReactionForward() {
        return pcrReactionForward;
    }

    /**
     * Sets the value of the pcrReactionForward property.
     * 
     * @param value
     *     allowed object is
     *     {@link PCRReaction.PCRReactionForward }
     *     
     */
    public void setPCRReactionForward(PCRReaction.PCRReactionForward value) {
        this.pcrReactionForward = value;
    }

    /**
     * Gets the value of the pcrReactionReverse property.
     * 
     * @return
     *     possible object is
     *     {@link PCRReaction.PCRReactionReverse }
     *     
     */
    public PCRReaction.PCRReactionReverse getPCRReactionReverse() {
        return pcrReactionReverse;
    }

    /**
     * Sets the value of the pcrReactionReverse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PCRReaction.PCRReactionReverse }
     *     
     */
    public void setPCRReactionReverse(PCRReaction.PCRReactionReverse value) {
        this.pcrReactionReverse = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PCRPrimerSet"/>
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
        "pcrPrimerSet"
    })
    public static class PCRReactionForward {

        @XmlElement(name = "PCRPrimerSet", required = true)
        protected PCRPrimerSet pcrPrimerSet;

        /**
         * Gets the value of the pcrPrimerSet property.
         * 
         * @return
         *     possible object is
         *     {@link PCRPrimerSet }
         *     
         */
        public PCRPrimerSet getPCRPrimerSet() {
            return pcrPrimerSet;
        }

        /**
         * Sets the value of the pcrPrimerSet property.
         * 
         * @param value
         *     allowed object is
         *     {@link PCRPrimerSet }
         *     
         */
        public void setPCRPrimerSet(PCRPrimerSet value) {
            this.pcrPrimerSet = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PCRPrimerSet"/>
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
        "pcrPrimerSet"
    })
    public static class PCRReactionReverse {

        @XmlElement(name = "PCRPrimerSet", required = true)
        protected PCRPrimerSet pcrPrimerSet;

        /**
         * Gets the value of the pcrPrimerSet property.
         * 
         * @return
         *     possible object is
         *     {@link PCRPrimerSet }
         *     
         */
        public PCRPrimerSet getPCRPrimerSet() {
            return pcrPrimerSet;
        }

        /**
         * Sets the value of the pcrPrimerSet property.
         * 
         * @param value
         *     allowed object is
         *     {@link PCRPrimerSet }
         *     
         */
        public void setPCRPrimerSet(PCRPrimerSet value) {
            this.pcrPrimerSet = value;
        }

    }

}
