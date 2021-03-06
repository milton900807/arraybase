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
 *         &lt;element name="InferenceSupport_category" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}EvidenceCategory"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InferenceSupport_type" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value" default="not-set">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="not-set"/>
 *                       &lt;enumeration value="similar-to-sequence"/>
 *                       &lt;enumeration value="similar-to-aa"/>
 *                       &lt;enumeration value="similar-to-dna"/>
 *                       &lt;enumeration value="similar-to-rna"/>
 *                       &lt;enumeration value="similar-to-mrna"/>
 *                       &lt;enumeration value="similiar-to-est"/>
 *                       &lt;enumeration value="similar-to-other-rna"/>
 *                       &lt;enumeration value="profile"/>
 *                       &lt;enumeration value="nucleotide-motif"/>
 *                       &lt;enumeration value="protein-motif"/>
 *                       &lt;enumeration value="ab-initio-prediction"/>
 *                       &lt;enumeration value="alignment"/>
 *                       &lt;enumeration value="other"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InferenceSupport_other-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InferenceSupport_same-species" minOccurs="0">
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
 *         &lt;element name="InferenceSupport_basis">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}EvidenceBasis"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InferenceSupport_pmids" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PubMedId"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InferenceSupport_dois" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}DOI"/>
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
    "inferenceSupportCategory",
    "inferenceSupportType",
    "inferenceSupportOtherType",
    "inferenceSupportSameSpecies",
    "inferenceSupportBasis",
    "inferenceSupportPmids",
    "inferenceSupportDois"
})
@XmlRootElement(name = "InferenceSupport")
public class InferenceSupport {

    @XmlElement(name = "InferenceSupport_category")
    protected InferenceSupport.InferenceSupportCategory inferenceSupportCategory;
    @XmlElement(name = "InferenceSupport_type")
    protected InferenceSupport.InferenceSupportType inferenceSupportType;
    @XmlElement(name = "InferenceSupport_other-type")
    protected String inferenceSupportOtherType;
    @XmlElement(name = "InferenceSupport_same-species")
    protected InferenceSupport.InferenceSupportSameSpecies inferenceSupportSameSpecies;
    @XmlElement(name = "InferenceSupport_basis", required = true)
    protected InferenceSupport.InferenceSupportBasis inferenceSupportBasis;
    @XmlElement(name = "InferenceSupport_pmids")
    protected InferenceSupport.InferenceSupportPmids inferenceSupportPmids;
    @XmlElement(name = "InferenceSupport_dois")
    protected InferenceSupport.InferenceSupportDois inferenceSupportDois;

    /**
     * Gets the value of the inferenceSupportCategory property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportCategory }
     *     
     */
    public InferenceSupport.InferenceSupportCategory getInferenceSupportCategory() {
        return inferenceSupportCategory;
    }

    /**
     * Sets the value of the inferenceSupportCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportCategory }
     *     
     */
    public void setInferenceSupportCategory(InferenceSupport.InferenceSupportCategory value) {
        this.inferenceSupportCategory = value;
    }

    /**
     * Gets the value of the inferenceSupportType property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportType }
     *     
     */
    public InferenceSupport.InferenceSupportType getInferenceSupportType() {
        return inferenceSupportType;
    }

    /**
     * Sets the value of the inferenceSupportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportType }
     *     
     */
    public void setInferenceSupportType(InferenceSupport.InferenceSupportType value) {
        this.inferenceSupportType = value;
    }

    /**
     * Gets the value of the inferenceSupportOtherType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInferenceSupportOtherType() {
        return inferenceSupportOtherType;
    }

    /**
     * Sets the value of the inferenceSupportOtherType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInferenceSupportOtherType(String value) {
        this.inferenceSupportOtherType = value;
    }

    /**
     * Gets the value of the inferenceSupportSameSpecies property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportSameSpecies }
     *     
     */
    public InferenceSupport.InferenceSupportSameSpecies getInferenceSupportSameSpecies() {
        return inferenceSupportSameSpecies;
    }

    /**
     * Sets the value of the inferenceSupportSameSpecies property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportSameSpecies }
     *     
     */
    public void setInferenceSupportSameSpecies(InferenceSupport.InferenceSupportSameSpecies value) {
        this.inferenceSupportSameSpecies = value;
    }

    /**
     * Gets the value of the inferenceSupportBasis property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportBasis }
     *     
     */
    public InferenceSupport.InferenceSupportBasis getInferenceSupportBasis() {
        return inferenceSupportBasis;
    }

