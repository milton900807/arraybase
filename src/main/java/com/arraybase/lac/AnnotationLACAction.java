package com.arraybase.lac;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import com.arraybase.flare.TMSolrServer;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;

public class AnnotationLACAction implements LACAction {

	private String target = null;
	private String data = null;

	public AnnotationLACAction(String _target, String _data) {
	}

	public LACActionProcess exec() throws LACExecException {
		NodeManager st = new NodeManager();
		// {{ VALIDATE THE PATH }}
		TNode node = st.getNode(data);
		if (node == null) {
			throw new LACExecException("Failed to find the path");
		}
		String solr = ABProperties.get(ABProperties.SOLRSITE, target);
		TMSolrServer solr_server = new TMSolrServer(solr);
		String[] data_ = data.split(",");
		HashMap<String, String> annotation = getAnnotationMap(data_[0],
				data_[1]);
		String solr_table = node.getLink();
		try {
			// solr_server.dynamicAppendSchema(solr_table, annotation);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LACExecException();
		}

		// the following commented code is the test code.. here as an example of
		// how to call the dynamicAppendSchema
		// String lac =
		// "milton_Repository_gne.research.htl.thisis_test.search(*:*)";
		// String hierarchy = "gne/research/sample_repository";
		// HashMap<String, String> annotation = getAnnotationMap ( hierarchy,
		// "milton");
		// String[] l = LAC.parse(lac);
		// String table = l[0];
		// try {
		// dynamicAppendSchema(table, annotation);
		// } catch (LoaderException e) {
		// e.printStackTrace();
		// }

		// solr_server.dynamicAppendSchema(s_table, table);
		throw new LACExecException();
	}

	public static void main(String[] args) {
		String hierarchy = "gne/research/web/dev/tomcat";

		// UUID uuid = UUID.fromString(hierarchy);
		// UUID uuid2 = UUID.fromString(hierarchy);
		// UUID uuid3 = UUID.fromString(hierarchy);
		// Log.info( " 1 : " + uuid.toString() + " \n " + " 2 : "+
		// uuid2.toString() + " \n 3 " + uuid3.toString() );
		// String tempResult =
		// UUID.nameUUIDFromBytes(hierarchy.getBytes()).toString();
		// System.out.println(tempResult);
	}

	public static void main__(String[] _t) {

		// {{{ need to merge this with the widgetfactory annotation panel }}}
		String alias = "/gne/research/alias_test";
		String table = "milton_Repository_v_c_studies";

		NodeManager nodeService = new NodeManager();

		// create an alias to a lac object.
		TNode node = nodeService.createAlias("milton", alias, SourceType.LINK,
				table + ".search(*:*)");

		// create the annotation table
		HashMap<String, String> annotation = getAnnotationMap(alias, "milton");
		String solr_table = node.getLink();
		String[] lac = LAC.parse(solr_table);
		try {
			// get a handle on the solr server
			String solr = ABProperties.get(ABProperties.SOLRSITE, null);
			TMSolrServer solr_server = new TMSolrServer(solr);
			Set<String> fields = annotation.keySet();
			ArrayList<String> remove_fields = new ArrayList<String>();
			for (String f : fields) {
				remove_fields.add(f);
			}
			// solr_server.dynamicAppendSchema(lac[0], annotation);
			// solr_server.dynamicRemoveAnnotation(lac[0], remove_fields);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> getAnnotationMap(String hierarchy,
			String user) {
		StringTokenizer st = new StringTokenizer(hierarchy, "/");
		ArrayList<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		String path = NameUtiles.convertToValidCharName(hierarchy);
		user = user + "_";
		String txt = "_txt";
		HashMap<String, String> data = new HashMap<String, String>();
		for (int i = 0; i < tokens.size(); i++) {
			String field_name = user + "_" + i + "_" + path + txt;
			data.put(field_name, tokens.get(i));
		}
		return data;
	}

	public String getLAC() {
		return null;
	}

}
