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
 *         &lt;element name="Author_name">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Person-id"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Author_level" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="primary"/>
 *                       &lt;enumeration value="secondary"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Author_role" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="compiler"/>
 *                       &lt;enumeration value="editor"/>
 *                       &lt;enumeration value="patent-assignee"/>
 *                       &lt;enumeration value="translator"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Author_affil" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Affil"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Author_is-corr" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="true"/>
 *                       &lt;enumeration value="false"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
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
    "authorName",
    "authorLevel",
    "authorRole",
    "authorAffil",
    "authorIsCorr"
})
@XmlRootElement(name = "Author")
public class Author {

    @XmlElement(name = "Author_name", required = true)
    protected Author.AuthorName authorName;
    @XmlElement(name = "Author_level")
    protected Author.AuthorLevel authorLevel;
    @XmlElement(name = "Author_role")
    protected Author.AuthorRole authorRole;
    @XmlElement(name = "Author_affil")
    protected Author.AuthorAffil authorAffil;
    @XmlElement(name = "Author_is-corr")
    protected Author.AuthorIsCorr authorIsCorr;

    /**
     * Gets the value of the authorName property.
     * 
     * @return
     *     possible object is
     *     {@link Author.AuthorName }
     *     
     */
    public Author.AuthorName getAuthorName() {
        return authorName;
    }

    /**
     * Sets the value of the authorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Author.AuthorName }
     *     
     */
    public void setAuthorName(Author.AuthorName value) {
        this.authorName = value;
    }

    /**
     * Gets the value of the authorLevel property.
     * 
     * @return
     *     possible object is
     *     {@link Author.AuthorLevel }
     *     
     */
    public Author.AuthorLevel getAuthorLevel() {
        return authorLevel;
    }

    /**
     * Sets the value of the authorLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Author.AuthorLevel }
     *     
     */
    public void setAuthorLevel(Author.AuthorLevel value) {
        this.authorLevel = value;
    }

    /**
     * Gets the value of the authorRole property.
     * 
     * @return
     *     possible object is
     *     {@link Author.AuthorRole }
     *     
     */
    public Author.AuthorRole getAuthorRole() {
        return authorRole;
    }

    /**
     * Sets the value of the authorRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link Author.AuthorRole }
     *     
     */
    public void setAuthorRole(Author.AuthorRole value) {
        this.authorRole = value;
    }

    /**
     * Gets the value of the authorAffil property.
     * 
     * @return
     *     possible object is
     *     {@link Author.AuthorAffil }
     *     
     */
    public Author.AuthorAffil getAuthorAffil() {
        return authorAffil;
    }

    /**
     * Sets the value of the authorAffil property.
     * 
     * @param value
     *     allowed object is
     *     {@link Author.AuthorAffil }
     *     
     */
    public void setAuthorAffil(Author.AuthorAffil value) {
        this.authorAffil = value;
    }

    /**
     * Gets the value of the authorIsCorr property.
     * 
     * @return
     *     possible object is
     *     {@link Author.AuthorIsCorr }
     *     
     */
    public Author.AuthorIsCorr getAuthorIsCorr() {
        return authorIsCorr;
    }

    /**
     * Sets the value of the authorIsCorr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Author.AuthorIsCorr }
     *     
     */
    public void setAuthorIsCorr(Author.AuthorIsCorr value) {
        this.authorIsCorr = value;
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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Affil"/>
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
        "affil"
    })
    public static class AuthorAffil {

        @XmlElement(name = "Affil", required = true)
        protected Affil affil;

        /**
         * Gets the value of the affil property.
         * 
         * @return
         *     possible object is
         *     {@link Affil }
         *     
         */
        public Affil getAffil() {
            return affil;
        }

        /**
         * Sets the value of the affil property.
         * 
         * @param value
         *     allowed object is
         *     {@link Affil }
         *     
         */
        public void setAffil(Affil value) {
            this.affil = value;
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
     *             &lt;enumeration value="true"/>
     *             &lt;enumeration value="false"/>
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
    public static class AuthorIsCorr {

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
     *             &lt;enumeration value="primary"/>
     *             &lt;enumeration value="secondary"/>
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
    public static class AuthorLevel {

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
     *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Person-id"/>
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
        "personId"
    })
    public static class AuthorName {

        @XmlElement(name = "Person-id", required = true)
        protected PersonId personId;

        /**
         * Gets the value of the personId property.
         * 
         * @return
         *     possible object is
         *     {@link PersonId }
         *     
         */
        public PersonId getPersonId() {
            return personId;
        }

        /**
         * Sets the value of the personId property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonId }
         *     
         */
        public void setPersonId(PersonId value) {
            this.personId = value;
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
     *             &lt;enumeration value="compiler"/>
     *             &lt;enumeration value="editor"/>
     *             &lt;enumeration value="patent-assignee"/>
     *             &lt;enumeration value="translator"/>
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
    public static class AuthorRole {

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