    /**
     * Sets the value of the inferenceSupportBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportBasis }
     *     
     */
    public void setInferenceSupportBasis(InferenceSupport.InferenceSupportBasis value) {
        this.inferenceSupportBasis = value;
    }

    /**
     * Gets the value of the inferenceSupportPmids property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportPmids }
     *     
     */
    public InferenceSupport.InferenceSupportPmids getInferenceSupportPmids() {
        return inferenceSupportPmids;
    }

    /**
     * Sets the value of the inferenceSupportPmids property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportPmids }
     *     
     */
    public void setInferenceSupportPmids(InferenceSupport.InferenceSupportPmids value) {
        this.inferenceSupportPmids = value;
    }

    /**
     * Gets the value of the inferenceSupportDois property.
     * 
     * @return
     *     possible object is
     *     {@link InferenceSupport.InferenceSupportDois }
     *     
     */
    public InferenceSupport.InferenceSupportDois getInferenceSupportDois() {
        return inferenceSupportDois;
    }

    /**
     * Sets the value of the inferenceSupportDois property.
     * 
     * @param value
     *     allowed object is
     *     {@link InferenceSupport.InferenceSupportDois }
     *     
     */
    public void setInferenceSupportDois(InferenceSupport.InferenceSupportDois value) {
        this.inferenceSupportDois = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}EvidenceBasis"/>
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
        "evidenceBasis"
    })
    public static class InferenceSupportBasis {

        @XmlElement(name = "EvidenceBasis", required = true)
        protected EvidenceBasis evidenceBasis;

        /**
         * Gets the value of the evidenceBasis property.
         * 
         * @return
         *     possible object is
         *     {@link EvidenceBasis }
         *     
         */
        public EvidenceBasis getEvidenceBasis() {
            return evidenceBasis;
        }

        /**
         * Sets the value of the evidenceBasis property.
         * 
         * @param value
         *     allowed object is
         *     {@link EvidenceBasis }
         *     
         */
        public void setEvidenceBasis(EvidenceBasis value) {
            this.evidenceBasis = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}EvidenceCategory"/>
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
        "evidenceCategory"
    })
    public static class InferenceSupportCategory {

        @XmlElement(name = "EvidenceCategory", required = true)
        protected EvidenceCategory evidenceCategory;

        /**
         * Gets the value of the evidenceCategory property.
         * 
         * @return
         *     possible object is
         *     {@link EvidenceCategory }
         *     
         */
        public EvidenceCategory getEvidenceCategory() {
            return evidenceCategory;
        }

        /**
         * Sets the value of the evidenceCategory property.
         * 
         * @param value
         *     allowed object is
         *     {@link EvidenceCategory }
         *     
         */
        public void setEvidenceCategory(EvidenceCategory value) {
            this.evidenceCategory = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}DOI"/>
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
        "doi"
    })
    public static class InferenceSupportDois {

        @XmlElement(name = "DOI")
        protected List<String> doi;

        /**
         * Gets the value of the doi property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the doi property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDOI().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getDOI() {
            if (doi == null) {
                doi = new ArrayList<String>();
            }
            return this.doi;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PubMedId"/>
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
        "pubMedId"
    })
    public static class InferenceSupportPmids {

        @XmlElement(name = "PubMedId")
        protected List<BigInteger> pubMedId;

        /**
         * Gets the value of the pubMedId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pubMedId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPubMedId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getPubMedId() {
            if (pubMedId == null) {
                pubMedId = new ArrayList<BigInteger>();
            }
            return this.pubMedId;
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
    public static class InferenceSupportSameSpecies {

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
     *       &lt;attribute name="value" default="not-set">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="not-set"/>
     *             &lt;enumeration value="similar-to-sequence"/>
     *             &lt;enumeration value="similar-to-aa"/>
     *             &lt;enumeration value="similar-to-dna"/>
     *             &lt;enumeration value="similar-to-rna"/>
     *             &lt;enumeration value="similar-to-mrna"/>
     *             &lt;enumeration value="similiar-to-est"/>
     *             &lt;enumeration value="similar-to-other-rna"/>
     *             &lt;enumeration value="profile"/>
     *             &lt;enumeration value="nucleotide-motif"/>
     *             &lt;enumeration value="protein-motif"/>
     *             &lt;enumeration value="ab-initio-prediction"/>
     *             &lt;enumeration value="alignment"/>
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
    public static class InferenceSupportType {

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
            if (valueAttribute == null) {
                return "not-set";
            } else {
                return valueAttribute;
            }
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
