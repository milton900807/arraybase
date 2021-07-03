package com.arraybase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ABFieldIterator implements Iterator {

	private Iterator<ArrayList<LinkedHashMap<String, Object>>> it = null;
	private String field = null;
	private ArrayList<LinkedHashMap<String, Object>> current = null;
	private int index = 0;

	public ABFieldIterator(
			Iterator<ArrayList<LinkedHashMap<String, Object>>> it, String field) {
		this.it = it;
		this.field = field;
	}

	public boolean hasNext() {

		if (current == null) {
			if (it.hasNext()) {
				current = it.next();
				if (current == null)
					return false;

			} else
				return false;
		}

		return index < current.size();

	}

	public Object next() {

		if (current != null && index < current.size())
			return getNext(field);
		else if (index >= current.size()) {
			increment_list();
			return next();
		}

		return null;
	}

	private Object getNext(String _field) {
		LinkedHashMap<String, Object> row = current.get(index++);
		return row.get(_field);
	}

	private void increment_list() {
		index = 0;
		if (it.hasNext())
			current = it.next();
		else {
			index = -1;
			current = null;
		}
	}

	public void remove() {
		it.remove();
	}

}
