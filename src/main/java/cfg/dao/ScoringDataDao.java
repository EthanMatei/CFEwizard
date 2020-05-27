package cfg.dao;
// http://stackoverflow.com/questions/16098046/how-to-print-double-value-without-scientific-notation-using-java
// import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;




import cfg.enums.Scores;
import cfg.model.ScoreResults;
import cfg.model.ScoringData;
import cfg.model.disease.DiseaseSelector;

public class ScoringDataDao extends AbstractDao<ScoringData> {
	
	private static final Log log = LogFactory.getLog(ScoringDataDao.class);
	
	
	public ScoringDataDao( Session sess, Transaction tx) {
		super(ScoringData.class, sess, tx);
	}

	
	
}
