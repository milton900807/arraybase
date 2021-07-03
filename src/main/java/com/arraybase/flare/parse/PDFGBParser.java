package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.arraybase.io.GBBlobFile;

public class PDFGBParser implements GBParser {

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {
		
		
		ByteArrayInputStream in = new ByteArrayInputStream(
				file.getAttachment1());

		
		GBStructuredContent st = null;
		try {
			st = StructuredContentFactory.parsePDFMetaData(in);
			
			
			
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		return st;
	}

}
