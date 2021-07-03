package com.arraybase.flare.parse;

import com.arraybase.io.GBBlobFile;

public interface GBParser {
	GBStructuredContent parse(GBBlobFile _in) throws GBParseException;
}
