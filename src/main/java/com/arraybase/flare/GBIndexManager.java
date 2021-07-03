package com.arraybase.flare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.arraybase.SearchConfig;
import com.arraybase.flare.file.GBFileStructure;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.flare.parse.GBParser;
import com.arraybase.flare.parse.GBStructuredContent;
import com.arraybase.flare.parse.ParseFactory;
import com.arraybase.io.GBBlobFile;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;

/**
 * This is a class that will work
 * 
 * @author donaldm
 */
public class GBIndexManager {

	private static GBLogger log = GBLogger.getLogger(GBIndexManager.class);

	public GBIndexManager() {

	}

	/**
	 * This is going to build the index from a test file.
	 * 
	 * @param args
	 */
	public void main(String[] args) {

	}

	public static IndexBuilderStatus add(ArrayList<SolrInputDocument> docs,
			GBBlobFile gbfile, String _path, String _name,
			String _description, String ids) {
		if (gbfile == null) {
			return new IndexBuilderStatus(BUILDStatus.FAILED);
		}
		log.debug("Parsing" + _name);
		GBParser parser = ParseFactory.getParser(_name);
		try {
			log.debug("\tUsing parser\t" + parser.getClass().toString());
			GBStructuredContent content = parser.parse(gbfile);
			if (content == null)
				return new IndexBuilderStatus(BUILDStatus.FAILED);
			String authors = content.getAuthors();
			String header = content.getFileHeaderInformation();
			String content_type = content.getType();
			String file_content = content.getContentAsString();
			String title = content.getTitle();

			SolrInputDocument sd = new SolrInputDocument();
			sd.addField("TMID_lastUpdated", new Date());
			sd.addField("TMID", TMID.create());
			sd.addField(GBFileStructure.FILE_NAME.name, _name);
			sd.addField(GBFileStructure.PATH.name, _path);
			sd.addField(GBFileStructure.DESCRIPTION.name, _description);
			sd.addField(GBFileStructure.MIME.name, content_type);
			sd.addField(GBFileStructure.IDS.name, header);
			sd.addField(GBFileStructure.CONTENT.name, file_content);
			sd.addField(GBFileStructure.AUTHORS.name, authors);
			sd.addField(GBFileStructure.TITLE.name, title);
			
			String[] tag = GBPathUtils.split ( _path );
			for ( String t : tag ){
				sd.addField("g_tag", t );
			}
			
			docs.add(sd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IndexBuilderStatus(BUILDStatus.FAILED);
	}

	/**
	 * This method will add a core to the solr input docs.
	 * 
	 * @param core
	 * @param n
	 */
	public static IndexBuilderStatus indexCore(String core,
			HttpSolrClient _solr, TNode n, String path) {
		int increment = 1000;
		String site = ABProperties.getSolrURL();
		GResults rs = TMSolrServer.search(site, core, "*:*", 0, increment,
				new SearchConfig(SearchConfig.RAW_SEARCH));
		
		// {{ IF NULL RESULTS CORE MUST NOT BE AVAILABLE }}
		if ( rs == null )
			return new IndexBuilderStatus(BUILDStatus.CORE_NOT_AVAILABLE);
		try {
				Collection<SolrInputDocument> cdoc = update(rs, n, path);
				if (cdoc != null && cdoc.size() > 0)
					_solr.add(cdoc);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if there are more hits.. then we need to get em.
		int totalhits = rs.getTotalHits();
		int index = increment;
		while (index < totalhits) {
			rs = TMSolrServer.search(site, core, "*:*", index, increment,
                    new SearchConfig(SearchConfig.RAW_SEARCH));
			try {
				Collection<SolrInputDocument> cdoc = update(rs, n, path);
				if (cdoc != null && cdoc.size() > 0)
					_solr.add(cdoc);
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			index += increment;
		}
		return new IndexBuilderStatus(BUILDStatus.SUCCESS);
	}

	/**
	 * Returns a list of docs for indexing.
	 * 
	 * @param rs
	 * @param n
	 * @param path
	 * @return
	 */
	private static Collection<SolrInputDocument> update(GResults rs, TNode n,
			String path) {
		ArrayList<GColumn> cols = rs.getColumns();
		ArrayList<GRow> rows = rs.getValues();
		ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (GRow r : rows) {
			SolrInputDocument sd = new SolrInputDocument();
			sd.addField("TMID_lastUpdated", new Date());
			sd.addField("TMID", TMID.create());
			sd.addField(GBFileStructure.FILE_NAME.name, n.getName());
			sd.addField(GBFileStructure.PATH.name, path);
			sd.addField(GBFileStructure.DESCRIPTION.name, n.getDescription());
			sd.addField(GBFileStructure.MIME.name, "table");
			// sd.addField(GBFileStructure.IDS.name, header);
			// sd.addField(GBFileStructure.AUTHORS.name, authors);
			// sd.addField(GBFileStructure.TITLE.name, title);
			String content = "";
			Map values = r.getData();
			for (GColumn c : cols) {
				Object ov = values.get(c.getName());
				if (ov != null) {
					String v = ov.toString();
					content += "\t" + c.getName() + "=" + v;
				}
			}
			sd.addField(GBFileStructure.CONTENT.name, content);
			docs.add(sd);
		}
		return docs;
	}
}
