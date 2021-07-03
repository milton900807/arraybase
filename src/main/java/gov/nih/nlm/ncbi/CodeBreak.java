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
 *         &lt;element name="Code-break_loc">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-loc"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Code-break_aa">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Code-break_aa_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Code-break_aa_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="Code-break_aa_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
    "codeBreakLoc",
    "codeBreakAa"
})
@XmlRootElement(name = "Code-break")
public class CodeBreak {

    @XmlElement(name = "Code-break_loc", required = true)
    protected CodeBreak.CodeBreakLoc codeBreakLoc;
    @XmlElement(name = "Code-break_aa", required = true)
    protected CodeBreak.CodeBreakAa codeBreakAa;

    /**
     * Gets the value of the codeBreakLoc property.
     * 
     * @return
     *     possible object is
     *     {@link CodeBreak.CodeBreakLoc }
     *     
     */
    public CodeBreak.CodeBreakLoc getCodeBreakLoc() {
        return codeBreakLoc;
    }

    /**
     * Sets the value of the codeBreakLoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeBreak.CodeBreakLoc }
     *     
     */
    public void setCodeBreakLoc(CodeBreak.CodeBreakLoc value) {
        this.codeBreakLoc = value;
    }

    /**
     * Gets the value of the codeBreakAa property.
     * 
     * @return
     *     possible object is
     *     {@link CodeBreak.CodeBreakAa }
     *     
     */
    public CodeBreak.CodeBreakAa getCodeBreakAa() {
        return codeBreakAa;
    }

    /**
     * Sets the value of the codeBreakAa property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeBreak.CodeBreakAa }
     *     
     */
    public void setCodeBreakAa(CodeBreak.CodeBreakAa value) {
        this.codeBreakAa = value;
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
     *         &lt;element name="Code-break_aa_ncbieaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Code-break_aa_ncbi8aa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="Code-break_aa_ncbistdaa" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "codeBreakAaNcbieaa",
        "codeBreakAaNcbi8Aa",
        "codeBreakAaNcbistdaa"
    })
    public static class CodeBreakAa {

        @XmlElement(name = "Code-break_aa_ncbieaa")
        protected BigInteger codeBreakAaNcbieaa;
        @XmlElement(name = "Code-break_aa_ncbi8aa")
        protected BigInteger codeBreakAaNcbi8Aa;
        @XmlElement(name = "Code-break_aa_ncbistdaa")
        protected BigInteger codeBreakAaNcbistdaa;

        /**
         * Gets the value of the codeBreakAaNcbieaa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getCodeBreakAaNcbieaa() {
            return codeBreakAaNcbieaa;
        }

        /**
         * Sets the value of the codeBreakAaNcbieaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setCodeBreakAaNcbieaa(BigInteger value) {
            this.codeBreakAaNcbieaa = value;
        }

        /**
         * Gets the value of the codeBreakAaNcbi8Aa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getCodeBreakAaNcbi8Aa() {
            return codeBreakAaNcbi8Aa;
        }

        /**
         * Sets the value of the codeBreakAaNcbi8Aa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setCodeBreakAaNcbi8Aa(BigInteger value) {
            this.codeBreakAaNcbi8Aa = value;
        }

        /**
         * Gets the value of the codeBreakAaNcbistdaa property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getCodeBreakAaNcbistdaa() {
            return codeBreakAaNcbistdaa;
        }

        /**
         * Sets the value of the codeBreakAaNcbistdaa property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setCodeBreakAaNcbistdaa(BigInteger value) {
            this.codeBreakAaNcbistdaa = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Seq-loc"/>
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
        "seqLoc"
    })
    public static class CodeBreakLoc {

        @XmlElement(name = "Seq-loc", required = true)
        protected SeqLoc seqLoc;

        /**
         * Gets the value of the seqLoc property.
         * 
         * @return
         *     possible object is
         *     {@link SeqLoc }
         *     
         */
        public SeqLoc getSeqLoc() {
            return seqLoc;
        }

        /**
         * Sets the value of the seqLoc property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqLoc }
         *     
         */
        public void setSeqLoc(SeqLoc value) {
            this.seqLoc = value;
        }

    }

}