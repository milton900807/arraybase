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
 *         &lt;element name="PDB-seq-id_mol">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PDB-mol-id"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PDB-seq-id_chain" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="PDB-seq-id_rel" minOccurs="0">
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
    "pdbSeqIdMol",
    "pdbSeqIdChain",
    "pdbSeqIdRel"
})
@XmlRootElement(name = "PDB-seq-id")
public class PDBSeqId {

    @XmlElement(name = "PDB-seq-id_mol", required = true)
    protected PDBSeqId.PDBSeqIdMol pdbSeqIdMol;
    @XmlElement(name = "PDB-seq-id_chain", required = true, defaultValue = "32")
    protected BigInteger pdbSeqIdChain;
    @XmlElement(name = "PDB-seq-id_rel")
    protected PDBSeqId.PDBSeqIdRel pdbSeqIdRel;

    /**
     * Gets the value of the pdbSeqIdMol property.
     * 
     * @return
     *     possible object is
     *     {@link PDBSeqId.PDBSeqIdMol }
     *     
     */
    public PDBSeqId.PDBSeqIdMol getPDBSeqIdMol() {
        return pdbSeqIdMol;
    }

    /**
     * Sets the value of the pdbSeqIdMol property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBSeqId.PDBSeqIdMol }
     *     
     */
    public void setPDBSeqIdMol(PDBSeqId.PDBSeqIdMol value) {
        this.pdbSeqIdMol = value;
    }

    /**
     * Gets the value of the pdbSeqIdChain property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPDBSeqIdChain() {
        return pdbSeqIdChain;
    }

    /**
     * Sets the value of the pdbSeqIdChain property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPDBSeqIdChain(BigInteger value) {
        this.pdbSeqIdChain = value;
    }

    /**
     * Gets the value of the pdbSeqIdRel property.
     * 
     * @return
     *     possible object is
     *     {@link PDBSeqId.PDBSeqIdRel }
     *     
     */
    public PDBSeqId.PDBSeqIdRel getPDBSeqIdRel() {
        return pdbSeqIdRel;
    }

    /**
     * Sets the value of the pdbSeqIdRel property.
     * 
     * @param value
     *     allowed object is
     *     {@link PDBSeqId.PDBSeqIdRel }
     *     
     */
    public void setPDBSeqIdRel(PDBSeqId.PDBSeqIdRel value) {
        this.pdbSeqIdRel = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PDB-mol-id"/>
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
        "pdbMolId"
    })
    public static class PDBSeqIdMol {

        @XmlElement(name = "PDB-mol-id", required = true)
        protected String pdbMolId;

        /**
         * Gets the value of the pdbMolId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPDBMolId() {
            return pdbMolId;
        }

        /**
         * Sets the value of the pdbMolId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPDBMolId(String value) {
            this.pdbMolId = value;
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
    public static class PDBSeqIdRel {

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

}
