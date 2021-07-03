package com.arraybase.flare.parse;

import com.arraybase.util.GBLogger;

public class ParseFactory {
	
	private static final String PDF = "pdf";
	private static final String DOC = "doc";
	private static final String XLS = "xls";
	private static final String PPT = "ppt";
	private static final String XML = "xml";
	
	private static GBLogger log = GBLogger.getLogger ( ParseFactory.class );
	
	public static GBParser getParser(String _type) {
		_type = _type.toLowerCase();
		_type = _type.trim();
		if (_type.endsWith(PDF)) {
			return new PDFGBParser();
		} else if (_type.endsWith(DOC)) {
			return new WordDocumentGBParser();
		} else if (_type.endsWith(XLS)) {
			return new XLSGBParser();
		} else if (_type.endsWith(PPT)) {
			return new PowerPointGBParser();
		} else if ( _type.endsWith (XML)){
			return new ABXMLParser ();
		}
		
		
		log.debug ( " name  " + _type );
		
		GBParser defaultParser = new DefaultGBParser();
		return defaultParser;
	}
	// public GBParser getParser ( String _type ){
	// return new TestGBTikaParser ();
	// }

}
