
table ab_table = /isis/temp/something.search(*);

where (ab_table[txtStart] > 299239 && ab_table[txtStart] < 399388 ){
		ab_table[gene_annotation] = ncbi.getGene(ab_table[gene_id])
}



