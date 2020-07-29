package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.TestingDao;
import cfe.model.Testing;
import cfe.utils.HibernateUtils;


public class TestingService {
	
	private static final Log log = LogFactory.getLog(TestingService.class);
	
	public static List<Testing> getAll() throws ServiceException {

		List<Testing> testings = new ArrayList<Testing>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
	    TestingDao testingDao = new TestingDao(session, tx);

		try	{
			testings = testingDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return testings;
	}

}