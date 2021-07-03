package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.io.GBOutputFileReader;
import com.arraybase.io.LineListener;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.IOUTILs;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.CommandlineJava;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RunUpdateObject implements GBPlugin {

	// mytable.setupdateobject(mymainclass,myjarfile)
	public String exec(String command, String variable_key)
			throws UsageException {

		// {{ 1. GET THE TARGET OBJECT }}
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		// this will update an index. n
		// node.refresh(where MTID > node.max(MTID))
		if (target == null) {
			GB.print("Failed to find the target : " + target);
			return null;
		}
		return "Node Update complete.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

	// update(com.arraybase.test.UpdateTest,gb.jar)
	public void update(final TNode node, NodeProperty update_object)
			throws UsageException {
		FileOutputStream fos = null;
		try {
			long node_id = node.getNode_id();
			NodeProperty updateObject = NodeManager.getNodeProperty(
					node.getNode_id(), NodePropertyType.UPDATE.name);
			if (updateObject != null) {
				byte[] updatefile = updateObject.getFile();
				if (updatefile == null) {
					throw new UsageException(
							"In order to update this Node you will need to store the executable (jar) that is responsible for updating it."
									+ "This is currently not stored... use the $target.setUpdateObject(myclass,myjar) command.");
				}
				// write the file.
				// call java -jar file java -cp myjar.jar myClass.
				// create an object of FileOutputStream
				File temp = File.createTempFile("_update", "jar");
				fos = new FileOutputStream(temp);
				fos.write(updatefile);
				fos.flush();
				Project project = new Project();
				project.setBaseDir(new File(System.getProperty("user.dir")));
				project.init();
				project.fireBuildStarted();

				Java javaTask = new Java();
				javaTask.setTaskName("runjava");
				javaTask.setProject(project);
				javaTask.setFork(true);
				File error = new File("error.out");
				javaTask.setError(error);
				File temp_out = File.createTempFile("" + node.getNode_id()
						+ "update", "out");
				File temp_err = File.createTempFile("" + node.getNode_id()
						+ "update", "err");
				javaTask.setOutput(temp_out);
				javaTask.setError(error);
				javaTask.setFailonerror(true);
				javaTask.setJar(temp);
				Argument node_arg = javaTask.createArg();
				node_arg.setValue("node=" + node.getName());
				Argument node_arg1 = javaTask.createArg();
				node_arg1.setValue("link=" + node.getLink());
				Argument node_arg2 = javaTask.createArg();
				node_arg2.setValue("solr=" + ABProperties.getSolrURL());
				// Argument jvmArgs = javaTask.createJvmarg();
				// jvmArgs.setLine("-Xms512m -Xmx512m");
				javaTask.init();
				try {
					javaTask.execute();
					CommandlineJava cml = javaTask.getCommandLine();
					GB.print(" cml : " + cml.toString());
					GBOutputFileReader gb = new GBOutputFileReader(temp_out);
					SetLinkListener stLink = new SetLinkListener(node);
					gb.addLineListener(stLink);
					
					CommandListener cmdListener = new CommandListener ();
					gb.addLineListener(cmdListener);
					
					gb.start();
				} catch (Exception _e) {

					GB.print(" FAILED TO EXECUTE UPDATE FOR NODE "
							+ node.getName() + " " + node.getNode_id());
					GB.print(" Using object " + updateObject.getName());
					GB.print(" \twith property id " + updateObject.getProp_id());

					_e.printStackTrace();
				}
			} else {

			}

		} catch (IOException _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(fos);
		}
	}

	class CommandListener implements LineListener {
		public String getStartsWithToken() {
			return "ab ";
		}

		public String getEndsWithToken() {
			return null;
		}

		public void lineFound(String _line) {
			String command = _line;
			int fi = command.indexOf(' ');
			String cmd = command.substring(fi+1);
			if ( cmd == null || cmd.length() <= 0 )
			{
				GB.print("Failed to run a command... where a command was expected ");
				GB.print ( "\tSkipping");
				return;
			}
			try {
				GB.execute(cmd.trim());
			} catch (UsageException e) {
				e.printStackTrace();
				GB.print("Failed to run a command." + e.getLocalizedMessage());
				GB.print ( "\tSkipping");
			}
		}
		

		
		
		
	}
	/**
	 *  This is an object that listens for a link
	 * @author jmilton
	 *
	 */
	class SetLinkListener implements LineListener {
		private TNode node = null;

		SetLinkListener(TNode node) {
			this.node = node;
		}

		public String getStartsWithToken() {
			return "setLink";
		}

		public String getEndsWithToken() {
			return null;
		}

		public void lineFound(String _line) {
			int index = _line.indexOf("setLink");
			String link = _line.substring(index + 7);
			int st = link.indexOf('(');
			int en = link.lastIndexOf(')');
			
			String nlink = link;
			if ( st >= 0 && en >  0 )
			{
				nlink = link.substring(st+1, en);
			}
			if ( nlink != null )
				nlink = nlink.trim();
			
			
			GB.print("Link update for node: " + node.getName() + " --> " + nlink);
			node.setLink(nlink);
			GBNodes mn = GB.getNodes();
			mn.save(node);
			GB.print("Node is saved. ");
		}

	}
}
