package com.arraybase.io;

import com.arraybase.*;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.*;
import com.arraybase.lac.LAC;
import com.arraybase.lac.LacFactory;
import com.arraybase.tm.NodeFactory;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import com.arraybase.util.Level;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class GBFileManager {
	public static final String S3 = "S3";
	private final static SimpleDateFormat sf = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss");
	public static final String JSON = "JSON";
	public static final String BINARY = "BINARY";
	public static final String TABLE = "TABLE";
	public static final String ABQ = "SQL";
	public static final String FASTA = "FASTA";
	public static final String XLSX = "XLSX";
	public static final String FTP = "FTP";
    public static final String JAR = "JAR";


    static GBLogger log = GBLogger.getLogger(GBFileManager.class);
	private DBConnectionManager dbcm = new DBConnectionManager();
	private SessionFactory sessionFactory = null;
	static {
		log.setLevel(Level.DEBUG);
	}

	public GBFileManager() {
	}

	public GBFileManager(DBConnectionManager _dbcm) {
		dbcm = _dbcm;
	}

	/**
	 * Save a file to a path
	 * 
	 * @param _user
	 * @param _file
	 * @param _path
	 */
	public void save(String _user, FileItem _file, String _type, String _path)
			throws LoaderException {
		String _file_name = _file.getName();
		BufferedInputStream in2 = new BufferedInputStream(
				new ByteArrayInputStream(_file.get()));
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(_file.getInputStream());

			if (_type == null || _type.length() <= 0) {
				_type = "binary";
			}
			if (_type.equalsIgnoreCase(GBFileType.BINARY.name)) {
				loadBinary(_file_name, in, _path, _user);
			} else if (_type.equalsIgnoreCase(GBFileType.TABLE.name)) {
				createTableFromFile(_file_name, in, _path, _user);
			} else if ((_file_name.endsWith(".xls") || _file_name.endsWith(".XLS"))
					&& LoadTableToSolr.isCorrectlyFormatted(in)) {
				String file_name = NameUtiles.convertToValidCharName(_path + "."
						+ _file_name);
				loadXLS(in2, file_name, _user);
				NodeManager tmnode = new NodeManager(dbcm);
				TNode node = tmnode.createPath(_path + "/" + _file_name, _user,
						SourceType.DB);
				node.setNodeType(SourceType.LINK.name);
				node.setCreatedDate(new Date());
				node.setCreated_by(_user);
				node.setOwner(_user);
				node.setLink(file_name + ".search(*:*)");
				try {
					tmnode.save(node);
				} catch (Exception e) {
					e.printStackTrace();
					throw new LoaderException(
							"TMNode object failed to save.  This could be a server configuration problem."
									+ _file.getName() + " path : " + _path);
				}

			} else {
				loadBinary(_file_name, in, _path, _user);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoaderException(
					"Failed to get the input stream for the file : "
							+ _file.getName() + " Path:" + _path);
		} finally {
			IOUTILs.closeResource(in);
			IOUTILs.closeResource(in2);
		}
	}

	/**
	 * Create an input stream that represents the formated file.
	 * 
	 * @param _file
	 * @return
	 */
	private static BufferedInputStream formatTable(FileItem _file) {

		BufferedInputStream in2 = new BufferedInputStream(
				new ByteArrayInputStream(_file.get()));

		return null;
	}

	/**
	 * loadXLS for the creating a table from an xls file.
	 * 
	 * @param _file_name
	 * @param in
	 * @param _path
	 * @param _user
	 * @throws LoaderException
	 */
	private void createTableFromFile(String _file_name, BufferedInputStream in,
			String _path, String _user) throws LoaderException {
		String file_name = NameUtiles.convertToValidCharName(_path + "."
				+ _file_name);
		loadXLS(in, file_name, _user);
		NodeManager tmnode = new NodeManager(dbcm);
		TNode node = tmnode.createPath(_path + "/" + _file_name, _user,
				SourceType.TABLE);
		node.setNodeType(SourceType.LINK.name);
		node.setCreatedDate(new Date());
		node.setCreated_by(_user);
		node.setOwner(_user);
		node.setLink(file_name + ".search(*:*)");
		try {
			tmnode.save(node);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoaderException(
					"TMNode object failed to save.  This could be a server configuration problem."
							+ _file_name + " path : " + _path);
		}
	}

	public static Integer getFileId(TNode node) {
		String lac = node.getLink();

		if (lac.startsWith("file.")) {
			String ata = LAC.getData(lac);
			int lastindex = ata.lastIndexOf('/');
			String file_node = ata.substring(lastindex + 1);
			try {
				Integer i = Integer.parseInt(file_node);
				return i;
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}
		return -1;

	}

	// returns a map with two keys: file_name (String) and file (byte [])
	private Map<String, Object> _getRawFile(long _fileid) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "select attachment1, attachment_name from ab_raw_files where file_id ="
					+ _fileid;
			con = dbcm.getJDBCConnection();

			st = con.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("file", rs.getBytes(1));
				map.put("file_name", rs.getString(2));
				return map;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(con);
		}
		System.err.println("Failed to find file with file_id : " + _fileid);
		return null;
	}

	public File exportFile(long _fileid, String _directory) {
        FileOutputStream fst = null;
		try {
			Map<String, Object> rawFile = _getRawFile(_fileid);
			if (rawFile == null)
				return null;
			File ff = new File(_directory, (String) rawFile.get("file_name"));
			fst = new FileOutputStream(ff);
			fst.write((byte[]) rawFile.get("file"));
			fst.flush();
			return ff;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            IOUTILs.closeResource(fst);
        }
        System.err.println("Failed to export the file to the directory : "
				+ _directory);
		return null;
	}

	public Map<String, Object> getRawFile(String path) {
		NodeManager tmnode = new NodeManager();
		TNode node = tmnode.getNode(path);
		if (node != null) {
			// if this is a raw file then we're all good.
			if (node.getNodeType().equals(SourceType.RAW_FILE.name)) {
				String lac = node.getLink();

				if (lac.startsWith("file.")) {
					String ata = LAC.getData(lac);
					int lastindex = ata.lastIndexOf('/');
					String file_node = ata.substring(lastindex + 1);
					try {
						Integer i = Integer.parseInt(file_node);
						return _getRawFile(i);
					} catch (Exception _e) {
						_e.printStackTrace();
					}
				}
				return null;
			} else if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)) {
				// we have the
				String lac = node.getLink();
				if (lac != null) {
					GBSearch g = new GBSearch();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					g.searchCore(lac, "*:*", 0, 100000, new PrintStream(out),
							null, new SearchConfig(SearchConfig.RAW_SEARCH));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("file_name", node.getName());
					map.put("file", out.toByteArray());
					return map;
				}
				log.error("Failed to find the link for the file  "
						+ node.getName());
				return null;
			} else {
				System.err.println("The path given is not a file.");
				return null;
			}
		} else {
			System.err.println("Path not found");
			return null;
		}
	}

	private void loadBinary(String _file_name, InputStream in,
			final String _path, String _user) {
        Session session = null;
        InputStream is = null;
		try {
			double fl = Math.random();
			File f = new File(_file_name);
			// File f = GFile.createTempFile(fl + "_" + _file_name);
			log.debug("File: " + f.getAbsolutePath());

			long file_length = f.length();
			byte[] bytes = new byte[(int) file_length];
			int offset = 0;
			int numRead = 0;
			is = new FileInputStream(f);
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ f.getName());
			}
			log.info("Connecting to database.. ");

			session = dbcm.getSession();
			session.beginTransaction();
			log.info("Connected! ");

			GBBlobFile file = new GBBlobFile();
			String desc = buildDescription(_file_name, _path, bytes, _user,
					new Date());
			file.setAttachment_desc(desc);
			System.out.println(" " + bytes.length + " bytes ");

			file.setAttachment1(bytes);
			file.setAttachment_name(NameUtiles
					.replaceCharsWithValid(_file_name));
			file.setLast_updated_date(new Date());
			file.setLast_saved_by_usr_id(_user);

			log.info("Writing file to database. ");
			session.save(file);

			NodeManager tm = new NodeManager();
			TNode node = tm.getNode(_path);
			if (node == null)
				node = tm.createPath(_path, _user, SourceType.NODE, session);

			String url = "localdb/GBBlobFile/" + file.getFile_id();
			url = LacFactory.create("raw_file", url);
			// {{ save the file name node }}
			TNode file_node = NodeFactory.createNode(_path, _user,
					file.getAttachment_name(), SourceType.RAW_FILE, session);
			file_node.setLink(url);

			session.getTransaction().commit();

		} catch (IOException _e) {
			_e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HBConnect.close(session);
            IOUTILs.closeResource(in);
		}
	}

	private static void save(long parent_id, TNode file_node, Session ses) {

		try {
			ses.save(file_node);
			Criteria c = ses.createCriteria(TNode.class).add(
					Restrictions.eq("node_id", parent_id));
			List l = c.list();
			if (l != null && l.size() > 0) {
				TNode parent = (TNode) l.get(0);
				parent.addCRef(file_node);
				ses.saveOrUpdate(parent);

			} else
				System.out.println(" no node found");

			NodeManager manager = new NodeManager();
			TPath _parent = manager.getPathForNode(parent_id, ses);

			TPath tpath = new TPath();
			tpath.setTMParent(_parent.getPath_id());
			tpath.setDescription("");
			tpath.setGroup_name(_parent.getGroup_name());
			tpath.setNode_id(file_node.getNode_id());
			tpath.setName(_parent.getName() + "/" + file_node.getName());
			manager.save(tpath, ses);

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			// do now close the session of params
		}
	}

	public String buildIndicies(String _path, String _userName)
			throws NodeNotFoundException {
		String name = NameUtiles.prepend(_userName,
				NameUtiles.convertToValidCharName(_path));
		HashMap<String, Map<String, String>> params = buildDefaultDirectoryTableParams();
		TableManager tmd = new TableManager(dbcm);
		// if the ping is zero from the solr ping api. Then
		// we know it is a valid schema.
		// let's delete it.
		if (TableManager.ping(name) == 0) {
			TableManager.deleteAll(name);
		} else {
			tmd.build(_userName, SourceType.DIRECTORY.name, name,
					"Directory Index", null, params, null);
		}
		String file_name = NameUtiles.prepend(_userName, _path + ".index");
		NodeManager tmnode = new NodeManager();
		TNode node = tmnode.createPath(_path + "/" + ".index", _userName,
				SourceType.DB);
		node.setNodeType(SourceType.LINK.name);
		node.setCreatedDate(new Date());
		node.setCreated_by(_userName);
		node.setOwner(_userName);
		node.setLink("solr.load(" + name + ")");
		try {
			NodeManager nm = new NodeManager();
			nm.save(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buildTheIndex(name, _path);
	}

	/**
	 * Build the index called name with the data from the path: _path
	 * 
	 * @param name
	 * @param _path
	 * @return
	 */
	private String buildTheIndex(String name, String _path)
			throws NodeNotFoundException {
		NodeManager service = new NodeManager();
		TNode node = service.getNode(_path);
		if (node == null) {
			throw new NodeNotFoundException(_path);
		}
		List<Integer> values = node.getReference();
		Map<Integer, TNode> _nodes_ = service.load(values);
		Set<Integer> keys = _nodes_.keySet();
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		solr_url += name;
        Session session = null;
		try {
			session = dbcm.getSession();
			synchronized (session) {
				session.beginTransaction();
				for (Integer i : keys) {
					TNode t = _nodes_.get(i);
					String _name = t.getName();
					Long l = t.getNode_id();
					// log.debug ( " t " + t.getNodeType () );
					if (t.getNodeType().equals(SourceType.RAW_FILE.name)) {
						String lac = t.getLink();
						String data = LAC.getData(lac);
						ArrayList<String> elms = new ArrayList<String>();
						// log.debug(" string tokenizer ");
						String[] eee = data.split("/");
						for (String e : eee) {
							elms.add(e);
						}
						String id_st = elms.get(elms.size() - 1);
						long id = Long.parseLong(id_st);
						Criteria c = session.createCriteria(GBBlobFile.class);
						c = c.add(Restrictions.eq("file_id", id));
						List<GBBlobFile> file = c.list();
						if (file != null) {
							GBBlobFile f = file.get(0);
							byte[] bdata = f.getAttachment1();
							ByteArrayInputStream bi = new ByteArrayInputStream(
									bdata);

							bi.close();
						}
					}
				}
			}
		} catch (HibernateException _exception) {
			_exception.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            HBConnect.close(session);
		}
		return "Indicies built";
	}

	private static HashMap<String, Map<String, String>> buildDefaultDirectoryTableParams() {
		HashMap<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();

		HashMap<String, String> doc_id = new HashMap<String, String>();
		doc_id.put("fieldName", "NODE_ID");
		doc_id.put("indexed", "true");
		doc_id.put("requiredField", "false");
		doc_id.put("dataType", "string");
		doc_id.put("defaultString", "unknown");
		doc_id.put("dictString", "No_Dictionary");
		maps.put("NODE_ID", doc_id);

		HashMap<String, String> doc_name = new HashMap<String, String>();
		doc_name.put("fieldName", "FILE_NAME");
		doc_name.put("indexed", "true");
		doc_name.put("requiredField", "false");
		doc_name.put("dataType", "string");
		doc_name.put("defaultString", "unknown");
		doc_name.put("dictString", "No_Dictionary");
		maps.put("FILE_NAME", doc_name);

		HashMap<String, String> doc_path = new HashMap<String, String>();
		doc_path.put("fieldName", "PATH");
		doc_path.put("indexed", "true");
		doc_path.put("requiredField", "false");
		doc_path.put("dataType", "string");
		doc_path.put("defaultString", "unknown");
		doc_path.put("dictString", "No_Dictionary");
		maps.put("PATH", doc_path);

		HashMap<String, String> doc_subject = new HashMap<String, String>();
		doc_subject.put("fieldName", "SUBJECT");
		doc_subject.put("indexed", "true");
		doc_subject.put("requiredField", "false");
		doc_subject.put("dataType", "Text");
		doc_subject.put("defaultString", "unknown");
		doc_subject.put("dictString", "No_Dictionary");
		maps.put("SUBJECT", doc_subject);

		HashMap<String, String> table_of_contents = new HashMap<String, String>();
		table_of_contents.put("fieldName", "TABLE_OF_CONTENTS");
		table_of_contents.put("indexed", "true");
		table_of_contents.put("requiredField", "false");
		table_of_contents.put("dataType", "Text");
		table_of_contents.put("defaultString", "unknown");
		table_of_contents.put("dictString", "No_Dictionary");
		maps.put("TABLE_OF_CONTENTS", table_of_contents);

		HashMap<String, String> doc_index = new HashMap<String, String>();
		doc_index.put("fieldName", "DOC_INDEX");
		doc_index.put("indexed", "true");
		doc_index.put("requiredField", "false");
		doc_index.put("dataType", "Text");
		doc_index.put("defaultString", "unknown");
		doc_index.put("dictString", "No_Dictionary");
		maps.put("DOC_INDEX", doc_index);

		HashMap<String, String> title_param = new HashMap<String, String>();
		title_param.put("fieldName", "TITLE");
		title_param.put("indexed", "true");
		title_param.put("requiredField", "false");
		title_param.put("dataType", "Text");
		title_param.put("defaultString", "unknown");
		title_param.put("dictString", "No_Dictionary");
		maps.put("TITLE", title_param);

		HashMap<String, String> word_count = new HashMap<String, String>();
		word_count.put("fieldName", "WORD_COUNT");
		word_count.put("indexed", "true");
		word_count.put("requiredField", "false");
		word_count.put("dataType", "int");
		word_count.put("defaultString", "unknown");
		word_count.put("dictString", "No_Dictionary");
		maps.put("WORD_COUNT", word_count);

		HashMap<String, String> attr = new HashMap<String, String>();
		attr.put("fieldName", "ATTRIBUTES");
		attr.put("indexed", "true");
		attr.put("requiredField", "false");
		attr.put("dataType", "Text");
		attr.put("defaultString", "unknown");
		attr.put("dictString", "No_Dictionary");
		maps.put("ATTRIBUTES", attr);

		HashMap<String, String> producer = new HashMap<String, String>();
		producer.put("fieldName", "CREATOR");
		producer.put("indexed", "true");
		producer.put("requiredField", "false");
		producer.put("dataType", "Text");
		producer.put("defaultString", "unknown");
		producer.put("dictString", "No_Dictionary");
		maps.put("CREATOR", producer);

		HashMap<String, String> authors = new HashMap<String, String>();
		authors.put("fieldName", "AUTHORS");
		authors.put("indexed", "true");
		authors.put("requiredField", "false");
		authors.put("dataType", "Text");
		authors.put("defaultString", "unknown");
		authors.put("dictString", "No_Dictionary");
		maps.put("AUTHORS", authors);

		HashMap<String, String> last_mod = new HashMap<String, String>();
		last_mod.put("fieldName", "LAST_MODIFIED");
		last_mod.put("indexed", "true");
		last_mod.put("requiredField", "true");
		last_mod.put("dataType", "Date");
		last_mod.put("defaultString", "");
		last_mod.put("dictString", "No_Dictionary");
		maps.put("LAST_MODIFIED", last_mod);

		HashMap<String, String> da = new HashMap<String, String>();
		da.put("fieldName", "DATE_CREATED");
		da.put("indexed", "true");
		da.put("requiredField", "true");
		da.put("dataType", "Date");
		da.put("defaultString", "");
		da.put("dictString", "No_Dictionary");
		maps.put("DATE_CREATED", da);

		HashMap<String, String> cont = new HashMap<String, String>();
		cont.put("fieldName", "CONTENT");
		cont.put("indexed", "true");
		cont.put("requiredField", "false");
		cont.put("dataType", "Text");
		cont.put("defaultString", "unknown");
		cont.put("dictString", "No_Dictionary");
		maps.put("CONTENT", cont);

		HashMap<String, String> header = new HashMap<String, String>();
		header.put("fieldName", "HEADER");
		header.put("indexed", "true");
		header.put("requiredField", "false");
		header.put("dataType", "Text");
		header.put("defaultString", "unknown");
		header.put("dictString", "No_Dictionary");
		maps.put("HEADER", header);

		HashMap<String, String> ctype = new HashMap<String, String>();
		ctype.put("fieldName", "CONTENT_TYPE");
		ctype.put("indexed", "true");
		ctype.put("requiredField", "false");
		ctype.put("dataType", "Text");
		ctype.put("defaultString", "unknown");
		ctype.put("dictString", "No_Dictionary");
		maps.put("CONTENT_TYPE", ctype);

		HashMap<String, String> pls = new HashMap<String, String>();
		pls.put("fieldName", "TYPE");
		pls.put("indexed", "true");
		pls.put("requiredField", "false");
		pls.put("dataType", "Text");
		pls.put("defaultString", "unknown");
		pls.put("dictString", "No_Dictionary");
		maps.put("TYPE", pls);

		return maps;

	}

	/**
	 * Try load the XLS file as an atomized table.
	 * 
	 * @param in
	 * @param _name
	 * @param _userName
	 */
	private void loadXLS(InputStream in, String _name, String _userName)
			throws LoaderException {
		LoadTableToSolr sl = new LoadTableToSolr();
		InputStream[] sp = copyStream(in, 3);

		int start_row = 4;
		XLSObject xls_ob = null;
		if (!LoadTableToSolr.isCorrectlyFormatted(sp[0])) {
			start_row = 1;
			xls_ob = sl.createGMObjectUnformated(sp[1],
					NameUtiles.prepend(_userName, _name));
		} else {
			xls_ob = sl.createGMObject(sp[1],
					NameUtiles.prepend(_userName, _name));
		}
		xls_ob.setStartRow(start_row);

		xls_ob = XLSIntegration.rename_fields_for_solr(xls_ob);
		HashMap<String, Map<String, String>> params = XLSIntegration
				.buildSolrFields(xls_ob);
		params = TMSolrServer.appendTMFields(params);
		// {{ SAVE THE LIBRARY IN THE DATABASE }}
		TableManager tmd = new TableManager(dbcm);
		tmd.build(_userName, TableManager.TMSOLR,
				NameUtiles.prepend(_userName, _name), "", "1", params, null);
		String[] fields = xls_ob.getFields();
		for (String field : fields) {
			log.debug("fields : " + field);
		}
		log.debug(" XLS ANNOTATION FILE COMPLETE ");
		LoadTableToSolr.load(sp[2], xls_ob);
	}

	/**
	 * copy data
	 * 
	 * @param in
	 * @return
	 */
	private static InputStream[] copyStream(InputStream in, int _num_needed) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		try {
			while ((n = in.read(buf)) >= 0)
				baos.write(buf, 0, n);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = baos.toByteArray();
		InputStream[] ii = new InputStream[_num_needed];
		for (int i = 0; i < _num_needed; i++) {
			InputStream is1 = new ByteArrayInputStream(content);
			ii[i] = is1;
		}
		return ii;
	}



	private static String buildDescription(String _file_name, String _path,
			byte[] bytes, String _user, Date date) {

		String desc = "<h3>" + _file_name + "</h3>" + "<p>Created: "
				+ sf.format(date) + "</p>" + "<p>Created by : " + _user
				+ "</p>" + "<p>File size : " + (bytes.length) / 1000
				+ " KB</p>";

		return desc;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(String _userId, File f, String _path) {
		try {
			this.save(_userId, _path, new FileInputStream(f), f.getName());
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}
	public void save(String _userId, String _path, InputStream is,
			String fileName) {
		loadBinary(fileName, is, _path, _userId);
	}
	/**
	 * Get the file given the file id
	 *
	 * @param file_id
	 * @return
	 */
	public GBBlobFile getFile(long file_id) {
		Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(GBBlobFile.class);
			c = c.add(Restrictions.eq("file_id", file_id));
			List<GBBlobFile> file = c.list();
			if (file != null) {
				GBBlobFile f = file.get(0);
				// make sure it's not a lazy load... pull it.
				byte[] bdata = f.getAttachment1();
				log.info("Pulled : " + (bdata.length * 1000) + " MB.");
				return f;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public String getQuickFileDescription(long _file_id) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
		String msg = "";
		try {
			con = dbcm.getJDBCConnection();
			st = con.createStatement();
			String sl = "select attachment_desc from ab_raw_files where file_id="
					+ _file_id;
			rs = st.executeQuery(sl);
			if (rs.next()) {
				String desc = rs.getString(1);
				msg = desc;
			}
			st.close();
		} catch (Exception _e) {
			_e.printStackTrace();
			return "";
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
		}
		return msg;
	}

	public String getFileDescription(long _file_id) {
		GBBlobFile b = getFile(_file_id);
		String msg = b.getAttachment_name();
		msg += "\n";
		byte[] bytes = b.getAttachment1();
		if (bytes == null || bytes.length <= 0) {
			msg += "No data.";
			return msg;
		}
		msg += b.getAttachment_desc();

		return msg;
	}

	/**
	 * Save the file to the database
	 * 
	 * @param file
	 */
	public void save(GBBlobFile file) {
        Session session = null;
		try {
			 session = dbcm.getSession();
			session.beginTransaction();
			session.save(file);
			session.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
	}

	public static void cutLine(String[] _args) {
		if (_args.length < 1)
			GB.printUsage("gb cutline $number $file");
		String line_index = _args[1];
		String file = _args[2];
		try {
			int v = Integer.parseInt(line_index);
			File f = new File(file);
			GBIO.clip(f, v);
		} catch (NumberFormatException _c) {
			GB.printUsage("You need to provide a number where you provided the value : "
					+ line_index);
			return;
		} catch (FileNotFoundException e) {
			GB.printUsage("The file was not found.");
		}
	}

	public static void export(String[] _args) {
		if (_args.length != 2 && _args.length != 3) {
			GB.printUsage("gb export $gbpath");
			GB.printUsage("OR");
			GB.printUsage("gb export $gbpath $database_export_file");
			GB.printUsage("\n\n");
			GB.printUsage("Export file: describes the location of where you want to dump the contents of the gb file. "
					+ "\n "
					+ "The following is an example of what that may look like:");
			GB.printUsage("url=jdbc:mysql://localhost:3306/ab?user=ab&password=ab");
			GB.printUsage("user=ab");
			GB.printUsage("pass=ab");
			GB.printUsage("driver=ab");
			GB.printUsage("table=test");
			GB.printUsage("test.rfk=rfk");// map the fields
			GB.printUsage("test.name=myname");
			// user=ab
			// pass=ab
			// url=jdbc:mysql://localhost:3306/ab?user=ab&password=ab
			// driver=com.mysql.jdbc.Driver
		}
		if (_args.length == 2) {
			String path = _args[1];
			getFile(path);
		} else {

			// {{ EXPORT A PATH TO A DATABASE }}
			// 1. path do db type node
			// 2. database descriptor file

			String path = _args[1];
			String database_descriptor = _args[2];
			try {
				GBNodes.exportPath(path, database_descriptor);
			} catch (NodeWrongTypeException e) {
				GB.printELog(e);
				GB.print(" Node path is not of type=database.  {db}");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * TODO: Add exception framework around this. For now it is using
	 * system.err.printlns
	 * 
	 * @param _path
	 * @return
	 */
	public static File getFile(String _path) {
		if (_path.indexOf('\r') > 0) {
			log.debug(" we have white space in the path ");
		}
		TNode node = GB.getNodes().getNode(_path);
		if (node != null && node.getNodeType() != null) {
			// if this is a raw file then we're all good.
			log.debug("Node : " + node.getName() + " type "
					+ node.getNodeType());
			if (node.getNodeType() != null) {
				if (node.getNodeType().equals(SourceType.RAW_FILE.name)) {
					String lac = node.getLink();
					log.debug(lac);
					if (lac != null && lac.startsWith("file.")) {
						String ata = LAC.getData(lac);
						int lastindex = ata.lastIndexOf('/');
						String file_node = ata.substring(lastindex + 1);
						try {
							Integer i = Integer.parseInt(file_node);
							GBFileManager file = new GBFileManager();
							return file.exportFile(i, ".");
						} catch (Exception _e) {
							_e.printStackTrace();
						}
					}
				}
				return null;
			} else if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)) {
				// we have the
				log.error("Untested method; ");
				try {
					throw new Exception("Untested Method ");
				} catch (Exception _e) {
					_e.printStackTrace();
				}
				String lac = node.getLink();
				if (lac != null) {
					return dump(lac, ".", node.getName());
				}
				log.error("Failed to find the link for the file  "
						+ node.getName());
				return null;
			}

			else {
				log.error("The path given is not a file.");
				return null;
			}

		} else {
			log.error("Path not found");
			return null;
		}
	}

	private static File dump(String lac, String string, String _name) {
		File f = new File(".");
		GBSearch g = GB.getSearch();
		File dump = new File(f, _name);
		PrintStream out;
		try {
			out = new PrintStream(dump);
			g.searchCore(lac, "*:*", 0, 100000, out, null, new SearchConfig(SearchConfig.RAW_SEARCH));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dump;
	}

}
