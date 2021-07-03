package com.arraybase;

import java.util.*;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.CurrentTimeForSolr;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.lac.LAC;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * A client object
 *
 * @author jmilton
 */
public class AB {

    private static GBLogger log = GBLogger.getLogger(AB.class);

    /**
     * Create a table given a path and a schema
     *
     * @param path
     * @param schema
     * @return
     */
    public static String createTable(String path, Map<String, String> schema) {
        String user = GB.getDefaultUser();
        TNode node = GB.createTable(user, path, schema);
        String[] lac = LAC.parse(node.getLink());
        String server = GB.getDefaultURL();
        return server + lac[0];
    }

    public static String createTable(String path, LinkedHashMap<String, Map<String, String>> _params) {
        String user = GB.getDefaultUser();
        TNode node = GB.getNodes().getNode(path);
        if (node == null) {
            node = GB.getNodes().mkNode(user, path, "", SourceType.TABLE);
        }
        String schema = NameUtiles.convertToValidCharName(path);
        DBConnectionManager dbcm = GB.getConnectionManager();
        NodeManager tmnode = new NodeManager(dbcm);
        String core = NameUtiles.convertToValidCharName(path);
        String link = "" + schema + ".search(*:*)";
        node.setLink(link);
        node.setLastEditedDate(new Date());
        // {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
        UUID idOne = UUID.randomUUID();
        HashMap<String, String> uuidp = new HashMap<String, String>();
        uuidp.put("fieldName", "TMID");
        uuidp.put("sortable", "true");
        uuidp.put("indexed", "true");
        uuidp.put("defaultString", idOne.toString());
        uuidp.put("dataType", "string");
        uuidp.put("requiredField", "true");
        _params.put("TMID", uuidp);

        HashMap<String, String> last_updated = new HashMap<String, String>();
        last_updated.put("fieldName", "TMID_lastUpdated");
        last_updated.put("sortable", "true");
        last_updated.put("indexed", "true");
        last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
        last_updated.put("dataType", "date");
        last_updated.put("requiredField", "true");
        _params.put("TMID_lastUpdated", last_updated);
        log.info("Generating the solr schema... ");

        String solrSite = ABProperties.get("solrSite");
        TMSolrServer.createSchema(user, solrSite, core, _params,
                false);
//		TNode node = GB.createTable(user, path, schema);
        core = core.trim();
        if (!core.startsWith("/")) {
            core = "/" + core;
        }
        String s = GB.getDefaultURL();
        s = s.trim();
        if (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        String server = s + core;
        Session hibernateSession = dbcm.getSession();
        hibernateSession.beginTransaction();
        tmnode.save(node, hibernateSession);
        // String _lib_name = NameUtiles.strip(_userName, _name);
        TTable litem = new TTable();
        try {
            // lock it
            synchronized (hibernateSession) {
                Criteria c = hibernateSession.createCriteria(TTable.class);
                c.add(Restrictions.eq("title", schema));
                List values = c.list();
                if (values != null && values.size() > 0) {
                    litem = (TTable) values.get(0);
                }
                litem.setDescription("");
                litem.setLastEdited(new Date());
                // litem.setSecurityStatus(_security + ".png");
                litem.setUser(user);
                litem.setSourceType(node.getNodeType());
                TMTableSettings tmset = litem.getSettings();
                if (tmset == null)
                    tmset = new TMTableSettings();
                Set<String> keys = _params.keySet();
                LinkedHashMap<String, Integer> order = new LinkedHashMap<String, Integer>();
                int index1 = 0;
                for (String key : keys) {
                    order.put(key, index1++);
                }
                tmset.setCol_order(order);
                litem.setSettings(tmset);
                litem.setTitle(schema);
                if (litem.getItemID() < 0)
                    hibernateSession.save(litem);
                else {
                    hibernateSession.update(litem);
                    hibernateSession.flush();
                }
                hibernateSession.getTransaction().commit();
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(hibernateSession);
        }


        return server;
    }

    /**
     * Get the table ABTable
     * <p>
     * /**
     * Get the table ABTable
     *
     * @param path
     * @return
     */
    public static ABTable getTable(String path) {
        ABTable tb = new ABTable(path);
        return tb;
    }

    public static void main(String[] _args) {
        String path = "/isis/rpkm/human/h19";
        ABTable table = AB.getTable(path);
        if (!table.hasField("mtid"))
            table.addField(ABFieldType.SINT, "mtid");
    }

    public static void loadJars(String plugins) {
        String[] s = plugins.split(",");
        for ( String p : s )
        {
//            AWSs3

        }

    }
}
