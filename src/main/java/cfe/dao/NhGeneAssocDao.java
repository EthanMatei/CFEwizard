package cfe.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Disorder;
import cfe.model.NhGeneAssoc;

public class NhGeneAssocDao extends AbstractDao<NhGeneAssoc> {

	public NhGeneAssocDao( Session sess, Transaction tx) {
		super(NhGeneAssoc.class, sess, tx);
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
