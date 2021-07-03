package com.arraybase.qmath.flexigraph;

public final class GlobalTime
{
	
	private static long seconds = 1L;
	
	public synchronized static void setSeconds ( long _seconds )
	{
		seconds = _seconds;
	}
	
	public synchronized static long getSeconds ()
	{
		return seconds;
	}
	
	
	
}
