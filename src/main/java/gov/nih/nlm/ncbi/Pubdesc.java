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
 *         &lt;element name="Pubdesc_pub">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Pub-equiv"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Pubdesc_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pubdesc_fig" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pubdesc_num" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Numbering"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Pubdesc_numexc" minOccurs="0">
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
 *         &lt;element name="Pubdesc_poly-a" minOccurs="0">
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
 *         &lt;element name="Pubdesc_maploc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pubdesc_seq-raw" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pubdesc_align-group" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="Pubdesc_comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pubdesc_reftype" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value" default="seq">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="seq"/>
 *                       &lt;enumeration value="sites"/>
 *                       &lt;enumeration value="feats"/>
 *                       &lt;enumeration value="no-target"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
    "pubdescPub",
    "pubdescName",
    "pubdescFig",
    "pubdescNum",
    "pubdescNumexc",
    "pubdescPolyA",
    "pubdescMaploc",
    "pubdescSeqRaw",
    "pubdescAlignGroup",
    "pubdescComment",
    "pubdescReftype"
})
@XmlRootElement(name = "Pubdesc")
public class Pubdesc {

    @XmlElement(name = "Pubdesc_pub", required = true)
    protected Pubdesc.PubdescPub pubdescPub;
    @XmlElement(name = "Pubdesc_name")
    protected String pubdescName;
    @XmlElement(name = "Pubdesc_fig")
    protected String pubdescFig;
    @XmlElement(name = "Pubdesc_num")
    protected Pubdesc.PubdescNum pubdescNum;
    @XmlElement(name = "Pubdesc_numexc")
    protected Pubdesc.PubdescNumexc pubdescNumexc;
    @XmlElement(name = "Pubdesc_poly-a")
    protected Pubdesc.PubdescPolyA pubdescPolyA;
    @XmlElement(name = "Pubdesc_maploc")
    protected String pubdescMaploc;
    @XmlElement(name = "Pubdesc_seq-raw")
    protected String pubdescSeqRaw;
    @XmlElement(name = "Pubdesc_align-group")
    protected BigInteger pubdescAlignGroup;
    @XmlElement(name = "Pubdesc_comment")
    protected String pubdescComment;
    @XmlElement(name = "Pubdesc_reftype")
    protected Pubdesc.PubdescReftype pubdescReftype;

    /**
     * Gets the value of the pubdescPub property.
     * 
     * @return
     *     possible object is
     *     {@link Pubdesc.PubdescPub }
     *     
     */
    public Pubdesc.PubdescPub getPubdescPub() {
        return pubdescPub;
    }

    /**
     * Sets the value of the pubdescPub property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pubdesc.PubdescPub }
     *     
     */
    public void setPubdescPub(Pubdesc.PubdescPub value) {
        this.pubdescPub = value;
    }

    /**
     * Gets the value of the pubdescName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubdescName() {
        return pubdescName;
    }

    /**
     * Sets the value of the pubdescName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubdescName(String value) {
        this.pubdescName = value;
    }

    /**
     * Gets the value of the pubdescFig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubdescFig() {
        return pubdescFig;
    }

    /**
     * Sets the value of the pubdescFig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubdescFig(String value) {
        this.pubdescFig = value;
    }

    /**
     * Gets the value of the pubdescNum property.
     * 
     * @return
     *     possible object is
     *     {@link Pubdesc.PubdescNum }
     *     
     */
    public Pubdesc.PubdescNum getPubdescNum() {
        return pubdescNum;
    }

    /**
     * Sets the value of the pubdescNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pubdesc.PubdescNum }
     *     
     */
    public void setPubdescNum(Pubdesc.PubdescNum value) {
        this.pubdescNum = value;
    }

    /**
     * Gets the value of the pubdescNumexc property.
     * 
     * @return
     *     possible object is
     *     {@link Pubdesc.PubdescNumexc }
     *     
     */
    public Pubdesc.PubdescNumexc getPubdescNumexc() {
        return pubdescNumexc;
    }

