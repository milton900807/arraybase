//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

import java.math.BigInteger;
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
 *         &lt;element name="Clone-ref_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Clone-ref_library" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Clone-ref_concordant" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="false">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="true"/>
 *                       &lt;enumeration value="false"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Clone-ref_unique" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="false">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="true"/>
 *                       &lt;enumeration value="false"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Clone-ref_placement-method" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="end-seq"/>
 *                       &lt;enumeration value="insert-alignment"/>
 *                       &lt;enumeration value="sts"/>
 *                       &lt;enumeration value="fish"/>
 *                       &lt;enumeration value="fingerprint"/>
 *                       &lt;enumeration value="end-seq-insert-alignment"/>
 *                       &lt;enumeration value="external"/>
 *                       &lt;enumeration value="curated"/>
 *                       &lt;enumeration value="other"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Clone-ref_clone-seq" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Clone-seq-set"/>
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
    "cloneRefName",
    "cloneRefLibrary",
    "cloneRefConcordant",
    "cloneRefUnique",
    "cloneRefPlacementMethod",
    "cloneRefCloneSeq"
})
@XmlRootElement(name = "Clone-ref")
public class CloneRef {

    @XmlElement(name = "Clone-ref_name", required = true)
    protected String cloneRefName;
    @XmlElement(name = "Clone-ref_library")
    protected String cloneRefLibrary;
    @XmlElement(name = "Clone-ref_concordant")
    protected CloneRef.CloneRefConcordant cloneRefConcordant;
    @XmlElement(name = "Clone-ref_unique")
    protected CloneRef.CloneRefUnique cloneRefUnique;
    @XmlElement(name = "Clone-ref_placement-method")
    protected CloneRef.CloneRefPlacementMethod cloneRefPlacementMethod;
    @XmlElement(name = "Clone-ref_clone-seq")
    protected CloneRef.CloneRefCloneSeq cloneRefCloneSeq;

    /**
     * Gets the value of the cloneRefName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloneRefName() {
        return cloneRefName;
    }

    /**
     * Sets the value of the cloneRefName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloneRefName(String value) {
        this.cloneRefName = value;
    }

    /**
     * Gets the value of the cloneRefLibrary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloneRefLibrary() {
        return cloneRefLibrary;
    }

    /**
     * Sets the value of the cloneRefLibrary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloneRefLibrary(String value) {
        this.cloneRefLibrary = value;
    }

    /**
     * Gets the value of the cloneRefConcordant property.
     * 
     * @return
     *     possible object is
     *     {@link CloneRef.CloneRefConcordant }
     *     
     */
    public CloneRef.CloneRefConcordant getCloneRefConcordant() {
        return cloneRefConcordant;
    }

    /**
     * Sets the value of the cloneRefConcordant property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloneRef.CloneRefConcordant }
     *     
     */
    public void setCloneRefConcordant(CloneRef.CloneRefConcordant value) {
        this.cloneRefConcordant = value;
    }

    /**
     * Gets the value of the cloneRefUnique property.
     * 
     * @return
     *     possible object is
     *     {@link CloneRef.CloneRefUnique }
     *     
     */
    public CloneRef.CloneRefUnique getCloneRefUnique() {
        return cloneRefUnique;
    }

    /**
     * Sets the value of the cloneRefUnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloneRef.CloneRefUnique }
     *     
     */
    public void setCloneRefUnique(CloneRef.CloneRefUnique value) {
        this.cloneRefUnique = value;
    }

    /**
     * Gets the value of the cloneRefPlacementMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CloneRef.CloneRefPlacementMethod }
     *     
     */
    public CloneRef.CloneRefPlacementMethod getCloneRefPlacementMethod() {
        return cloneRefPlacementMethod;
    }

    /**
     * Sets the value of the cloneRefPlacementMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloneRef.CloneRefPlacementMethod }
     *     
     */
    public void setCloneRefPlacementMethod(CloneRef.CloneRefPlacementMethod value) {
        this.cloneRefPlacementMethod = value;
    }

    /**
     * Gets the value of the cloneRefCloneSeq property.
     * 
     * @return
     *     possible object is
     *     {@link CloneRef.CloneRefCloneSeq }
     *     
     */
    public CloneRef.CloneRefCloneSeq getCloneRefCloneSeq() {
        return cloneRefCloneSeq;
    }

    /**
     * Sets the value of the cloneRefCloneSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloneRef.CloneRefCloneSeq }
     *     
     */
    public void setCloneRefCloneSeq(CloneRef.CloneRefCloneSeq value) {
        this.cloneRefCloneSeq = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Clone-seq-set"/>
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
        "cloneSeqSet"
    })
    public static class CloneRefCloneSeq {

        @XmlElement(name = "Clone-seq-set", required = true)
        protected CloneSeqSet cloneSeqSet;

        /**
         * Gets the value of the cloneSeqSet property.
         * 
         * @return
         *     possible object is
         *     {@link CloneSeqSet }
         *     
         */
        public CloneSeqSet getCloneSeqSet() {
            return cloneSeqSet;
        }

        /**
         * Sets the value of the cloneSeqSet property.
         * 
         * @param value
         *     allowed object is
         *     {@link CloneSeqSet }
         *     
         */
        public void setCloneSeqSet(CloneSeqSet value) {
            this.cloneSeqSet = value;
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
     *       &lt;attribute name="value" default="false">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="true"/>
     *             &lt;enumeration value="false"/>
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
    public static class CloneRefConcordant {

        @XmlAttribute(name = "value")
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
            if (value == null) {
                return "false";
            } else {
                return value;
            }
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
     *             &lt;enumeration value="end-seq"/>
     *             &lt;enumeration value="insert-alignment"/>
     *             &lt;enumeration value="sts"/>
     *             &lt;enumeration value="fish"/>
     *             &lt;enumeration value="fingerprint"/>
     *             &lt;enumeration value="end-seq-insert-alignment"/>
     *             &lt;enumeration value="external"/>
     *             &lt;enumeration value="curated"/>
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
    public static class CloneRefPlacementMethod {

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
     *       &lt;attribute name="value" default="false">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="true"/>
     *             &lt;enumeration value="false"/>
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
    public static class CloneRefUnique {

        @XmlAttribute(name = "value")
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
            if (value == null) {
                return "false";
            } else {
                return value;
            }
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