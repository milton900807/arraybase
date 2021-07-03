//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


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
 *         &lt;element name="Seq-gap_type">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="unknown"/>
 *                       &lt;enumeration value="fragment"/>
 *                       &lt;enumeration value="clone"/>
 *                       &lt;enumeration value="short-arm"/>
 *                       &lt;enumeration value="heterochromatin"/>
 *                       &lt;enumeration value="centromere"/>
 *                       &lt;enumeration value="telomere"/>
 *                       &lt;enumeration value="repeat"/>
 *                       &lt;enumeration value="contig"/>
 *                       &lt;enumeration value="scaffold"/>
 *                       &lt;enumeration value="contamination"/>
 *                       &lt;enumeration value="other"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Seq-gap_linkage" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="unlinked"/>
 *                       &lt;enumeration value="linked"/>
 *                       &lt;enumeration value="other"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Seq-gap_linkage-evidence" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Linkage-evidence"/>
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
    "seqGapType",
    "seqGapLinkage",
    "seqGapLinkageEvidence"
})
@XmlRootElement(name = "Seq-gap")
public class SeqGap {

    @XmlElement(name = "Seq-gap_type", required = true)
    protected SeqGap.SeqGapType seqGapType;
    @XmlElement(name = "Seq-gap_linkage")
    protected SeqGap.SeqGapLinkage seqGapLinkage;
    @XmlElement(name = "Seq-gap_linkage-evidence")
    protected SeqGap.SeqGapLinkageEvidence seqGapLinkageEvidence;

    /**
     * Gets the value of the seqGapType property.
     * 
     * @return
     *     possible object is
     *     {@link SeqGap.SeqGapType }
     *     
     */
    public SeqGap.SeqGapType getSeqGapType() {
        return seqGapType;
    }

    /**
     * Sets the value of the seqGapType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqGap.SeqGapType }
     *     
     */
    public void setSeqGapType(SeqGap.SeqGapType value) {
        this.seqGapType = value;
    }

    /**
     * Gets the value of the seqGapLinkage property.
     * 
     * @return
     *     possible object is
     *     {@link SeqGap.SeqGapLinkage }
     *     
     */
    public SeqGap.SeqGapLinkage getSeqGapLinkage() {
        return seqGapLinkage;
    }

    /**
     * Sets the value of the seqGapLinkage property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqGap.SeqGapLinkage }
     *     
     */
    public void setSeqGapLinkage(SeqGap.SeqGapLinkage value) {
        this.seqGapLinkage = value;
    }

    /**
     * Gets the value of the seqGapLinkageEvidence property.
     * 
     * @return
     *     possible object is
     *     {@link SeqGap.SeqGapLinkageEvidence }
     *     
     */
    public SeqGap.SeqGapLinkageEvidence getSeqGapLinkageEvidence() {
        return seqGapLinkageEvidence;
    }

    /**
     * Sets the value of the seqGapLinkageEvidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqGap.SeqGapLinkageEvidence }
     *     
     */
    public void setSeqGapLinkageEvidence(SeqGap.SeqGapLinkageEvidence value) {
        this.seqGapLinkageEvidence = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="unlinked"/>
     *             &lt;enumeration value="linked"/>
     *             &lt;enumeration value="other"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class SeqGapLinkage {

        @XmlValue
        protected BigInteger value;
        @XmlAttribute(name = "value")
        protected String valueAttribute;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setValue(BigInteger value) {
            this.value = value;
        }

        /**
         * Gets the value of the valueAttribute property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValueAttribute() {
            return valueAttribute;
        }

        /**
         * Sets the value of the valueAttribute property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValueAttribute(String value) {
            this.valueAttribute = value;
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
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Linkage-evidence"/>
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
        "linkageEvidence"
    })
    public static class SeqGapLinkageEvidence {

        @XmlElement(name = "Linkage-evidence")
        protected List<LinkageEvidence> linkageEvidence;

        /**
         * Gets the value of the linkageEvidence property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the linkageEvidence property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLinkageEvidence().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LinkageEvidence }
         * 
         * 
         */
        public List<LinkageEvidence> getLinkageEvidence() {
            if (linkageEvidence == null) {
                linkageEvidence = new ArrayList<LinkageEvidence>();
            }
            return this.linkageEvidence;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="unknown"/>
     *             &lt;enumeration value="fragment"/>
     *             &lt;enumeration value="clone"/>
     *             &lt;enumeration value="short-arm"/>
     *             &lt;enumeration value="heterochromatin"/>
     *             &lt;enumeration value="centromere"/>
     *             &lt;enumeration value="telomere"/>
     *             &lt;enumeration value="repeat"/>
     *             &lt;enumeration value="contig"/>
     *             &lt;enumeration value="scaffold"/>
     *             &lt;enumeration value="contamination"/>
     *             &lt;enumeration value="other"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class SeqGapType {

        @XmlValue
        protected BigInteger value;
        @XmlAttribute(name = "value")
        protected String valueAttribute;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setValue(BigInteger value) {
            this.value = value;
        }

        /**
         * Gets the value of the valueAttribute property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValueAttribute() {
            return valueAttribute;
        }

        /**
         * Sets the value of the valueAttribute property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValueAttribute(String value) {
            this.valueAttribute = value;
        }

    }

}