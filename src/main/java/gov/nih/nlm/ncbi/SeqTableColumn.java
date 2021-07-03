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
 *         &lt;element name="SeqTable-column_header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-column-info"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SeqTable-column_data" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-multi-data"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SeqTable-column_sparse" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-sparse-index"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SeqTable-column_default" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-single-data"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SeqTable-column_sparse-other" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-single-data"/>
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
    "seqTableColumnHeader",
    "seqTableColumnData",
    "seqTableColumnSparse",
    "seqTableColumnDefault",
    "seqTableColumnSparseOther"
})
@XmlRootElement(name = "SeqTable-column")
public class SeqTableColumn {

    @XmlElement(name = "SeqTable-column_header", required = true)
    protected SeqTableColumn.SeqTableColumnHeader seqTableColumnHeader;
    @XmlElement(name = "SeqTable-column_data")
    protected SeqTableColumn.SeqTableColumnData seqTableColumnData;
    @XmlElement(name = "SeqTable-column_sparse")
    protected SeqTableColumn.SeqTableColumnSparse seqTableColumnSparse;
    @XmlElement(name = "SeqTable-column_default")
    protected SeqTableColumn.SeqTableColumnDefault seqTableColumnDefault;
    @XmlElement(name = "SeqTable-column_sparse-other")
    protected SeqTableColumn.SeqTableColumnSparseOther seqTableColumnSparseOther;

    /**
     * Gets the value of the seqTableColumnHeader property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableColumn.SeqTableColumnHeader }
     *     
     */
    public SeqTableColumn.SeqTableColumnHeader getSeqTableColumnHeader() {
        return seqTableColumnHeader;
    }

    /**
     * Sets the value of the seqTableColumnHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableColumn.SeqTableColumnHeader }
     *     
     */
    public void setSeqTableColumnHeader(SeqTableColumn.SeqTableColumnHeader value) {
        this.seqTableColumnHeader = value;
    }

    /**
     * Gets the value of the seqTableColumnData property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableColumn.SeqTableColumnData }
     *     
     */
    public SeqTableColumn.SeqTableColumnData getSeqTableColumnData() {
        return seqTableColumnData;
    }

    /**
     * Sets the value of the seqTableColumnData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableColumn.SeqTableColumnData }
     *     
     */
    public void setSeqTableColumnData(SeqTableColumn.SeqTableColumnData value) {
        this.seqTableColumnData = value;
    }

    /**
     * Gets the value of the seqTableColumnSparse property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableColumn.SeqTableColumnSparse }
     *     
     */
    public SeqTableColumn.SeqTableColumnSparse getSeqTableColumnSparse() {
        return seqTableColumnSparse;
    }

    /**
     * Sets the value of the seqTableColumnSparse property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableColumn.SeqTableColumnSparse }
     *     
     */
    public void setSeqTableColumnSparse(SeqTableColumn.SeqTableColumnSparse value) {
        this.seqTableColumnSparse = value;
    }

    /**
     * Gets the value of the seqTableColumnDefault property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableColumn.SeqTableColumnDefault }
     *     
     */
    public SeqTableColumn.SeqTableColumnDefault getSeqTableColumnDefault() {
        return seqTableColumnDefault;
    }

    /**
     * Sets the value of the seqTableColumnDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableColumn.SeqTableColumnDefault }
     *     
     */
    public void setSeqTableColumnDefault(SeqTableColumn.SeqTableColumnDefault value) {
        this.seqTableColumnDefault = value;
    }

    /**
     * Gets the value of the seqTableColumnSparseOther property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableColumn.SeqTableColumnSparseOther }
     *     
     */
    public SeqTableColumn.SeqTableColumnSparseOther getSeqTableColumnSparseOther() {
        return seqTableColumnSparseOther;
    }

    /**
     * Sets the value of the seqTableColumnSparseOther property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableColumn.SeqTableColumnSparseOther }
     *     
     */
    public void setSeqTableColumnSparseOther(SeqTableColumn.SeqTableColumnSparseOther value) {
        this.seqTableColumnSparseOther = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-multi-data"/>
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
        "seqTableMultiData"
    })
    public static class SeqTableColumnData {

        @XmlElement(name = "SeqTable-multi-data", required = true)
        protected SeqTableMultiData seqTableMultiData;

        /**
         * Gets the value of the seqTableMultiData property.
         * 
         * @return
         *     possible object is
         *     {@link SeqTableMultiData }
         *     
         */
        public SeqTableMultiData getSeqTableMultiData() {
            return seqTableMultiData;
        }

        /**
         * Sets the value of the seqTableMultiData property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqTableMultiData }
         *     
         */
        public void setSeqTableMultiData(SeqTableMultiData value) {
            this.seqTableMultiData = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-single-data"/>
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
        "seqTableSingleData"
    })
    public static class SeqTableColumnDefault {

        @XmlElement(name = "SeqTable-single-data", required = true)
        protected SeqTableSingleData seqTableSingleData;

        /**
         * Gets the value of the seqTableSingleData property.
         * 
         * @return
         *     possible object is
         *     {@link SeqTableSingleData }
         *     
         */
        public SeqTableSingleData getSeqTableSingleData() {
            return seqTableSingleData;
        }

        /**
         * Sets the value of the seqTableSingleData property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqTableSingleData }
         *     
         */
        public void setSeqTableSingleData(SeqTableSingleData value) {
            this.seqTableSingleData = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-column-info"/>
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
        "seqTableColumnInfo"
    })
    public static class SeqTableColumnHeader {

        @XmlElement(name = "SeqTable-column-info", required = true)
        protected SeqTableColumnInfo seqTableColumnInfo;

        /**
         * Gets the value of the seqTableColumnInfo property.
         * 
         * @return
         *     possible object is
         *     {@link SeqTableColumnInfo }
         *     
         */
        public SeqTableColumnInfo getSeqTableColumnInfo() {
            return seqTableColumnInfo;
        }

        /**
         * Sets the value of the seqTableColumnInfo property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqTableColumnInfo }
         *     
         */
        public void setSeqTableColumnInfo(SeqTableColumnInfo value) {
            this.seqTableColumnInfo = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-sparse-index"/>
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
        "seqTableSparseIndex"
    })
    public static class SeqTableColumnSparse {

        @XmlElement(name = "SeqTable-sparse-index", required = true)
        protected SeqTableSparseIndex seqTableSparseIndex;

        /**
         * Gets the value of the seqTableSparseIndex property.
         * 
         * @return
         *     possible object is
         *     {@link SeqTableSparseIndex }
         *     
         */
        public SeqTableSparseIndex getSeqTableSparseIndex() {
            return seqTableSparseIndex;
        }

        /**
         * Sets the value of the seqTableSparseIndex property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqTableSparseIndex }
         *     
         */
        public void setSeqTableSparseIndex(SeqTableSparseIndex value) {
            this.seqTableSparseIndex = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}SeqTable-single-data"/>
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
        "seqTableSingleData"
    })
    public static class SeqTableColumnSparseOther {

        @XmlElement(name = "SeqTable-single-data", required = true)
        protected SeqTableSingleData seqTableSingleData;

        /**
         * Gets the value of the seqTableSingleData property.
         * 
         * @return
         *     possible object is
         *     {@link SeqTableSingleData }
         *     
         */
        public SeqTableSingleData getSeqTableSingleData() {
            return seqTableSingleData;
        }

        /**
         * Sets the value of the seqTableSingleData property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqTableSingleData }
         *     
         */
        public void setSeqTableSingleData(SeqTableSingleData value) {
            this.seqTableSingleData = value;
        }

    }

}