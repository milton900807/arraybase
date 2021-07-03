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
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="Genetic-code_E">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Genetic-code_E_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Genetic-code_E_id" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Genetic-code_E_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Genetic-code_E_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *                   &lt;element name="Genetic-code_E_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *                   &lt;element name="Genetic-code_E_sncbieaa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Genetic-code_E_sncbi8aa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *                   &lt;element name="Genetic-code_E_sncbistdaa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *                 &lt;/choice>
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
    "geneticCodeE"
})
@XmlRootElement(name = "Genetic-code")
public class GeneticCode {

    @XmlElement(name = "Genetic-code_E")
    protected List<GeneticCode.GeneticCodeE> geneticCodeE;

    /**
     * Gets the value of the geneticCodeE property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geneticCodeE property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeneticCodeE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeneticCode.GeneticCodeE }
     * 
     * 
     */
    public List<GeneticCode.GeneticCodeE> getGeneticCodeE() {
        if (geneticCodeE == null) {
            geneticCodeE = new ArrayList<GeneticCode.GeneticCodeE>();
        }
        return this.geneticCodeE;
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
     *       &lt;choice>
     *         &lt;element name="Genetic-code_E_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Genetic-code_E_id" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Genetic-code_E_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Genetic-code_E_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
     *         &lt;element name="Genetic-code_E_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
     *         &lt;element name="Genetic-code_E_sncbieaa" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Genetic-code_E_sncbi8aa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
     *         &lt;element name="Genetic-code_E_sncbistdaa" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
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
        "geneticCodeEName",
        "geneticCodeEId",
        "geneticCodeENcbieaa",
        "geneticCodeENcbi8Aa",
        "geneticCodeENcbistdaa",
        "geneticCodeESncbieaa",
        "geneticCodeESncbi8Aa",
        "geneticCodeESncbistdaa"
    })
    public static class GeneticCodeE {

        @XmlElement(name = "Genetic-code_E_name")
        protected String geneticCodeEName;
        @XmlElement(name = "Genetic-code_E_id")
        protected BigInteger geneticCodeEId;
        @XmlElement(name = "Genetic-code_E_ncbieaa")
        protected String geneticCodeENcbieaa;
        @XmlElement(name = "Genetic-code_E_ncbi8aa", type = String.class)
        @XmlJavaTypeAdapter(HexBinaryAdapter.class)
        @XmlSchemaType(name = "hexBinary")
        protected byte[] geneticCodeENcbi8Aa;
        @XmlElement(name = "Genetic-code_E_ncbistdaa", type = String.class)
        @XmlJavaTypeAdapter(HexBinaryAdapter.class)
        @XmlSchemaType(name = "hexBinary")
        protected byte[] geneticCodeENcbistdaa;
        @XmlElement(name = "Genetic-code_E_sncbieaa")
        protected String geneticCodeESncbieaa;
        @XmlElement(name = "Genetic-code_E_sncbi8aa", type = String.class)
        @XmlJavaTypeAdapter(HexBinaryAdapter.class)
        @XmlSchemaType(name = "hexBinary")
        protected byte[] geneticCodeESncbi8Aa;
        @XmlElement(name = "Genetic-code_E_sncbistdaa", type = String.class)
        @XmlJavaTypeAdapter(HexBinaryAdapter.class)
        @XmlSchemaType(name = "hexBinary")
        protected byte[] geneticCodeESncbistdaa;

        /**
         * Gets the value of the geneticCodeEName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGeneticCodeEName() {
            return geneticCodeEName;
        }

        /**
         * Sets the value of the geneticCodeEName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeEName(String value) {
            this.geneticCodeEName = value;
        }

        /**
         * Gets the value of the geneticCodeEId property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getGeneticCodeEId() {
            return geneticCodeEId;
        }

        /**
         * Sets the value of the geneticCodeEId property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setGeneticCodeEId(BigInteger value) {
            this.geneticCodeEId = value;
        }

        /**
         * Gets the value of the geneticCodeENcbieaa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGeneticCodeENcbieaa() {
            return geneticCodeENcbieaa;
        }

        /**
         * Sets the value of the geneticCodeENcbieaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeENcbieaa(String value) {
            this.geneticCodeENcbieaa = value;
        }

        /**
         * Gets the value of the geneticCodeENcbi8Aa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public byte[] getGeneticCodeENcbi8Aa() {
            return geneticCodeENcbi8Aa;
        }

        /**
         * Sets the value of the geneticCodeENcbi8Aa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeENcbi8Aa(byte[] value) {
            this.geneticCodeENcbi8Aa = value;
        }

        /**
         * Gets the value of the geneticCodeENcbistdaa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public byte[] getGeneticCodeENcbistdaa() {
            return geneticCodeENcbistdaa;
        }

        /**
         * Sets the value of the geneticCodeENcbistdaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeENcbistdaa(byte[] value) {
            this.geneticCodeENcbistdaa = value;
        }

        /**
         * Gets the value of the geneticCodeESncbieaa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGeneticCodeESncbieaa() {
            return geneticCodeESncbieaa;
        }

        /**
         * Sets the value of the geneticCodeESncbieaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeESncbieaa(String value) {
            this.geneticCodeESncbieaa = value;
        }

        /**
         * Gets the value of the geneticCodeESncbi8Aa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public byte[] getGeneticCodeESncbi8Aa() {
            return geneticCodeESncbi8Aa;
        }

        /**
         * Sets the value of the geneticCodeESncbi8Aa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeESncbi8Aa(byte[] value) {
            this.geneticCodeESncbi8Aa = value;
        }

        /**
         * Gets the value of the geneticCodeESncbistdaa property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public byte[] getGeneticCodeESncbistdaa() {
            return geneticCodeESncbistdaa;
        }

        /**
         * Sets the value of the geneticCodeESncbistdaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeneticCodeESncbistdaa(byte[] value) {
            this.geneticCodeESncbistdaa = value;
        }

    }

}
