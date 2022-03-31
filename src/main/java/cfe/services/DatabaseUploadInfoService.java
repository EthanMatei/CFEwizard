package cfe.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.CfeResultsDao;
import cfe.dao.DatabaseUploadInfoDao;
import cfe.model.DatabaseUploadInfo;
import cfe.utils.HibernateUtils;


public class DatabaseUploadInfoService {
	
	private static final Logger log = Logger.getLogger(DatabaseUploadInfoService.class.getName());
	
	
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
			log.severe( exception.getMessage() );
			tx.rollback();			
		} 
		finally	{
			session.close();
		}

	}


}