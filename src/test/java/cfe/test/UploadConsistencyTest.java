package cfe.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import cfe.dao.HuBrainGexDao;
import cfe.model.HuBrainGex;
import cfe.parser.Parser;
import cfe.utils.HibernateUtils;


public class UploadConsistencyTest{


	private int getNumRecords(Session session, String tblname) {
		int numRec = 0;
		
		Query query = session.createQuery("SELECT COUNT(*) FROM " + tblname);
				
		Object obj = query.iterate().next();
		
		if (obj != null)	{
			
			numRec = Integer.parseInt(obj.toString());
		}
		return numRec;
		
	}
	
	private int getNumRecordsScoringData(Session session, String tblname) {
		int numRec = 0;
		
		Query query = session.createQuery("SELECT COUNT(*) FROM ScoringData where srcTableName = '" + tblname + "'");
				
		Object obj = query.iterate().next();
		
		if (obj != null)	{
			
			numRec = Integer.parseInt(obj.toString());
		}
		return numRec;		
	}
}
