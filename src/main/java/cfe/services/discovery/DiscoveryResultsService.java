package cfe.services.discovery;

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

import cfe.services.ServiceException;

import cfe.dao.discovery.DiscoveryResultsDao;
import cfe.model.discovery.DiscoveryResults;
import cfe.utils.HibernateUtils;


public class DiscoveryResultsService {
	
	private static final Log log = LogFactory.getLog(DiscoveryResultsService.class);
	
	
	public static List<DiscoveryResults> getAll() {

		List<DiscoveryResults> discoveryResults = new ArrayList<DiscoveryResults>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		DiscoveryResultsDao discoveryResultsDao = new DiscoveryResultsDao(session, tx);

		try	{
			discoveryResults = discoveryResultsDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
		} 
		finally	{
			session.close();
		}

		return discoveryResults;
	}
	
	public static void save(DiscoveryResults discoveryResults) {

        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        DiscoveryResultsDao discoveryResultsDao = new DiscoveryResultsDao(session, tx);
        
        try {
            discoveryResultsDao.save(discoveryResults);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
	}
	
	public static DiscoveryResults get(Integer discoveryResultsId) {
        DiscoveryResults discoveryResults = null;
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        DiscoveryResultsDao discoveryResultsDao = new DiscoveryResultsDao(session, tx);
        
        try {
            discoveryResults = discoveryResultsDao.getById(discoveryResultsId);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
        
        return discoveryResults;
	}
	
    public static List<DiscoveryResults> getAllMetadata() {
        List<DiscoveryResults> results = null;
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        DiscoveryResultsDao discoveryResultsDao = new DiscoveryResultsDao(session, tx);
        
        try {
            results = discoveryResultsDao.getAllMetadata();
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
}