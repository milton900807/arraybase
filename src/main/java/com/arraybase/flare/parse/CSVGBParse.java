package com.arraybase.flare.parse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.arraybase.io.GBBlobFile;

public class CSVGBParse implements GBParser {

	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {

		ByteArrayInputStream in = new ByteArrayInputStream(
				file.getAttachment1());
		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		String g;
		try {
			ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
			boolean first_time = true;
			g = bin.readLine();
			String header = g;
			StringBuffer str = new StringBuffer();
			while (g != null) {
				g = g.replaceAll("\"\"", "\"NULL\"");
				// Now remove all of the quotes
				g = g.replaceAll("\"", "");
				str.append(g);
				g = bin.readLine();
			}
			GBStructuredContent stc = new GBStructuredContent();
			stc.setBuffer(str);
			stc.setHeader(header);
			stc.setType("cvs");
			return stc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
