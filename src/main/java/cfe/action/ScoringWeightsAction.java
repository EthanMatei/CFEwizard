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
	
	private double discoveryWeight;
	private double prioritizationWeight;
	private double validationWeight;
	private double testingWeight;

	private List<cfe.enums.CfeScoringWeights> weights;
	
	
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
		
		log.info("Discovery: " + this.discoveryWeight);
		log.info("Prioritization: " + this.prioritizationWeight);
		log.info("Validation: " + this.validationWeight);
		log.info("Testing: " + this.testingWeight);

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
				List<cfe.enums.CfeScoringWeights> weights = new ArrayList<cfe.enums.CfeScoringWeights>();
				cfe.enums.CfeScoringWeights weight;

				weight = cfe.enums.CfeScoringWeights.DISCOVERY;
				weight.setWeight( this.discoveryWeight );
				weights.add(weight);

				weight = cfe.enums.CfeScoringWeights.PRIORITIZATION;
				weight.setWeight( this.prioritizationWeight);
				weights.add(weight);

				weight = cfe.enums.CfeScoringWeights.VALIDATION;
				weight.setWeight( this.validationWeight);
				weights.add(weight);

				weight = cfe.enums.CfeScoringWeights.TESTING;
				weight.setWeight( this.testingWeight );
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
		
		boolean res = (this.discoveryWeight < 0.0)
				    || (this.prioritizationWeight < 0.0)
				    || (this.validationWeight < 0.0)
				    || (this.testingWeight < 0.0)  
		;

		if (res) 
			addActionError( "ERROR: Scoring weights cannot be negative." );
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.userSession = session;
	}

	public double getDiscoveryWeight() {
		return discoveryWeight;
	}

	public void setDiscoveryWeight(double discoveryWeight) {
		this.discoveryWeight = discoveryWeight;
	}

	public double getPrioritizationWeight() {
		return prioritizationWeight;
	}

	public void setPrioritizationWeight(double prioritizationWeight) {
		this.prioritizationWeight = prioritizationWeight;
	}

	public double getValidationWeight() {
		return validationWeight;
	}

	public void setValidationWeight(double validationWeight) {
		this.validationWeight = validationWeight;
	}

	public double getTestingWeight() {
		return testingWeight;
	}

	public void setTestingWeight(double testingWeight) {
		this.testingWeight = testingWeight;
	}

	public static Log getLog() {
		return log;
	}
	
}
