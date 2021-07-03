package com.arraybase.test;

import com.arraybase.ABTable;
import com.arraybase.ABaseNode;
import com.arraybase.search.ABaseResults;

public class TestAsyncStatsCommit {
	
	
	public static void main(String[] args){


		Thread t = null;

		Runnable run = new Runnable() {
			@Override
			public void run() {



				for  ( int i = 0; i < 10000; i++) {
					String d = "/isis/targets/sequence_features";
					String[] cols = {"FEATURE_NAME"};

					ABTable table = new ABTable(d);
					String q = "*";
					table.search(q, cols, 0, 100);
				}

			}
		};
		t = new Thread ( run );
		t.start();;

		System.out.println ( "done.");
		
	}
	

}
