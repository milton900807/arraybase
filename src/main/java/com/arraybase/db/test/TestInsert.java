package com.arraybase.db.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.arraybase.GB;
import com.arraybase.modules.UsageException;

public class TestInsert {

	public static void main(String[] _args) {

		testSolrToSolrInsert();
	}
	public static void testSolrToSolrInsert() {
		String resource_dir = "/test/rpkm";
		String props = "url=ab://isis/test/experiments3\n"
				+ "export=Localization,Platform,RNA_Seq_expid,comments,date\n"
				+ "query=*:*\n" + "increment=1000";
		try {
			File f = File.createTempFile("abq_", ".abq");
			PrintStream pt = new PrintStream(f);
			pt.print(props);
			pt.flush();
			pt.close();
			String path = f.getAbsolutePath();
			try {
				GB.gogb("insert", "" + path, "into", resource_dir + "/values",
						"where", "RNA_Seq_expid", "=", "RNA_Seq_expid");
			} catch (UsageException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
