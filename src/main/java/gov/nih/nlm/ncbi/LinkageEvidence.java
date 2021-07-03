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
 *         &lt;element name="Linkage-evidence_type">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="paired-ends"/>
 *                       &lt;enumeration value="align-genus"/>
 *                       &lt;enumeration value="align-xgenus"/>
 *                       &lt;enumeration value="align-trnscpt"/>
 *                       &lt;enumeration value="within-clone"/>
 *                       &lt;enumeration value="clone-contig"/>
 *                       &lt;enumeration value="map"/>
 *                       &lt;enumeration value="strobe"/>
 *                       &lt;enumeration value="unspecified"/>
 *                       &lt;enumeration value="pcr"/>
 *                       &lt;enumeration value="other"/>
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
    "linkageEvidenceType"
})
@XmlRootElement(name = "Linkage-evidence")
public class LinkageEvidence {

    @XmlElement(name = "Linkage-evidence_type", required = true)
    protected LinkageEvidence.LinkageEvidenceType linkageEvidenceType;

    /**
     * Gets the value of the linkageEvidenceType property.
     * 
     * @return
     *     possible object is
     *     {@link LinkageEvidence.LinkageEvidenceType }
     *     
     */
    public LinkageEvidence.LinkageEvidenceType getLinkageEvidenceType() {
        return linkageEvidenceType;
    }

    /**
     * Sets the value of the linkageEvidenceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinkageEvidence.LinkageEvidenceType }
     *     
     */
    public void setLinkageEvidenceType(LinkageEvidence.LinkageEvidenceType value) {
        this.linkageEvidenceType = value;
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
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="paired-ends"/>
     *             &lt;enumeration value="align-genus"/>
     *             &lt;enumeration value="align-xgenus"/>
     *             &lt;enumeration value="align-trnscpt"/>
     *             &lt;enumeration value="within-clone"/>
     *             &lt;enumeration value="clone-contig"/>
     *             &lt;enumeration value="map"/>
     *             &lt;enumeration value="strobe"/>
     *             &lt;enumeration value="unspecified"/>
     *             &lt;enumeration value="pcr"/>
     *             &lt;enumeration value="other"/>
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
    public static class LinkageEvidenceType {

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

}