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
 *         &lt;element name="Trna-ext_aa" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Trna-ext_aa_iupacaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Trna-ext_aa_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Trna-ext_aa_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Trna-ext_aa_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Trna-ext_codon" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="Trna-ext_codon_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Trna-ext_anticodon" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-loc"/>
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
    "trnaExtAa",
    "trnaExtCodon",
    "trnaExtAnticodon"
})
@XmlRootElement(name = "Trna-ext")
public class TrnaExt {

    @XmlElement(name = "Trna-ext_aa")
    protected TrnaExt.TrnaExtAa trnaExtAa;
    @XmlElement(name = "Trna-ext_codon")
    protected TrnaExt.TrnaExtCodon trnaExtCodon;
    @XmlElement(name = "Trna-ext_anticodon")
    protected TrnaExt.TrnaExtAnticodon trnaExtAnticodon;

    /**
     * Gets the value of the trnaExtAa property.
     * 
     * @return
     *     possible object is
     *     {@link TrnaExt.TrnaExtAa }
     *     
     */
    public TrnaExt.TrnaExtAa getTrnaExtAa() {
        return trnaExtAa;
    }

    /**
     * Sets the value of the trnaExtAa property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrnaExt.TrnaExtAa }
     *     
     */
    public void setTrnaExtAa(TrnaExt.TrnaExtAa value) {
        this.trnaExtAa = value;
    }

    /**
     * Gets the value of the trnaExtCodon property.
     * 
     * @return
     *     possible object is
     *     {@link TrnaExt.TrnaExtCodon }
     *     
     */
    public TrnaExt.TrnaExtCodon getTrnaExtCodon() {
        return trnaExtCodon;
    }

    /**
     * Sets the value of the trnaExtCodon property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrnaExt.TrnaExtCodon }
     *     
     */
    public void setTrnaExtCodon(TrnaExt.TrnaExtCodon value) {
        this.trnaExtCodon = value;
    }

    /**
     * Gets the value of the trnaExtAnticodon property.
     * 
     * @return
     *     possible object is
     *     {@link TrnaExt.TrnaExtAnticodon }
     *     
     */
    public TrnaExt.TrnaExtAnticodon getTrnaExtAnticodon() {
        return trnaExtAnticodon;
    }

    /**
     * Sets the value of the trnaExtAnticodon property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrnaExt.TrnaExtAnticodon }
     *     
     */
    public void setTrnaExtAnticodon(TrnaExt.TrnaExtAnticodon value) {
        this.trnaExtAnticodon = value;
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
     *       &lt;choice>
     *         &lt;element name="Trna-ext_aa_iupacaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Trna-ext_aa_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Trna-ext_aa_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Trna-ext_aa_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "trnaExtAaIupacaa",
        "trnaExtAaNcbieaa",
        "trnaExtAaNcbi8Aa",
        "trnaExtAaNcbistdaa"
    })
    public static class TrnaExtAa {

        @XmlElement(name = "Trna-ext_aa_iupacaa")
        protected BigInteger trnaExtAaIupacaa;
        @XmlElement(name = "Trna-ext_aa_ncbieaa")
        protected BigInteger trnaExtAaNcbieaa;
        @XmlElement(name = "Trna-ext_aa_ncbi8aa")
        protected BigInteger trnaExtAaNcbi8Aa;
        @XmlElement(name = "Trna-ext_aa_ncbistdaa")
        protected BigInteger trnaExtAaNcbistdaa;

        /**
         * Gets the value of the trnaExtAaIupacaa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTrnaExtAaIupacaa() {
            return trnaExtAaIupacaa;
        }

        /**
         * Sets the value of the trnaExtAaIupacaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTrnaExtAaIupacaa(BigInteger value) {
            this.trnaExtAaIupacaa = value;
        }

        /**
         * Gets the value of the trnaExtAaNcbieaa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTrnaExtAaNcbieaa() {
            return trnaExtAaNcbieaa;
        }

        /**
         * Sets the value of the trnaExtAaNcbieaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTrnaExtAaNcbieaa(BigInteger value) {
            this.trnaExtAaNcbieaa = value;
        }

        /**
         * Gets the value of the trnaExtAaNcbi8Aa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTrnaExtAaNcbi8Aa() {
            return trnaExtAaNcbi8Aa;
        }

        /**
         * Sets the value of the trnaExtAaNcbi8Aa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTrnaExtAaNcbi8Aa(BigInteger value) {
            this.trnaExtAaNcbi8Aa = value;
        }

        /**
         * Gets the value of the trnaExtAaNcbistdaa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTrnaExtAaNcbistdaa() {
            return trnaExtAaNcbistdaa;
        }

        /**
         * Sets the value of the trnaExtAaNcbistdaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTrnaExtAaNcbistdaa(BigInteger value) {
            this.trnaExtAaNcbistdaa = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-loc"/>
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
        "seqLoc"
    })
    public static class TrnaExtAnticodon {

        @XmlElement(name = "Seq-loc", required = true)
        protected SeqLoc seqLoc;

        /**
         * Gets the value of the seqLoc property.
         * 
         * @return
         *     possible object is
         *     {@link SeqLoc }
         *     
         */
        public SeqLoc getSeqLoc() {
            return seqLoc;
        }

        /**
         * Sets the value of the seqLoc property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqLoc }
         *     
         */
        public void setSeqLoc(SeqLoc value) {
            this.seqLoc = value;
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
     *         &lt;element name="Trna-ext_codon_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "trnaExtCodonE"
    })
    public static class TrnaExtCodon {

        @XmlElement(name = "Trna-ext_codon_E")
        protected List<BigInteger> trnaExtCodonE;

        /**
         * Gets the value of the trnaExtCodonE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the trnaExtCodonE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTrnaExtCodonE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getTrnaExtCodonE() {
            if (trnaExtCodonE == null) {
                trnaExtCodonE = new ArrayList<BigInteger>();
            }
            return this.trnaExtCodonE;
        }

    }

}
