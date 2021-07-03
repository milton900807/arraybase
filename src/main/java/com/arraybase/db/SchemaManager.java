package com.arraybase.db;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import com.arraybase.util.Level;
import com.arraybase.util.GBLogger;

/**
 * Schema manager will build/rebuild/drop schemas etc This can be used for
 * migration purposes and database initialization purposes.
 * 
 * @author donaldm
 * 
 */
public class SchemaManager {

	private GBLogger log = GBLogger.getLogger(SchemaManager.class);

	public void build(File config_file) {
		log.setLevel(Level.ALL);
		Configuration config = new Configuration();
		Configuration configuration = config.configure(config_file);
		config.buildMappings();
		Map classes = config.createMappings().getClasses();
//		Properties prop = config.getProperties();
//		Enumeration e = prop.keys();
//		while (e.hasMoreElements()) {
//			String key = e.nextElement().toString();
//			String value = prop.getProperty(key);
//			System.out.println( "key : " + key + " \tvalue: " + value );
//			//log.debug("" + key + " == " + value);
//		}
		Set<String> c = classes.keySet();
		for ( String k : c ){
			System.out.println ( " k : "+ k + " value : " + classes.get(k));
		}
		SchemaExport exp = new SchemaExport ( config );
		exp.create(true, true);
	}

	public static void buildTablerManager(){
		String config = "src/main/resources/tableManager.mysql.xml";
		File f = new File(config);
		System.out.println(" f : " + f.getAbsolutePath());
		SchemaManager sch = new SchemaManager();
		sch.build(f);
	}
	public static void buildTreeManager(){
		String config = "src/main/resources/treeManager.mysql.xml";
		File f = new File(config);
		System.out.println(" f : " + f.getAbsolutePath());
		SchemaManager sch = new SchemaManager();
		sch.build(f);
	}
	public static void buildFileManager(){
		String config = "src/main/resources/fileManager.mysql.xml";
		File f = new File(config);
		System.out.println(" f : " + f.getAbsolutePath());
		SchemaManager sch = new SchemaManager();
		sch.build(f);
	}
	public static void main ( String[] _ags ){
		SchemaManager.buildTablerManager();
	}
	
	
}
