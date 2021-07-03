package com.arraybase.flare.parse;

import com.arraybase.io.GBBlobFile;
import com.arraybase.util.IOUTILs;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultGBParser implements GBParser {
	private Tika tika = new Tika();

	/**
	 * A {@link DocumentSelector} that accepts only images.
	 */
	private static class ImageDocumentSelector implements DocumentSelector {
		public boolean select(Metadata metadata) {
			String type = metadata.get(Metadata.CONTENT_TYPE);
			return type != null && type.startsWith("image/");
		}
	}

	public GBStructuredContent parse(GBBlobFile buf) throws GBParseException {
		// Tue Jul 03 06:55:32 PDT 2012
		// 'EEE MMM dd HH:mm:ss z yyyy'
		// GBStructuredContent st =
		// StructuredContentFactory.build("application/pdf", buf);
		return null;
		// Reader reader = tika.parse(buf);
		// int charsRead;
		// char[] cbuf = new char[1024];
		// while ((charsRead = reader.read(cbuf)) != -1) {
		// System.out.print(cbuf);
		// }
	}

	public static ContentHandler getXmlContentHandler(Writer xmlBuffer)
			throws TransformerConfigurationException {
		SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();
		handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
		handler.setResult(new StreamResult(xmlBuffer));
		return handler;
	}

	public static ContentHandler getContentHandler(Writer writer)
			throws TransformerConfigurationException {
		ContentHandler handler = new WriteOutContentHandler(writer);
		return handler;
	}

	private static class ImageSavingParser extends AbstractParser {
		private Map<String, File> wanted = new HashMap<String, File>();
		private Parser downstreamParser;
		private File tmpDir;

		private ImageSavingParser(Parser downstreamParser) {
			this.downstreamParser = downstreamParser;

			try {
				File t = File.createTempFile("tika", ".test");
				tmpDir = t.getParentFile();
			} catch (IOException e) {
			}
		}

		public File requestSave(String embeddedName) throws IOException {
			String suffix = ".tika";

			int splitAt = embeddedName.lastIndexOf('.');
			if (splitAt > 0) {
				embeddedName.substring(splitAt);
			}

			File tmp = File.createTempFile("tika-embedded-", suffix);
			wanted.put(embeddedName, tmp);
			return tmp;
		}

		public Set<MediaType> getSupportedTypes(ParseContext context) {
			return null;
		}

		public void parse(InputStream stream, ContentHandler handler,
				Metadata metadata, ParseContext context) throws IOException,
				SAXException, TikaException {
			String name = metadata.get(Metadata.RESOURCE_NAME_KEY);
			FileOutputStream out = null;
			try {
				if (name != null && wanted.containsKey(name)) {
					out = new FileOutputStream(wanted.get(name));
				} else {
					if (downstreamParser != null) {
						downstreamParser.parse(stream, handler, metadata, context);
					}
				}
			} finally {
				IOUTILs.closeResource(out);
			}
		}
	}
}
