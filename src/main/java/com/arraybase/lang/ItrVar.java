package com.arraybase.lang;

import java.util.Iterator;

import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;

public class ItrVar extends GBV<Iterator> {
	public ItrVar(Iterator f) {
		super (f);
	}
	
	public void reset ( ){
		Iterator it = get();
		if ( it instanceof GBSearchIterator )
		{
			GBSearchIterator gbi = (GBSearchIterator)it;
			gbi.reset ();
		}
	}
	public String getPath ()
	{
		Iterator it = get();
		if ( it instanceof GBSearchIterator )
		{
			GBSearchIterator gbi = (GBSearchIterator)it;
			return gbi.getPath();
		}
		return null;
		
	}
	
	
	
}
