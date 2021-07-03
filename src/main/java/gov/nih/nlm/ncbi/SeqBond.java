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
 *         &lt;element name="Seq-bond_a">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-point"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Seq-bond_b" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-point"/>
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
    "seqBondA",
    "seqBondB"
})
@XmlRootElement(name = "Seq-bond")
public class SeqBond {

    @XmlElement(name = "Seq-bond_a", required = true)
    protected SeqBond.SeqBondA seqBondA;
    @XmlElement(name = "Seq-bond_b")
    protected SeqBond.SeqBondB seqBondB;

    /**
     * Gets the value of the seqBondA property.
     * 
     * @return
     *     possible object is
     *     {@link SeqBond.SeqBondA }
     *     
     */
    public SeqBond.SeqBondA getSeqBondA() {
        return seqBondA;
    }

    /**
     * Sets the value of the seqBondA property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqBond.SeqBondA }
     *     
     */
    public void setSeqBondA(SeqBond.SeqBondA value) {
        this.seqBondA = value;
    }

    /**
     * Gets the value of the seqBondB property.
     * 
     * @return
     *     possible object is
     *     {@link SeqBond.SeqBondB }
     *     
     */
    public SeqBond.SeqBondB getSeqBondB() {
        return seqBondB;
    }

    /**
     * Sets the value of the seqBondB property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqBond.SeqBondB }
     *     
     */
    public void setSeqBondB(SeqBond.SeqBondB value) {
        this.seqBondB = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-point"/>
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
        "seqPoint"
    })
    public static class SeqBondA {

        @XmlElement(name = "Seq-point", required = true)
        protected SeqPoint seqPoint;

        /**
         * Gets the value of the seqPoint property.
         * 
         * @return
         *     possible object is
         *     {@link SeqPoint }
         *     
         */
        public SeqPoint getSeqPoint() {
            return seqPoint;
        }

        /**
         * Sets the value of the seqPoint property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqPoint }
         *     
         */
        public void setSeqPoint(SeqPoint value) {
            this.seqPoint = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-point"/>
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
        "seqPoint"
    })
    public static class SeqBondB {

        @XmlElement(name = "Seq-point", required = true)
        protected SeqPoint seqPoint;

        /**
         * Gets the value of the seqPoint property.
         * 
         * @return
         *     possible object is
         *     {@link SeqPoint }
         *     
         */
        public SeqPoint getSeqPoint() {
            return seqPoint;
        }

        /**
         * Sets the value of the seqPoint property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqPoint }
         *     
         */
        public void setSeqPoint(SeqPoint value) {
            this.seqPoint = value;
        }

    }

}