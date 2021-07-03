package com.arraybase.flare;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.arraybase.util.IOUTILs;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.util.ABProperties;
import com.arraybase.util.Level;
import com.arraybase.util.GBLogger;

/**
 * Load
 * 
 * @author donaldm
 */
public class LoadTableToSolr {
	private static GBLogger lg = GBLogger.getLogger(LoadTableToSolr.class);
	public static final int TITLE = 1;
	private static final int TYPE = 2;
	private static final int ANNOTATION = 3;
	private static final int COMMENTS = 4;

	public static void main(String[] _args) {
		try {
			LoadTableToSolr sl = new LoadTableToSolr();
			File f = new File("./test/htl_request_.xls");
			FileInputStream st = new FileInputStream(f);
			XLSObject xls_ob = sl.createGMObject(st, "hello_world");
			String[] fields = xls_ob.getFields();
			for (String field : fields) {
				System.out.println(" fields : " + field);
			}
			System.out.println(" XLS ANNOTATION FILE COMPLETE ");
			FileInputStream st2 = new FileInputStream(f);

			xls_ob.setName("milton_Repository_xls8");
			load(st2, xls_ob);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (LoaderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempt to see if the input document is a correctly formated xls file.
	 * 
	 * @return
	 */
	public static boolean isCorrectlyFormatted(InputStream _fin) {
		ErrorLog el = new ErrorLog();
		POIFSFileSystem poifs;
		try {
			BufferedInputStream fin = new BufferedInputStream(_fin);
			poifs = new POIFSFileSystem(fin);
			String short_description = poifs.getShortDescription();
			DirectoryNode node = poifs.getRoot();
			Iterator it = node.getEntries();
			HSSFWorkbook wb = new HSSFWorkbook(poifs);
			// first we will load the first sheet
			HSSFSheet sheet1 = wb.getSheetAt(0);
			if (sheet1 == null) {
				el.setMsg("The \"Form 0\" sheet is not accessible");
				return false;
			}
			int rowcount = sheet1.getLastRowNum();
			int startrow = sheet1.getFirstRowNum();
			// {{ BUILD THE JAVA OBJECT }}
			XLSObject gobject = new XLSObject("temp_table", "\\s+");

			for (int i = startrow; i < 5; i++) {
				HSSFRow row = sheet1.getRow(i);
				if (row == null)
					continue;
				int cellNumber = row.getLastCellNum();
				int firstCellnumber = row.getFirstCellNum();
				int ti = i + 1;
				if (ti == (TITLE)) {
					String[] titles = parseStrings(row);
					gobject.setFields(titles);
				} else if (ti == TYPE) {
					String[] type = parseStrings(row,
							gobject.getFields().length);
					gobject.setTypes(type);
				} else if (ti == ANNOTATION) {
					String[] annotation = parseStrings(row,
							gobject.getFields().length);
					gobject.setAnnotations(annotation);
				} else if (ti == COMMENTS) {
					String[] comments = parseStrings(row,
							gobject.getFields().length);
					gobject.setComs(comments);
				}
			}

			// check the types... and see if all the values on the second row
			// are correct types.
			for (String t : gobject.getTypes()) {
				if (!XLSTypes.isAType(t))
					return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception _ee) {
			_ee.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * method for building the GMObject and the database schema.
	 * 
	 * @return
	 * @throws LoaderException
	 */
	public XLSObject createGMObject(InputStream fin, String _table_name)
			throws LoaderException {
		ErrorLog el = new ErrorLog();
		try {
			lg.info("starting...\n\n\n");
			POIFSFileSystem poifs = new POIFSFileSystem(fin);
			HSSFWorkbook wb = new HSSFWorkbook(poifs);
			// first we will load the first sheet
			HSSFSheet sheet1 = wb.getSheetAt(0);
			if (sheet1 == null) {
				el.setMsg("The \"Form 0\" sheet is not accessible");
				throw new LoaderException(el);
			}

			int startrow = sheet1.getFirstRowNum();
			XLSObject gobject = new XLSObject(_table_name, "\\s+");

			// {{ BUILD THE JAVA OBJECT }}
			for (int i = startrow; i < 5; i++) {
				HSSFRow row = sheet1.getRow(i);
				if (row == null)
					continue;
				int cellNumber = row.getLastCellNum();
				int firstCellnumber = row.getFirstCellNum();
				lg.info("\t\tROW:" + i);
				// current row index with respect to excel file
				// first row should be the version
				int ti = i + 1;
				if (ti == (TITLE)) {
					String[] titles = parseStrings(row);
					gobject.setFields(titles);
				} else if (ti == TYPE) {
					String[] type = parseStrings(row,
							gobject.getFields().length);

					// dtermine the valid types
					if (!valid(type)) {
						type = createDefaultTypes(gobject.getFields().length);
						gobject.setStartRow(1);
						gobject.setTypes(type);
						break;
					}
					gobject.setTypes(type);
					// verify that all the types recorded are valid:
					// boolean valid_types =

				} else if (ti == ANNOTATION) {
					String[] annotation = parseStrings(row,
							gobject.getFields().length);
					gobject.setAnnotations(annotation);
				} else if (ti == COMMENTS) {
					String[] comments = parseStrings(row,
							gobject.getFields().length);
					gobject.setComs(comments);
				}
			}
			if (el.count() > 0)
				throw new LoaderException(el);
			return normalize(gobject);
		} catch (IOException _e) {
			_e.printStackTrace();
			lg.fatal("IO failure.  " + _e.getLocalizedMessage());
			throw new LoaderException(_e.getLocalizedMessage());
		}
	}

	private String[] createDefaultTypes(int length) {
		String[] s = new String[length];
		for (int i = 0; i < length; i++) {
			s[i] = "string";
		}
		return s;
	}

	private boolean valid(String[] types) {
		for (String t : types) {
			if (!XLSTypes.isAType(t))
				return false;
		}
		return true;
	}

	private static String[] parseStrings(HSSFRow row) {
		Iterator<Cell> iter = row.cellIterator();
		ArrayList<String> fields = new ArrayList<String>();

		while (iter.hasNext()) {
			Cell cel = iter.next();
			// we expect string values for the title fields
			// String t = cel.getStringCellValue();
			String t = "";
			if (cel.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				double value = cel.getNumericCellValue();
				t = value + "";
			} else {
				RichTextString rt = cel.getRichStringCellValue();
				if (rt == null)
					t = "";
				else
					t = cel.getRichStringCellValue().getString();
			}
			fields.add(t);
		}

		String[] f = fields.toArray(new String[fields.size()]);
		return f;
	}

	private XLSObject normalize(XLSObject gobject) {

		String[] fields = gobject.getFields();
		String[] types = gobject.getTypes();
		String[] ann = gobject.getAnnotations();
		String[] com = gobject.getComs();

		if (ann == null || ann.length != fields.length) {
			gobject.setAnnotations(buildNormalArray(fields, ann));
		}
		if (com == null || com.length != fields.length) {
			gobject.setComs(buildNormalArray(fields, com));
		}

		return gobject;
	}

	private String[] buildNormalArray(String[] fields, String[] ann) {
		ArrayList<String> ann_ = new ArrayList<String>();
		if (ann == null) {
			for (int i = 0; i < fields.length; i++) {
				ann_.add("");
			}
		} else {
			for (int i = 0; i < fields.length; i++) {
				if (i < ann.length)
					ann_.add(ann[i]);
				else
					ann_.add("");
			}
		}
		return ann_.toArray(new String[fields.length]);
	}

	/**
	 * Use this when you want to parse the row with respect to the total number
	 * of columns. This is important for rows that may not have values. We will
	 * want to copy a string value.
	 * 
	 * @param row
	 * @param _total_columns
	 * @return
	 */
	private static String[] parseStrings(HSSFRow row, int _total_columns) {
		Iterator<Cell> iter = row.cellIterator();
		ArrayList<String> fields = new ArrayList<String>();
		for (int i = 0; i < _total_columns; i++) {
			Cell cel = row.getCell(i);
			String t = "";
			if (cel != null) {
				if (cel.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					double value = cel.getNumericCellValue();
					t = value + "";
				} else {
					RichTextString rt = cel.getRichStringCellValue();
					if (rt == null)
						t = "";
					else
						t = cel.getRichStringCellValue().getString();
				}
			}
			// System.out.println ( "\t\t\t\t index : " + index++ + " = " + t );
			fields.add(t);
		}

		String[] f = fields.toArray(new String[fields.size()]);
		return f;
	}

	public static void loadTxt(String f_in, String _delim,
			boolean _include_row_number, XLSObject _ob) throws LoaderException {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(f_in));
			loadTxt(_include_row_number, fin, _delim, _ob, _ob.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void loadTxt(boolean _include_rownumber_index,
			BufferedReader fin, String _field_delim, XLSObject _ob,
			String _solr_core_name) throws LoaderException {
		ErrorLog el = new ErrorLog();
        PrintStream st = null;
		try {
			lg.setLevel(Level.DEBUG);
			lg.info("starting loader...\n\n\n");
			String[] types = _ob.getTypes();
			// {{ IF THE TYPES ARE NOT DEFINED WE NEED TO SET SOME DEFAULT
			// VALUES }}
			if (types == null || types.length <= 0) {
				_ob.setTypes(XLSObject.DEFAULT_TYPE);
				types = _ob.getTypes();
			}
			String[] fields = _ob.getFields();
			String solr_url = getSolrURL();
			String solr_dir = getSolrDir();
//			lg.debug("CONNECTING TO THE SOLR URL : " + solr_url);
			String stat_msg = "Error";
			if (solr_url == null) {
				throw new LoaderException(stat_msg,
						"Please provide a solr url in the properties: e.g. solr.url");
			}
			if (solr_dir == null) {
				throw new LoaderException(stat_msg,
						"Please provide a solr config directory in the properties file.  e.g. solr.dir");
			}
			if (_solr_core_name == null || _solr_core_name.length() <= 0) {
				throw new LoaderException(
						stat_msg,
						"Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
			}

			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			print(fields);
			ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			String line = fin.readLine();
			int index = 0;
			st = GBIO.createLogStream(_solr_core_name + "_loader");
			int row_number_index = 0;
			int line_number = 0;
			while (line != null) {
				if (in(index, _ob.getIgnoreRows())) {
				} else {
					SolrInputDocument solr_doc = new SolrInputDocument();
					String[] pline = line.split(_field_delim);
					if (pline.length > fields.length) {
						GB.print("\t" + line + " len" + pline.length);
					}
					for (int j = 0; j < pline.length; j++) {
						String t = pline[j];
						try {
							if (j >= types.length) {
								GB.print("Type not available for " + t);
							} else
								t = XLS.tryToParse(t, types[j]);
						} catch (LoaderException e) {
							e.printStackTrace();
							st.println("" + line + " failed.  --"
									+ e.getMessage());
						} catch (GBParseException e) {
							// e.printStackTrace();
							st.println("" + line + " failed.  --"
									+ e.getMessage());
							GB.print(line);
							t = null;
						}
						if (t != null && t.length() > 0) {
							if (j >= fields.length) {
								GB.print("\t Problem loading when the field count does not match the delim row count of the data.  ");
							} else if (fields[j] == null
									|| fields[j].length() <= 0) {
								lg.debug("FIELD IS NULL !");
								st.println("" + line + " failed.  --"
										+ "Field was null");
							} else
								solr_doc.addField(fields[j], t);
						}
					}
					if (_include_rownumber_index)
						solr_doc.addField("row_int", row_number_index++);
					solr_doc.addField("TMID", TMID.create());
					solr_doc.addField("TMID_lastUpdated", new Date());
					try {
						docs.add(solr_doc);
					} catch (Exception _e) {
						_e.printStackTrace();
						st.println("" + line + " failed.  --" + _e.getMessage());
					}
				}
				index++;
				line_number++;
				line = fin.readLine();

				if ((index % 500) == 0) {
                    HttpSolrClient solr = null;
					try {
						solr = new HttpSolrClient.Builder(solr_url + _solr_core_name).build();
						// need this to be compatible with new versions of solr server.
						solr.setParser(new XMLResponseParser());
//						System.out.println ( docs.size() + " docs ");
						solr.add(docs);
						solr.commit();
					} catch (SolrServerException e) {
						e.printStackTrace();
						st.println("" + line + " failed.  --" + e.getMessage());
						System.err
								.println("\n\n\t\t" + e.getMessage() + "\n\n");
						// throw new LoaderException(
						// "Failed to add the solr docs to the solr server"
						// + e.getLocalizedMessage());
					} finally {
                        IOUTILs.closeResource(solr);
                    }
                    docs.clear();
				}
			}
			st.close();


            HttpSolrClient solr = null;
			try {
				solr = new HttpSolrClient.Builder(solr_url + _solr_core_name).build();
				// need this to be compatible with new versions of solr server.
				solr.setParser(new XMLResponseParser());
				solr.add(docs);
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
				System.err.println("\n\n\t\t" + e.getMessage() + "\n\n");
				// throw new LoaderException(
				// "Failed to add the solr docs to the solr server"
				// + e.getLocalizedMessage());
			} finally {
                IOUTILs.closeResource(solr);
            }
            docs.clear();
			GB.print(" A total of  "+ line_number + " lines were loaded.");
			// if (el.count() > 0)
			// throw new LoaderException(el);
		} catch (IOException _e) {
			_e.printStackTrace();
			lg.fatal("IO failure.  " + _e.getLocalizedMessage());
		} finally {
            IOUTILs.closeResource(st);
		}
	}

	private static boolean in(int index, int[] ignoreRows) {
		if (ignoreRows == null || ignoreRows.length <= 0)
			return false;
		for (int i : ignoreRows) {
			if (i == index)
				return true;
		}
		return false;
	}

	/**
	 * Load the data into the schema
	 * 
	 * @param _ob
	 * @throws LoaderException
	 */
	public static void load(InputStream fin, XLSObject _ob)
			throws LoaderException {
		HttpSolrClient solr = null;
		ErrorLog el = new ErrorLog();
		try {
			lg.setLevel(Level.DEBUG);
			lg.info("starting loader...\n\n\n");
			POIFSFileSystem poifs = new POIFSFileSystem(fin);
			HSSFWorkbook wb = new HSSFWorkbook(poifs);
			HSSFSheet sheet1 = wb.getSheetAt(0);
			if (sheet1 == null) {
				el.setMsg("The \"Form 0\" sheet is not accessible");
				throw new LoaderException(el);
			}

			int rowcount = sheet1.getLastRowNum() + 1;
			int startrow = sheet1.getFirstRowNum();
			String[] types = _ob.getTypes();

			// {{ IF THE TYPES ARE NOT DEFINED WE NEED TO SET SOME DEFAULT
			// VALUES }}
			if (types == null) {
				_ob.setTypes(XLSObject.DEFAULT_TYPE);
				types = _ob.getTypes();
			}

			String[] fields = _ob.getFields();
			String solr_url = getSolrURL();
			String solr_dir = getSolrDir();
			lg.debug("CONNECTING TO THE SOLR URL : " + solr_url);
			String stat_msg = "Error";
			String table_name = _ob.getTableName();
			table_name = table_name.replace(' ', '_');
			if (solr_url == null) {
				throw new LoaderException(stat_msg,
						"Please provide a solr url in the properties: e.g. solr.url");
			}
			if (solr_dir == null) {
				throw new LoaderException(stat_msg,
						"Please provide a solr config directory in the properties file.  e.g. solr.dir");
			}
			if (table_name == null || table_name.length() <= 0) {
				throw new LoaderException(
						stat_msg,
						"Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
			}

			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			solr = new HttpSolrClient.Builder(solr_url + table_name).build();

			// need this to be compatible with new versions of solr server.
			solr.setParser(new XMLResponseParser());

			int total = 0;
			print(fields);
			ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			for (int i = startrow + _ob.getStartRow(); i < rowcount; i++) {
				HSSFRow row = sheet1.getRow(i);
				if (row == null && i < rowcount) {
					continue;
				}
				lg.info("ROW:" + i + " / : " + rowcount);
				// now we loop over the fields
				SolrInputDocument solr_doc = new SolrInputDocument();
				// String row_str = "";
				for (int col = 0; col < fields.length; col++) {
					HSSFCell cell = row.getCell(col);
					String t = XLS.getStringValue(cell);
					// if you can not find the actual value make sure you enter
					// a default value.
					try {
						t = XLS.tryToParse(t, types[col]);
					} catch (GBParseException e) {
						e.printStackTrace();
						GB.print(t);
					}
					if (t != null && t.length() > 0) {
						// row_str += fields[col] + "=" + t + "\t";
						if (fields[col] == null || fields[col].length() <= 0) {
							lg.debug("FIELD IS NULL !");
						}
						solr_doc.addField(fields[col], t);
					}
				}
				solr_doc.addField("TMID", TMID.create());
				solr_doc.addField("TMID_lastUpdated", new Date());
				lg.debug("=========================================================================================================");
				// lg.debug(row_str);
				try {
					docs.add(solr_doc);
				} catch (Exception _e) {
					_e.printStackTrace();
					System.err.println("\n\n\t\t" + _e.getMessage() + "\n\n");
				}
				// for (int ij = 0; ij < types.length; ij++) {
				// HSSFCell cell = row.getCell(ij);
				// setValue(cell, types[ij], fields[ij], ob);
				// }
				total++;
			}
			try {
				solr.add(docs);
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
				System.err.println("\n\n\t\t" + e.getMessage() + "\n\n");
				throw new LoaderException(
						"Failed to add the solr docs to the solr server"
								+ e.getLocalizedMessage());
			}
			docs.clear();
			if (el.count() > 0)
				throw new LoaderException(el);
		} catch (IOException _e) {
			_e.printStackTrace();

			lg.fatal("IO failure.  " + _e.getLocalizedMessage());
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	private static void print(String[] fields) {
		lg.debug("=-=-==-=-=-=-=-=- FIELDS =-=-==-=-=-=-=-=-");
		for (String f : fields) {
			lg.debug("\t\t" + f);
		}
		lg.debug("=-=-==-=-=-=-=-=- FIELDS =-=-==-=-=-=-=-=-");
	}

	private static String getSolrDir() {
		String solrRoot = ABProperties.get(ABProperties.SOLRSITE);
		return solrRoot;
	}

	private static String getSolrURL() {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		return solr_url;
	}

	public XLSObject createGMObjectUnformated(InputStream fin,
			String _table_name) throws LoaderException {
		ErrorLog el = new ErrorLog();
		try {
			lg.info("starting...\n\n\n");
			POIFSFileSystem poifs = new POIFSFileSystem(fin);
			HSSFWorkbook wb = new HSSFWorkbook(poifs);
			// first we will load the first sheet
			HSSFSheet sheet1 = wb.getSheetAt(0);
			if (sheet1 == null) {
				el.setMsg("The \"Form 0\" sheet is not accessible");
				throw new LoaderException(el);
			}
			int startrow = sheet1.getFirstRowNum();
			XLSObject gobject = new XLSObject(_table_name, "\\s+");

			// {{ BUILD THE JAVA OBJECT }}
			for (int i = startrow; i < 1; i++) {
				HSSFRow row = sheet1.getRow(i);
				if (row == null)
					continue;
				int cellNumber = row.getLastCellNum();
				int firstCellnumber = row.getFirstCellNum();
				lg.info("\t\tROW:" + i);
				// current row index with respect to excel file
				// first row should be the version
				int ti = i + 1;
				if (ti == (TITLE)) {
					String[] titles = parseStrings(row);

					titles = NameUtiles.convertToValidNames(titles);

					gobject.setFields(titles);
				} else if (ti == TYPE) {
					String[] types = parseTypes(row);
					gobject.setTypes(types);
				}
			}

			String[] ttypes = gobject.getTypes();
			if (ttypes == null || ttypes.length < gobject.getFields().length) {
				String[] types = new String[gobject.getFields().length];
				for (int i = 0; i < types.length; i++) {
					types[i] = "text";
				}
				gobject.setTypes(types);
			}
			if (el.count() > 0)
				throw new LoaderException(el);
			return normalize(gobject);
		} catch (IOException _e) {
			_e.printStackTrace();
			lg.fatal("IO failure.  " + _e.getLocalizedMessage());
			throw new LoaderException(_e.getLocalizedMessage());
		}
	}

	private static String[] parseTypes(HSSFRow row) {
		Iterator<Cell> iter = row.cellIterator();
		ArrayList<String> fields = new ArrayList<String>();

		while (iter.hasNext()) {
			Cell cel = iter.next();
			if (cel.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				fields.add("float");
			} else if (cel.getCellType() == Cell.CELL_TYPE_BLANK)
				fields.add("text");
			else if (cel.getCellType() == Cell.CELL_TYPE_BOOLEAN)
				fields.add("boolean");
			else {
				fields.add("text");
			}
		}

		String[] f = fields.toArray(new String[fields.size()]);
		return f;
	}

}
