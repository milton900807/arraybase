package com.arraybase.test;

import com.arraybase.ABaseNode;
import com.arraybase.search.ABaseResults;

public class Speed_ {
	
	
	public static void main(String[] args){
		
		

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
		ABaseResults results = ABaseNode.get(
				"/isis/data/genomic/map/direct_mappingv1", "isisno:" + 301012,
				cols, 0, 1000);		
		System.out.println ( "done.");
		
	}
	

}
