//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 11:55:22 AM PDT 
//


package gov.nih.nlm.ncbi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="GBFeature_key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GBFeature_location" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GBFeature_intervals" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBInterval"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GBFeature_operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GBFeature_partial5" minOccurs="0">
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
 *         &lt;element name="GBFeature_partial3" minOccurs="0">
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
 *         &lt;element name="GBFeature_quals" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBQualifier"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GBFeature_xrefs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBXref"/>
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
    "gbFeatureKey",
    "gbFeatureLocation",
    "gbFeatureIntervals",
    "gbFeatureOperator",
    "gbFeaturePartial5",
    "gbFeaturePartial3",
    "gbFeatureQuals",
    "gbFeatureXrefs"
})
@XmlRootElement(name = "GBFeature")
public class GBFeature {

    @XmlElement(name = "GBFeature_key", required = true)
    protected String gbFeatureKey;
    @XmlElement(name = "GBFeature_location", required = true)
    protected String gbFeatureLocation;
    @XmlElement(name = "GBFeature_intervals")
    protected GBFeature.GBFeatureIntervals gbFeatureIntervals;
    @XmlElement(name = "GBFeature_operator")
    protected String gbFeatureOperator;
    @XmlElement(name = "GBFeature_partial5")
    protected GBFeature.GBFeaturePartial5 gbFeaturePartial5;
    @XmlElement(name = "GBFeature_partial3")
    protected GBFeature.GBFeaturePartial3 gbFeaturePartial3;
    @XmlElement(name = "GBFeature_quals")
    protected GBFeature.GBFeatureQuals gbFeatureQuals;
    @XmlElement(name = "GBFeature_xrefs")
    protected GBFeature.GBFeatureXrefs gbFeatureXrefs;

    /**
     * Gets the value of the gbFeatureKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGBFeatureKey() {
        return gbFeatureKey;
    }

    /**
     * Sets the value of the gbFeatureKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGBFeatureKey(String value) {
        this.gbFeatureKey = value;
    }

    /**
     * Gets the value of the gbFeatureLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGBFeatureLocation() {
        return gbFeatureLocation;
    }

    /**
     * Sets the value of the gbFeatureLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGBFeatureLocation(String value) {
        this.gbFeatureLocation = value;
    }

    /**
     * Gets the value of the gbFeatureIntervals property.
     * 
     * @return
     *     possible object is
     *     {@link GBFeature.GBFeatureIntervals }
     *     
     */
    public GBFeature.GBFeatureIntervals getGBFeatureIntervals() {
        return gbFeatureIntervals;
    }

    /**
     * Sets the value of the gbFeatureIntervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link GBFeature.GBFeatureIntervals }
     *     
     */
    public void setGBFeatureIntervals(GBFeature.GBFeatureIntervals value) {
        this.gbFeatureIntervals = value;
    }

    /**
     * Gets the value of the gbFeatureOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGBFeatureOperator() {
        return gbFeatureOperator;
    }

    /**
     * Sets the value of the gbFeatureOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGBFeatureOperator(String value) {
        this.gbFeatureOperator = value;
    }

    /**
     * Gets the value of the gbFeaturePartial5 property.
     * 
     * @return
     *     possible object is
     *     {@link GBFeature.GBFeaturePartial5 }
     *     
     */
    public GBFeature.GBFeaturePartial5 getGBFeaturePartial5() {
        return gbFeaturePartial5;
    }

    /**
     * Sets the value of the gbFeaturePartial5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link GBFeature.GBFeaturePartial5 }
     *     
     */
    public void setGBFeaturePartial5(GBFeature.GBFeaturePartial5 value) {
        this.gbFeaturePartial5 = value;
    }

    /**
     * Gets the value of the gbFeaturePartial3 property.
     * 
     * @return
     *     possible object is
     *     {@link GBFeature.GBFeaturePartial3 }
     *     
     */
    public GBFeature.GBFeaturePartial3 getGBFeaturePartial3() {
        return gbFeaturePartial3;
    }

    /**
     * Sets the value of the gbFeaturePartial3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link GBFeature.GBFeaturePartial3 }
     *     
     */
    public void setGBFeaturePartial3(GBFeature.GBFeaturePartial3 value) {
        this.gbFeaturePartial3 = value;
    }

    /**
     * Gets the value of the gbFeatureQuals property.
     * 
     * @return
     *     possible object is
     *     {@link GBFeature.GBFeatureQuals }
     *     
     */
    public GBFeature.GBFeatureQuals getGBFeatureQuals() {
        return gbFeatureQuals;
    }

    /**
     * Sets the value of the gbFeatureQuals property.
     * 
     * @param value
     *     allowed object is
     *     {@link GBFeature.GBFeatureQuals }
     *     
     */
    public void setGBFeatureQuals(GBFeature.GBFeatureQuals value) {
        this.gbFeatureQuals = value;
    }

    /**
     * Gets the value of the gbFeatureXrefs property.
     * 
     * @return
     *     possible object is
     *     {@link GBFeature.GBFeatureXrefs }
     *     
     */
    public GBFeature.GBFeatureXrefs getGBFeatureXrefs() {
        return gbFeatureXrefs;
    }

    /**
     * Sets the value of the gbFeatureXrefs property.
     * 
     * @param value
     *     allowed object is
     *     {@link GBFeature.GBFeatureXrefs }
     *     
     */
    public void setGBFeatureXrefs(GBFeature.GBFeatureXrefs value) {
        this.gbFeatureXrefs = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBInterval"/>
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
        "gbInterval"
    })
    public static class GBFeatureIntervals {

        @XmlElement(name = "GBInterval")
        protected List<GBInterval> gbInterval;

        /**
         * Gets the value of the gbInterval property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gbInterval property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGBInterval().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GBInterval }
         * 
         * 
         */
        public List<GBInterval> getGBInterval() {
            if (gbInterval == null) {
                gbInterval = new ArrayList<GBInterval>();
            }
            return this.gbInterval;
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
    public static class GBFeaturePartial3 {

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
    public static class GBFeaturePartial5 {

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
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBQualifier"/>
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
        "gbQualifier"
    })
    public static class GBFeatureQuals {

        @XmlElement(name = "GBQualifier")
        protected List<GBQualifier> gbQualifier;

        /**
         * Gets the value of the gbQualifier property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gbQualifier property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGBQualifier().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GBQualifier }
         * 
         * 
         */
        public List<GBQualifier> getGBQualifier() {
            if (gbQualifier == null) {
                gbQualifier = new ArrayList<GBQualifier>();
            }
            return this.gbQualifier;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}GBXref"/>
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
        "gbXref"
    })
    public static class GBFeatureXrefs {

        @XmlElement(name = "GBXref")
        protected List<GBXref> gbXref;

        /**
         * Gets the value of the gbXref property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gbXref property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGBXref().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GBXref }
         * 
         * 
         */
        public List<GBXref> getGBXref() {
            if (gbXref == null) {
                gbXref = new ArrayList<GBXref>();
            }
            return this.gbXref;
        }

    }

}
