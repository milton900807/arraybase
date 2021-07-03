package com.arraybase;

import java.util.Properties;

import com.arraybase.modules.InsertFactory;
import com.arraybase.modules.InstallFactory;
import com.arraybase.modules.ModFactory;

public class Mod {

	public static GBModule getModule(String cmd, String type, Properties _props)
			throws GBModuleNotFoundException {
		if (cmd.equalsIgnoreCase(GB.INSTALL)) {
			GBModule m = InstallFactory.create(type);
			if (m == null)
				new GBModuleNotFoundException("Failed to find the module for "
						+ cmd + " --> " + type);
			return m;
		} else if (cmd.equalsIgnoreCase(GB.BUILD)) {
			GBModule m = GBModuleBuildFactory.create(type, null);
			if (m == null)
				new GBModuleNotFoundException("Failed to find the module for "
						+ cmd + " --> " + type);
			return m;
		} else if (cmd.equalsIgnoreCase(GB.SET)) {
			GBModule m = ModFactory.create(type);
			if (m == null)
				new GBModuleNotFoundException("Failed to find the module for "
						+ cmd + " --> " + type);
			return m;
		} else if (cmd.equalsIgnoreCase(GB.INSERT)) {
			GBModule m = InsertFactory.create(type, _props);
			if (m == null)
				throw new GBModuleNotFoundException(
						" Failed to find the gb module for type = " + type
								+ " and properties : " + _props.toString());
			return m;
		} else
			throw new GBModuleNotFoundException(
					"Failed to find the module for " + cmd + " --> " + type);
	}

}
