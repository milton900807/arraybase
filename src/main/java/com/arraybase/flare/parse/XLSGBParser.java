package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.arraybase.io.GBBlobFile;
import com.arraybase.util.GBLogger;

public class XLSGBParser implements GBParser {

	private GBLogger log = GBLogger.getLogger(XLSGBParser.class);

	public XLSGBParser() {
		log.debug(" XLS Parser ");
	}

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {
		// not sure about the contxt.
		ParseContext context = new ParseContext();
		// let's define the parser.
		OfficeParser parser = new OfficeParser();
		// context.set(Parser.class, imageParser);
		StringWriter xmlBuffer = new StringWriter();
		TeeContentHandler tc;
		try {
			ToTextContentHandler cn = new ToTextContentHandler();
			Metadata metadata = new Metadata();
			ByteArrayInputStream in = new ByteArrayInputStream(
					file.getAttachment1());
			log.debug ( " contents : "+ cn.toString() );
			parser.parse(in, cn, metadata, context);
			StringBuffer str = new StringBuffer(cn.toString());
			String[] attrs = metadata.names();
			System.out.println(" attributes :  " + attrs.length);

			GBStructuredContent st = new GBStructuredContent();
			st.setBuffer(str);

			return st;

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
