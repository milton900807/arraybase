package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.TeeContentHandler;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import com.arraybase.io.GBBlobFile;

public class PPTParser implements GBParser {
	
	private Logger log = Logger.getLogger(PPTParser.class);
	

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {
		// not sure about the contxt.
		ParseContext context = new ParseContext();
		// let's define the parser.
		Parser parser = new OfficeParser();
		// context.set(Parser.class, imageParser);
		StringWriter xmlBuffer = new StringWriter();
		TeeContentHandler tc;
		try {
			
			ByteArrayInputStream bi = new ByteArrayInputStream(
					file.getAttachment1());
			
			tc = new TeeContentHandler(
					DefaultGBParser.getXmlContentHandler(xmlBuffer));
			Metadata metadata = new Metadata();
			parser.parse(bi, tc, metadata, context);

			String[] attrs = metadata.names();
			System.out.println(" attributes :  " + attrs.length);

			GBStructuredContent st = new GBStructuredContent();
			return st;

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
}
