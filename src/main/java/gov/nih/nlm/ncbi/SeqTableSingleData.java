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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="SeqTable-single-data_int" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="SeqTable-single-data_real" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="SeqTable-single-data_string" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SeqTable-single-data_bytes" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *         &lt;element name="SeqTable-single-data_bit">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
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
 *         &lt;element name="SeqTable-single-data_loc">
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
 *         &lt;element name="SeqTable-single-data_id">
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
 *         &lt;element name="SeqTable-single-data_interval">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-interval"/>
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
    "seqTableSingleDataInt",
    "seqTableSingleDataReal",
    "seqTableSingleDataString",
    "seqTableSingleDataBytes",
    "seqTableSingleDataBit",
    "seqTableSingleDataLoc",
    "seqTableSingleDataId",
    "seqTableSingleDataInterval"
})
@XmlRootElement(name = "SeqTable-single-data")
public class SeqTableSingleData {

    @XmlElement(name = "SeqTable-single-data_int")
    protected BigInteger seqTableSingleDataInt;
    @XmlElement(name = "SeqTable-single-data_real")
    protected Double seqTableSingleDataReal;
    @XmlElement(name = "SeqTable-single-data_string")
    protected String seqTableSingleDataString;
    @XmlElement(name = "SeqTable-single-data_bytes", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] seqTableSingleDataBytes;
    @XmlElement(name = "SeqTable-single-data_bit")
    protected SeqTableSingleData.SeqTableSingleDataBit seqTableSingleDataBit;
    @XmlElement(name = "SeqTable-single-data_loc")
    protected SeqTableSingleData.SeqTableSingleDataLoc seqTableSingleDataLoc;
    @XmlElement(name = "SeqTable-single-data_id")
    protected SeqTableSingleData.SeqTableSingleDataId seqTableSingleDataId;
    @XmlElement(name = "SeqTable-single-data_interval")
    protected SeqTableSingleData.SeqTableSingleDataInterval seqTableSingleDataInterval;

    /**
     * Gets the value of the seqTableSingleDataInt property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeqTableSingleDataInt() {
        return seqTableSingleDataInt;
    }

    /**
     * Sets the value of the seqTableSingleDataInt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeqTableSingleDataInt(BigInteger value) {
        this.seqTableSingleDataInt = value;
    }

    /**
     * Gets the value of the seqTableSingleDataReal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSeqTableSingleDataReal() {
        return seqTableSingleDataReal;
    }

    /**
     * Sets the value of the seqTableSingleDataReal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSeqTableSingleDataReal(Double value) {
        this.seqTableSingleDataReal = value;
    }

    /**
     * Gets the value of the seqTableSingleDataString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqTableSingleDataString() {
        return seqTableSingleDataString;
    }

    /**
     * Sets the value of the seqTableSingleDataString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqTableSingleDataString(String value) {
        this.seqTableSingleDataString = value;
    }

    /**
     * Gets the value of the seqTableSingleDataBytes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getSeqTableSingleDataBytes() {
        return seqTableSingleDataBytes;
    }

    /**
     * Sets the value of the seqTableSingleDataBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqTableSingleDataBytes(byte[] value) {
        this.seqTableSingleDataBytes = value;
    }

    /**
     * Gets the value of the seqTableSingleDataBit property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableSingleData.SeqTableSingleDataBit }
     *     
     */
    public SeqTableSingleData.SeqTableSingleDataBit getSeqTableSingleDataBit() {
        return seqTableSingleDataBit;
    }

    /**
     * Sets the value of the seqTableSingleDataBit property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableSingleData.SeqTableSingleDataBit }
     *     
     */
    public void setSeqTableSingleDataBit(SeqTableSingleData.SeqTableSingleDataBit value) {
        this.seqTableSingleDataBit = value;
    }

    /**
     * Gets the value of the seqTableSingleDataLoc property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableSingleData.SeqTableSingleDataLoc }
     *     
     */
    public SeqTableSingleData.SeqTableSingleDataLoc getSeqTableSingleDataLoc() {
        return seqTableSingleDataLoc;
    }

    /**
     * Sets the value of the seqTableSingleDataLoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableSingleData.SeqTableSingleDataLoc }
     *     
     */
    public void setSeqTableSingleDataLoc(SeqTableSingleData.SeqTableSingleDataLoc value) {
        this.seqTableSingleDataLoc = value;
    }

    /**
     * Gets the value of the seqTableSingleDataId property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableSingleData.SeqTableSingleDataId }
     *     
     */
    public SeqTableSingleData.SeqTableSingleDataId getSeqTableSingleDataId() {
        return seqTableSingleDataId;
    }

    /**
     * Sets the value of the seqTableSingleDataId property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableSingleData.SeqTableSingleDataId }
     *     
     */
    public void setSeqTableSingleDataId(SeqTableSingleData.SeqTableSingleDataId value) {
        this.seqTableSingleDataId = value;
    }

    /**
     * Gets the value of the seqTableSingleDataInterval property.
     * 
     * @return
     *     possible object is
     *     {@link SeqTableSingleData.SeqTableSingleDataInterval }
     *     
     */
    public SeqTableSingleData.SeqTableSingleDataInterval getSeqTableSingleDataInterval() {
        return seqTableSingleDataInterval;
    }

    /**
     * Sets the value of the seqTableSingleDataInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqTableSingleData.SeqTableSingleDataInterval }
     *     
     */
    public void setSeqTableSingleDataInterval(SeqTableSingleData.SeqTableSingleDataInterval value) {
        this.seqTableSingleDataInterval = value;
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
     *       &lt;attribute name="value" use="required">
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
    public static class SeqTableSingleDataBit {

        @XmlAttribute(name = "value", required = true)
        protected String valueAttribute;

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
    public static class SeqTableSingleDataId {

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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-interval"/>
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
        "seqInterval"
    })
    public static class SeqTableSingleDataInterval {

        @XmlElement(name = "Seq-interval", required = true)
        protected SeqInterval seqInterval;

        /**
         * Gets the value of the seqInterval property.
         * 
         * @return
         *     possible object is
         *     {@link SeqInterval }
         *     
         */
        public SeqInterval getSeqInterval() {
            return seqInterval;
        }

        /**
         * Sets the value of the seqInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqInterval }
         *     
         */
        public void setSeqInterval(SeqInterval value) {
            this.seqInterval = value;
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
    public static class SeqTableSingleDataLoc {

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

}
