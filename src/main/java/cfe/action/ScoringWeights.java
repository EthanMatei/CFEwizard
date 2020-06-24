package cfe.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.ScoringDataDao;
import cfe.utils.Authorization;
import cfe.utils.HibernateUtils;

/**
 * Struts2 action class for setting global scoring weights.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class ScoringWeights extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 4461152837155935053L;
	private static final Log log = LogFactory.getLog(ScoringWeights.class);
	
	private Map<String, Object> userSession;
	
	private double discovery;
	private double prioritization;
	private double validation;
	private double testing;

	
	public String initialize() {
		String result = SUCCESS;
		if (!Authorization.isLoggedIn(userSession)) {
			result = LOGIN;
		}
		log.info("result: " + result);
		return result;
	}
	
	public String execute() throws Exception {
		
		log.info("Entered values: ");
		
		log.info("Discovery: " + this.discovery);
		log.info("Prioritization: " + this.prioritization);
		log.info("Validation: " + this.validation);
		log.info("Testing: " + this.testing);

		//Session session = HibernateUtils.getSession();		
		//Transaction tx = session.beginTransaction();
		//ScoringDataDao scDao = new ScoringDataDao(session, tx);

		String rtn = ERROR;

		if (!Authorization.isLoggedIn(userSession)) {
			rtn = LOGIN;
		}
		else {
			try	{
				//------------------------------------------------------------------
				// Store the selected scoring weights in the session
				//------------------------------------------------------------------
				List<cfe.enums.ScoringWeights> weights = new ArrayList<cfe.enums.ScoringWeights>();
				cfe.enums.ScoringWeights weight;

				weight = cfe.enums.ScoringWeights.DISCOVERY;
				weight.setScore( this.discovery );
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.PRIORITIZATION;
				weight.setScore( this.prioritization );
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.VALIDATION;
				weight.setScore( this.validation );
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.TESTING;
				weight.setScore( this.testing );
				weights.add(weight);

				userSession.put("weights", weights);

				rtn = SUCCESS;
			} 
			catch (Exception e)	{
				//tx.rollback();			
			}
			finally	{
				//session.close();
			}
		}
		
		return rtn;
	}


	public void validate() {
		
		boolean res = (this.discovery < 0.0)
				    || (this.prioritization < 0.0)
				    || (this.validation < 0.0)
				    || (this.testing < 0.0)  
		;

		if (res) 
			addActionError( "ERROR: Scores cannot be negative." );
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.userSession = session;
	}

	public double getDiscovery() {
		return discovery;
	}

	public void setDiscovery(double discovery) {
		this.discovery = discovery;
	}

	public double getPrioritization() {
		return prioritization;
	}

	public void setPrioritization(double prioritization) {
		this.prioritization = prioritization;
	}

	public double getValidation() {
		return validation;
	}

	public void setValidation(double validation) {
		this.validation = validation;
	}

	public double getTesting() {
		return testing;
	}

	public void setTesting(double testing) {
		this.testing = testing;
	}

	public static Log getLog() {
		return log;
	}
	
}
