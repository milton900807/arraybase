package com.arraybase.tm;

import java.net.ConnectException;

import com.arraybase.GB;
import com.arraybase.lac.LAC;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;



public class FieldNode extends TNode {

	private TNode parent = null;
	private String path = null;
	private String type = "Field";
	private String field_name = null;

	private static GBLogger log = GBLogger.getLogger(FieldNode.class);
	static {
		log.setLevel(GBLogger.DEBUG);
	}

	public FieldNode(TNode _parent, String _path) {
		parent = _parent;
		path = _path;
		field_name = GBPathUtils.getLeaf(path);
		setName ( field_name);
		setLink ( parent.getLink() );
		setNode_id(-1);
		setCreated_by(parent.getCreated_by());
		setCreatedDate(parent.getCreatedDate());
		setNodeType(parent.getNodeType() + "/" + type);
	}

	public String getPath() {
		return path;
	}

	public String getNodeType() {
		log.debug("Getting the node type");

		String lac = parent.getLink();
		log.debug(lac);

		String target = LAC.getTarget(lac);
		log.debug(target);
		try {
			GColumn col = GB.getGBTables().getField(target, field_name);
			String type = col.getType();
			return type;
		} catch (ConnectException e) {
			e.printStackTrace();
		}

		return null;
	}

	public TNode getParent() {
		return parent;
	}

}
