package com.arraybase.tm;

import com.arraybase.util.GBLogger;
import com.arraybase.util.Level;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import java.lang.reflect.Field;
import java.util.*;

public class ResultsFactory {

	private static GBLogger log = GBLogger.getLogger(ResultsFactory.class);
	static {
		log.setLevel(Level.DEBUG);
	}

	public static GResults buildBeanResults(String _schema,
			QueryResponse response) {
		try {
			Class cla = Thread.currentThread().getContextClassLoader()
					.loadClass("com.tissuematch.tmcm.schema." + _schema);
			List pojolist = response.getBeans(cla);
			ArrayList resultList = new ArrayList();
			for (int i = 0; i < pojolist.size(); i++) {
				Object object = pojolist.get(i);
				resultList.add(pojolist.get(i));
			}
			// build the column property list
			Field[] fields = cla.getDeclaredFields();
			ArrayList<GColumn> colist = new ArrayList<GColumn>();
			for (int i = 0; i < fields.length; i++) {
				Class type = fields[i].getType();
				GColumn cp = new GColumn();
				cp.setType(type.getName());
				cp.setTitle(fields[i].getName());
				cp.setWidth(10000);
				colist.add(cp);
			}
			GResults r = new GResults();
			r.setColumns(colist);
			r.setTarget(_schema);
			r.setTotalHits(response.getResults().size());
			r.setType(_schema);
			r.setSuccessfulSearch(true);
			r.setValues(resultList);
			return r;
		} catch (Exception _e) {
			log.error(" Failed to load  the class : " + _schema);
			_e.printStackTrace();
		}
		log.info("Failed to bulid the results.. returning null results");
		return null;
	}

	public static GResults buildResults(String _schema, int start, int increment, QueryResponse response) {
		try {
			SolrDocumentList list = response.getResults();
			NamedList tresponse = response.getResponse();
			SolrDocumentList response_object = (SolrDocumentList) tresponse
					.get("response");
			long numfound = response_object.getNumFound();
			
			Long numFound_l = new Long(numfound);
			GResults r = new GResults();
			if (numfound <= 0) {
				r.setSuccessfulSearch(true);
				r.setMessage("No values found");
				return r;
			}

			if ( increment > list.size())
				increment = list.size ();
			
			// log.info("We have the solr document list " + list.size());
			ArrayList resultList = new ArrayList();
			Collection<String> fields__ = null;
			for (int i = 0; i < increment; i++) {
				// we can create the generic result object here.
				SolrDocument doc = list.get(i);
				if (i == 0) {
					fields__ = doc.getFieldNames();
				}
				resultList.add(buildResult(doc));
			}
			r.setTotalHits(numFound_l.intValue());
			r.setSuccessfulSearch(true);
			r.setValues(resultList);

			
			
			if (fields__ == null)
				return r;

			String[] fields = new String[fields__.size()];
			int index = 0;
			for (String c : fields__) {
				fields[index++] = c;
			}
			ResultMetaData rs = new ResultMetaData();
			HashMap<String, String> tableMap = new HashMap<String, String>();
			for (int i = 0; i < fields.length; i++) {
				tableMap.put(fields[i], fields[i]);
				rs.addColumn(fields[i], fields[i], "", 100, true);
			}
			r.setResultDescriptor(rs);
			if (_schema != null) {
				r.setTarget(_schema);
				r.setType(_schema);
			}
			r.setTotalHits(numFound_l.intValue());
			r.setSuccessfulSearch(true);
			r.setValues(resultList);
			
			
			return r;
		} catch (Exception _e) {
			log.error(" Failed to load  the class : " + _schema);
			_e.printStackTrace();
		}
		log.info("Failed to bulid the results.. returning null results");
		return null;
	}

	public static GResults buildResults(int start, int increment, QueryResponse response) {
		return buildResults(null, start, increment, response);
	}


	private static GRow buildResult(SolrDocument doc) {

		Collection<String> collection = doc.getFieldNames();
		HashMap map = new HashMap();

		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object object = doc.getFieldValue(key);
			map.put(key, object);
		}
		GRow r = new GRow();
		r.setData(map);

		return r;
	}

	/**
	 * This is a union table descriptor object
	 * 
	 * @param rrd
	 * @param lrd
	 * @deprecated
	 * @return
	 */
	public static ResultMetaData unionResultDesciptor(ResultMetaData rrd,
			ResultMetaData lrd) {
		ResultMetaData newr = new ResultMetaData();

		HashMap<String, String> fields_r_map = rrd.getFieldMap();
		HashMap<String, String> fields_l_map = lrd.getFieldMap();
		HashMap<String, String> type_map_r = rrd.getTypeMap();
		HashMap<String, String> type_map_l = lrd.getTypeMap();

		// merge the fields and the type first and then merge the columns
		HashMap<String, String> merged_fields = new HashMap<String, String>();
		Set<String> setr = fields_r_map.keySet();
		Set<String> setl = fields_l_map.keySet();
		for (String sr : setr) {
			String sr_f = fields_r_map.get(sr);
			if (fields_l_map.get(sr) != null) {
				merged_fields.put(sr + "_r", sr);
			} else
				merged_fields.put(sr, sr);
		}
		for (String sl : setl) {
			String sl_f = fields_l_map.get(sl);
			if (fields_r_map.get(sl) != null) {
				merged_fields.put(sl + "_l", sl);
			} else
				merged_fields.put(sl, sl);
		}
		// now we do the types
		HashMap<String, String> merged_types = new HashMap<String, String>();
		Set<String> setrt = type_map_r.keySet();
		Set<String> setlt = type_map_l.keySet();
		for (String sr : setrt) {
			String sr_t = type_map_r.get(sr);
			if (type_map_l.get(sr) != null)
				merged_types.put(sr + "_r", sr_t);
			else
				merged_types.put(sr, sr_t);
		}
		for (String sl : setlt) {
			String sl_t = type_map_l.get(sl);
			if (type_map_r.get(sl) != null)
				merged_types.put(sl + "_l", sl_t);
			else
				merged_types.put(sl, sl_t);
		}

		newr.setFieldMap(merged_fields);
		newr.setTypeMap(merged_types);

		Set<String> mfields = merged_fields.keySet();

		// for (String field : mfields) {
		// String mp = merged_fields.get(field);
		// newr.addColumn(field, mp, "", 300, true);
		// }
		return newr;
	}

	public static HashMap<String, String> mapFields(String target,
			HashMap<String, String> fieldMap) {
		HashMap<String, String> nfields = new HashMap<String, String>();
		Set<String> f = fieldMap.keySet();
		for (String field : f) {
			nfields.put(field, target + "_" + field);
		}
		return nfields;
	}

	public static HashMap<String, String> mapTypes(String target,
			HashMap<String, String> _types, HashMap<String, String> _field_map) {
		HashMap<String, String> newFieldMap = new HashMap<String, String>();
		Set<String> fields = _types.keySet();
		for (String f : fields) {
			newFieldMap.put(_field_map.get(f), _types.get(f));
		}
		return newFieldMap;
	}

}
