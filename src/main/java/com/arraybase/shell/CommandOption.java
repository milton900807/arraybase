package com.arraybase.shell;

public class CommandOption {

	private String command = null;
	private String[] options = null;
	private String buffer = "";

	public CommandOption(String _command, String[] options) {
		this.command = _command;
		this.options = options;
	}

	public CommandOption() {
	}

	public String toString() {

		String t = "" + command + "\n";
		for (String o : options) {
			t += "\t" + o + "\n";
		}
		return t;

	}

	public void setCurrentBuffer(String b) {
		this.buffer = b;

	}

	public String getCurrentBuffer() {
		return buffer;
	}

	public String getNewBufferCommand() {
		return null;
	}

}
