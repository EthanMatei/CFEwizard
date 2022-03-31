package cfe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.CfeResultsDao;
import cfe.dao.DiscoveryDao;
import cfe.model.Discovery;
import cfe.utils.HibernateUtils;


public class DiscoveryService {
	
	private static final Logger log = Logger.getLogger(DiscoveryService.class.getName());
	
	public static List<Discovery> getAll() throws ServiceException {

		List<Discovery> discoveries = new ArrayList<Discovery>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		DiscoveryDao discoveryDao = new DiscoveryDao(session, tx);

		try	{
			discoveries = discoveryDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return discoveries;
	}

}