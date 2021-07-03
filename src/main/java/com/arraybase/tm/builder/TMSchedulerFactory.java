package com.arraybase.tm.builder;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * 
 * @author donaldm
 */
public class TMSchedulerFactory {

	public static TMLibSchedule getSchedule(String _name) {
		Session ses = null;
		DBConnectionManager db = new DBConnectionManager();
		try {
			ses = db.getSession();
			ses.beginTransaction();
			Criteria c = ses.createCriteria(TMLibSchedule.class).add(
					Restrictions.eq("name", _name));
			List l = c.list();
			if (l != null && l.size() > 0) {
				TMLibSchedule sts = (TMLibSchedule) l.get(0);
				return sts;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(ses);
		}
		return null;
	}

}
