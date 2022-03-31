package cfe.dao;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.DatabaseUploadInfo;


public class DatabaseUploadInfoDao extends AbstractDao<DatabaseUploadInfo> {
	
	private static final Logger log = Logger.getLogger(DatabaseUploadInfoDao.class.getName());
	
	
	public DatabaseUploadInfoDao( Session sess, Transaction tx) {
		super(DatabaseUploadInfo.class, sess, tx);
	}
	
	@Override
	public List<DatabaseUploadInfo> getAll() {
		List<DatabaseUploadInfo> entities = null;
		
        Query query = sess.createQuery("from DatabaseUploadInfo order by uploadTime desc");
        entities = query.list();
		return entities;
	}

}
