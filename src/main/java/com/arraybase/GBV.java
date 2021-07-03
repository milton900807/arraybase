package com.arraybase;

public class GBV<T> {

	private T t = null;

	public GBV(T t) {
		set(t);
	}

	public void set(T t) {
		this.t = t;
	}

	public T get() {
		return this.t;
	}

	public String toString() {
		if (t == null)
			return "null variable";
		return t.toString();
	}
	
	public void reset ()
	{
		
	}


}
