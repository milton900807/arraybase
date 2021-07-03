package com.arraybase.var.app;

public class GenomicPosition {

	private String raw = null;
	private String chrom = null;
	private long start = -3l;
	private long end = -2l;
	private char strand = '?';

	public GenomicPosition(String raw) {
		build(raw);

	}

	private void build(String raw) {
//      -13:19255852-19255833 (1-20) 100%
//		+2:21239503-21239522  (1-20)   100%
		String r = raw.trim();
		if (r.startsWith("+")) {
			strand = '+';
		} else
			strand = '-';
		int tst = r.indexOf(':') + 1;
		int minus = r.indexOf('-', tst);
		int end = r.indexOf('(');
		String start_number = r.substring(tst, minus);
		String end_number = r.substring(minus + 1, end);

		Number snum = verifyNumber(start_number);
		Number endnum = verifyNumber(end_number);
		
		System.out.println ( " start " + snum.toString() + "  TO  "  + endnum );
		
	}

	private Number verifyNumber(String num) {

		try {
			Number number = Long.parseLong(num.trim());
			if (number != null)
				return number;
		} catch (NumberFormatException exception) {
			exception.printStackTrace();
		}
		return null;

	}

}
