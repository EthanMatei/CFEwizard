package cfg.services;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import cfg.utils.HibernateUtils;


public class TableInfoService {
	
	private static final Log log = LogFactory.getLog(TableInfoService.class);
	
	public static long getCount(String className) throws ServiceException {
        Long count;
        
		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();

		try	{
			count = (Long) session.createCriteria(className).setProjection(Projections.rowCount()).uniqueResult();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return count.longValue();
	}
	
	public static int deleteAll(String className) throws ServiceException {
        int count = 0;
        
		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();

		try	{
			String hql = String.format("delete from %s", className);
			Query query = session.createQuery(hql);
			count =  query.executeUpdate();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return count;
	}

}