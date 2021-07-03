package com.arraybase;

import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;

import java.io.IOException;

public class GBAdmin {

	
	/**
	 * @param url: The location of the solr instance (excluding the core name)
	 * @param coreName: The solr core name 
	 * @throws AdminRequestFailed
	 */
	public static void removeCore(String url, String coreName) throws AdminRequestFailed {
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(url).build();
			CoreAdminRequest request = new CoreAdminRequest();
			CoreAdminAction co = CoreAdminAction.UNLOAD;
			request.setAction(co);
			request.setCoreName(coreName);
			try {
				request.process(solr);
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			IOUTILs.closeResource(solr);
		}
		throw new AdminRequestFailed ( "Failed to remove the core " + url + " with core name " + coreName);
	}
	
	
	

}
