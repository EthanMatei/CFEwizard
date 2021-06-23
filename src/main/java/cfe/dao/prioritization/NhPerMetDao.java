package cfe.dao.prioritization;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.AbstractDao;

import cfe.model.prioritization.Disorder;
import cfe.model.prioritization.NhPerMet;

public class NhPerMetDao extends AbstractDao<NhPerMet> {
	
	public NhPerMetDao( Session sess, Transaction tx) {
		super(NhPerMet.class, sess, tx);
	}
	
	
	public List<Disorder> getDisorders() {
		String queryString = "SELECT DISTINCT new Disorder(psychiatricDomain, subDomain, relevantDisorder) FROM " 
				             + this.getClass().getSimpleName().replace("Dao",  "")
				             + " WHERE psychiatricDomain IS NOT NULL AND subDomain IS NOT NULL AND relevantDisorder IS NOT NULL";				             
		Query query = sess.createQuery( queryString );
		List<Disorder> disorders = (List<Disorder>) query.list();
		return disorders;
	}
}
