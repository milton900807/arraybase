package com.arraybase.lac;

public class LACActionFactory {
	public static LACAction create(String _target, String _action, String _data) {

		LACAction la = null;
		if (_action.equalsIgnoreCase(LACAction.ANNOTATE)) {
			return new AnnotationLACAction(_target, _data);
		}
		if (_action.equalsIgnoreCase(LACAction.CREATE_PROJECT_ROW)) {
			return new ProjectRowLACAction(_target, _data);
		} else if (_action.equals(LACAction.LIST_FIELDS)) {
			return new ListFieldsLAC(_target);
		} else if (_action.equalsIgnoreCase(LACAction.SEARCH_CORE)) {
			return new SearchCoreLACAction(_target, _action, _data);
		}
		return new SearchLACAction(_target, _data);
	}

	public static LACAction create(String _lac) {
		String[] lac = LAC.parse(_lac);
		return create(lac[0], lac[1], lac[2]);
	}
}
