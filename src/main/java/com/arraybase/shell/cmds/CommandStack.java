package com.arraybase.shell.cmds;

import java.util.Stack;

public class CommandStack {

	private Stack<String> cmds = new Stack<String>();

	public void push(String cmd) {
		cmds.push(cmd);
	}

	public String pop() {
		return cmds.pop();
	}
}
