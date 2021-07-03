package com.arraybase.tm;

public class DefaultWhereClause implements WhereClause {

	private String left = null;
	private String right = null;
	private String operation = null;

	private String joinExpression = null;
	private WhereClause joinTo = null;

	public DefaultWhereClause(String _field_l, String _field_r) {
		left = _field_l;
		right = _field_r;
		operation = "equals";
	}

	public DefaultWhereClause(String _left, String _operator, String _right) {
		left = _left;
		operation = _operator;
		right = _right;
	}

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	public String getOperator() {
		return operation;
	}

	public void setJoinExpression(String string) {
		joinExpression = string;
	}

	public void setExpressionClause(WhereClause _clause) {
		joinTo = _clause;
	}

	public String getJoinExpression() {
		return joinExpression;
	}
}
