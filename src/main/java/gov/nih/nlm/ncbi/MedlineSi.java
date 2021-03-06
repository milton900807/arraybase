//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

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
 *         &lt;element name="Medline-si_type">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="ddbj"/>
 *                       &lt;enumeration value="carbbank"/>
 *                       &lt;enumeration value="embl"/>
 *                       &lt;enumeration value="hdb"/>
 *                       &lt;enumeration value="genbank"/>
 *                       &lt;enumeration value="hgml"/>
 *                       &lt;enumeration value="mim"/>
 *                       &lt;enumeration value="msd"/>
 *                       &lt;enumeration value="pdb"/>
 *                       &lt;enumeration value="pir"/>
 *                       &lt;enumeration value="prfseqdb"/>
 *                       &lt;enumeration value="psd"/>
 *                       &lt;enumeration value="swissprot"/>
 *                       &lt;enumeration value="gdb"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Medline-si_cit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "medlineSiType",
    "medlineSiCit"
})
@XmlRootElement(name = "Medline-si")
public class MedlineSi {

    @XmlElement(name = "Medline-si_type", required = true)
    protected MedlineSi.MedlineSiType medlineSiType;
    @XmlElement(name = "Medline-si_cit")
    protected String medlineSiCit;

    /**
     * Gets the value of the medlineSiType property.
     * 
     * @return
     *     possible object is
     *     {@link MedlineSi.MedlineSiType }
     *     
     */
    public MedlineSi.MedlineSiType getMedlineSiType() {
        return medlineSiType;
    }

    /**
     * Sets the value of the medlineSiType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedlineSi.MedlineSiType }
     *     
     */
    public void setMedlineSiType(MedlineSi.MedlineSiType value) {
        this.medlineSiType = value;
    }

    /**
     * Gets the value of the medlineSiCit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedlineSiCit() {
        return medlineSiCit;
    }

    /**
     * Sets the value of the medlineSiCit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedlineSiCit(String value) {
        this.medlineSiCit = value;
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
     *             &lt;enumeration value="ddbj"/>
     *             &lt;enumeration value="carbbank"/>
     *             &lt;enumeration value="embl"/>
     *             &lt;enumeration value="hdb"/>
     *             &lt;enumeration value="genbank"/>
     *             &lt;enumeration value="hgml"/>
     *             &lt;enumeration value="mim"/>
     *             &lt;enumeration value="msd"/>
     *             &lt;enumeration value="pdb"/>
     *             &lt;enumeration value="pir"/>
     *             &lt;enumeration value="prfseqdb"/>
     *             &lt;enumeration value="psd"/>
     *             &lt;enumeration value="swissprot"/>
     *             &lt;enumeration value="gdb"/>
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
    public static class MedlineSiType {

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

}
