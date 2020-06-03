package cfe.parser;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import cfe.dao.*;
import cfe.model.*;
import cfe.utils.HibernateUtils;
import cfe.utils.ParseResult;
import cfe.utils.TableParseResult;
import cfe.utils.Util;

/**
 * Class for parsing MS Access database files.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class MSAccessParser implements IParser {
	
	private static final Log log = LogFactory.getLog(MSAccessParser.class);
	
	private List<String> validationMsgs = new ArrayList<String>(10);
	private ParseResult parseResult;
	
	public void parse(String filename) throws Exception {
		
		log.info("Processing uploaded ms file " + filename);
		
		parseResult = new ParseResult(filename);
		
		File dbfile = new File(filename);
		
		Database db = DatabaseBuilder.open(dbfile);
		
		Set<String> tablenames = db.getTableNames();
				
		Session session = HibernateUtils.getSession();
		
		//ArrayList<String> tblnames = new ArrayList<String>(20);
		
		//--------------------------------------------------------------------
		// For each table in the database...
		//--------------------------------------------------------------------
		for (String tablename : tablenames)	{

			log.info("Found  table " + tablename);
			parseResult.addTableParseResult(tablename);

			try {
				/************************ Begin HUBRAIN TABLES *******************************/

				if (tablename.contains(cfe.enums.Tables.HU_BRAIN_GEX.getLabel())) {
					HuBrainGex entity = new HuBrainGex();
					List<HuBrainGex> hbgs = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuBrainGexDao hbgDao = new HuBrainGexDao(session, tx);

					hbgDao.deleteAll(cfe.enums.Tables.HU_BRAIN_GEX.getTblName());
					hbgDao.saveAll(hbgs);

					// tblnames.add(cfe.enums.Tables.HU_BRAIN_GEX.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.HU_BRAIN_MET.getLabel())) {
					HuBrainMet entity = new HuBrainMet();
					List<HuBrainMet> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuBrainMetDao hbgDao = new HuBrainMetDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.HU_BRAIN_MET.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_BRAIN_MET.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.HU_BRAIN_PROT.getLabel())) {
					HuBrainProt entity = new HuBrainProt();
					List<HuBrainProt> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuBrainProtDao hbgDao = new HuBrainProtDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.HU_BRAIN_PROT.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_BRAIN_PROT.getClassname());

					/************************ End HUBRAIN TABLES *******************************/

					/************************ Begin HUGENE TABLES ******************************/
				} else if (tablename.contains(cfe.enums.Tables.HU_GENE_ASSOC.getLabel())) {
					HuGeneAssoc entity = new HuGeneAssoc();
					List<HuGeneAssoc> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuGeneAssocDao hbgDao = new HuGeneAssocDao(session, tx);

					hbgDao.deleteAll(cfe.enums.Tables.HU_GENE_ASSOC.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_GENE_ASSOC.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.HU_GENE_CNV.getLabel())) {
					HuGeneCNV entity = new HuGeneCNV();
					List<HuGeneCNV> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuGeneCNVDao hbgDao = new HuGeneCNVDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.HU_GENE_CNV.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_GENE_CNV.getClassname());
				}

				/**
				 * Disable Linkage for now else if
				 * (tablename.contains(cfe.enums.Tables.HU_GENE_LINKAGE.getLabel())) {
				 * List<HuGeneLinkage> hbps = new ArrayList<HuGeneLinkage>();
				 * 
				 * Parser<HuGeneLinkage> parser = new Parser<HuGeneLinkage>();
				 * 
				 * parser.parseTable(db, tablename, "cfe.model.HuGeneLinkage", hbps);
				 * validationMsgs.addAll(parser.getValidationMsgs());
				 * 
				 * Transaction tx = session.beginTransaction(); HuGeneLinkageDao hbgDao = new
				 * HuGeneLinkageDao(session, tx);
				 * hbgDao.deleteAll(cfe.enums.Tables.HU_GENE_LINKAGE.getTblName());
				 * hbgDao.saveAll(hbps);
				 * //tblnames.add(cfe.enums.Tables.HU_GENE_LINKAGE.getClassname());
				 * 
				 * /* *********************** End HUGENE TABLES ****************************** *
				 * /
				 * 
				 * /* *********************** Begin HUPER TABLES ***************************** *
				 * / }
				 */
				else if (tablename.contains(cfe.enums.Tables.HU_PER_GEX.getLabel())) {
					HuPerGex entity = new HuPerGex();
					List<HuPerGex> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuPerGexDao hbgDao = new HuPerGexDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.HU_PER_GEX.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_PER_GEX.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.HU_PER_MET.getLabel())) {
					HuPerMet entity = new HuPerMet();
					List<HuPerMet> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuPerMetDao hbgDao = new HuPerMetDao(session, tx);

					hbgDao.deleteAll(cfe.enums.Tables.HU_PER_MET.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_PER_MET.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.HU_PER_PROT.getLabel())) {
					HuPerProt entity = new HuPerProt();
					List<HuPerProt> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					HuPerProtDao hbgDao = new HuPerProtDao(session, tx);

					hbgDao.deleteAll(cfe.enums.Tables.HU_PER_PROT.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.HU_PER_PROT.getClassname());

					/************************ End HUPER TABLES ***********************************/

					/************************ Begin NHBRAIN TABLES *******************************/
				} else if (tablename.contains(cfe.enums.Tables.NH_BRAIN_GEX.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhBrainGex entity = new NhBrainGex();
					List<NhBrainGex> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhBrainGexDao hbgDao = new NhBrainGexDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_BRAIN_GEX.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_BRAIN_GEX.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.NH_BRAIN_MET.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhBrainMet entity = new NhBrainMet();
					List<NhBrainMet> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhBrainMetDao hbgDao = new NhBrainMetDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_BRAIN_MET.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_BRAIN_MET.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.NH_BRAIN_PROT.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhBrainProt entity = new NhBrainProt();
					List<NhBrainProt> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhBrainProtDao hbgDao = new NhBrainProtDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_BRAIN_PROT.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_BRAIN_PROT.getClassname());
					/************************ END NHBRAIN TABLES *******************************/

				} else if (tablename.contains(cfe.enums.Tables.NH_GENE_ASSOC.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhGeneAssoc entity = new NhGeneAssoc();
					List<NhGeneAssoc> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhGeneAssocDao hbgDao = new NhGeneAssocDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_GENE_ASSOC.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_GENE_ASSOC.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.NH_GENE_CNV.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhGeneCNV entity = new NhGeneCNV();
					List<NhGeneCNV> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhGeneCNVDao hbgDao = new NhGeneCNVDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_GENE_CNV.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_GENE_CNV.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.NH_PER_MET.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhPerMet entity = new NhPerMet();
					List<NhPerMet> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhPerMetDao hbgDao = new NhPerMetDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_PER_MET.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_PER_MET.getClassname());

					/************************ END NHBRAIN TABLES *******************************/
				} else if (tablename.contains(cfe.enums.Tables.NH_PER_PROT.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhPerProt entity = new NhPerProt();
					List<NhPerProt> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhPerProtDao hbgDao = new NhPerProtDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_PER_PROT.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_PER_PROT.getClassname());

				} else if (tablename.contains(cfe.enums.Tables.NH_PER_GEX.getLabel())) {
					// DateAdded in the ms access is TEXT. Need to change to date
					NhPerGex entity = new NhPerGex();
					List<NhPerGex> entities = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					NhPerGexDao hbgDao = new NhPerGexDao(session, tx);
					hbgDao.deleteAll(cfe.enums.Tables.NH_PER_GEX.getTblName());
					hbgDao.saveAll(entities);
					// tblnames.add(cfe.enums.Tables.NH_PER_GEX.getClassname());
				} else {

					log.warn("Ignored tablename " + tablename);
					parseResult.setTableStatus(tablename, TableParseResult.Status.IGNORED);
				}
			} catch (Exception exception) {
				this.parseResult.setTableStatus(tablename, TableParseResult.Status.ERROR);
				String errorMessage = exception.getLocalizedMessage();
				Throwable cause = exception.getCause();
				if (cause != null) {
					errorMessage += ": " + cause.getLocalizedMessage();
				}
				this.parseResult.addTableIssue(tablename, errorMessage);
				throw exception; // re-throw the exception
			}
		}
		db.close();
		
		session.close();
		
		session = null;
		
		
		/* Jim ??????????? 
		 * Perhaps this code was necessary with the parallel file upload version,
		 * but I can't see why it is needed with the serial version
		 *
		// Give some time to release the session
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        ***************************/
		
		log.info("Done processing ms uploaded file " + filename);
		log.info(Util.getFreeMemory());
	}

	
	public <T> List<T> parseTable(Database db, String tableName, String className) throws Exception {
		List<T> entities = new ArrayList<T>();
		Parser<T> parser = new Parser<T>();
		
		validationMsgs.clear();
		
	    parser.parseTable(db, tableName, className, entities);

	    this.parseResult.setTableStatus(tableName, TableParseResult.Status.PROCESSED);
		validationMsgs.addAll(parser.getValidationMsgs());
		for (String issue: validationMsgs) {
			this.parseResult.addTableIssue(tableName, issue);
		}

		return entities;
	}
	
	/*
	public <T> List<T> parseTable(Database db, String tablename, cfe.enums.Tables table, Session session) {
		List<T> hbgs = new ArrayList<T>();
		
		Parser<T> parser = new Parser<T>();
		
		parser.parseTable(db, tablename, "cfe.model." + table.getClassname(), hbgs);
		
		validationMsgs.addAll(parser.getValidationMsgs());
		
		for (String issue: validationMsgs) {
			this.parseResult.addTableIssue(tablename, issue);
		}
		
		return hbgs;

		Transaction tx = session.beginTransaction();
		D hbgDao = new D(session, tx);
		
		hbgDao.deleteAll(table.getTblName());
		hbgDao.saveAll(hbgs);

		//tblnames.add(table.getClassname());
	}
	*/
	
	
	public List<String> getValidationMsgs() {
		return this.validationMsgs;
	}
	
	public ParseResult getParseResult() {
		return this.parseResult;
	}
}
