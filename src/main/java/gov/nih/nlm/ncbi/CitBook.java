//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.18 at 09:24:32 PM PDT 
//


package gov.nih.nlm.ncbi;

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
 *         &lt;element name="Cit-book_title">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-book_coll" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Cit-book_authors">
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
 *         &lt;element name="Cit-book_imp">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Imprint"/>
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
    "citBookTitle",
    "citBookColl",
    "citBookAuthors",
    "citBookImp"
})
@XmlRootElement(name = "Cit-book")
public class CitBook {

    @XmlElement(name = "Cit-book_title", required = true)
    protected CitBook.CitBookTitle citBookTitle;
    @XmlElement(name = "Cit-book_coll")
    protected CitBook.CitBookColl citBookColl;
    @XmlElement(name = "Cit-book_authors", required = true)
    protected CitBook.CitBookAuthors citBookAuthors;
    @XmlElement(name = "Cit-book_imp", required = true)
    protected CitBook.CitBookImp citBookImp;

    /**
     * Gets the value of the citBookTitle property.
     * 
     * @return
     *     possible object is
     *     {@link CitBook.CitBookTitle }
     *     
     */
    public CitBook.CitBookTitle getCitBookTitle() {
        return citBookTitle;
    }

    /**
     * Sets the value of the citBookTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitBook.CitBookTitle }
     *     
     */
    public void setCitBookTitle(CitBook.CitBookTitle value) {
        this.citBookTitle = value;
    }

    /**
     * Gets the value of the citBookColl property.
     * 
     * @return
     *     possible object is
     *     {@link CitBook.CitBookColl }
     *     
     */
    public CitBook.CitBookColl getCitBookColl() {
        return citBookColl;
    }

    /**
     * Sets the value of the citBookColl property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitBook.CitBookColl }
     *     
     */
    public void setCitBookColl(CitBook.CitBookColl value) {
        this.citBookColl = value;
    }

    /**
     * Gets the value of the citBookAuthors property.
     * 
     * @return
     *     possible object is
     *     {@link CitBook.CitBookAuthors }
     *     
     */
    public CitBook.CitBookAuthors getCitBookAuthors() {
        return citBookAuthors;
    }

    /**
     * Sets the value of the citBookAuthors property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitBook.CitBookAuthors }
     *     
     */
    public void setCitBookAuthors(CitBook.CitBookAuthors value) {
        this.citBookAuthors = value;
    }

    /**
     * Gets the value of the citBookImp property.
     * 
     * @return
     *     possible object is
     *     {@link CitBook.CitBookImp }
     *     
     */
    public CitBook.CitBookImp getCitBookImp() {
        return citBookImp;
    }

    /**
     * Sets the value of the citBookImp property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitBook.CitBookImp }
     *     
     */
    public void setCitBookImp(CitBook.CitBookImp value) {
        this.citBookImp = value;
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
    public static class CitBookAuthors {

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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
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
        "title"
    })
    public static class CitBookColl {

        @XmlElement(name = "Title", required = true)
        protected Title title;

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link Title }
         *     
         */
        public Title getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link Title }
         *     
         */
        public void setTitle(Title value) {
            this.title = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Imprint"/>
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
        "imprint"
    })
    public static class CitBookImp {

        @XmlElement(name = "Imprint", required = true)
        protected Imprint imprint;

        /**
         * Gets the value of the imprint property.
         * 
         * @return
         *     possible object is
         *     {@link Imprint }
         *     
         */
        public Imprint getImprint() {
            return imprint;
        }

        /**
         * Sets the value of the imprint property.
         * 
         * @param value
         *     allowed object is
         *     {@link Imprint }
         *     
         */
        public void setImprint(Imprint value) {
            this.imprint = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Title"/>
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
        "title"
    })
    public static class CitBookTitle {

        @XmlElement(name = "Title", required = true)
        protected Title title;

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link Title }
         *     
         */
        public Title getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link Title }
         *     
         */
        public void setTitle(Title value) {
            this.title = value;
        }

    }

}
