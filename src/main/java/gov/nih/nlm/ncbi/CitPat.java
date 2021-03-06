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
 *         &lt;element name="Cit-pat_title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cit-pat_authors">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-pat_country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cit-pat_doc-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cit-pat_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Cit-pat_date-issue" minOccurs="0">
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
 *         &lt;element name="Cit-pat_class" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="Cit-pat_class_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-pat_app-number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Cit-pat_app-date" minOccurs="0">
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
 *         &lt;element name="Cit-pat_applicants" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-pat_assignees" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-pat_priority" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Patent-priority"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-pat_abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "citPatTitle",
    "citPatAuthors",
    "citPatCountry",
    "citPatDocType",
    "citPatNumber",
    "citPatDateIssue",
    "citPatClass",
    "citPatAppNumber",
    "citPatAppDate",
    "citPatApplicants",
    "citPatAssignees",
    "citPatPriority",
    "citPatAbstract"
})
@XmlRootElement(name = "Cit-pat")
public class CitPat {

    @XmlElement(name = "Cit-pat_title", required = true)
    protected String citPatTitle;
    @XmlElement(name = "Cit-pat_authors", required = true)
    protected CitPat.CitPatAuthors citPatAuthors;
    @XmlElement(name = "Cit-pat_country", required = true)
    protected String citPatCountry;
    @XmlElement(name = "Cit-pat_doc-type", required = true)
    protected String citPatDocType;
    @XmlElement(name = "Cit-pat_number")
    protected String citPatNumber;
    @XmlElement(name = "Cit-pat_date-issue")
    protected CitPat.CitPatDateIssue citPatDateIssue;
    @XmlElement(name = "Cit-pat_class")
    protected CitPat.CitPatClass citPatClass;
    @XmlElement(name = "Cit-pat_app-number")
    protected String citPatAppNumber;
    @XmlElement(name = "Cit-pat_app-date")
    protected CitPat.CitPatAppDate citPatAppDate;
    @XmlElement(name = "Cit-pat_applicants")
    protected CitPat.CitPatApplicants citPatApplicants;
    @XmlElement(name = "Cit-pat_assignees")
    protected CitPat.CitPatAssignees citPatAssignees;
    @XmlElement(name = "Cit-pat_priority")
    protected CitPat.CitPatPriority citPatPriority;
    @XmlElement(name = "Cit-pat_abstract")
    protected String citPatAbstract;

    /**
     * Gets the value of the citPatTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatTitle() {
        return citPatTitle;
    }

    /**
     * Sets the value of the citPatTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatTitle(String value) {
        this.citPatTitle = value;
    }

    /**
     * Gets the value of the citPatAuthors property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatAuthors }
     *     
     */
    public CitPat.CitPatAuthors getCitPatAuthors() {
        return citPatAuthors;
    }

    /**
     * Sets the value of the citPatAuthors property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatAuthors }
     *     
     */
    public void setCitPatAuthors(CitPat.CitPatAuthors value) {
        this.citPatAuthors = value;
    }

    /**
     * Gets the value of the citPatCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatCountry() {
        return citPatCountry;
    }

    /**
     * Sets the value of the citPatCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatCountry(String value) {
        this.citPatCountry = value;
    }

    /**
     * Gets the value of the citPatDocType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatDocType() {
        return citPatDocType;
    }

    /**
     * Sets the value of the citPatDocType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatDocType(String value) {
        this.citPatDocType = value;
    }

    /**
     * Gets the value of the citPatNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatNumber() {
        return citPatNumber;
    }

    /**
     * Sets the value of the citPatNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatNumber(String value) {
        this.citPatNumber = value;
    }

    /**
     * Gets the value of the citPatDateIssue property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatDateIssue }
     *     
     */
    public CitPat.CitPatDateIssue getCitPatDateIssue() {
        return citPatDateIssue;
    }

    /**
     * Sets the value of the citPatDateIssue property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatDateIssue }
     *     
     */
    public void setCitPatDateIssue(CitPat.CitPatDateIssue value) {
        this.citPatDateIssue = value;
    }

    /**
     * Gets the value of the citPatClass property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatClass }
     *     
     */
    public CitPat.CitPatClass getCitPatClass() {
        return citPatClass;
    }

    /**
     * Sets the value of the citPatClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatClass }
     *     
     */
    public void setCitPatClass(CitPat.CitPatClass value) {
        this.citPatClass = value;
    }

