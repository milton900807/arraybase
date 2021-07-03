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
 *         &lt;element name="Seq-graph_title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Seq-graph_comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Seq-graph_loc">
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
 *         &lt;element name="Seq-graph_title-x" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Seq-graph_title-y" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Seq-graph_comp" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="Seq-graph_a" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Seq-graph_b" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Seq-graph_numval" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Seq-graph_graph">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Seq-graph_graph_real">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.ncbi.nlm.nih.gov}Real-graph"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Seq-graph_graph_int">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.ncbi.nlm.nih.gov}Int-graph"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Seq-graph_graph_byte">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.ncbi.nlm.nih.gov}Byte-graph"/>
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
    "seqGraphTitle",
    "seqGraphComment",
    "seqGraphLoc",
    "seqGraphTitleX",
    "seqGraphTitleY",
    "seqGraphComp",
    "seqGraphA",
    "seqGraphB",
    "seqGraphNumval",
    "seqGraphGraph"
})
@XmlRootElement(name = "Seq-graph")
public class SeqGraph {

    @XmlElement(name = "Seq-graph_title")
    protected String seqGraphTitle;
    @XmlElement(name = "Seq-graph_comment")
    protected String seqGraphComment;
    @XmlElement(name = "Seq-graph_loc", required = true)
    protected SeqGraph.SeqGraphLoc seqGraphLoc;
    @XmlElement(name = "Seq-graph_title-x")
    protected String seqGraphTitleX;
    @XmlElement(name = "Seq-graph_title-y")
    protected String seqGraphTitleY;
    @XmlElement(name = "Seq-graph_comp")
    protected BigInteger seqGraphComp;
    @XmlElement(name = "Seq-graph_a")
    protected Double seqGraphA;
    @XmlElement(name = "Seq-graph_b")
    protected Double seqGraphB;
    @XmlElement(name = "Seq-graph_numval", required = true)
    protected BigInteger seqGraphNumval;
    @XmlElement(name = "Seq-graph_graph", required = true)
    protected SeqGraph.SeqGraphGraph seqGraphGraph;

    /**
     * Gets the value of the seqGraphTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqGraphTitle() {
        return seqGraphTitle;
    }

    /**
     * Sets the value of the seqGraphTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqGraphTitle(String value) {
        this.seqGraphTitle = value;
    }

    /**
     * Gets the value of the seqGraphComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqGraphComment() {
        return seqGraphComment;
    }

    /**
     * Sets the value of the seqGraphComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqGraphComment(String value) {
        this.seqGraphComment = value;
    }

    /**
     * Gets the value of the seqGraphLoc property.
     * 
     * @return
     *     possible object is
     *     {@link SeqGraph.SeqGraphLoc }
     *     
     */
    public SeqGraph.SeqGraphLoc getSeqGraphLoc() {
        return seqGraphLoc;
    }

    /**
     * Sets the value of the seqGraphLoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqGraph.SeqGraphLoc }
     *     
     */
    public void setSeqGraphLoc(SeqGraph.SeqGraphLoc value) {
        this.seqGraphLoc = value;
    }

    /**
     * Gets the value of the seqGraphTitleX property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqGraphTitleX() {
        return seqGraphTitleX;
    }

    /**
     * Sets the value of the seqGraphTitleX property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqGraphTitleX(String value) {
        this.seqGraphTitleX = value;
    }

    /**
     * Gets the value of the seqGraphTitleY property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqGraphTitleY() {
        return seqGraphTitleY;
    }

    /**
     * Sets the value of the seqGraphTitleY property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqGraphTitleY(String value) {
        this.seqGraphTitleY = value;
    }

    /**
     * Gets the value of the seqGraphComp property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeqGraphComp() {
        return seqGraphComp;
    }

    /**
     * Sets the value of the seqGraphComp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeqGraphComp(BigInteger value) {
        this.seqGraphComp = value;
    }

    /**
     * Gets the value of the seqGraphA property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSeqGraphA() {
        return seqGraphA;
    }

    /**
     * Sets the value of the seqGraphA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSeqGraphA(Double value) {
        this.seqGraphA = value;
    }

    /**
     * Gets the value of the seqGraphB property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSeqGraphB() {
        return seqGraphB;
    }

    /**
     * Sets the value of the seqGraphB property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSeqGraphB(Double value) {
        this.seqGraphB = value;
    }

    /**
     * Gets the value of the seqGraphNumval property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeqGraphNumval() {
        return seqGraphNumval;
    }

    /**
     * Sets the value of the seqGraphNumval property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeqGraphNumval(BigInteger value) {
        this.seqGraphNumval = value;
    }

    /**
     * Gets the value of the seqGraphGraph property.
     * 
     * @return
     *     possible object is
     *     {@link SeqGraph.SeqGraphGraph }
     *     
     */
    public SeqGraph.SeqGraphGraph getSeqGraphGraph() {
        return seqGraphGraph;
    }

