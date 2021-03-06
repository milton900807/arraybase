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
 *         &lt;element name="HG-Commentary_link">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Link"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="HG-Commentary_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HG-Commentary_caption" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HG-Commentary_provider" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HG-Commentary_other-links" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Link"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="HG-Commentary_other-commentaries" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Commentary"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="HG-Commentary_taxid" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="HG-Commentary_geneid" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
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
    "hgCommentaryLink",
    "hgCommentaryDescription",
    "hgCommentaryCaption",
    "hgCommentaryProvider",
    "hgCommentaryOtherLinks",
    "hgCommentaryOtherCommentaries",
    "hgCommentaryTaxid",
    "hgCommentaryGeneid"
})
@XmlRootElement(name = "HG-Commentary")
public class HGCommentary {

    @XmlElement(name = "HG-Commentary_link", required = true)
    protected HGCommentary.HGCommentaryLink hgCommentaryLink;
    @XmlElement(name = "HG-Commentary_description")
    protected String hgCommentaryDescription;
    @XmlElement(name = "HG-Commentary_caption")
    protected String hgCommentaryCaption;
    @XmlElement(name = "HG-Commentary_provider")
    protected String hgCommentaryProvider;
    @XmlElement(name = "HG-Commentary_other-links")
    protected HGCommentary.HGCommentaryOtherLinks hgCommentaryOtherLinks;
    @XmlElement(name = "HG-Commentary_other-commentaries")
    protected HGCommentary.HGCommentaryOtherCommentaries hgCommentaryOtherCommentaries;
    @XmlElement(name = "HG-Commentary_taxid")
    protected BigInteger hgCommentaryTaxid;
    @XmlElement(name = "HG-Commentary_geneid")
    protected BigInteger hgCommentaryGeneid;

    /**
     * Gets the value of the hgCommentaryLink property.
     * 
     * @return
     *     possible object is
     *     {@link HGCommentary.HGCommentaryLink }
     *     
     */
    public HGCommentary.HGCommentaryLink getHGCommentaryLink() {
        return hgCommentaryLink;
    }

    /**
     * Sets the value of the hgCommentaryLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link HGCommentary.HGCommentaryLink }
     *     
     */
    public void setHGCommentaryLink(HGCommentary.HGCommentaryLink value) {
        this.hgCommentaryLink = value;
    }

    /**
     * Gets the value of the hgCommentaryDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHGCommentaryDescription() {
        return hgCommentaryDescription;
    }

    /**
     * Sets the value of the hgCommentaryDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHGCommentaryDescription(String value) {
        this.hgCommentaryDescription = value;
    }

    /**
     * Gets the value of the hgCommentaryCaption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHGCommentaryCaption() {
        return hgCommentaryCaption;
    }

    /**
     * Sets the value of the hgCommentaryCaption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHGCommentaryCaption(String value) {
        this.hgCommentaryCaption = value;
    }

    /**
     * Gets the value of the hgCommentaryProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHGCommentaryProvider() {
        return hgCommentaryProvider;
    }

    /**
     * Sets the value of the hgCommentaryProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHGCommentaryProvider(String value) {
        this.hgCommentaryProvider = value;
    }

    /**
     * Gets the value of the hgCommentaryOtherLinks property.
     * 
     * @return
     *     possible object is
     *     {@link HGCommentary.HGCommentaryOtherLinks }
     *     
     */
    public HGCommentary.HGCommentaryOtherLinks getHGCommentaryOtherLinks() {
        return hgCommentaryOtherLinks;
    }

    /**
     * Sets the value of the hgCommentaryOtherLinks property.
     * 
     * @param value
     *     allowed object is
     *     {@link HGCommentary.HGCommentaryOtherLinks }
     *     
     */
    public void setHGCommentaryOtherLinks(HGCommentary.HGCommentaryOtherLinks value) {
        this.hgCommentaryOtherLinks = value;
    }

    /**
     * Gets the value of the hgCommentaryOtherCommentaries property.
     * 
     * @return
     *     possible object is
     *     {@link HGCommentary.HGCommentaryOtherCommentaries }
     *     
     */
    public HGCommentary.HGCommentaryOtherCommentaries getHGCommentaryOtherCommentaries() {
        return hgCommentaryOtherCommentaries;
    }

    /**
     * Sets the value of the hgCommentaryOtherCommentaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link HGCommentary.HGCommentaryOtherCommentaries }
     *     
     */
    public void setHGCommentaryOtherCommentaries(HGCommentary.HGCommentaryOtherCommentaries value) {
        this.hgCommentaryOtherCommentaries = value;
    }

    /**
     * Gets the value of the hgCommentaryTaxid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHGCommentaryTaxid() {
        return hgCommentaryTaxid;
    }

    /**
     * Sets the value of the hgCommentaryTaxid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHGCommentaryTaxid(BigInteger value) {
        this.hgCommentaryTaxid = value;
    }

    /**
     * Gets the value of the hgCommentaryGeneid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHGCommentaryGeneid() {
        return hgCommentaryGeneid;
    }

    /**
     * Sets the value of the hgCommentaryGeneid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHGCommentaryGeneid(BigInteger value) {
        this.hgCommentaryGeneid = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Link"/>
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
        "hgLink"
    })
    public static class HGCommentaryLink {

        @XmlElement(name = "HG-Link", required = true)
        protected HGLink hgLink;

        /**
         * Gets the value of the hgLink property.
         * 
         * @return
         *     possible object is
         *     {@link HGLink }
         *     
         */
        public HGLink getHGLink() {
            return hgLink;
        }

        /**
         * Sets the value of the hgLink property.
         * 
         * @param value
         *     allowed object is
         *     {@link HGLink }
         *     
         */
        public void setHGLink(HGLink value) {
            this.hgLink = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Commentary"/>
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
        "hgCommentary"
    })
    public static class HGCommentaryOtherCommentaries {

        @XmlElement(name = "HG-Commentary")
        protected List<HGCommentary> hgCommentary;

        /**
         * Gets the value of the hgCommentary property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hgCommentary property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHGCommentary().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HGCommentary }
         * 
         * 
         */
        public List<HGCommentary> getHGCommentary() {
            if (hgCommentary == null) {
                hgCommentary = new ArrayList<HGCommentary>();
            }
            return this.hgCommentary;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}HG-Link"/>
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
        "hgLink"
    })
    public static class HGCommentaryOtherLinks {

        @XmlElement(name = "HG-Link")
        protected List<HGLink> hgLink;

        /**
         * Gets the value of the hgLink property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hgLink property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHGLink().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HGLink }
         * 
         * 
         */
        public List<HGLink> getHGLink() {
            if (hgLink == null) {
                hgLink = new ArrayList<HGLink>();
            }
            return this.hgLink;
        }

    }

}
