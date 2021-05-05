package cfe.utils;


import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class for storing the the merged data tables used to construct the Discovery cohort.
 * 
 * @author Jim Mullen
 *
 */
public class CohortTable extends DataTable {
	
	private static final Log log = LogFactory.getLog(CohortTable.class);

	private TreeSet<String> subjects;
	private int lowVisits;   // number of visits with low phene value for subjects in cohort
	private int highVisits;  // number of visits with high phene value for subjects in cohort
	
	public CohortTable() {
		super("PheneVisit");
		subjects = new TreeSet<String>();
	}
	
	public int getNumberOfSubjects() {
		return this.subjects.size();
	}

	public TreeSet<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(TreeSet<String> subjects) {
		this.subjects = subjects;
	}

	public int getLowVisits() {
		return lowVisits;
	}

	public void setLowVisits(int lowVisits) {
		this.lowVisits = lowVisits;
	}

	public int getHighVisits() {
		return highVisits;
	}

	public void setHighVisits(int highVisits) {
		this.highVisits = highVisits;
	}
	
}
