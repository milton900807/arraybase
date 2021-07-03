package com.arraybase.io;

import java.io.File;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class GBFileCrawler {

	private GBNodes nodes = GB.getNodes();
	private static GBLogger log = GBLogger.getLogger(GBFileCrawler.class);

	public static void main(String[] args) {
		GBFileCrawler gv = new GBFileCrawler();
		File f = new File("/Users");
		gv.recursiveTraversal("tester", f);
	}

	public void recursiveTraversal(String _userid, File fileObject) {

		if (fileObject.isDirectory()) {
			TNode node = nodes.getNode(fileObject.getAbsolutePath());
			if (node == null) {
				String path = GBPathUtils.adjustPathChars(fileObject
						.getAbsolutePath());
				GBNodes.mkdir(_userid, path);
				log.debug(path);
			}
			File allFiles[] = fileObject.listFiles();
			for (File aFile : allFiles) { /* This is line 48 */
				recursiveTraversal(_userid, aFile);
			}

		} else if (fileObject.isFile()) {
			String file = fileObject.getName();
			File parent = fileObject.getParentFile();
			String path = GBPathUtils.adjustPathChars(parent.getAbsolutePath());
			GBNodes.save(_userid, file, path);
		}
	}

	public static void go(String[] _args) {
		if (_args.length != 3) {
			GB.printUsage("Please provide $userid $directory");
			return;
		}
		String userid = _args[1];
		String localfile = _args[2];
		GBFileCrawler gv = new GBFileCrawler();
		File f = new File(localfile);

		if (f.isDirectory())
			gv.recursiveTraversal(userid, f);
		else
			GB.printUsage(" The second parameter to the crawl must be a path to a directory ");

	}

}
