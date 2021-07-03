package com.arraybase.db.test;

import java.util.ArrayList;
import java.util.Map;

import com.arraybase.ABaseNode;
import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;

public class ABResultsTest {

	/**
	 * This will build nodes for testing purposes.
	 * 
	 * @param _test
	 */
	public static void main(String[] _test) {
		

		String ISIS_NO = "isisno";
		String START = "start";
		String END = "end";
		String FSTART = "feature_start";
		String FEND = "feature_end";
		String feature_type = "feature";
		String gene = "gene";
		String chromosome = "chrom";
		String annotation_source = "data_src";
		String strand = "strand";
		String feature_strand = "feature_strand";
		String transcript_id = "transcipt_id";
		String exon_number = "exon_number";
		String exon_id = "exon_id";
		String intron_id = "intron_id";
		String gene_name = "gene_name";

		String[] cols = { ISIS_NO, START, END, FSTART, FEND, feature_type,
				gene, chromosome, annotation_source, strand, feature_strand,
				transcript_id, exon_number, exon_id, intron_id, gene_name };
		ABaseResults results = ABaseNode.get("/isis/data/genomic/map/test",
				"isisno:" + 301012, cols, 0, 1000);
		ArrayList<GRow> list = results.getValues();
		if (list == null) {
		}
		for (GRow row : list) {
			Map data = row.getData();
		
			System.out.println ( " d : "+ data.size ());
		}
		
		
	}

}
