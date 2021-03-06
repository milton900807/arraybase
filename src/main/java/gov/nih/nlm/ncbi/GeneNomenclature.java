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
 *         &lt;element name="Gene-nomenclature_status">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="unknown"/>
 *                       &lt;enumeration value="official"/>
 *                       &lt;enumeration value="interim"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Gene-nomenclature_symbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Gene-nomenclature_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Gene-nomenclature_source" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Dbtag"/>
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
    "geneNomenclatureStatus",
    "geneNomenclatureSymbol",
    "geneNomenclatureName",
    "geneNomenclatureSource"
})
@XmlRootElement(name = "Gene-nomenclature")
public class GeneNomenclature {

    @XmlElement(name = "Gene-nomenclature_status", required = true)
    protected GeneNomenclature.GeneNomenclatureStatus geneNomenclatureStatus;
    @XmlElement(name = "Gene-nomenclature_symbol")
    protected String geneNomenclatureSymbol;
    @XmlElement(name = "Gene-nomenclature_name")
    protected String geneNomenclatureName;
    @XmlElement(name = "Gene-nomenclature_source")
    protected GeneNomenclature.GeneNomenclatureSource geneNomenclatureSource;

    /**
     * Gets the value of the geneNomenclatureStatus property.
     * 
     * @return
     *     possible object is
     *     {@link GeneNomenclature.GeneNomenclatureStatus }
     *     
     */
    public GeneNomenclature.GeneNomenclatureStatus getGeneNomenclatureStatus() {
        return geneNomenclatureStatus;
    }

    /**
     * Sets the value of the geneNomenclatureStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneNomenclature.GeneNomenclatureStatus }
     *     
     */
    public void setGeneNomenclatureStatus(GeneNomenclature.GeneNomenclatureStatus value) {
        this.geneNomenclatureStatus = value;
    }

    /**
     * Gets the value of the geneNomenclatureSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneNomenclatureSymbol() {
        return geneNomenclatureSymbol;
    }

    /**
     * Sets the value of the geneNomenclatureSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneNomenclatureSymbol(String value) {
        this.geneNomenclatureSymbol = value;
    }

    /**
     * Gets the value of the geneNomenclatureName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneNomenclatureName() {
        return geneNomenclatureName;
    }

    /**
     * Sets the value of the geneNomenclatureName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneNomenclatureName(String value) {
        this.geneNomenclatureName = value;
    }

    /**
     * Gets the value of the geneNomenclatureSource property.
     * 
     * @return
     *     possible object is
     *     {@link GeneNomenclature.GeneNomenclatureSource }
     *     
     */
    public GeneNomenclature.GeneNomenclatureSource getGeneNomenclatureSource() {
        return geneNomenclatureSource;
    }

    /**
     * Sets the value of the geneNomenclatureSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneNomenclature.GeneNomenclatureSource }
     *     
     */
    public void setGeneNomenclatureSource(GeneNomenclature.GeneNomenclatureSource value) {
        this.geneNomenclatureSource = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Dbtag"/>
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
        "dbtag"
    })
    public static class GeneNomenclatureSource {

        @XmlElement(name = "Dbtag", required = true)
        protected Dbtag dbtag;

        /**
         * Gets the value of the dbtag property.
         * 
         * @return
         *     possible object is
         *     {@link Dbtag }
         *     
         */
        public Dbtag getDbtag() {
            return dbtag;
        }

        /**
         * Sets the value of the dbtag property.
         * 
         * @param value
         *     allowed object is
         *     {@link Dbtag }
         *     
         */
        public void setDbtag(Dbtag value) {
            this.dbtag = value;
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
     *             &lt;enumeration value="unknown"/>
     *             &lt;enumeration value="official"/>
     *             &lt;enumeration value="interim"/>
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
    public static class GeneNomenclatureStatus {

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
