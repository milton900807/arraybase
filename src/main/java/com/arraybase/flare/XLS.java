package com.arraybase.flare;

import com.arraybase.AB;
import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.modules.GBTypes;
import com.arraybase.modules.UsageException;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XLS {

	private static GBLogger log = GBLogger.getLogger(XLS.class);
	private static SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyy");

	public static void main(String[] args) {
		try {
			// test for loading multiple sheets.
			GB.gogb("import",
					"-user=jeff",
					"-type=XLSX",
					"C:\\Users\\jmilton\\dev\\ab\\src\\test\\resources\\xls_load\\multiple_sheet_sample.xlsx",
					"/test2/sample");
			// test for loading a single xls sheet.
			// GB.gogb("import -user=jeff -type=XLSX /src/test/resources/sample.xslx /test/sample");
		} catch (UsageException e) {
			e.printStackTrace();
		}
	}

	public static void main___(String[] _args) {
		FileInputStream st = null;
		FileInputStream st2 = null;
		try {
			LoadTableToSolr sl = new LoadTableToSolr();
			File f = new File("./test/date_loader_test.xls");
			if (f.exists()) {
				log.info(" We have found the file ");
			} else {
				System.out.println(" privde input file ");
				GB.exit(1);
			}

			st = new FileInputStream(f);
			XLSObject _ob = sl.createGMObject(st, "hello_world");
			st2 = new FileInputStream(f);

			POIFSFileSystem poifs = new POIFSFileSystem(st2);
			HSSFWorkbook wb = new HSSFWorkbook(poifs);
			HSSFSheet sheet1 = wb.getSheetAt(0);
			if (sheet1 == null) {
			}
			int rowcount = sheet1.getLastRowNum();
			int startrow = sheet1.getFirstRowNum();
			String[] types = _ob.getTypes();
			String[] fields = _ob.getFields();
			String fi = "";
			for (int i = startrow + 4; i < rowcount; i++) {
				HSSFRow row = sheet1.getRow(i);
				if (row == null && i < rowcount) {
					continue;
				}
				String row_str = "";
				for (int col = 0; col < fields.length; col++) {
					HSSFCell cell = row.getCell(col);
					String t = XLS.getStringValue(cell);
					t = XLS.tryToParse(t, types[col]);
					row_str += fields[col] + "=" + t + "\t";
					if (fields[col] == null || fields[col].length() <= 0) {
					}
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(st);
			IOUTILs.closeResource(st2);
		}
	}

	// The lexical space of dateTime consists of finite-length sequences of
	// characters of the form: '-'? yyyy '-' mm '-' dd 'T' hh ':' mm ':' ss ('.'
	// s+)? (zzzzzz)?, where
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss'Z'");

	public static String getStringValue(HSSFCell cell) {

		if (cell == null)
			return null;
		int type = cell.getCellType();
		if (type == HSSFCell.CELL_TYPE_BLANK) {
			return "";
		} else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
			return "" + cell.getBooleanCellValue();
		} else if (type == HSSFCell.CELL_TYPE_ERROR) {
			return "" + cell.getErrorCellValue();
		} else if (type == HSSFCell.CELL_TYPE_FORMULA) {
			return "" + cell.getCellFormula();
		} else if (type == HSSFCell.CELL_TYPE_NUMERIC) {
			return "" + cell.getNumericCellValue();
		} else if (type == HSSFCell.CELL_TYPE_STRING) {
			return "" + cell.getStringCellValue();
		} else
			return null;
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
					Date date = HSSFDateUtil.getJavaDate(i, true);
					return format.format(date);
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

	private static Pattern pp = Pattern.compile("[0-9]+(\\.[0-9]+)?");

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

	final static SimpleDateFormat sf = new SimpleDateFormat("yyyy,MM,dd");
	final static SimpleDateFormat sf2 = new SimpleDateFormat("yyyy/MM/dd");

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

	private static boolean typeis(String _type, String... t) {
		if (_type == null)
			return false;
		for (String _t : t) {
			if (_type.equalsIgnoreCase(_t))
				return true;
		}
		return false;
	}

	/**
	 * Load multiple sheets from a workbook as mutliple tables
	 * 
	 * @param u
	 * @param wb
	 * @param ab_file
	 * @throws FieldTitleNotFoundException
	 */
	public static void loadMultipleSheets(String u, XSSFWorkbook wb,
			String ab_file) throws FieldTitleNotFoundException {
		int count = wb.getNumberOfSheets();
		for (int i = 0; i < count; i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			String file = ab_file + "/"
					+ NameUtiles.convertToValidCharName(sheet.getSheetName());
			loadSheet(sheet, file);
		}
	}

	
	/**
	 *  Load an excel sheet
	 * @param sheet
	 * @param abfile
	 * @throws FieldTitleNotFoundException
	 */
	private static void loadSheet(XSSFSheet sheet, String abfile)
			throws FieldTitleNotFoundException {
		Map<String, String> schema = createschema(sheet);
		AB.createTable(abfile, schema);
		int start_row = 1;
		if (containsTypes(sheet)) {
			// start with third row.
			start_row = 2;
		}
		int index = 0;
		ABTable table = new ABTable(abfile);
		Iterator<Row> rows = sheet.rowIterator();
		int INCREMENT = 1000000;
		int count = 0;
		ArrayList<LinkedHashMap<String, Object>> values = new ArrayList<LinkedHashMap<String, Object>>();
		while (rows.hasNext()) {
			Row r = rows.next();
			if (index >= start_row) {
				LinkedHashMap<String, Object> row_values = getValues(r, schema);
				values.add(row_values);
				if (count >= INCREMENT) {
					table.append(values);
					values = new ArrayList<LinkedHashMap<String, Object>>();
					count = 0;
				}
				count++;
			}
			index++;
		}
		table.append(values);
	}

	/**
	 * 
	 * @return
	 */
	private static LinkedHashMap<String, Object> getValues(Row r,
			Map<String, String> schema) {
		LinkedHashMap<String, Object> row_values = new LinkedHashMap<String, Object>();
		Set<Entry<String, String>> title = schema.entrySet();
		int cell_index = 0;
		for (Entry<String, String> ti : title) {
			String key = ti.getKey();
			String value = ti.getValue();
			Cell cell = r.getCell(cell_index);
			if (cell != null) {
				Object object_value = getValue(cell);
				if (object_value instanceof String)
					row_values.put(key.trim(), object_value.toString().trim());
				else
					row_values.put(key.trim(), object_value);
			}
			cell_index++;
		}
		return row_values;
	}

	private static Object getValue(Cell cell) {
		if (cell == null)
			return null;
		int type = cell.getCellType();
		if (type == XSSFCell.CELL_TYPE_BLANK) {
			return "";
		} else if (type == XSSFCell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		} else if (type == XSSFCell.CELL_TYPE_ERROR) {
			return cell.getErrorCellValue();
		} else if (type == XSSFCell.CELL_TYPE_FORMULA) {
			return cell.getCellFormula();
		} else if (type == XSSFCell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue();
		} else if (type == XSSFCell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		} else
			return null;
	}

	private static boolean containsTypes(XSSFSheet sheet) {
		int first_row_number = sheet.getFirstRowNum();
		XSSFRow second = sheet.getRow(first_row_number + 1);
		if (second != null) {
			return determineIfThisRowContainsTypes(second);
		}
		return false;
	}

	private static Map<String, String> createschema(XSSFSheet sheet)
			throws FieldTitleNotFoundException {
		int first_row_number = sheet.getFirstRowNum();

		// {{ DETERMINE IF THE SECOND ROW CONTAINS TYPE VALUES... IF SO WE CAN
		// USE THESE... OTHERWISE LOAD THEM AS VALUES }}
		boolean hasTypes = false;
		XSSFRow second = sheet.getRow(first_row_number + 1);
		if (second != null) {
			hasTypes = determineIfThisRowContainsTypes(second);
		}

		XSSFRow row = sheet.getRow(first_row_number);
		Iterator<Cell> it = row.cellIterator();
		int cell_field_index = 0;
		ArrayList<String> titles = new ArrayList<String>();
		while (it.hasNext()) {
			Cell c = it.next();
			String cell_value = c.getStringCellValue();
			if (cell_value == null || cell_value.length() <= 0)
				throw new FieldTitleNotFoundException(c.getSheet()
						.getSheetName() + ": " + cell_field_index);
			cell_field_index++;
			titles.add(cell_value);
		}

		LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
		if (hasTypes)
			types = createSchema(titles, sheet.getRow(first_row_number + 1));
		else {
			for (String t : titles) {
				types.put(t.trim(), "string_ci");
			}
		}
		return types;
	}

	/**
	 * 
	 * @param titles
	 * @param row
	 * @return
	 */
	private static LinkedHashMap<String, String> createSchema(
			ArrayList<String> titles, XSSFRow row) {
		LinkedHashMap<String, String> ch = new LinkedHashMap<String, String>();
		Iterator<Cell> it = row.cellIterator();
		int index = 0;
		while (it.hasNext()) {
			Cell c = it.next();
			String cell_value = c.getStringCellValue();
			String title = titles.get(index);
			if (cell_value != null)
				cell_value = cell_value.trim();
			ch.put(title.trim(), cell_value);
			index++;
		}
		return ch;
	}

	/**
	 * 
	 * @param second
	 * @return
	 */
	private static boolean determineIfThisRowContainsTypes(XSSFRow second) {
		Iterator<Cell> it = second.iterator();
		boolean test = true;
		while (it.hasNext()) {
			Cell c = it.next();
			try {
				String cell_value = c.getStringCellValue();
				if (cell_value == null || cell_value.length() <= 0)
					return false;
				if (!GBTypes.isValidType(cell_value))
					test = false;
			} catch (Exception _e) {
				return false;
			}
		}
		return test;
	}

	public static void loadSingleSheet(String u, XSSFWorkbook wb, String ab_file) {
		XSSFSheet sheet = wb.getSheetAt(0);
		String file = ab_file + "/"
				+ NameUtiles.convertToValidCharName(sheet.getSheetName());
		try {
			loadSheet(sheet, file);
		} catch (FieldTitleNotFoundException e) {
			e.printStackTrace();
			GB.print ( e.getLocalizedMessage() );
		}
	}}