    /**
     * Gets the value of the citPatAppNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatAppNumber() {
        return citPatAppNumber;
    }

    /**
     * Sets the value of the citPatAppNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatAppNumber(String value) {
        this.citPatAppNumber = value;
    }

    /**
     * Gets the value of the citPatAppDate property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatAppDate }
     *     
     */
    public CitPat.CitPatAppDate getCitPatAppDate() {
        return citPatAppDate;
    }

    /**
     * Sets the value of the citPatAppDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatAppDate }
     *     
     */
    public void setCitPatAppDate(CitPat.CitPatAppDate value) {
        this.citPatAppDate = value;
    }

    /**
     * Gets the value of the citPatApplicants property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatApplicants }
     *     
     */
    public CitPat.CitPatApplicants getCitPatApplicants() {
        return citPatApplicants;
    }

    /**
     * Sets the value of the citPatApplicants property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatApplicants }
     *     
     */
    public void setCitPatApplicants(CitPat.CitPatApplicants value) {
        this.citPatApplicants = value;
    }

    /**
     * Gets the value of the citPatAssignees property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatAssignees }
     *     
     */
    public CitPat.CitPatAssignees getCitPatAssignees() {
        return citPatAssignees;
    }

    /**
     * Sets the value of the citPatAssignees property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatAssignees }
     *     
     */
    public void setCitPatAssignees(CitPat.CitPatAssignees value) {
        this.citPatAssignees = value;
    }

    /**
     * Gets the value of the citPatPriority property.
     * 
     * @return
     *     possible object is
     *     {@link CitPat.CitPatPriority }
     *     
     */
    public CitPat.CitPatPriority getCitPatPriority() {
        return citPatPriority;
    }

    /**
     * Sets the value of the citPatPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitPat.CitPatPriority }
     *     
     */
    public void setCitPatPriority(CitPat.CitPatPriority value) {
        this.citPatPriority = value;
    }

    /**
     * Gets the value of the citPatAbstract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPatAbstract() {
        return citPatAbstract;
    }

    /**
     * Sets the value of the citPatAbstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPatAbstract(String value) {
        this.citPatAbstract = value;
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
    public static class CitPatAppDate {

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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
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
        "authList"
    })
    public static class CitPatApplicants {

        @XmlElement(name = "Auth-list", required = true)
        protected AuthList authList;

        /**
         * Gets the value of the authList property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList }
         *     
         */
        public AuthList getAuthList() {
            return authList;
        }

        /**
         * Sets the value of the authList property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList }
         *     
         */
        public void setAuthList(AuthList value) {
            this.authList = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
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
        "authList"
    })
    public static class CitPatAssignees {

        @XmlElement(name = "Auth-list", required = true)
        protected AuthList authList;

        /**
         * Gets the value of the authList property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList }
         *     
         */
        public AuthList getAuthList() {
            return authList;
        }

        /**
         * Sets the value of the authList property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList }
         *     
         */
        public void setAuthList(AuthList value) {
            this.authList = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Auth-list"/>
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
        "authList"
    })
    public static class CitPatAuthors {

        @XmlElement(name = "Auth-list", required = true)
        protected AuthList authList;

        /**
         * Gets the value of the authList property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList }
         *     
         */
        public AuthList getAuthList() {
            return authList;
        }

        /**
         * Sets the value of the authList property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList }
         *     
         */
        public void setAuthList(AuthList value) {
            this.authList = value;
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
     *         &lt;element name="Cit-pat_class_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "citPatClassE"
    })
    public static class CitPatClass {

        @XmlElement(name = "Cit-pat_class_E")
        protected List<String> citPatClassE;

        /**
         * Gets the value of the citPatClassE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the citPatClassE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCitPatClassE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getCitPatClassE() {
            if (citPatClassE == null) {
                citPatClassE = new ArrayList<String>();
            }
            return this.citPatClassE;
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
    public static class CitPatDateIssue {

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
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Patent-priority"/>
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
        "patentPriority"
    })
    public static class CitPatPriority {

        @XmlElement(name = "Patent-priority")
        protected List<PatentPriority> patentPriority;

        /**
         * Gets the value of the patentPriority property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the patentPriority property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPatentPriority().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PatentPriority }
         * 
         * 
         */
        public List<PatentPriority> getPatentPriority() {
            if (patentPriority == null) {
                patentPriority = new ArrayList<PatentPriority>();
            }
            return this.patentPriority;
        }

    }

}
