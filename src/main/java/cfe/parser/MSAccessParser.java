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
	
	/**
	 * Parses the specified MS Access database file and stores the appropriate data
	 * in the application's database.
	 */
	public void parse(String filename) throws Exception {
		
		log.info("Processing uploaded ms file " + filename);
		
		parseResult = new ParseResult(filename);
		
		log.info("parseResult filename: " + parseResult.getFileName());
		
		File dbfile = new File(filename);
		
		Database db = DatabaseBuilder.open(dbfile);
		
		log.info("database object successfully created");
		
		Set<String> tablenames = db.getTableNames();
		
		log.info("number of database tables: " + tablenames.size());
		
		Session session = HibernateUtils.getSession();

		//--------------------------------------------------------------------
		// For each table in the database...
		//--------------------------------------------------------------------
		for (String tablename : tablenames)	{

			log.info("Found table " + tablename);
			parseResult.addTableParseResult(tablename);


		    try {
				
				if (tablename.contains(cfe.enums.CfeTables.DISCOVERY.getLabel())) {
					// DISCOVERY DATABASE TABLE
					Discovery entity = new Discovery();
					Set<String> fieldNames = entity.getDataFieldNames();
					List<Discovery> discoveries = this.parseTable(db, tablename, fieldNames, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					DiscoveryDao discoveryDao = new DiscoveryDao(session, tx);

					discoveryDao.deleteAll(cfe.enums.CfeTables.DISCOVERY.getTblName());
					discoveryDao.saveAll(discoveries);
				} 
				else if (tablename.contains(cfe.enums.CfeTables.PRIORITIZATION.getLabel())) {
					// PRIORITIZATION DATABASE TABLE
					Prioritization entity = new Prioritization();
					Set<String> fieldNames = entity.getDataFieldNames();
					List<Prioritization> prioritizations = this.parseTable(db, tablename, fieldNames, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					PrioritizationDao prioritizationDao = new PrioritizationDao(session, tx);

					prioritizationDao.deleteAll(cfe.enums.CfeTables.PRIORITIZATION.getTblName());
					prioritizationDao.saveAll(prioritizations);
				}
				else if (tablename.contains(cfe.enums.CfeTables.VALIDATION.getLabel())) {
					// VALIDATION DATABASE TABLE
					Validation entity = new Validation();
					Set<String> fieldNames = entity.getDataFieldNames();
					List<Validation> validations = this.parseTable(db, tablename, fieldNames, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					ValidationDao validationDao = new ValidationDao(session, tx);

					validationDao.deleteAll(cfe.enums.CfeTables.VALIDATION.getTblName());
					validationDao.saveAll(validations);
				}
				else if (tablename.contains(cfe.enums.CfeTables.TESTING.getLabel())) {
					// TESTING DATABASE TABLE
					Testing entity = new Testing();
					Set<String> fieldNames = entity.getDataFieldNames();
					List<Testing> testings = this.parseTable(db, tablename, fieldNames, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					TestingDao testingDao = new TestingDao(session, tx);

					testingDao.deleteAll(cfe.enums.CfeTables.TESTING.getTblName());
					testingDao.saveAll(testings);
				} 
				else {
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

		
		log.info("Done processing ms uploaded file " + filename);
		log.info(Util.getFreeMemory());
	}

	
	public <T> List<T> parseTable(Database db, String tableName, Set<String> fieldNames, String className) throws Exception {
		List<T> entities = new ArrayList<T>();
		Parser<T> parser = new Parser<T>();
		
		validationMsgs.clear();
		
	    parser.parseTable(db, tableName, fieldNames, className, entities);

	    this.parseResult.setTableStatus(tableName, TableParseResult.Status.PROCESSED);
		validationMsgs.addAll(parser.getValidationMessages());
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
