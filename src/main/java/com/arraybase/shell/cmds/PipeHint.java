package com.arraybase.shell.cmds;

import com.arraybase.shell.CommandOption;

public class PipeHint extends CommandOption {

	public PipeHint() {

	}

	public String toString() {
		String h = "\n\ndiff($column1, ..., column n )[column1][...][column_n][diff]\n";
		h+= "\n\nmean";
		return h;
	}

}
