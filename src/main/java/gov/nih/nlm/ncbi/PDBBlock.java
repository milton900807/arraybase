//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

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
 *         &lt;element name="PDB-block_deposition">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Date"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PDB-block_class" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PDB-block_compound">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="PDB-block_compound_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PDB-block_source">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="PDB-block_source_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PDB-block_exp-method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PDB-block_replace" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PDB-replace"/>
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
    "pdbBlockDeposition",
    "pdbBlockClass",
    "pdbBlockCompound",
    "pdbBlockSource",
    "pdbBlockExpMethod",
    "pdbBlockReplace"
})
@XmlRootElement(name = "PDB-block")
public class PDBBlock {

    @XmlElement(name = "PDB-block_deposition", required = true)
    protected PDBBlock.PDBBlockDeposition pdbBlockDeposition;
    @XmlElement(name = "PDB-block_class", required = true)
    protected String pdbBlockClass;
    @XmlElement(name = "PDB-block_compound", required = true)
    protected PDBBlock.PDBBlockCompound pdbBlockCompound;
    @XmlElement(name = "PDB-block_source", required = true)
    protected PDBBlock.PDBBlockSource pdbBlockSource;
    @XmlElement(name = "PDB-block_exp-method")
    protected String pdbBlockExpMethod;
    @XmlElement(name = "PDB-block_replace")
    protected PDBBlock.PDBBlockReplace pdbBlockReplace;

    /**
     * Gets the value of the pdbBlockDeposition property.
     * 
     * @return
     *     possible object is
     *     {@link PDBBlock.PDBBlockDeposition }
     *     
     */
    public PDBBlock.PDBBlockDeposition getPDBBlockDeposition() {
        return pdbBlockDeposition;
    }

    /**
     * Sets the value of the pdbBlockDeposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBBlock.PDBBlockDeposition }
     *     
     */
    public void setPDBBlockDeposition(PDBBlock.PDBBlockDeposition value) {
        this.pdbBlockDeposition = value;
    }

    /**
     * Gets the value of the pdbBlockClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPDBBlockClass() {
        return pdbBlockClass;
    }

    /**
     * Sets the value of the pdbBlockClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPDBBlockClass(String value) {
        this.pdbBlockClass = value;
    }

    /**
     * Gets the value of the pdbBlockCompound property.
     * 
     * @return
     *     possible object is
     *     {@link PDBBlock.PDBBlockCompound }
     *     
     */
    public PDBBlock.PDBBlockCompound getPDBBlockCompound() {
        return pdbBlockCompound;
    }

    /**
     * Sets the value of the pdbBlockCompound property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBBlock.PDBBlockCompound }
     *     
     */
    public void setPDBBlockCompound(PDBBlock.PDBBlockCompound value) {
        this.pdbBlockCompound = value;
    }

    /**
     * Gets the value of the pdbBlockSource property.
     * 
     * @return
     *     possible object is
     *     {@link PDBBlock.PDBBlockSource }
     *     
     */
    public PDBBlock.PDBBlockSource getPDBBlockSource() {
        return pdbBlockSource;
    }

    /**
     * Sets the value of the pdbBlockSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBBlock.PDBBlockSource }
     *     
     */
    public void setPDBBlockSource(PDBBlock.PDBBlockSource value) {
        this.pdbBlockSource = value;
    }

    /**
     * Gets the value of the pdbBlockExpMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPDBBlockExpMethod() {
        return pdbBlockExpMethod;
    }

    /**
     * Sets the value of the pdbBlockExpMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPDBBlockExpMethod(String value) {
        this.pdbBlockExpMethod = value;
    }

    /**
     * Gets the value of the pdbBlockReplace property.
     * 
     * @return
     *     possible object is
     *     {@link PDBBlock.PDBBlockReplace }
     *     
     */
    public PDBBlock.PDBBlockReplace getPDBBlockReplace() {
        return pdbBlockReplace;
    }

    /**
     * Sets the value of the pdbBlockReplace property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBBlock.PDBBlockReplace }
     *     
     */
    public void setPDBBlockReplace(PDBBlock.PDBBlockReplace value) {
        this.pdbBlockReplace = value;
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
     *         &lt;element name="PDB-block_compound_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "pdbBlockCompoundE"
    })
    public static class PDBBlockCompound {

        @XmlElement(name = "PDB-block_compound_E")
        protected List<String> pdbBlockCompoundE;

        /**
         * Gets the value of the pdbBlockCompoundE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pdbBlockCompoundE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPDBBlockCompoundE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPDBBlockCompoundE() {
            if (pdbBlockCompoundE == null) {
                pdbBlockCompoundE = new ArrayList<String>();
            }
            return this.pdbBlockCompoundE;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Date"/>
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
        "date"
    })
    public static class PDBBlockDeposition {

        @XmlElement(name = "Date", required = true)
        protected Date date;

        /**
         * Gets the value of the date property.
         * 
         * @return
         *     possible object is
         *     {@link Date }
         *     
         */
        public Date getDate() {
            return date;
        }

        /**
         * Sets the value of the date property.
         * 
         * @param value
         *     allowed object is
         *     {@link Date }
         *     
         */
        public void setDate(Date value) {
            this.date = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PDB-replace"/>
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
        "pdbReplace"
    })
    public static class PDBBlockReplace {

        @XmlElement(name = "PDB-replace", required = true)
        protected PDBReplace pdbReplace;

        /**
         * Gets the value of the pdbReplace property.
         * 
         * @return
         *     possible object is
         *     {@link PDBReplace }
         *     
         */
        public PDBReplace getPDBReplace() {
            return pdbReplace;
        }

        /**
         * Sets the value of the pdbReplace property.
         * 
         * @param value
         *     allowed object is
         *     {@link PDBReplace }
         *     
         */
        public void setPDBReplace(PDBReplace value) {
            this.pdbReplace = value;
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
     *         &lt;element name="PDB-block_source_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "pdbBlockSourceE"
    })
    public static class PDBBlockSource {

        @XmlElement(name = "PDB-block_source_E")
        protected List<String> pdbBlockSourceE;

        /**
         * Gets the value of the pdbBlockSourceE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pdbBlockSourceE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPDBBlockSourceE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPDBBlockSourceE() {
            if (pdbBlockSourceE == null) {
                pdbBlockSourceE = new ArrayList<String>();
            }
            return this.pdbBlockSourceE;
        }

    }

}
