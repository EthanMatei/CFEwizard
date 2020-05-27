package cfg.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;



import cfg.dao.DatabaseUploadInfoDao;
import cfg.model.DatabaseUploadInfo;
import cfg.utils.HibernateUtils;


public class DatabaseUploadInfoService {
	
	private static final Log log = LogFactory.getLog(DatabaseUploadInfoService.class);
	
	
	public static List<DatabaseUploadInfo> getAll() {

		List<DatabaseUploadInfo> infos = new ArrayList<DatabaseUploadInfo>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		DatabaseUploadInfoDao databaseUploadInfoDao = new DatabaseUploadInfoDao(session, tx);

		try	{
			infos = databaseUploadInfoDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
		} 
		finally	{
			session.close();
		}

		return infos;
	}
	
	/**
	 * Updates the Disorder table with values form the research data tables.
	 */
	public static void update(DatabaseUploadInfo info) {

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		
		DatabaseUploadInfoDao databaseUploadInfoDao = new DatabaseUploadInfoDao(session, tx);
		
		try	{
			
			databaseUploadInfoDao.save(info);
			tx.commit();
		}
		catch (Exception exception)	{
			log.error( exception.getMessage() );
			tx.rollback();			
		} 
		finally	{
			session.close();
		}

	}


}