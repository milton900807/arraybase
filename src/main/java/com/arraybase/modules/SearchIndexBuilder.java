package com.arraybase.modules;

import com.arraybase.GB;
import com.arraybase.GBModule;
import com.arraybase.GBNodes;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.GBIndexManager;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.io.GBBlobFile;
import com.arraybase.io.GBFileManager;
import com.arraybase.lac.FileLac;
import com.arraybase.lac.No_LAC_ElementFoundException;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchIndexBuilder implements GBModule {

	static GBLogger log = GBLogger.getLogger(SearchIndexBuilder.class);

	private DBConnectionManager dbcm = new DBConnectionManager();

	public String getModName() {
		return "Index builder";
	}
	public void exec(Map<String, Object> l) throws UsageException
	{
		throw new UsageException ( "This is not implemented. ");
	}

	public void exec(List<String> l) throws UsageException {

		if (l == null || l.size() < 0)
			throw new UsageException(
					"You must provide a starting path to index.");
		String path = l.get(0);
		if (path != null) {
			path = path.trim();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
		}
		log.debug("Root: " + path);

		// {{ the first thing we need to do is get the files }}
		GBNodes nodes = GB.getNodes();
		TPath root = nodes.getPath(path);
		TNode rnode = nodes.getNode(root.getNode_id());
		List<TNode> chld = nodes.getNodes(rnode.getReference());
		String solr_core = ABProperties.getSearchCore();
		HttpSolrClient solr = new HttpSolrClient.Builder(solr_core).build();
		try {
			solr.setParser(new XMLResponseParser());
			try {
				SolrPingResponse response = solr.ping();
				response.getResponseHeader();
			} catch (SolrServerException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for (TNode n : chld) {
				update(n, root.getName(), nodes, solr);
			}
			try {
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	/**
	 * add the node to the solr indicies This is a long running method...
	 * beware! -depending on how deep your trees go!
	 * 
	 * @param n
	 * @param solr
	 * @param solr
	 */
	private void update(TNode n, String _path, GBNodes nodes,
			HttpSolrClient solr) {
		log.debug("\t\t\tPath: " + _path);
		try {
			if (n.getNodeType().equalsIgnoreCase(SourceType.RAW_FILE.getName())) {
				log.debug("Loading file " + _path + "/" + n.getName());
				long file_id = FileLac.getFileID(n.getLink());
				GBFileManager fileManager = new GBFileManager(dbcm);
				GBBlobFile file = fileManager.getFile(file_id);
				// {{ ADD THIS FILE TO THE DOCUMENTS LIST }}
				log.debug("Indexing " + _path);
				ArrayList<SolrInputDocument> doc = new ArrayList<SolrInputDocument>();
				GBIndexManager.add(doc, file, _path, n.getName(),
						n.getDescription(), n.getLink());
				try {
					if (doc != null && doc.size() > 0)
						solr.add(doc);
				} catch (SolrServerException e) {
					e.printStackTrace();
				}
			} else if (n.getNodeType()
					.equalsIgnoreCase(SourceType.DB.getName())) {
				// this is a table.
				log.debug("Loading table " + _path + "/" + n.getName());
				String lac = n.getLink();
				if (lac != null) {
					String core = GBSolr.getCoreFromLAC(lac, dbcm);
					if (core == null || core.length() <= 0
							|| core.equalsIgnoreCase("null"))
						return;// we're not indexing children of this node... so
								// just get out
					log.debug("\tcore->" + core);
					GBIndexManager.indexCore(core, solr, n, _path);
				}
			}
			List<Integer> chldrs = n.getReference();
			for (Integer i : chldrs) {
				ArrayList<SolrInputDocument> sd = new ArrayList<SolrInputDocument>();
				TNode node = nodes.getNode(i);
				if (node != null) {
					TPath path = nodes.getTPath(node);
					if (path != null)
						update(node, path.getName(), nodes, solr);
				}
				try {
					if (sd != null && sd.size() > 0)
						solr.add(sd);
				} catch (SolrServerException e) {
					e.printStackTrace();
				}
			}
			try {
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (No_LAC_ElementFoundException e) {
			log.error("Failed to index the file : " + _path);
			e.printStackTrace();

		} catch (IOException e) {
			log.error("Failed to index the file : " + _path);
			e.printStackTrace();
		}
	}
}
