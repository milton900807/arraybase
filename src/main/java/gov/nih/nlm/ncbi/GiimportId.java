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
 *         &lt;element name="Giimport-id_id" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Giimport-id_db" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Giimport-id_release" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "giimportIdId",
    "giimportIdDb",
    "giimportIdRelease"
})
@XmlRootElement(name = "Giimport-id")
public class GiimportId {

    @XmlElement(name = "Giimport-id_id", required = true)
    protected BigInteger giimportIdId;
    @XmlElement(name = "Giimport-id_db")
    protected String giimportIdDb;
    @XmlElement(name = "Giimport-id_release")
    protected String giimportIdRelease;

    /**
     * Gets the value of the giimportIdId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGiimportIdId() {
        return giimportIdId;
    }

    /**
     * Sets the value of the giimportIdId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGiimportIdId(BigInteger value) {
        this.giimportIdId = value;
    }

    /**
     * Gets the value of the giimportIdDb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiimportIdDb() {
        return giimportIdDb;
    }

    /**
     * Sets the value of the giimportIdDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiimportIdDb(String value) {
        this.giimportIdDb = value;
    }

    /**
     * Gets the value of the giimportIdRelease property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiimportIdRelease() {
        return giimportIdRelease;
    }

    /**
     * Sets the value of the giimportIdRelease property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiimportIdRelease(String value) {
        this.giimportIdRelease = value;
    }

}
