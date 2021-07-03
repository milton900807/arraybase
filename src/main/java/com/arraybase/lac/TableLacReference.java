package com.arraybase.lac;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.tm.tables.TTable;
import com.arraybase.util.GBLogger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Manages table references..
 */
public class TableLacReference implements LACReference {

    private TTable table = null;
    private GBLogger log = GBLogger.getLogger(TableLacReference.class);

    public void save(DBConnectionManager db) throws LacReferenceSaveException {
        if (table == null) {
            log.error(" Table was not loaded.");
            return;
        }
        Session se = null;
        try {
            se = db.getSession();
            se.beginTransaction();
            if (table != null)
                table.setItemID(-1);
            se.save(table);
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(se);
        }
    }

    /**
     * The reference for this object
     */
    public String getReference() {
        // {{ THIS IS THE SOLR REFERENCE FOR THIS OBJECT}}
        String ref = NameUtiles.prepend(table.getUser(), table.getTitle());
        return ref;
    }


    /**
     * Construct the object from a database given a connection manager
     *
     * @param link
     * @param _connection
     */
    public void load(String link, DBConnectionManager _connection)
            throws LoadFailedException {

        String data = LAC.getData(link);
        Session session = null;
        try {
            session = _connection.getSession();
            session.beginTransaction();
            Criteria c = session.createCriteria(TTable.class);
            if (link.startsWith("com.tissuematch.tm3.mylib.TMLibrary")) {
                Integer datav = Integer.parseInt(data);
                c.add(Restrictions.eq("itemID", datav));
            } else {
                String[] lac = LAC.parse(link);

                log.install("LAC target : " + lac[0] + " for the link " + link);
                // END THE LOAD IF THE LAC DOES NOT RETURN THE CORRECT VALUE
                if (lac == null || lac.length != 3 || lac[0] == null
                        || (!lac[0].contains("_Repository_"))) {
                    log.error("Failed to find the target for the Table lac "
                            + link);
                    _connection.close();
                    return;
                }
                String name = NameUtiles.strip(lac[0]);
                // we now can search the libs given the schema name == title
                c.add(Restrictions.eq("title", name));
            }
            List l = c.list();
            if (l == null || l.size() <= 0) {
                throw new LoadFailedException("Failed to find the item for "
                        + data);

            }
            TTable t = (TTable) l.get(0);
            log.install(" Table was found : " + t.getTitle());
            t.getSubitems();
            session.flush();
//			t.setItemID(-1);
            table = t;
            // session.close();
            if (session != null)
                HBConnect.close(session);
        } catch (Exception _e) {
            _e.printStackTrace();
            throw new LoadFailedException(_e);
        } finally {
            HBConnect.close(session);
        }
    }

}
