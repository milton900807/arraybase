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
 *         &lt;element name="PRF-ExtraSrc_host" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRF-ExtraSrc_part" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRF-ExtraSrc_state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRF-ExtraSrc_strain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRF-ExtraSrc_taxon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "prfExtraSrcHost",
    "prfExtraSrcPart",
    "prfExtraSrcState",
    "prfExtraSrcStrain",
    "prfExtraSrcTaxon"
})
@XmlRootElement(name = "PRF-ExtraSrc")
public class PRFExtraSrc {

    @XmlElement(name = "PRF-ExtraSrc_host")
    protected String prfExtraSrcHost;
    @XmlElement(name = "PRF-ExtraSrc_part")
    protected String prfExtraSrcPart;
    @XmlElement(name = "PRF-ExtraSrc_state")
    protected String prfExtraSrcState;
    @XmlElement(name = "PRF-ExtraSrc_strain")
    protected String prfExtraSrcStrain;
    @XmlElement(name = "PRF-ExtraSrc_taxon")
    protected String prfExtraSrcTaxon;

    /**
     * Gets the value of the prfExtraSrcHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRFExtraSrcHost() {
        return prfExtraSrcHost;
    }

    /**
     * Sets the value of the prfExtraSrcHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRFExtraSrcHost(String value) {
        this.prfExtraSrcHost = value;
    }

    /**
     * Gets the value of the prfExtraSrcPart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRFExtraSrcPart() {
        return prfExtraSrcPart;
    }

    /**
     * Sets the value of the prfExtraSrcPart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRFExtraSrcPart(String value) {
        this.prfExtraSrcPart = value;
    }

    /**
     * Gets the value of the prfExtraSrcState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRFExtraSrcState() {
        return prfExtraSrcState;
    }

    /**
     * Sets the value of the prfExtraSrcState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRFExtraSrcState(String value) {
        this.prfExtraSrcState = value;
    }

    /**
     * Gets the value of the prfExtraSrcStrain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRFExtraSrcStrain() {
        return prfExtraSrcStrain;
    }

    /**
     * Sets the value of the prfExtraSrcStrain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRFExtraSrcStrain(String value) {
        this.prfExtraSrcStrain = value;
    }

    /**
     * Gets the value of the prfExtraSrcTaxon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRFExtraSrcTaxon() {
        return prfExtraSrcTaxon;
    }

    /**
     * Sets the value of the prfExtraSrcTaxon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRFExtraSrcTaxon(String value) {
        this.prfExtraSrcTaxon = value;
    }

}
