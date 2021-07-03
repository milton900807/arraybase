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
 *       &lt;choice>
 *         &lt;element name="ArticleId_pubmed">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PubMedId"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_medline">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}MedlineUID"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_doi">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}DOI"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_pii">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PII"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_pmcid">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmcID"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_pmcpid">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmcPid"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_pmpid">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmPid"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ArticleId_other">
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
    "articleIdPubmed",
    "articleIdMedline",
    "articleIdDoi",
    "articleIdPii",
    "articleIdPmcid",
    "articleIdPmcpid",
    "articleIdPmpid",
    "articleIdOther"
})
@XmlRootElement(name = "ArticleId")
public class ArticleId {

    @XmlElement(name = "ArticleId_pubmed")
    protected ArticleId.ArticleIdPubmed articleIdPubmed;
    @XmlElement(name = "ArticleId_medline")
    protected ArticleId.ArticleIdMedline articleIdMedline;
    @XmlElement(name = "ArticleId_doi")
    protected ArticleId.ArticleIdDoi articleIdDoi;
    @XmlElement(name = "ArticleId_pii")
    protected ArticleId.ArticleIdPii articleIdPii;
    @XmlElement(name = "ArticleId_pmcid")
    protected ArticleId.ArticleIdPmcid articleIdPmcid;
    @XmlElement(name = "ArticleId_pmcpid")
    protected ArticleId.ArticleIdPmcpid articleIdPmcpid;
    @XmlElement(name = "ArticleId_pmpid")
    protected ArticleId.ArticleIdPmpid articleIdPmpid;
    @XmlElement(name = "ArticleId_other")
    protected ArticleId.ArticleIdOther articleIdOther;

    /**
     * Gets the value of the articleIdPubmed property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdPubmed }
     *     
     */
    public ArticleId.ArticleIdPubmed getArticleIdPubmed() {
        return articleIdPubmed;
    }

    /**
     * Sets the value of the articleIdPubmed property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdPubmed }
     *     
     */
    public void setArticleIdPubmed(ArticleId.ArticleIdPubmed value) {
        this.articleIdPubmed = value;
    }

    /**
     * Gets the value of the articleIdMedline property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdMedline }
     *     
     */
    public ArticleId.ArticleIdMedline getArticleIdMedline() {
        return articleIdMedline;
    }

    /**
     * Sets the value of the articleIdMedline property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdMedline }
     *     
     */
    public void setArticleIdMedline(ArticleId.ArticleIdMedline value) {
        this.articleIdMedline = value;
    }

    /**
     * Gets the value of the articleIdDoi property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdDoi }
     *     
     */
    public ArticleId.ArticleIdDoi getArticleIdDoi() {
        return articleIdDoi;
    }

    /**
     * Sets the value of the articleIdDoi property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdDoi }
     *     
     */
    public void setArticleIdDoi(ArticleId.ArticleIdDoi value) {
        this.articleIdDoi = value;
    }

    /**
     * Gets the value of the articleIdPii property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdPii }
     *     
     */
    public ArticleId.ArticleIdPii getArticleIdPii() {
        return articleIdPii;
    }

    /**
     * Sets the value of the articleIdPii property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdPii }
     *     
     */
    public void setArticleIdPii(ArticleId.ArticleIdPii value) {
        this.articleIdPii = value;
    }

    /**
     * Gets the value of the articleIdPmcid property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdPmcid }
     *     
     */
    public ArticleId.ArticleIdPmcid getArticleIdPmcid() {
        return articleIdPmcid;
    }

    /**
     * Sets the value of the articleIdPmcid property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdPmcid }
     *     
     */
    public void setArticleIdPmcid(ArticleId.ArticleIdPmcid value) {
        this.articleIdPmcid = value;
    }

    /**
     * Gets the value of the articleIdPmcpid property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdPmcpid }
     *     
     */
    public ArticleId.ArticleIdPmcpid getArticleIdPmcpid() {
        return articleIdPmcpid;
    }

    /**
     * Sets the value of the articleIdPmcpid property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdPmcpid }
     *     
     */
    public void setArticleIdPmcpid(ArticleId.ArticleIdPmcpid value) {
        this.articleIdPmcpid = value;
    }

    /**
     * Gets the value of the articleIdPmpid property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdPmpid }
     *     
     */
    public ArticleId.ArticleIdPmpid getArticleIdPmpid() {
        return articleIdPmpid;
    }

    /**
     * Sets the value of the articleIdPmpid property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdPmpid }
     *     
     */
    public void setArticleIdPmpid(ArticleId.ArticleIdPmpid value) {
        this.articleIdPmpid = value;
    }

    /**
     * Gets the value of the articleIdOther property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleId.ArticleIdOther }
     *     
     */
    public ArticleId.ArticleIdOther getArticleIdOther() {
        return articleIdOther;
    }

    /**
     * Sets the value of the articleIdOther property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleId.ArticleIdOther }
     *     
     */
    public void setArticleIdOther(ArticleId.ArticleIdOther value) {
        this.articleIdOther = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}DOI"/>
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
        "doi"
    })
    public static class ArticleIdDoi {

        @XmlElement(name = "DOI", required = true)
        protected String doi;

        /**
         * Gets the value of the doi property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDOI() {
            return doi;
        }

        /**
         * Sets the value of the doi property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDOI(String value) {
            this.doi = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}MedlineUID"/>
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
        "medlineUID"
    })
    public static class ArticleIdMedline {

        @XmlElement(name = "MedlineUID", required = true)
        protected BigInteger medlineUID;

        /**
         * Gets the value of the medlineUID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMedlineUID() {
            return medlineUID;
        }

        /**
         * Sets the value of the medlineUID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMedlineUID(BigInteger value) {
            this.medlineUID = value;
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
    public static class ArticleIdOther {

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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PII"/>
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
        "pii"
    })
    public static class ArticleIdPii {

        @XmlElement(name = "PII", required = true)
        protected String pii;

        /**
         * Gets the value of the pii property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPII() {
            return pii;
        }

        /**
         * Sets the value of the pii property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPII(String value) {
            this.pii = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmcID"/>
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
        "pmcID"
    })
    public static class ArticleIdPmcid {

        @XmlElement(name = "PmcID", required = true)
        protected BigInteger pmcID;

        /**
         * Gets the value of the pmcID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getPmcID() {
            return pmcID;
        }

        /**
         * Sets the value of the pmcID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setPmcID(BigInteger value) {
            this.pmcID = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmcPid"/>
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
        "pmcPid"
    })
    public static class ArticleIdPmcpid {

        @XmlElement(name = "PmcPid", required = true)
        protected String pmcPid;

        /**
         * Gets the value of the pmcPid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPmcPid() {
            return pmcPid;
        }

        /**
         * Sets the value of the pmcPid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPmcPid(String value) {
            this.pmcPid = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PmPid"/>
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
        "pmPid"
    })
    public static class ArticleIdPmpid {

        @XmlElement(name = "PmPid", required = true)
        protected String pmPid;

        /**
         * Gets the value of the pmPid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPmPid() {
            return pmPid;
        }

        /**
         * Sets the value of the pmPid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPmPid(String value) {
            this.pmPid = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}PubMedId"/>
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
        "pubMedId"
    })
    public static class ArticleIdPubmed {

        @XmlElement(name = "PubMedId", required = true)
        protected BigInteger pubMedId;

        /**
         * Gets the value of the pubMedId property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getPubMedId() {
            return pubMedId;
        }

        /**
         * Sets the value of the pubMedId property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setPubMedId(BigInteger value) {
            this.pubMedId = value;
        }

    }

}
