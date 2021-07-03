package com.arraybase.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

import java.util.*;

/**
 * Created by jmilton on 2/24/2017.
 */
// create table tablename from aws.dynamodb-tablename (field1, field2)
public class CreateTableIndexFromAWSDynamoDB implements com.arraybase.GBPlugin {

    private String monomer_id_key = "monomer.monomer.alternateId";
    private String polymer_type_key = "monomer.polymerType";
    private String dynamodb_endpoint = "http://dynamodb.us-east-1.amazonaws.com";
    private String table_name = "monomer_library";


    public String exec(String command, String variable_key) throws UsageException {
        int from_index = command.indexOf("from") + 5;
        int create_index = command.indexOf("table") + 6;
        String dynamodb_table_name = command.substring(from_index);
        String path = command.substring(create_index, from_index - 5);
        if (path == null || path.length() < 0) {
            GB.print(" Path was not determined from the command : " + command);
            return null;
        }

        GB.print(" Default endpoint used : " + dynamodb_endpoint);
        path = path.trim();
        GB.print("Creating table : " + path);
        dynamodb_table_name = dynamodb_table_name.trim();

        // {{ FIRST THING IS TO CONNECT TO THE DDYNAMODB TABLE }}
        AmazonDynamoDBClient client = new AmazonDynamoDBClient()
                .withEndpoint(dynamodb_endpoint);
        DynamoDB dynamoDB = new DynamoDB(client);

        int i = dynamodb_table_name.indexOf('.');
        int fi = dynamodb_table_name.indexOf('(');
        int fc = dynamodb_table_name.lastIndexOf(')');


        String dbtable = dynamodb_table_name;
        String[] fields = null;
        if (i > 0) {
            if (fi <= 0)
                dbtable = dynamodb_table_name.substring(i + 1);
            else
                dbtable = dynamodb_table_name.substring(i + 1, fi);
        }
        // parse the feild values
        if (fi > 0) {
            String sb = dynamodb_table_name.substring(fi + 1, fc);
            sb = sb.trim();
            fields = sb.split(",");
            for (String field : fields) {

                System.out.println("parsed field " + field);
            }

        }

        dbtable = dbtable.trim();


        Table table = dynamoDB.getTable(dbtable.trim());

        String param_prj = "";
        for (String f : fields)
            param_prj += f + ",";
        param_prj = param_prj.substring(0, param_prj.length() - 1);

        if (table != null) {
            TableDescription description = table.describe();
            List<AttributeDefinition> attrs = description.getAttributeDefinitions();
            for (AttributeDefinition ad : attrs) {
                GB.print(" at : " + ad.getAttributeName() + " " + ad.getAttributeType());
            }


            Map<String, AttributeValue> lastKeyEvaluated = null;
            Map<String, String> schema_definition = null;
            ABTable new_table = null;
            do {
                ScanRequest scanRequest = new ScanRequest()
                        .withTableName(dbtable)
                        .withProjectionExpression(param_prj)
                        .withLimit(10)
                        .withExclusiveStartKey(lastKeyEvaluated);
                ScanResult result = client.scan(scanRequest);


                for (Map<String, AttributeValue> item : result.getItems()) {


                    HashMap<String, Object> data = new HashMap<String, Object>();
                    for (String field_name : fields) {
                        AttributeValue at = item.get(field_name);

                        if (schema_definition == null) {
                            schema_definition = build_schema(item);
                            Map<String, String> schema = build_ab_schema(schema_definition);
                            String absolutepath = GB.pwd() + "/" + path;
                            GB.createTable("abuser", absolutepath, schema);
                            new_table = new ABTable(absolutepath);
                        }

                        if (new_table == null)
                            throw new UsageException(" Failed to create the table from the dynamodb schema ");


                        String type = schema_definition.get(field_name);
                        if (type != null) {
                            if (type.equalsIgnoreCase("N")) {
                                System.out.println(field_name + " " + at.getN());
                                data.put(field_name, at.getN());
                            } else if (type.equalsIgnoreCase("S")) {
                                data.put(field_name, at.getS());
//                                System.out.println ( field_name + " " + at.getS() );
                            }
                        }else
                        {
                            GB.print ( " ERROR : A Field was specified but doesn't appear to be in the database " );
                        }
//                            int isisno = Integer.parseInt(item.get ( "isisno").getN());
//                            String helm = item.get ( "helm" ).getS();

                    }
                    new_table.append(data, false);

                }
                new_table.commit();
//                    create table testdb from aws.oligodb (isisno,helm)
                lastKeyEvaluated = result.getLastEvaluatedKey();
            } while (lastKeyEvaluated != null);
        }
//
//
//            // build the schema object:
//            Map<String, String> schema = new LinkedHashMap<String, String>();
//            for (GColumn gc : desc) {
//                String type = gc.getType();
//                String name = gc.getName();
//                if (name.contains("__900807") || name.equalsIgnoreCase("_version_")) {
//
//                } else {
//                    schema.put(name, type);
//                }
//            }
//            schema.remove("TMID_lastUpdated");
//            schema.remove("TMID");
//
//            Set<String> fields = schema.keySet();
//            String[] descl = new String[fields.size()];
//            int i = 0;
//            for (String dd : fields) {
//                descl[i++] = dd;
//            }
//
//
//            String absolutepath = GB.pwd() + "/" + path;
//            GB.createTable("abuser", absolutepath, schema);
//
//            ABTable new_table = new ABTable(absolutepath);
////
//            HttpSolrClient client = new HttpSolrClient(url);
//            for ( int docs=0; docs<MAX_DOCS; docs+=INCREMENT) {
//                buildtablefromquery("*:*", descl, docs, INCREMENT, client, new_table);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            GB.print("Does not appear this a valid URL string : " + url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            GB.print("Could not connect to : " + url);
//        }
//
//
//        System.out.println(" create the table from a url string : " + command);
//
        return null;

    }

    private Map<String, String> build_ab_schema(Map<String, String> awsmap) {
        HashMap<String, String> map = new HashMap<String, String>();
        Set<String> s = awsmap.keySet();
        for (String field : s) {
            map.put(field, "string_ci");
        }
        return map;
    }

    private Map<String, String> build_schema(Map<String, AttributeValue> item) {

        HashMap<String, String> map = new HashMap<String, String>();
        Set<String> s = item.keySet();
        for (String field : s) {
            AttributeValue av = item.get(field);
            if (av.getN() != null) {
                map.put(field, "N");
            } else if (av.getS() != null) {
                map.put(field, "S");
            } else if (av.getBOOL() != null) {
                map.put(field, "B");
            }
        }
        return map;

    }

    @Override
    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
