package com.arraybase.flare.parse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.arraybase.io.GBBlobFile;

public class ABXMLParser implements GBParser {

	/**
	 * Parse xml
	 */
	public GBStructuredContent parse(GBBlobFile file) throws GBParseException {

		ByteArrayInputStream in = new ByteArrayInputStream(
				file.getAttachment1());
		GBStructuredContent st = new GBStructuredContent("xml");
		try {
			StringBuffer buffer = new StringBuffer();
			byte[] b = new byte[1024];
			int length;
			while ((length = in.read(b)) > 0) {
				buffer.append(b);
			}
			st.setBuffer(buffer);
			st.setAuthors(file.getLast_saved_by_usr_id());
			st.setCreated_date(file.getLast_updated_date());

			st.setType("xml");
		} catch (IOException _e) {
			_e.printStackTrace();
		}
		return st;
	}

}