    /**
     * Sets the value of the seqGraphGraph property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeqGraph.SeqGraphGraph }
     *     
     */
    public void setSeqGraphGraph(SeqGraph.SeqGraphGraph value) {
        this.seqGraphGraph = value;
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
     *         &lt;element name="Seq-graph_graph_real">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Real-graph"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Seq-graph_graph_int">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Int-graph"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Seq-graph_graph_byte">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.ncbi.nlm.nih.gov}Byte-graph"/>
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
        "seqGraphGraphReal",
        "seqGraphGraphInt",
        "seqGraphGraphByte"
    })
    public static class SeqGraphGraph {

        @XmlElement(name = "Seq-graph_graph_real")
        protected SeqGraph.SeqGraphGraph.SeqGraphGraphReal seqGraphGraphReal;
        @XmlElement(name = "Seq-graph_graph_int")
        protected SeqGraph.SeqGraphGraph.SeqGraphGraphInt seqGraphGraphInt;
        @XmlElement(name = "Seq-graph_graph_byte")
        protected SeqGraph.SeqGraphGraph.SeqGraphGraphByte seqGraphGraphByte;

        /**
         * Gets the value of the seqGraphGraphReal property.
         * 
         * @return
         *     possible object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphReal }
         *     
         */
        public SeqGraph.SeqGraphGraph.SeqGraphGraphReal getSeqGraphGraphReal() {
            return seqGraphGraphReal;
        }

        /**
         * Sets the value of the seqGraphGraphReal property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphReal }
         *     
         */
        public void setSeqGraphGraphReal(SeqGraph.SeqGraphGraph.SeqGraphGraphReal value) {
            this.seqGraphGraphReal = value;
        }

        /**
         * Gets the value of the seqGraphGraphInt property.
         * 
         * @return
         *     possible object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphInt }
         *     
         */
        public SeqGraph.SeqGraphGraph.SeqGraphGraphInt getSeqGraphGraphInt() {
            return seqGraphGraphInt;
        }

        /**
         * Sets the value of the seqGraphGraphInt property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphInt }
         *     
         */
        public void setSeqGraphGraphInt(SeqGraph.SeqGraphGraph.SeqGraphGraphInt value) {
            this.seqGraphGraphInt = value;
        }

        /**
         * Gets the value of the seqGraphGraphByte property.
         * 
         * @return
         *     possible object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphByte }
         *     
         */
        public SeqGraph.SeqGraphGraph.SeqGraphGraphByte getSeqGraphGraphByte() {
            return seqGraphGraphByte;
        }

        /**
         * Sets the value of the seqGraphGraphByte property.
         * 
         * @param value
         *     allowed object is
         *     {@link SeqGraph.SeqGraphGraph.SeqGraphGraphByte }
         *     
         */
        public void setSeqGraphGraphByte(SeqGraph.SeqGraphGraph.SeqGraphGraphByte value) {
            this.seqGraphGraphByte = value;
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
         *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Byte-graph"/>
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
            "byteGraph"
        })
        public static class SeqGraphGraphByte {

            @XmlElement(name = "Byte-graph", required = true)
            protected ByteGraph byteGraph;

            /**
             * Gets the value of the byteGraph property.
             * 
             * @return
             *     possible object is
             *     {@link ByteGraph }
             *     
             */
            public ByteGraph getByteGraph() {
                return byteGraph;
            }

            /**
             * Sets the value of the byteGraph property.
             * 
             * @param value
             *     allowed object is
             *     {@link ByteGraph }
             *     
             */
            public void setByteGraph(ByteGraph value) {
                this.byteGraph = value;
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
         *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Int-graph"/>
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
            "intGraph"
        })
        public static class SeqGraphGraphInt {

            @XmlElement(name = "Int-graph", required = true)
            protected IntGraph intGraph;

            /**
             * Gets the value of the intGraph property.
             * 
             * @return
             *     possible object is
             *     {@link IntGraph }
             *     
             */
            public IntGraph getIntGraph() {
                return intGraph;
            }

            /**
             * Sets the value of the intGraph property.
             * 
             * @param value
             *     allowed object is
             *     {@link IntGraph }
             *     
             */
            public void setIntGraph(IntGraph value) {
                this.intGraph = value;
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
         *         &lt;element ref="{http://www.ncbi.nlm.nih.gov}Real-graph"/>
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
            "realGraph"
        })
        public static class SeqGraphGraphReal {

            @XmlElement(name = "Real-graph", required = true)
            protected RealGraph realGraph;

            /**
             * Gets the value of the realGraph property.
             * 
             * @return
             *     possible object is
             *     {@link RealGraph }
             *     
             */
            public RealGraph getRealGraph() {
                return realGraph;
            }

            /**
             * Sets the value of the realGraph property.
             * 
             * @param value
             *     allowed object is
             *     {@link RealGraph }
             *     
             */
            public void setRealGraph(RealGraph value) {
                this.realGraph = value;
            }

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
    public static class SeqGraphLoc {

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