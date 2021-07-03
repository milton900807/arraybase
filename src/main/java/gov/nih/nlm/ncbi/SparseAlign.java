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
 *         &lt;element name="Sparse-align_first-id">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-id"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_second-id">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-id"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_numseg" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Sparse-align_first-starts">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="Sparse-align_first-starts_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_second-starts">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="Sparse-align_second-starts_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_lens">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="Sparse-align_lens_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_second-strands" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Na-strand"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-align_seg-scores" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Score"/>
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
    "sparseAlignFirstId",
    "sparseAlignSecondId",
    "sparseAlignNumseg",
    "sparseAlignFirstStarts",
    "sparseAlignSecondStarts",
    "sparseAlignLens",
    "sparseAlignSecondStrands",
    "sparseAlignSegScores"
})
@XmlRootElement(name = "Sparse-align")
public class SparseAlign {

    @XmlElement(name = "Sparse-align_first-id", required = true)
    protected SparseAlign.SparseAlignFirstId sparseAlignFirstId;
    @XmlElement(name = "Sparse-align_second-id", required = true)
    protected SparseAlign.SparseAlignSecondId sparseAlignSecondId;
    @XmlElement(name = "Sparse-align_numseg", required = true)
    protected BigInteger sparseAlignNumseg;
    @XmlElement(name = "Sparse-align_first-starts", required = true)
    protected SparseAlign.SparseAlignFirstStarts sparseAlignFirstStarts;
    @XmlElement(name = "Sparse-align_second-starts", required = true)
    protected SparseAlign.SparseAlignSecondStarts sparseAlignSecondStarts;
    @XmlElement(name = "Sparse-align_lens", required = true)
    protected SparseAlign.SparseAlignLens sparseAlignLens;
    @XmlElement(name = "Sparse-align_second-strands")
    protected SparseAlign.SparseAlignSecondStrands sparseAlignSecondStrands;
    @XmlElement(name = "Sparse-align_seg-scores")
    protected SparseAlign.SparseAlignSegScores sparseAlignSegScores;

    /**
     * Gets the value of the sparseAlignFirstId property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignFirstId }
     *     
     */
    public SparseAlign.SparseAlignFirstId getSparseAlignFirstId() {
        return sparseAlignFirstId;
    }

    /**
     * Sets the value of the sparseAlignFirstId property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignFirstId }
     *     
     */
    public void setSparseAlignFirstId(SparseAlign.SparseAlignFirstId value) {
        this.sparseAlignFirstId = value;
    }

    /**
     * Gets the value of the sparseAlignSecondId property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignSecondId }
     *     
     */
    public SparseAlign.SparseAlignSecondId getSparseAlignSecondId() {
        return sparseAlignSecondId;
    }

    /**
     * Sets the value of the sparseAlignSecondId property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignSecondId }
     *     
     */
    public void setSparseAlignSecondId(SparseAlign.SparseAlignSecondId value) {
        this.sparseAlignSecondId = value;
    }

    /**
     * Gets the value of the sparseAlignNumseg property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSparseAlignNumseg() {
        return sparseAlignNumseg;
    }

    /**
     * Sets the value of the sparseAlignNumseg property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSparseAlignNumseg(BigInteger value) {
        this.sparseAlignNumseg = value;
    }

    /**
     * Gets the value of the sparseAlignFirstStarts property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignFirstStarts }
     *     
     */
    public SparseAlign.SparseAlignFirstStarts getSparseAlignFirstStarts() {
        return sparseAlignFirstStarts;
    }

    /**
     * Sets the value of the sparseAlignFirstStarts property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignFirstStarts }
     *     
     */
    public void setSparseAlignFirstStarts(SparseAlign.SparseAlignFirstStarts value) {
        this.sparseAlignFirstStarts = value;
    }

    /**
     * Gets the value of the sparseAlignSecondStarts property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignSecondStarts }
     *     
     */
    public SparseAlign.SparseAlignSecondStarts getSparseAlignSecondStarts() {
        return sparseAlignSecondStarts;
    }

    /**
     * Sets the value of the sparseAlignSecondStarts property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignSecondStarts }
     *     
     */
    public void setSparseAlignSecondStarts(SparseAlign.SparseAlignSecondStarts value) {
        this.sparseAlignSecondStarts = value;
    }

    /**
     * Gets the value of the sparseAlignLens property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignLens }
     *     
     */
    public SparseAlign.SparseAlignLens getSparseAlignLens() {
        return sparseAlignLens;
    }

    /**
     * Sets the value of the sparseAlignLens property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignLens }
     *     
     */
    public void setSparseAlignLens(SparseAlign.SparseAlignLens value) {
        this.sparseAlignLens = value;
    }