    /**
     * Sets the value of the pubdescNumexc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pubdesc.PubdescNumexc }
     *     
     */
    public void setPubdescNumexc(Pubdesc.PubdescNumexc value) {
        this.pubdescNumexc = value;
    }

    /**
     * Gets the value of the pubdescPolyA property.
     * 
     * @return
     *     possible object is
     *     {@link Pubdesc.PubdescPolyA }
     *     
     */
    public Pubdesc.PubdescPolyA getPubdescPolyA() {
        return pubdescPolyA;
    }

    /**
     * Sets the value of the pubdescPolyA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pubdesc.PubdescPolyA }
     *     
     */
    public void setPubdescPolyA(Pubdesc.PubdescPolyA value) {
        this.pubdescPolyA = value;
    }

    /**
     * Gets the value of the pubdescMaploc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubdescMaploc() {
        return pubdescMaploc;
    }

    /**
     * Sets the value of the pubdescMaploc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubdescMaploc(String value) {
        this.pubdescMaploc = value;
    }

    /**
     * Gets the value of the pubdescSeqRaw property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubdescSeqRaw() {
        return pubdescSeqRaw;
    }

    /**
     * Sets the value of the pubdescSeqRaw property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubdescSeqRaw(String value) {
        this.pubdescSeqRaw = value;
    }

    /**
     * Gets the value of the pubdescAlignGroup property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPubdescAlignGroup() {
        return pubdescAlignGroup;
    }

    /**
     * Sets the value of the pubdescAlignGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPubdescAlignGroup(BigInteger value) {
        this.pubdescAlignGroup = value;
    }

    /**
     * Gets the value of the pubdescComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubdescComment() {
        return pubdescComment;
    }

    /**
     * Sets the value of the pubdescComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubdescComment(String value) {
        this.pubdescComment = value;
    }

    /**
     * Gets the value of the pubdescReftype property.
     * 
     * @return
     *     possible object is
     *     {@link Pubdesc.PubdescReftype }
     *     
     */
    public Pubdesc.PubdescReftype getPubdescReftype() {
        return pubdescReftype;
    }

    /**
     * Sets the value of the pubdescReftype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pubdesc.PubdescReftype }
     *     
     */
    public void setPubdescReftype(Pubdesc.PubdescReftype value) {
        this.pubdescReftype = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Numbering"/>
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
        "numbering"
    })
    public static class PubdescNum {

        @XmlElement(name = "Numbering", required = true)
        protected Numbering numbering;

        /**
         * Gets the value of the numbering property.
         * 
         * @return
         *     possible object is
         *     {@link Numbering }
         *     
         */
        public Numbering getNumbering() {
            return numbering;
        }

        /**
         * Sets the value of the numbering property.
         * 
         * @param value
         *     allowed object is
         *     {@link Numbering }
         *     
         */
        public void setNumbering(Numbering value) {
            this.numbering = value;
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
    public static class PubdescNumexc {

        @XmlAttribute(name = "value", required = true)
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
            return value;
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
    public static class PubdescPolyA {

        @XmlAttribute(name = "value", required = true)
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
            return value;
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
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Pub-equiv"/>
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
        "pubEquiv"
    })
    public static class PubdescPub {

        @XmlElement(name = "Pub-equiv", required = true)
        protected PubEquiv pubEquiv;

        /**
         * Gets the value of the pubEquiv property.
         * 
         * @return
         *     possible object is
         *     {@link PubEquiv }
         *     
         */
        public PubEquiv getPubEquiv() {
            return pubEquiv;
        }

        /**
         * Sets the value of the pubEquiv property.
         * 
         * @param value
         *     allowed object is
         *     {@link PubEquiv }
         *     
         */
        public void setPubEquiv(PubEquiv value) {
            this.pubEquiv = value;
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
     *       &lt;attribute name="value" default="seq">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="seq"/>
     *             &lt;enumeration value="sites"/>
     *             &lt;enumeration value="feats"/>
     *             &lt;enumeration value="no-target"/>
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
    public static class PubdescReftype {

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
                return "seq";
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
