package com.arraybase.var.app;

import java.util.ArrayList;

public class Oligo {

	private String sequence = null;
	private ArrayList<GenomicPosition> genomicPosition = new ArrayList<GenomicPosition> ();

	public Oligo(String sequence) {
		this.sequence = sequence;
	}

	public void addTarget(GenomicPosition genomic_position) {
		genomicPosition.add ( genomic_position );
	}
	

}
