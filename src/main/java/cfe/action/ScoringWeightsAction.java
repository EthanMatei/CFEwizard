package cfe.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.utils.Authorization;
import cfe.utils.HibernateUtils;

/**
 * Struts2 action class for setting global scoring weights.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class ScoringWeightsAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 4461152837155935053L;
	private static final Log log = LogFactory.getLog(ScoringWeightsAction.class);
	
	private Map<String, Object> userSession;
	
	private double discoveryScore;
	private double prioritizationScore;
	private double validationScore;
	private double testingScore;

	private List<cfe.enums.ScoringWeights> weights;
	
	
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
		
		log.info("Discovery: " + this.discoveryScore);
		log.info("Prioritization: " + this.prioritizationScore);
		log.info("Validation: " + this.validationScore);
		log.info("Testing: " + this.testingScore);

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
				weight.setScore( this.discoveryScore );
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.PRIORITIZATION;
				weight.setScore( this.prioritizationScore);
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.VALIDATION;
				weight.setScore( this.validationScore);
				weights.add(weight);

				weight = cfe.enums.ScoringWeights.TESTING;
				weight.setScore( this.testingScore );
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
		
		boolean res = (this.discoveryScore < 0.0)
				    || (this.prioritizationScore < 0.0)
				    || (this.validationScore < 0.0)
				    || (this.testingScore < 0.0)  
		;

		if (res) 
			addActionError( "ERROR: Scores cannot be negative." );
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.userSession = session;
	}

	public double getDiscoveryScore() {
		return discoveryScore;
	}

	public void setDiscoveryScore(double discoveryScore) {
		this.discoveryScore = discoveryScore;
	}

	public double getPrioritizationScore() {
		return prioritizationScore;
	}

	public void setPrioritizationScore(double prioritizationScore) {
		this.prioritizationScore = prioritizationScore;
	}

	public double getValidationScore() {
		return validationScore;
	}

	public void setValidationScore(double validationScore) {
		this.validationScore = validationScore;
	}

	public double getTestingScore() {
		return testingScore;
	}

	public void setTestingScore(double testingScore) {
		this.testingScore = testingScore;
	}

	public static Log getLog() {
		return log;
	}
	
}
