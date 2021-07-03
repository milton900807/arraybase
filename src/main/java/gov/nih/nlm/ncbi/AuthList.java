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
 *         &lt;element name="Auth-list_names">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Auth-list_names_std">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                             &lt;element ref="{http://www.ncbi.nlm.nih.gov}Author"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Auth-list_names_ml">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                             &lt;element name="Auth-list_names_ml_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Auth-list_names_str">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                             &lt;element name="Auth-list_names_str_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Auth-list_affil" minOccurs="0">
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
    "authListNames",
    "authListAffil"
})
@XmlRootElement(name = "Auth-list")
public class AuthList {

    @XmlElement(name = "Auth-list_names", required = true)
    protected AuthList.AuthListNames authListNames;
    @XmlElement(name = "Auth-list_affil")
    protected AuthList.AuthListAffil authListAffil;

    /**
     * Gets the value of the authListNames property.
     * 
     * @return
     *     possible object is
     *     {@link AuthList.AuthListNames }
     *     
     */
    public AuthList.AuthListNames getAuthListNames() {
        return authListNames;
    }

    /**
     * Sets the value of the authListNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthList.AuthListNames }
     *     
     */
    public void setAuthListNames(AuthList.AuthListNames value) {
        this.authListNames = value;
    }

    /**
     * Gets the value of the authListAffil property.
     * 
     * @return
     *     possible object is
     *     {@link AuthList.AuthListAffil }
     *     
     */
    public AuthList.AuthListAffil getAuthListAffil() {
        return authListAffil;
    }

    /**
     * Sets the value of the authListAffil property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthList.AuthListAffil }
     *     
     */
    public void setAuthListAffil(AuthList.AuthListAffil value) {
        this.authListAffil = value;
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
    public static class AuthListAffil {

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
     *       &lt;choice>
     *         &lt;element name="Auth-list_names_std">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Author"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Auth-list_names_ml">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element name="Auth-list_names_ml_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Auth-list_names_str">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element name="Auth-list_names_str_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "authListNamesStd",
        "authListNamesMl",
        "authListNamesStr"
    })
    public static class AuthListNames {

        @XmlElement(name = "Auth-list_names_std")
        protected AuthList.AuthListNames.AuthListNamesStd authListNamesStd;
        @XmlElement(name = "Auth-list_names_ml")
        protected AuthList.AuthListNames.AuthListNamesMl authListNamesMl;
        @XmlElement(name = "Auth-list_names_str")
        protected AuthList.AuthListNames.AuthListNamesStr authListNamesStr;

        /**
         * Gets the value of the authListNamesStd property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList.AuthListNames.AuthListNamesStd }
         *     
         */
        public AuthList.AuthListNames.AuthListNamesStd getAuthListNamesStd() {
            return authListNamesStd;
        }

        /**
         * Sets the value of the authListNamesStd property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList.AuthListNames.AuthListNamesStd }
         *     
         */
        public void setAuthListNamesStd(AuthList.AuthListNames.AuthListNamesStd value) {
            this.authListNamesStd = value;
        }

        /**
         * Gets the value of the authListNamesMl property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList.AuthListNames.AuthListNamesMl }
         *     
         */
        public AuthList.AuthListNames.AuthListNamesMl getAuthListNamesMl() {
            return authListNamesMl;
        }

        /**
         * Sets the value of the authListNamesMl property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList.AuthListNames.AuthListNamesMl }
         *     
         */
        public void setAuthListNamesMl(AuthList.AuthListNames.AuthListNamesMl value) {
            this.authListNamesMl = value;
        }

        /**
         * Gets the value of the authListNamesStr property.
         * 
         * @return
         *     possible object is
         *     {@link AuthList.AuthListNames.AuthListNamesStr }
         *     
         */
        public AuthList.AuthListNames.AuthListNamesStr getAuthListNamesStr() {
            return authListNamesStr;
        }

        /**
         * Sets the value of the authListNamesStr property.
         * 
         * @param value
         *     allowed object is
         *     {@link AuthList.AuthListNames.AuthListNamesStr }
         *     
         */
        public void setAuthListNamesStr(AuthList.AuthListNames.AuthListNamesStr value) {
            this.authListNamesStr = value;
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
         *         &lt;element name="Auth-list_names_ml_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "authListNamesMlE"
        })
        public static class AuthListNamesMl {

            @XmlElement(name = "Auth-list_names_ml_E")
            protected List<String> authListNamesMlE;

            /**
             * Gets the value of the authListNamesMlE property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the authListNamesMlE property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAuthListNamesMlE().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getAuthListNamesMlE() {
                if (authListNamesMlE == null) {
                    authListNamesMlE = new ArrayList<String>();
                }
                return this.authListNamesMlE;
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
         *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Author"/>
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
            "author"
        })
        public static class AuthListNamesStd {

            @XmlElement(name = "Author")
            protected List<Author> author;

            /**
             * Gets the value of the author property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the author property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAuthor().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Author }
             * 
             * 
             */
            public List<Author> getAuthor() {
                if (author == null) {
                    author = new ArrayList<Author>();
                }
                return this.author;
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
         *         &lt;element name="Auth-list_names_str_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "authListNamesStrE"
        })
        public static class AuthListNamesStr {

            @XmlElement(name = "Auth-list_names_str_E")
            protected List<String> authListNamesStrE;

            /**
             * Gets the value of the authListNamesStrE property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the authListNamesStrE property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAuthListNamesStrE().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getAuthListNamesStrE() {
                if (authListNamesStrE == null) {
                    authListNamesStrE = new ArrayList<String>();
                }
                return this.authListNamesStrE;
            }

        }

    }

}
