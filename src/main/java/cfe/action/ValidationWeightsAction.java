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
 * Struts2 action class for setting validation scoring weights.
 * 
 * @author Jim Mullen
 *
 */
public class ValidationWeightsAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 4461152837155935053L;
	private static final Log log = LogFactory.getLog(ValidationWeightsAction.class);
	
	private Map<String, Object> userSession;

	private double nonStepwiseWeight;
    private double stepwiseWeight;
    private double nominalWeight;
    private double bonferroniWeight;

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
		
		log.info("Non-Stepwise: " + this.nonStepwiseWeight);
		log.info("Stepwise: " + this.stepwiseWeight);
		log.info("Nominal: " + this.nominalWeight);
		log.info("Bonferroni: " + this.bonferroniWeight);

		String rtn = ERROR;

		if (!Authorization.isLoggedIn(userSession)) {
			rtn = LOGIN;
		}
		else {
			try	{
				//------------------------------------------------------------------
				// Store the selected scoring weights in the session
				//------------------------------------------------------------------
				List<cfe.enums.CfeValidationWeights> weights = new ArrayList<cfe.enums.CfeValidationWeights>();
				cfe.enums.CfeValidationWeights weight;

				weight = cfe.enums.CfeValidationWeights.NON_STEPWISE;
				weight.setWeight( this.nonStepwiseWeight );
				weights.add(weight);

				weight = cfe.enums.CfeValidationWeights.STEPWISE;
				weight.setWeight( this.stepwiseWeight );
				weights.add(weight);

				weight = cfe.enums.CfeValidationWeights.NOMINAL;
				weight.setWeight( this.nominalWeight );
				weights.add(weight);

				weight = cfe.enums.CfeValidationWeights.BONFERRONI;
				weight.setWeight( this.bonferroniWeight );
				weights.add(weight);

				userSession.put("validationWeights", weights);

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
		
		boolean res = (this.nonStepwiseWeight < 0.0)
				    || (this.stepwiseWeight < 0.0)
				    || (this.nominalWeight < 0.0)
				    || (this.bonferroniWeight < 0.0)  
		;

		if (res) 
			addActionError( "ERROR: Scoring weights cannot be negative." );
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.userSession = session;
	}


	public double getNonStepwiseWeight() {
		return nonStepwiseWeight;
	}

	public void setNonStepwiseWeight(double nonStepwiseWeight) {
		this.nonStepwiseWeight = nonStepwiseWeight;
	}

	public double getStepwiseWeight() {
		return stepwiseWeight;
	}

	public void setStepwiseWeight(double stepwiseWeight) {
		this.stepwiseWeight = stepwiseWeight;
	}

	public double getNominalWeight() {
		return nominalWeight;
	}

	public void setNominalWeight(double nominalWeight) {
		this.nominalWeight = nominalWeight;
	}

	public double getBonferroniWeight() {
		return bonferroniWeight;
	}

	public void setBonferroniWeight(double bonferroniWeight) {
		this.bonferroniWeight = bonferroniWeight;
	}

	public static Log getLog() {
		return log;
	}
	
}
