package com.arraybase;

import java.util.Map;

public class SearchConfig {
	private int mode = 0;
	public final static int RAW_SEARCH = 0;
	public final static int NODE_CONFIG = 1;

	public final static String prependQ = "prependQuery";
	public final static String appendQ = "appendQuery";
	public final static String modeQ = "modQuery";
	private Map<String, String> config_props = null;

	public SearchConfig() {

	}

	public SearchConfig(int _mode) {
		mode = _mode;
	}

	public Map<String, String> getConfigProperties() {
		return config_props;
	}

	public void setConfigProperties(Map<String, String> config) {
		config_props = config;
	}

	/**
	 * Modify the search string based on the node properties.
	 * 
	 * @param _search
	 * @param node
	 * @return
	 */
	public String updateSearchString(String _search, Map<String, String> node) {
		String newString = _search;
		if (node == null)
			return _search;
		if (node.containsKey(prependQ)) {
			String value = node.get(prependQ).toString().trim();
			if (value.startsWith("*") || value.startsWith("AND")
					|| value.startsWith("OR")) {
				GB.print(" The desired prepend parameters are not correct.  Will search without them");
			} else {

				if (_search.contains(":")) {
					String[] sp = _search.split("\\s+");
					if (sp.length > 0) {
						if (sp[0].equals("*:*")) {
							newString = _search;
						} else
							newString = node.get(prependQ).toString() + " "
									+ _search;
					} else
						newString = node.get(prependQ).toString() + " "
								+ _search;
				} else
					newString = node.get(prependQ).toString() + " " + _search;
			}
		}

		if (node.containsKey(appendQ)) {
			newString = newString + " " + node.get(appendQ).toString();
		}

		if (node.containsKey(modeQ)) {
			String value = node.get(modeQ).toString();
			newString = value.replaceAll("(\\$query)", _search);
		}
		return newString;
	}

	public int getMode() {
		return mode;
	}

	public String updateSearchString(String _searchString) {
		return updateSearchString(_searchString, config_props);
	}

	private String[] distinct_fields = null;
	
	public String[] getDistinctFields() {
		return distinct_fields;
	}

}
