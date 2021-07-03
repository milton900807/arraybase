package com.arraybase.evolve;

import com.arraybase.ABTable;
import com.arraybase.tm.GRow;

import java.util.LinkedHashMap;

public class DataSourceFunction implements EvolveFunction {
    EvolveDataStoreFunction edf = null;
    String key = null;
    private LinkedHashMap<String, EvolveDataStoreFunction> dataSource;
    public DataSourceFunction(String field_name, String params, ABTable t) {
        System.out.println (" params " + params );
        int startindex = params.indexOf('.');
        int endindex = params.indexOf('.', startindex+1);
        int temp = params.indexOf('[');
        if ( temp < endindex ){
            // then we start with an array object
            endindex = temp;
        }
        this.key = params.substring(startindex+1, endindex);
    }
    public void eval(GRow gRow) {

        if ( this.dataSource != null && key != null ){
            EvolveDataStoreFunction df = this.dataSource.get(key);
//            System.out.println ( " df " + df. );
        }


        System.out.println ( " \t g row " + dataSource.get("oligodb"));
    }

    public void setDataSource(LinkedHashMap<String, EvolveDataStoreFunction> dataSource) {
        this.dataSource = dataSource;
    }
}
