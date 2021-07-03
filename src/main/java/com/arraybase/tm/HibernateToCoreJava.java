package com.arraybase.tm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class HibernateToCoreJava {

	private static GBLogger logger = GBLogger.getLogger(HibernateToCoreJava.class);

	public static TTable convert(TTable tm) {
		tm.setLastEdited(convert(tm.getLastEdited()));
		tm.setSubitems(convert(tm.getSubitems()));
		tm.setServer(convert(tm.getServer()));
		convert(tm.getSettings());
		return tm;
	}

	private static void convert(TMTableSettings settings) {
		if (settings != null) {
			settings.setProperties(convert(settings.getProperties()));
			settings.setCol_order(convert(settings.getCol_order()));
		}
	}

	private static Date convert(Date createDate) {
		if (createDate != null) {
			if (createDate instanceof java.sql.Timestamp) {
				Date convertedDate = new Date(createDate.getTime());
				return convertedDate;
			} else if (createDate instanceof java.sql.Date) {
				Date convertedDate = new Date(createDate.getTime());
				return convertedDate;
			}
		}
		return null;
	}

	public static ArrayList convert(List s) {
		if (s == null || s.size() <= 0)
			return new ArrayList();
		String className = s.getClass().getCanonicalName();
		logger.info("converting " + className + " to ArrayList ");
		ArrayList hs = new ArrayList();
		if (s == null)
			return hs;
		for (Iterator iterator = s.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof TNode) {
				hs.add(convert((TNode) object));
			} else
				hs.add(object);
		}
		logger.info(" arraylist has been successfully created with : "
				+ hs.size() + " count ");
		return hs;
	}

	public static Object deepCopy(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			Object object_ = new ObjectInputStream(bais).readObject();
			return object_;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Set convert(Set subitems) {

		LinkedHashSet lo = new LinkedHashSet();
		for (Object o : subitems) {
			if (o instanceof TNode) {
				lo.add(convert((TNode) o));
			}
		}
		return lo;
	}

	/**
	 * convert the map to a java.util.Map
	 * 
	 * @param _map
	 * @return
	 */
	public static Map convert(Map _map) {
		LinkedHashMap map = new LinkedHashMap();
		Set keys = _map.keySet();
		for (Object key : keys) {
			Object value = _map.get(key);
			if (value instanceof TNode) {
				value = convert((TNode) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static TNode convert(TNode cvalue) {
		List<Integer> refs = cvalue.getReference();
//		refs = convert(refs);
		cvalue.setReference(refs);
		return cvalue;
	}

	private static List<TNode> convertTMNode_c(List<TNode> s) {
		String className = s.getClass().getCanonicalName();
		logger.info("converting " + className + " to ArrayList ");
		if (className.equalsIgnoreCase("java.util.ArrayList"))
			return (ArrayList) s;
		ArrayList<TNode> hs = new ArrayList<TNode>();
		if (s == null || s.size() <= 0)
			return hs;
		for (TNode t : s) {
			hs.add(convert(t));
		}
		logger.info(" arraylist has been successfully created with : "
				+ hs.size() + " count ");

		return hs;
	}

	public static TMNodeSet convert(TMNodeSet set) {
		TNode node = convert(set.getParent());
		set.setParent(node);
		set.setSub(convert(set.getSub()));
		return set;
	}

}
