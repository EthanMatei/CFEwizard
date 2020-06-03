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

//	@Test
//	public void test() {
//		
//		//boolean loaded = TestUtil.loadDatabaseFiles();
//		//assertTrue( loaded );
//				
//		// Just some basic timing
//		// Could have used nanoTime(), but we don't need that much precision
//		long startTime = System.currentTimeMillis();
//	
//		// Our target table
//		String tablename = cfe.enums.Tables.HU_BRAIN_GENE.getClassname();
//		
//		Session session = HibernateUtils.getSession();
//		
//		// Check to see how many records there are currently in the table
//		int numExistingRecords =  getNumRecords(session, tablename);
//		
//		//String path = "C:\\Docs\\CFG_Score\\CFGWizard_for_Michel\\CFGWizard_for_Michel\\db_07Nov\\";
//        String path = "./test-data/ms-access/";
//		
//		String filename = "HUBRAIN (NJ 10-30-2013).accdb";
//				
//		File dbfile = new File(path + filename);
//		
//		assertNotNull(dbfile);
//		
//		try	{
//			
//			Database db = DatabaseBuilder.open(dbfile);
//			
//			List<HuBrainGene> hbgs = new ArrayList<HuBrainGene>();
//			
//			assertNotNull(hbgs);
//			
//			Set<String> tablenames = db.getTableNames();
//			
//			for (String tblname : tablenames)	{
//				
//				if (tblname.contains(cfe.enums.Tables.HU_BRAIN_GENE.getLabel()))	{
//														
//					Parser<HuBrainGene> parser = new Parser<HuBrainGene>();
//			
//					assertNotNull(parser);
//			
//					parser.parseTable(db, tblname, "cfe.model.HuBrainGene", hbgs);
//					
//					parser = null;
//				}
//			}
//			
//			db.close();
//			
//			Transaction tx = session.beginTransaction();
//			
//			HuBrainGeneDao hbgDao = new HuBrainGeneDao(session, tx);
//			
//			// Clean the table (remove current old data)
//			hbgDao.deleteAll(cfe.enums.Tables.HU_BRAIN_GENE.getTblName());
//			
//			// Check to see it the table is clean
//			int numCurrentRecords =  getNumRecords(session, tablename);
//			
//			if (numCurrentRecords > 0)	{
//				throw new Exception ("Table " + tablename + " not deleted");
//			}
//		
//			// Insert new data
//			hbgDao.saveAll(hbgs);
//			
//			int numInsertedRecords = getNumRecords(session, tablename);
//			
//			// Successful insert? 
//			if (numInsertedRecords != hbgs.size())	{
//				throw new Exception ("Error inserting records in table " + tablename + ".NumParsedRecords: " + hbgs.size() + ". NumInsertedRecords = " + numInsertedRecords);
//			}
//			
//			System.out.println(tablename + ": numExistingRecords = " + numExistingRecords);
//			System.out.println(tablename + ": NumParsedRecords = " + hbgs.size());
//			System.out.println(tablename + ": numInsertedRecords = " + numInsertedRecords);
//			
//			// Now we check ScoringData
//			int numTotalRecords =  getNumRecords(session, "ScoringData");
//			
//			// Existing records for tablename
//			numExistingRecords = getNumRecordsScoringData(session, tablename);
//			
//			// Now we check to see if data was updated
//			numInsertedRecords = getNumRecordsScoringData(session, tablename);
//			
//			if (numInsertedRecords != numExistingRecords)	{
//				throw new Exception ("Error inserting records in ScoringData for table " + tablename + ". NumExistingRecords: " + numExistingRecords + ". NumInsertedRecords = " + numInsertedRecords);
//			}
//			System.out.println(tablename + ": ScoringData numTotalRecords = " + numTotalRecords);
//			System.out.println(tablename + ": ScoringData numExistingRecords = " + numExistingRecords);
//			System.out.println(tablename + ": ScoringData numInsertedRecords = " + numInsertedRecords);
//			
//			long endTime = System.currentTimeMillis();
//			
//			double totalTime = (endTime - startTime)*0.001; // in seconds
//			
//			if (totalTime > 60.0)
//				System.out.println("Done. Total time: " + totalTime/60.0 + " minutes");
//			else
//				System.out.println("Done. Total time: " + totalTime + " seconds");
//		} catch (Exception ioe) {
//			
//			fail("Error " + ioe);	
//			
//		} finally	{
//		
//			session.close(); 
//			session = null;
//		}
//	}

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
