package com.arraybase.flare;

import java.util.ArrayList;
import java.util.HashMap;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class PatternHandler extends org.xml.sax.helpers.DefaultHandler {

		HashMap nameToClass; // Map from servlet name to servlet class name
		HashMap nameToPatterns; // Map from servlet name to url patterns

		// private HashMap<String, String> data = new HashMap<String, String>();
		private ArrayList<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>();

		StringBuffer accumulator; // Accumulate text
		String servletName, servletClass, servletPattern; // Remember text

		// Called at the beginning of parsing. We use it as an init() method
		public void startDocument() {
			accumulator = new StringBuffer();
			nameToClass = new HashMap();
			nameToPatterns = new HashMap();
		}

		// When the parser encounters plain text (not XML elements), it calls
		// this method, which accumulates them in a string buffer.
		// Note that this method may be called multiple times, even with no
		// intervening elements.
		public void characters(char[] buffer, int start, int length) {
			accumulator.append(buffer, start, length);
		}

		// At the beginning of each new element, erase any accumulated text.
		public void startElement(String namespaceURL, String localName,
				String qname, Attributes attributes) {
			if (localName.equals("field")) {
				String name = attributes.getValue("name");
				String type = attributes.getValue("type");
				String indexed = attributes.getValue("indexed");
				if (!name.equals("allterms"))
					addField(name, type, indexed);
			}
			accumulator.setLength(0);
		}

		private void addField(String name, String type, String indexed) {

			LinkedHashMap<String, String> valu = new LinkedHashMap<String, String>();
			valu.put("name", name);
			valu.put("type", type);
			valu.put("indexed", indexed);
			data.add(valu);
		}

		public ArrayList<LinkedHashMap<String, String>> getData() {
			return data;
		}

		// Take special action when we reach the end of selected elements.
		// Although we don't use a validating parser, this method does assume
		// that the web.xml file we're parsing is valid.
		public void endElement(String namespaceURL, String localName, String qname) {

			if (localName.equals("fieldName")) {

			}

			if (localName.equals("servlet-name")) { // Store servlet name
				servletName = accumulator.toString().trim();
			} else if (localName.equals("servlet-class")) { // Store servlet class
				servletClass = accumulator.toString().trim();
			} else if (localName.equals("url-pattern")) { // Store servlet pattern
				servletPattern = accumulator.toString().trim();
			} else if (localName.equals("servlet")) { // Map name to class
				nameToClass.put(servletName, servletClass);
			} else if (localName.equals("servlet-mapping")) {// Map name to pattern
				List patterns = (List) nameToPatterns.get(servletName);
				if (patterns == null) {
					patterns = new ArrayList();
					nameToPatterns.put(servletName, patterns);
				}
				patterns.add(servletPattern);
			}
		}

		// Called at the end of parsing. Used here to print our results.
		public void endDocument() {
			List servletNames = new ArrayList(nameToClass.keySet());
			Collections.sort(servletNames);
			for (Iterator iterator = servletNames.iterator(); iterator.hasNext();) {
				String name = (String) iterator.next();
				String classname = (String) nameToClass.get(name);
				List patterns = (List) nameToPatterns.get(name);
				System.out.println("Servlet: " + name);
				System.out.println("Class: " + classname);
				if (patterns != null) {
					System.out.println("Patterns:");
					for (Iterator i = patterns.iterator(); i.hasNext();) {
						System.out.println("\t" + i.next());
					}
				}
				System.out.println();
			}
		}

		// Issue a warning
		public void warning(SAXParseException exception) {
			System.err.println("WARNING: line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
		}

		// Report a parsing error
		public void error(SAXParseException exception) {
			System.err.println("ERROR: line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
		}

		// Report a non-recoverable error and exit
		public void fatalError(SAXParseException exception) throws SAXException {
			System.err.println("FATAL: line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
			throw (exception);
		}
	}
