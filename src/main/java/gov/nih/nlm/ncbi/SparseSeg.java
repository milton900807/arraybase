//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

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
 *         &lt;element name="Sparse-seg_master-id" minOccurs="0">
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
 *         &lt;element name="Sparse-seg_rows">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Sparse-align"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sparse-seg_row-scores" minOccurs="0">
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
 *         &lt;element name="Sparse-seg_ext" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Sparse-seg-ext"/>
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
    "sparseSegMasterId",
    "sparseSegRows",
    "sparseSegRowScores",
    "sparseSegExt"
})
@XmlRootElement(name = "Sparse-seg")
public class SparseSeg {

    @XmlElement(name = "Sparse-seg_master-id")
    protected SparseSeg.SparseSegMasterId sparseSegMasterId;
    @XmlElement(name = "Sparse-seg_rows", required = true)
    protected SparseSeg.SparseSegRows sparseSegRows;
    @XmlElement(name = "Sparse-seg_row-scores")
    protected SparseSeg.SparseSegRowScores sparseSegRowScores;
    @XmlElement(name = "Sparse-seg_ext")
    protected SparseSeg.SparseSegExt sparseSegExt;

    /**
     * Gets the value of the sparseSegMasterId property.
     * 
     * @return
     *     possible object is
     *     {@link SparseSeg.SparseSegMasterId }
     *     
     */
    public SparseSeg.SparseSegMasterId getSparseSegMasterId() {
        return sparseSegMasterId;
    }

    /**
     * Sets the value of the sparseSegMasterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseSeg.SparseSegMasterId }
     *     
     */
    public void setSparseSegMasterId(SparseSeg.SparseSegMasterId value) {
        this.sparseSegMasterId = value;
    }

    /**
     * Gets the value of the sparseSegRows property.
     * 
     * @return
     *     possible object is
     *     {@link SparseSeg.SparseSegRows }
     *     
     */
    public SparseSeg.SparseSegRows getSparseSegRows() {
        return sparseSegRows;
    }

    /**
     * Sets the value of the sparseSegRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseSeg.SparseSegRows }
     *     
     */
    public void setSparseSegRows(SparseSeg.SparseSegRows value) {
        this.sparseSegRows = value;
    }

    /**
     * Gets the value of the sparseSegRowScores property.
     * 
     * @return
     *     possible object is
     *     {@link SparseSeg.SparseSegRowScores }
     *     
     */
    public SparseSeg.SparseSegRowScores getSparseSegRowScores() {
        return sparseSegRowScores;
    }

    /**
     * Sets the value of the sparseSegRowScores property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseSeg.SparseSegRowScores }
     *     
     */
    public void setSparseSegRowScores(SparseSeg.SparseSegRowScores value) {
        this.sparseSegRowScores = value;
    }

    /**
     * Gets the value of the sparseSegExt property.
     * 
     * @return
     *     possible object is
     *     {@link SparseSeg.SparseSegExt }
     *     
     */
    public SparseSeg.SparseSegExt getSparseSegExt() {
        return sparseSegExt;
    }

    /**
     * Sets the value of the sparseSegExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link SparseSeg.SparseSegExt }
     *     
     */
    public void setSparseSegExt(SparseSeg.SparseSegExt value) {
        this.sparseSegExt = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Sparse-seg-ext"/>
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
        "sparseSegExt"
    })
    public static class SparseSegExt {

        @XmlElement(name = "Sparse-seg-ext")
        protected List<gov.nih.nlm.ncbi.SparseSegExt> sparseSegExt;

        /**
         * Gets the value of the sparseSegExt property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sparseSegExt property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSparseSegExt().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link gov.nih.nlm.ncbi.SparseSegExt }
         * 
         * 
         */
        public List<gov.nih.nlm.ncbi.SparseSegExt> getSparseSegExt() {
            if (sparseSegExt == null) {
                sparseSegExt = new ArrayList<gov.nih.nlm.ncbi.SparseSegExt>();
            }
            return this.sparseSegExt;
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
    public static class SparseSegMasterId {

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
    public static class SparseSegRowScores {

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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Sparse-align"/>
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
        "sparseAlign"
    })
    public static class SparseSegRows {

        @XmlElement(name = "Sparse-align")
        protected List<SparseAlign> sparseAlign;

        /**
         * Gets the value of the sparseAlign property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sparseAlign property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSparseAlign().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SparseAlign }
         * 
         * 
         */
        public List<SparseAlign> getSparseAlign() {
            if (sparseAlign == null) {
                sparseAlign = new ArrayList<SparseAlign>();
            }
            return this.sparseAlign;
        }

    }

}
