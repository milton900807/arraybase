package com.arraybase.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.tika.Tika;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.flare.parse.GBParser;
import com.arraybase.flare.parse.GBParserFactory;
import com.arraybase.flare.parse.GBStructuredContent;
import com.arraybase.io.GBBlobFile;

public class GBFileManagerIndexer {

	
	DBConnectionManager dbc = new DBConnectionManager ();
	
	/**
	 * Build the solr document for the fields given the type
	 * 
	 * @param _fileuri
	 * @param _fileType
	 * @param _fields
	 * @return
	 */
	public SolrDocument buildSolrDoc(String _fileuri, String _fileType,
			List<String> _fields) {
		try {
			GBBlobFile f = dbc.loadFile(_fileuri);
			String type = GBFileManagerIndexer.detectType(f
					.getAttachment_name());
			byte[] bb = f.getAttachment1();
			System.out.println(" bytes " + bb.length);
			ByteArrayInputStream in = new ByteArrayInputStream(bb);
			// String tika_type = tika.detect(in);
			// System.out.println ( " tika : "+ tika_type );
			GBParser p = GBParserFactory.makeParser(type);
			GBStructuredContent content = p.parse(f);
			if (content != null)
				System.out
						.println(" \tcontent " + content.getContentAsString());
			in.close();
			// Parser parser = new AutoDetectParser();
			// Reader tika_reader = tika.parse(in);
			// BufferedReader bread = new BufferedReader ( tika_reader );
			// String b = bread.readLine();
			// while ( b != null )
			// {
			// System.out.println( b );
			// }
		} catch (GBParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	private static String detectType(String name) {
		if (name == null)
			name = "unknown";
		String t = name.toLowerCase();
		t = t.trim();
		if (t.endsWith(".csv"))
			return "text/csv";
		else if (t.endsWith(".pdf"))
			return "application/pdf";
		else if (t.endsWith(".docx"))
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		else if (t.endsWith("ppt")) {
			return "application/vnd.ms-powerpoint";
		}
		Tika tika = new Tika();
		return tika.detect(name);
	}

}
