package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.*;
import org.apache.poi.hwpf.extractor.*;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.hwpf.usermodel.HeaderStories;

import com.arraybase.io.GBBlobFile;

import java.io.*;
import java.util.Date;

public class WordDocumentGBParser implements GBParser {

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {
		// return StructuredContentFactory.parseWordDocCrap(_in);
		ByteArrayInputStream bi = new ByteArrayInputStream(
				file.getAttachment1());
		return readMyDocument(bi);
	}

	public static GBStructuredContent readMyDocument(InputStream in) {
		GBStructuredContent docc = new GBStructuredContent("word");
		POIFSFileSystem fs = null;
		try {
			fs = new POIFSFileSystem(in);
			HWPFDocument doc = new HWPFDocument(fs);

			String author = doc.getSummaryInformation().getAuthor();
			SummaryInformation sum = doc.getSummaryInformation();
			Date created_date = sum.getCreateDateTime();
			author += ", last author: " + sum.getLastAuthor();

			docc.setAuthors(author);
			docc.setCreated_date(created_date);

			String content = readParagraphs(doc);
			docc.setBuffer(new StringBuffer(content));

			int pageNumber = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docc;
	}

	public static String readParagraphs(HWPFDocument doc) throws Exception {
		WordExtractor we = new WordExtractor(doc);

		/** Get the total number of paragraphs **/
		String[] paragraphs = we.getParagraphText();
		System.out.println("Total Paragraphs: " + paragraphs.length);
		String t = "";

		for (int i = 0; i < paragraphs.length; i++) {
			t += "\n" + paragraphs[i].toString();
			// System.out.println("Length of paragraph " + (i + 1) + ": "
			// + paragraphs[i].length());
			// System.out.println(paragraphs[i].toString());
		}
		return t;
	}
}
