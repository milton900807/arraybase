package com.arraybase.shell.cmds;

import javax.swing.JFrame;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class TestFrame implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		JFrame f = new JFrame ( "e" );
		f.setSize ( 200, 300 );
		f.setLocation(200, 200);
		f.setVisible(true);
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		// TODO Auto-generated method stub
		return null;
	}

}
