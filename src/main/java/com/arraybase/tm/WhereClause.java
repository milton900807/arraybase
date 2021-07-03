package com.arraybase.tm;

import java.util.Set;

public interface WhereClause {

	String getLeft();

	String getRight();

	String getOperator();

	void setJoinExpression(String string);

	String getJoinExpression();

}
