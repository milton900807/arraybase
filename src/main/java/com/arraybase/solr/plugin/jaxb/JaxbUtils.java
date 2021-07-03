package com.arraybase.solr.plugin.jaxb;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class JaxbUtils {

	private static final Class<Schema> SCHEMA_CLASS = Schema.class;

	private static JAXBContext getJaxbContext() throws JAXBException {
		return JAXBContext.newInstance(SCHEMA_CLASS);
	}

	public static Object urlToJaxb(String urlString) {
		Object result = null;
		try {
			URL url = new URL(urlString);
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			result = unmarshaller.unmarshal(url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static Object fileToJaxb(String filePath) {
		Object result = null;
		try {
			File url = new File(filePath);
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			result = unmarshaller.unmarshal(url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static Object inputStreamToJaxb(InputStream is) {
		Object result = null;
		try {
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			result = unmarshaller.unmarshal(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static void jaxbToFile(Object obj, File file,String schemaLoc) {
		try {
			Marshaller marshaller = getJaxbContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			if (schemaLoc !=null && !schemaLoc.isEmpty()) {
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,schemaLoc);
			}
			marshaller.marshal(obj, file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void jaxbToFile(Object obj, File file) {
		jaxbToFile(obj,file,null);
	}

	public static void jaxbToFile(Object obj, String filePath) {
		File file = new File(filePath);
		jaxbToFile(obj, file);
	}
	
	public static void jaxbToFile(Object obj, String filePath, String schemaLoc) {
		File file = new File(filePath);
		jaxbToFile(obj, file,schemaLoc);
	}

}
