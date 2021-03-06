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
 *         &lt;element name="CommonString-table_strings">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="CommonString-table_strings_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CommonString-table_indexes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="CommonString-table_indexes_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
    "commonStringTableStrings",
    "commonStringTableIndexes"
})
@XmlRootElement(name = "CommonString-table")
public class CommonStringTable {

    @XmlElement(name = "CommonString-table_strings", required = true)
    protected CommonStringTable.CommonStringTableStrings commonStringTableStrings;
    @XmlElement(name = "CommonString-table_indexes", required = true)
    protected CommonStringTable.CommonStringTableIndexes commonStringTableIndexes;

    /**
     * Gets the value of the commonStringTableStrings property.
     * 
     * @return
     *     possible object is
     *     {@link CommonStringTable.CommonStringTableStrings }
     *     
     */
    public CommonStringTable.CommonStringTableStrings getCommonStringTableStrings() {
        return commonStringTableStrings;
    }

    /**
     * Sets the value of the commonStringTableStrings property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonStringTable.CommonStringTableStrings }
     *     
     */
    public void setCommonStringTableStrings(CommonStringTable.CommonStringTableStrings value) {
        this.commonStringTableStrings = value;
    }

    /**
     * Gets the value of the commonStringTableIndexes property.
     * 
     * @return
     *     possible object is
     *     {@link CommonStringTable.CommonStringTableIndexes }
     *     
     */
    public CommonStringTable.CommonStringTableIndexes getCommonStringTableIndexes() {
        return commonStringTableIndexes;
    }

    /**
     * Sets the value of the commonStringTableIndexes property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonStringTable.CommonStringTableIndexes }
     *     
     */
    public void setCommonStringTableIndexes(CommonStringTable.CommonStringTableIndexes value) {
        this.commonStringTableIndexes = value;
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
     *         &lt;element name="CommonString-table_indexes_E" type="{http://www.w3.org/2001/XMLSchema}integer"/>
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
        "commonStringTableIndexesE"
    })
    public static class CommonStringTableIndexes {

        @XmlElement(name = "CommonString-table_indexes_E")
        protected List<BigInteger> commonStringTableIndexesE;

        /**
         * Gets the value of the commonStringTableIndexesE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the commonStringTableIndexesE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCommonStringTableIndexesE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * 
         * 
         */
        public List<BigInteger> getCommonStringTableIndexesE() {
            if (commonStringTableIndexesE == null) {
                commonStringTableIndexesE = new ArrayList<BigInteger>();
            }
            return this.commonStringTableIndexesE;
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
     *         &lt;element name="CommonString-table_strings_E" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "commonStringTableStringsE"
    })
    public static class CommonStringTableStrings {

        @XmlElement(name = "CommonString-table_strings_E")
        protected List<String> commonStringTableStringsE;

        /**
         * Gets the value of the commonStringTableStringsE property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the commonStringTableStringsE property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCommonStringTableStringsE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getCommonStringTableStringsE() {
            if (commonStringTableStringsE == null) {
                commonStringTableStringsE = new ArrayList<String>();
            }
            return this.commonStringTableStringsE;
        }

    }

}
