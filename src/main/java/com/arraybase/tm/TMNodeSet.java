package com.arraybase.tm;

import java.util.List;

import com.arraybase.tm.tree.TNode;

/**
 * DataObject with parent and children nodes this is mostly used for passing
 * objects to the client side.
 * 
 * @author donaldm
 * 
 */
public class TMNodeSet {
	private TNode tmnode = null;
	private List<TMNodeSet> sub = null;

	public TMNodeSet(TNode t) {
		tmnode = t;
	}

	public TMNodeSet() {
	}

	public void setSub(List<TMNodeSet> _sub) {
		sub = _sub;
	}

	public void setParent(TNode tm) {
		tmnode = tm;
	}

	public TNode getParent() {
		return tmnode;
	}

	public List<TMNodeSet> getSub() {
		return sub;
	}
}
