package com.arraybase.flare.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ProgressMonitorInputStream;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.arraybase.util.GBLogger;

public class StructuredContentFactory {

	private static GBLogger log = GBLogger
			.getLogger(StructuredContentFactory.class);

	private static SimpleDateFormat pdf_date_formater = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy");
	private static SimpleDateFormat doc_date_formater = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	// "2011-06-14T15:53:50Z"
	public static GBStructuredContent build(String type, InputStream buf) {
		try {
			if (type.equalsIgnoreCase("application/pdf")) {
				return parsePDFMetaData(buf);
			} else if (type
					.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
				return docParseMetaData(type, buf);
			} else if (type.equalsIgnoreCase("application/vnd.ms-powerpoint")) {
				return pptParseMetaData(buf);
			}
			return parsePDFMetaData(buf);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GBStructuredContent pptParseMetaData(InputStream buf)
			throws TransformerConfigurationException, IOException,
			SAXException, TikaException {

		ParseContext context = new ParseContext();
		Parser parser = new AutoDetectParser();
		// context.set(Parser.class, imageParser);
		StringWriter xmlBuffer = new StringWriter();
		ToTextContentHandler tcc = new ToTextContentHandler(xmlBuffer);
		TeeContentHandler tc = new TeeContentHandler(tcc);
		Metadata metadata = new Metadata();
		ProgressMonitorInputStream input = new ProgressMonitorInputStream(null,
				"Indexing...", buf);
		parser.parse(input, tc, metadata, context);
		StringBuffer sbbuf = new StringBuffer(tcc.toString());

		// Application-Name
		// Author
		// Company
		// Content-Type
		// Creation-Date
		// Edit-Time
		// Last-Modified
		// Last-Save-Date
		// Revision-Number
		// Slide-Count
		// Word-Count
		// cp:revision
		// creator
		// date
		// dc:creator
		// dc:title
		// dcterms:created
		// dcterms:modified
		// extended-properties:Application
		// extended-properties:Company
		// meta:author
		// meta:creation-date
		// meta:last-author
		// meta:save-date
		// meta:slide-count
		// meta:word-count
		// modified2012-10-10 10:37:17.937

		GBStructuredContent st = new GBStructuredContent();
		String[] names = metadata.names();
		String lst_mod = metadata.get("Last-Modified");
		String word_count = metadata.get("Word-Count");
		String title = metadata.get("dc:title");
		String publisher = metadata.get("Company");
		String subject = metadata.get("cp:revision");
		String author = metadata.get("meta:last-author");
		String created_date = metadata.get("date");
		st.setBuffer(sbbuf);

		String slide_count = metadata.get("Slide-Count");
		subject += "Slide Count: " + slide_count;

		try {
			if (created_date != null) {
				Date cdate = doc_date_formater.parse(created_date);
				st.setCreated_date(cdate);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		try {
			if (lst_mod != null) {
				Date lmod = doc_date_formater.parse(lst_mod);
				st.setLastModified(lmod);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		st.setSubject(subject);
		st.setCreator(publisher);
		st.setAuthors(author);
		st.setTitle(title);
		if (word_count != null) {
			try {
				int count = Integer.parseInt(word_count);
				st.setWordCount(count);
			} catch (NumberFormatException _ef) {
				_ef.printStackTrace();
			}

		}
		return st;
	}

	// [Last-Printed, Revision-Number, Template, Page-Count, subject,
	// Application-Name,
	// Author, Last-Modified, Application-Version, date,
	// Character-Count-With-Spaces,
	// Total-Time, publisher, creator, Word-Count, title, Line-Count, Character
	// Count, Content-Type, Paragraph-Count]
	private static GBStructuredContent docParseMetaData(String _type,
			InputStream buf) throws TransformerConfigurationException,
			IOException, SAXException, TikaException {

		ParseContext context = new ParseContext();
		Parser parser = new AutoDetectParser();
		StringWriter xmlBuffer = new StringWriter();
		TeeContentHandler tc = new TeeContentHandler(
				DefaultGBParser.getXmlContentHandler(xmlBuffer));
		Metadata metadata = new Metadata();
		ProgressMonitorInputStream input = new ProgressMonitorInputStream(null,
				"Indexing...", buf);
		parser.parse(input, tc, metadata, context);

		GBStructuredContent st = new GBStructuredContent(_type);
		String[] names = metadata.names();

		for (String n : names) {
			log.debug("\n\n" + n);
		}

		String lst_mod = metadata.get("Last-Modified");
		String word_count = metadata.get("Word-Count");
		String title = metadata.get("title");
		String publisher = metadata.get("publisher");
		String subject = metadata.get("subject");
		String author = metadata.get("Author");
		String created_date = metadata.get("date");

		try {
			Date cdate = doc_date_formater.parse(created_date);
			st.setCreated_date(cdate);
		} catch (Exception _e) {
			log.debug("Failed to parse the doc date : " + lst_mod);
		}
		try {
			Date lmod = doc_date_formater.parse(lst_mod);
			st.setLastModified(lmod);
		} catch (Exception _e) {
			log.debug("Failed to parse the doc date : " + lst_mod);
		}
		st.setSubject(subject);
		st.setCreator(publisher);
		st.setAuthors(author);
		st.setTitle(title);
		if (word_count != null) {
			try {
				int count = Integer.parseInt(word_count);
				st.setWordCount(count);
			} catch (NumberFormatException _ef) {
				_ef.printStackTrace();
			}

		}
		return st;
	}

	public static GBStructuredContent parseMetaDatacsv(InputStream buf)
			throws TransformerConfigurationException, IOException,
			SAXException, TikaException {

		ParseContext context = new ParseContext();
		Parser parser = new AutoDetectParser();
		// context.set(Parser.class, imageParser);
		StringWriter xmlBuffer = new StringWriter();
		TeeContentHandler tc = new TeeContentHandler(
				DefaultGBParser.getXmlContentHandler(xmlBuffer));
		Metadata metadata = new Metadata();
		ProgressMonitorInputStream input = new ProgressMonitorInputStream(null,
				"Indexing...", buf);
		parser.parse(input, tc, metadata, context);

		// Application-Name
		// Author
		// Company
		// Content-Type
		// Creation-Date
		// Edit-Time
		// Last-Modified
		// Last-Save-Date
		// Revision-Number
		// Slide-Count
		// Word-Count
		// cp:revision
		// creator
		// date
		// dc:creator
		// dc:title
		// dcterms:created
		// dcterms:modified
		// extended-properties:Application
		// extended-properties:Company
		// meta:author
		// meta:creation-date
		// meta:last-author
		// meta:save-date
		// meta:slide-count
		// meta:word-count
		// modified2012-10-10 10:37:17.937

		GBStructuredContent st = new GBStructuredContent();
		String[] names = metadata.names();
		String lst_mod = metadata.get("Last-Modified");
		String word_count = metadata.get("Word-Count");
		String title = metadata.get("dc:title");
		String publisher = metadata.get("Company");
		String subject = metadata.get("cp:revision");
		String author = metadata.get("meta:last-author");
		String created_date = metadata.get("date");

		String slide_count = metadata.get("Slide-Count");
		subject += "Slide Count: " + slide_count;

		try {
			Date cdate = doc_date_formater.parse(created_date);
			st.setCreated_date(cdate);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		try {
			Date lmod = doc_date_formater.parse(lst_mod);
			st.setLastModified(lmod);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		st.setSubject(subject);
		st.setCreator(publisher);
		st.setAuthors(author);
		st.setTitle(title);
		if (word_count != null) {
			try {
				int count = Integer.parseInt(word_count);
				st.setWordCount(count);
			} catch (NumberFormatException _ef) {
				_ef.printStackTrace();
			}
		}
		return st;
	}

	private static SimpleDateFormat pdf_date_formater1 = new SimpleDateFormat(
			"yyyy-mm-dd EEE MMM dd HH:mm:ss z yyyy");

	// 2012-03-10T22:35:44Z
	public static GBStructuredContent parsePDFMetaData(InputStream buf)
			throws TransformerConfigurationException, IOException,
			SAXException, TikaException {

		ParseContext context = new ParseContext();
		Parser parser = new PDFParser();
		StringWriter xmlBuffer = new StringWriter();
		TeeContentHandler tc = new TeeContentHandler(new ToTextContentHandler(
				xmlBuffer));
		Metadata metadata = new Metadata();
		// ProgressMonitorInputStream input = new
		// ProgressMonitorInputStream(null,
		// "Indexing...", buf);
		parser.parse(buf, tc, metadata, context);
		xmlBuffer.flush();

		StringBuffer s_buffer = xmlBuffer.getBuffer();
		log.debug(" buffered : " + s_buffer);

		String type = metadata.get("Content-Type");
		GBStructuredContent st = new GBStructuredContent(type);
		st.setBuffer(s_buffer);
		String producer = metadata.get("producer");
		String lst_mod = metadata.get("Last-Modified");
		String created = metadata.get("created");
		String creator = metadata.get("creator");

		st.setProducer(producer);
		st.setCreator(creator);

		// we are going to get the structure object given the metadata
		try {
			if (created != null) {
				Date created_date = pdf_date_formater.parse(created);
				st.setCreated_date(created_date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (lst_mod != null) {
			// 2010-04-19T16:10:31Z
			try {
				Date lst_mod_date = pdf_date_formater.parse(lst_mod);
				st.setLastModified(lst_mod_date);
			} catch (ParseException _ee) {
				try {
					Date lst_mode = doc_date_formater.parse(lst_mod);
					st.setLastModified(lst_mode);
				} catch (ParseException _de) {
					// "2010-10-03T05:28:47Z"
					// 2012-09-28T09:31:09Z doc params
					// _de.printStackTrace();
					_de.printStackTrace();
				}

			}
		}
		return st;
	}

	public static GBStructuredContent parseWordDocCrap(InputStream _in) {
		ParseContext context = new ParseContext();
		// StringWriter xmlBuffer = new StringWriter();

		BodyContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		Parser parser = new OfficeParser();
		try {
			log.debug("Parsing");
			parser.parse(_in, textHandler, metadata, context);
			_in.close();
			log.debug("\tComplete");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

		// Metadata name : Revision-Number
		// Metadata name : Last-Author
		// Metadata name : Template
		// Metadata name : subject
		// Metadata name : Page-Count
		// Metadata name : Application-Name
		// Metadata name : Author
		// Metadata name : Word-Count
		// Metadata name : xmpTPg:NPages
		// Metadata name : Edit-Time
		// Metadata name : Creation-Date
		// Metadata name : title
		// Metadata name : Character Count
		// Metadata name : Content-Type
		// Metadata name : Keywords
		// Metadata name : Last-Save-Date

		for (String s : metadata.names()) {
			System.out.println("Metadata name : " + s);
		}
		GBStructuredContent docc = new GBStructuredContent("word");

		docc.setAuthors(metadata.get("Author"));
		docc.setTitle(metadata.get("title"));
		docc.setSubject(metadata.get("subject"));
		docc.setBuffer(new StringBuffer(parser.toString()));
		return docc;
	}

}