    /**
     * Gets the value of the sparseAlignSecondStrands property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignSecondStrands }
     *     
     */
    public SparseAlign.SparseAlignSecondStrands getSparseAlignSecondStrands() {
        return sparseAlignSecondStrands;
    }

    /**
     * Sets the value of the sparseAlignSecondStrands property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignSecondStrands }
     *     
     */
    public void setSparseAlignSecondStrands(SparseAlign.SparseAlignSecondStrands value) {
        this.sparseAlignSecondStrands = value;
    }

    /**
     * Gets the value of the sparseAlignSegScores property.
     * 
     * @return
     *     possible object is
     *     {@link SparseAlign.SparseAlignSegScores }
     *     
     */
    public SparseAlign.SparseAlignSegScores getSparseAlignSegScores() {
        return sparseAlignSegScores;
    }

    /**
     * Sets the value of the sparseAlignSegScores property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseAlign.SparseAlignSegScores }
     *     
     */
    public void setSparseAlignSegScores(SparseAlign.SparseAlignSegScores value) {
        this.sparseAlignSegScores = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-id"/>
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
        "seqId"
    })
    public static class SparseAlignFirstId {

        @XmlElement(name = "Seq-id", required = true)
        protected SeqId seqId;

        /**
         * Gets the value of the seqId property.
         * 
         * @return
         *     possible object is
         *     {@link SeqId }
         *     
         */
        public SeqId getSeqId() {
            return seqId;
        }

        /**
         * Sets the value of the seqId property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqId }
         *     
         */
        public void setSeqId(SeqId value) {
            this.seqId = value;
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
     *         &lt;element name="Sparse-align_first-starts_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "sparseAlignFirstStartsE"
    })
    public static class SparseAlignFirstStarts {

        @XmlElement(name = "Sparse-align_first-starts_E")
        protected List<BigInteger> sparseAlignFirstStartsE;

        /**
         * Gets the value of the sparseAlignFirstStartsE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sparseAlignFirstStartsE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSparseAlignFirstStartsE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getSparseAlignFirstStartsE() {
            if (sparseAlignFirstStartsE == null) {
                sparseAlignFirstStartsE = new ArrayList<BigInteger>();
            }
            return this.sparseAlignFirstStartsE;
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
     *         &lt;element name="Sparse-align_lens_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "sparseAlignLensE"
    })
    public static class SparseAlignLens {

        @XmlElement(name = "Sparse-align_lens_E")
        protected List<BigInteger> sparseAlignLensE;

        /**
         * Gets the value of the sparseAlignLensE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sparseAlignLensE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSparseAlignLensE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getSparseAlignLensE() {
            if (sparseAlignLensE == null) {
                sparseAlignLensE = new ArrayList<BigInteger>();
            }
            return this.sparseAlignLensE;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-id"/>
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
        "seqId"
    })
    public static class SparseAlignSecondId {

        @XmlElement(name = "Seq-id", required = true)
        protected SeqId seqId;

        /**
         * Gets the value of the seqId property.
         * 
         * @return
         *     possible object is
         *     {@link SeqId }
         *     
         */
        public SeqId getSeqId() {
            return seqId;
        }

        /**
         * Sets the value of the seqId property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqId }
         *     
         */
        public void setSeqId(SeqId value) {
            this.seqId = value;
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
     *         &lt;element name="Sparse-align_second-starts_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "sparseAlignSecondStartsE"
    })
    public static class SparseAlignSecondStarts {

        @XmlElement(name = "Sparse-align_second-starts_E")
        protected List<BigInteger> sparseAlignSecondStartsE;

        /**
         * Gets the value of the sparseAlignSecondStartsE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sparseAlignSecondStartsE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSparseAlignSecondStartsE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getSparseAlignSecondStartsE() {
            if (sparseAlignSecondStartsE == null) {
                sparseAlignSecondStartsE = new ArrayList<BigInteger>();
            }
            return this.sparseAlignSecondStartsE;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Na-strand"/>
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
        "naStrand"
    })
    public static class SparseAlignSecondStrands {

        @XmlElement(name = "Na-strand")
        protected List<NaStrand> naStrand;

        /**
         * Gets the value of the naStrand property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the naStrand property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNaStrand().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NaStrand }
         * 
         * 
         */
        public List<NaStrand> getNaStrand() {
            if (naStrand == null) {
                naStrand = new ArrayList<NaStrand>();
            }
            return this.naStrand;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Score"/>
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
        "score"
    })
    public static class SparseAlignSegScores {

        @XmlElement(name = "Score")
        protected List<Score> score;

        /**
         * Gets the value of the score property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the score property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getScore().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Score }
         * 
         * 
         */
        public List<Score> getScore() {
            if (score == null) {
                score = new ArrayList<Score>();
            }
            return this.score;
        }

    }

}
