package com.arraybase.evolve;
import com.arraybase.ABTable;
public class EvolveFunctionFactory {
    public static EvolveFunction create(String field_name, String functionKey, String params, ABTable t) {
        if (functionKey.equalsIgnoreCase("build_search_monomers")) {
            return new ChemistrySearchMonomersFunction(field_name, params, t);
        } else if (functionKey.equalsIgnoreCase("get_three_prime_chain")) {
            return new ThreePrimeChemistry(field_name, params, t);
        } else if (functionKey.equalsIgnoreCase("get_five_prime_chain")) {
            return new FivePrimeChemistry(field_name, params, t);
        } else {
            return new FetchFunctionEvolve(field_name, params, t);
        }
    }
    public static EvolveDataStoreFunction createDataSource(String function_key, String param) {
        return new FetchDatasourceFunctionEvolve(param);
    }
    public static EvolveFunction createDataSourcEV(String key, String function_value, ABTable t) {
        return new DataSourceFunction ( key, function_value, t);
    }
}
