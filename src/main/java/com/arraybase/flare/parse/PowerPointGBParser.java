package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.arraybase.io.GBBlobFile;
import com.arraybase.util.GBLogger;

public class PowerPointGBParser implements GBParser {

	private static GBLogger log = GBLogger.getLogger(PowerPointGBParser.class);

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {
		GBStructuredContent st = null;
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(
					file.getAttachment1());
			st = StructuredContentFactory.pptParseMetaData(bi);
			
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
