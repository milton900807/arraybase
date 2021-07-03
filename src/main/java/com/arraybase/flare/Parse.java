package com.arraybase.flare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arraybase.flare.parse.GBParseException;
import com.arraybase.util.GBLogger;

public class Parse {

	private static GBLogger log = GBLogger.getLogger(XLS.class);
	private static SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyy");
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss'Z'");
	final static SimpleDateFormat sf = new SimpleDateFormat("yyyy,MM,dd");
	final static SimpleDateFormat sf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static Pattern pp = Pattern.compile("[0-9]+(\\.[0-9]+)?");

	public static boolean isAType(String _type) {
		return typeis(_type, "integer", "int", "sint", "Integer", "i", "I")
				|| typeis(_type, "double", "sdouble", "Double", "D", "d")
				|| typeis(_type, "string", "text", "String", "S", "s")
				|| typeis(_type, "float", "sfloat", "Float", "F", "f")
				|| typeis(_type, "Date", "date");
	}

	public static String tryToParse(String _value, String _type)
			throws LoaderException, GBParseException {
		if (_value == null)
			return "";

		if (typeis(_type, "integer", "int", "sint", "Integer", "i", "I")) {
			try {
				if (_value.contains(".")
						&& ((_value.contains("E") || _value.contains("e")))) {
					Long d = Double.valueOf(_value).longValue();
					Integer i = d.intValue();
					return i.toString();
				} else if (_value.contains(".")) {
					Long d = Double.valueOf(_value).longValue();
					Integer i = d.intValue();
					return i.toString();
				}
				Integer i = int_field_manager(_value);
				// Integer i = Integer.parseInt(_value);
				return i.toString();
			} catch (Exception _e) {
				_e.printStackTrace();
				return "";
			}
		} else if (typeis(_type, "double", "sdouble", "Double", "D", "d")) {
			try {

				if (_value.contains(".")
						&& ((_value.contains("E") || _value.contains("e")))) {
					Long d = Double.valueOf(_value).longValue();
					Double i = d.doubleValue();
					return i.toString();
				}
				Double i = parseDouble(_value);
				return i.toString();
			} catch (Exception _e) {
				_e.printStackTrace();
				return "";
			}
		} else if (typeis(_type, "string", "text", "String", "S", "s")) {
			try {
				return _value;
			} catch (Exception _e) {
				_e.printStackTrace();
				return "";
			}
		} else if (typeis(_type, "float", "sfloat", "Float", "F", "f")) {
			try {
				if (_value.contains(".")
						&& ((_value.contains("E") || _value.contains("e")))) {
					Long d = Double.valueOf(_value).longValue();
					Float i = d.floatValue();
					return i.toString();
				}
				Float i = parseFloat(_value);
				return i.toString();
			} catch (Exception _e) {
				_e.printStackTrace();
				throw new GBParseException(_value + " is not of type : "
						+ _type);
			}
		} else if (typeis(_type, "Date", "date")) {
			if (_value == null || _value.length() <= 0
					|| _value.equalsIgnoreCase("null")) {
			} else {
				// log.info("value : " + _value);
				try {
					// I would love a way to catch all the variations that can
					// be updated here... in some way a continuous integration
					if (_value.startsWith("=")
							|| _value.toUpperCase().startsWith("DATE")) {
						return format.format(parseDate(_value));
					}

					// if the value contains a reference to a format
					// that uses slashes
					// let's try to parse it.
					if (_value.contains("/")) {
						try {
							Date df = format2.parse(_value);
							return format.format(df);
						} catch (Exception _e) {
							// we should add another format here.
							// but will have to do this later. (time)

						}
					}

					Double i = Double.parseDouble(_value);
					log.info("\t\t\t\t\t\t" + i);
					return format.format(i);
					// log.info(" we have the date object : " +
					// format.format(date) + " vs " + format.format(dd));
				} catch (Exception _e) {
					_e.printStackTrace();
					return format.format(new Date(1000, 1, 1));
				}
			}

		}
		return _value;
	}

	private static Float parseFloat(String _value) throws GBParseException {
		if (_value != null) {
			Matcher m = pp.matcher(_value);
			if (m.matches()) {
				try {
					return Float.parseFloat(_value);
				} catch (NumberFormatException _f) {
					_f.printStackTrace();
					throw new GBParseException(_value + " is not a float ");
				}
			} else {
				throw new GBParseException(_value + " is not a float type");
			}
		}
		throw new GBParseException(_value + " not a float ");
	}

	private static Double parseDouble(String _value) {
		if (_value != null) {
			Matcher m = pp.matcher(_value);
			if (m.matches()) {
				try {
					return Double.parseDouble(_value);
				} catch (NumberFormatException _f) {
					_f.printStackTrace();
				}
			} else {
				System.err.println(" failed to parse the float : " + _value);
			}
		}
		return -1d;
	}

	private static Date parseDate(String _value) {

		int index = _value.indexOf("(");
		int end_index = _value.lastIndexOf(')');
		String sub = _value.substring(index + 1, end_index);
		sub = sub.trim();
		try {
			Date d = sf.parse(sub);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();

			try {
				Date d = sf2.parse(sub);
				return d;
			} catch (ParseException e2) {
				e2.printStackTrace();
			}

			return null;
		}
	}

	private static Integer int_field_manager(String _value) {
		if (_value != null) {
			Matcher m = pp.matcher(_value);
			if (m.matches()) {
				try {
					return Integer.parseInt(_value);
				} catch (NumberFormatException _f) {
					_f.printStackTrace();

					Double d = Double.parseDouble(_value);
					Integer i = d.intValue();
					return i;

				}
			} else {
				System.err.println(" failed to parse the integer value ->  "
						+ _value + "<- ");
			}
		}
		return -1;
	}

	private static boolean typeis(String _type, String... t) {
		if (_type == null)
			return false;
		for (String _t : t) {
			if (_type.equalsIgnoreCase(_t))
				return true;
		}
		return false;
	}
}
