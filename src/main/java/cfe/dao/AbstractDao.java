package cfe.dao;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.action.DiscoveryAction;
import cfe.utils.HibernateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract DAO class that provides common methods for all DAOs that extend it.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 * @param <T>
 */
public abstract class AbstractDao<T> extends BaseDao {
	
	// check out:
	// http://www.hiberbook.com/HiberBookWeb/learn.jsp?tutorial=21advanceddaos
	
	protected Session sess;
	protected Transaction tx;
	private Class<T> persistentClass;
    private static Logger log = Logger.getLogger(AbstractDao.class.getName());

	
	protected AbstractDao(Class<T> c, Session sess, Transaction tx ){
		this.sess = sess;
		this.tx = tx;
		this.persistentClass = c;
	}

	
	public void save(T t) throws HibernateException{
			
		try {
			sess.save(t);
			sess.flush(); // <- Allows you to "see" id after called
		} catch (HibernateException e){
			tx.rollback();
			//e.printStackTrace();
			log.severe("Hibernate error: " + e.toString());			
			throw e;
		}
	}
	
	// Use this method for batch inserts
	public void saveAll(List<T> ts) throws HibernateException{
		
		int count = 1;
		try {
			for (T t : ts)	{
				//sess.merge(t);
				sess.save(t);
				count++;
				if (count == HibernateUtils.BATCH_SIZE){
					sess.flush();
					sess.clear();
					count = 1;
				}
			}
			tx.commit();
		} catch (HibernateException e){
			tx.rollback();
			e.printStackTrace();
			log.severe("Hibernate error: " + e.toString());			
			throw e;
		}
	}

	
	public void saveAllRows(List<T> ts) throws HibernateException{

		int count = 1;

		for (T t : ts)	{
			sess.save(t);
			count++;
			if (count == HibernateUtils.BATCH_SIZE){
				sess.flush();
				//sess.clear();
				count = 1;
			}
		}
		
		sess.flush();
	}
	
	
	
	/**
	 * Gets all the entities of the specified class.
	 * @return
	 */
	public List<T> getAll() {
		List<T> entities = null;
		
        Query query = sess.createQuery("from " + persistentClass.getName());
        entities = query.list();
		return entities;
	}
	
	/**
	 * Gets the number of entities of the specified class.
	 * @return
	 */
	public long getCount() {
		long count = 0;
        Query query = sess.createQuery("count(*) from " + persistentClass.getName());
        count = query.getFirstResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> list(String fromTable){
		
		List<T> data = null;
		try {
			data = sess.createQuery(fromTable).list();
		} catch (HibernateException e) {
			log.severe("Hibernate error: " + e.toString());			
			//e.printStackTrace();
			throw e;
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public T getById(long id){
		return (T) sess.get( persistentClass, new Long(id));
	}
	
	public void delete(T t){
		try {
		    sess.delete(t);
		} catch (HibernateException e){
			log.severe("Hibernate error: " + e.toString());
			tx.rollback();
			throw e;
		}
	}
	
	// Cleans the table
	public void deleteAll(String tblname){
		try {
			log.info("Resetting table " + tblname);
			String sql = "DELETE FROM `" + tblname.replaceAll("`",  "") + "`";
			//Query query = sess.createSQLQuery("delete from :inTblName")		
			//.setString("inTblName", tblname);
			Query query = sess.createSQLQuery(sql);
			query.executeUpdate();

		} catch (HibernateException e){
			log.severe("Hibernate error: " + e.toString());
			tx.rollback();
			throw e;
		}
	}
	
	public void deleteAll() {
		Query cQuery = sess.createSQLQuery("SET SQL_SAFE_UPDATES = 0");
		cQuery.executeUpdate();
		
        String queryString = "DELETE FROM " + persistentClass.getName();
        Query query = sess.createQuery(queryString);
        query.executeUpdate();
	}
}
