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
 *         &lt;element name="HG-Domain_begin" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="HG-Domain_end" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="HG-Domain_pssm-id" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="HG-Domain_cdd-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HG-Domain_cdd-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "hgDomainBegin",
    "hgDomainEnd",
    "hgDomainPssmId",
    "hgDomainCddId",
    "hgDomainCddName"
})
@XmlRootElement(name = "HG-Domain")
public class HGDomain {

    @XmlElement(name = "HG-Domain_begin", required = true)
    protected BigInteger hgDomainBegin;
    @XmlElement(name = "HG-Domain_end", required = true)
    protected BigInteger hgDomainEnd;
    @XmlElement(name = "HG-Domain_pssm-id")
    protected BigInteger hgDomainPssmId;
    @XmlElement(name = "HG-Domain_cdd-id")
    protected String hgDomainCddId;
    @XmlElement(name = "HG-Domain_cdd-name")
    protected String hgDomainCddName;

    /**
     * Gets the value of the hgDomainBegin property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHGDomainBegin() {
        return hgDomainBegin;
    }

    /**
     * Sets the value of the hgDomainBegin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHGDomainBegin(BigInteger value) {
        this.hgDomainBegin = value;
    }

    /**
     * Gets the value of the hgDomainEnd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHGDomainEnd() {
        return hgDomainEnd;
    }

    /**
     * Sets the value of the hgDomainEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHGDomainEnd(BigInteger value) {
        this.hgDomainEnd = value;
    }

    /**
     * Gets the value of the hgDomainPssmId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHGDomainPssmId() {
        return hgDomainPssmId;
    }

    /**
     * Sets the value of the hgDomainPssmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHGDomainPssmId(BigInteger value) {
        this.hgDomainPssmId = value;
    }

    /**
     * Gets the value of the hgDomainCddId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHGDomainCddId() {
        return hgDomainCddId;
    }

    /**
     * Sets the value of the hgDomainCddId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHGDomainCddId(String value) {
        this.hgDomainCddId = value;
    }

    /**
     * Gets the value of the hgDomainCddName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHGDomainCddName() {
        return hgDomainCddName;
    }

    /**
     * Sets the value of the hgDomainCddName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHGDomainCddName(String value) {
        this.hgDomainCddName = value;
    }

}