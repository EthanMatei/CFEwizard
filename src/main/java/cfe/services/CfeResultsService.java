package cfe.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.CfeResultsDao;
import cfe.model.CfeResults;
import cfe.utils.HibernateUtils;


public class CfeResultsService {
	
	private static final Log log = LogFactory.getLog(CfeResultsService.class);
	
	
	public static List<CfeResults> getAll() {

		List<CfeResults> cfeResults = new ArrayList<CfeResults>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		CfeResultsDao cfeResultsDao = new CfeResultsDao(session, tx);

		try	{
			cfeResults = cfeResultsDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
		} 
		finally	{
			session.close();
		}

		return cfeResults;
	}
	
	public static void save(CfeResults cfeResults) {

        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsDao cfeResultsDao = new CfeResultsDao(session, tx);
        
        try {
            cfeResultsDao.save(cfeResults);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
	}
	
	public static CfeResults get(Long cfeResultsId) {
        CfeResults cfeResults = null;
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsDao cfeResultsDao = new CfeResultsDao(session, tx);
        
        try {
            cfeResults = cfeResultsDao.getById(cfeResultsId);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
        
        return cfeResults;
	}
	
    public static List<CfeResults> getAllMetadata() {
        List<CfeResults> results = null;
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsDao cfeResultsDao = new CfeResultsDao(session, tx);
        
        try {
            results = cfeResultsDao.getAllMetadata();
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
        
        return results;
    }	
    
    public static void deleteById(Long cfeResultsId) {
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsDao cfeResultsDao = new CfeResultsDao(session, tx);
        
        try {
            CfeResults cfeResults = cfeResultsDao.getById(cfeResultsId);
            cfeResultsDao.delete(cfeResults);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }        
    }
}