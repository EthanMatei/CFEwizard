package cfe.action.prioritization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import cfe.action.BaseAction;
import cfe.utils.Authorization;

/**
 * Struts2 action class for setting global scoring weights.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class ScoringWeights extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 4461152837155935053L;
	private static final Logger log = Logger.getLogger(ScoringWeights.class.getName());
	
	private Map<String, Object> userSession;
	
	private Long discoveryId;
	private Double discoveryScoreCutoff;
	private String geneListFileName;
	
	private double huBrainScore;
	private double huPerScore;
	private double huGeneCnvScore;
	private double huGeneAssocScore;
	private double huGeneLinkageScore;
	
	private double nhBrainScore;
	private double nhPerScore;
	private double nhGeneCnvScore;
	private double nhGeneAssocScore;
	private double nhGeneLinkageScore;

	public ScoringWeights() {
        this.setCurrentTab("Other Functions");    
        this.setCurrentSubTab("Scoring");
        this.setCurrentStep(2);
	}
	
	public double getHuBrainScore() {		
		return huBrainScore;
	}
	
	public void setHuBrainScore(double huBrainScore) {
		this.huBrainScore = huBrainScore;
	}
	
	public String initialize() {
		String result = SUCCESS;
		if (!Authorization.isLoggedIn(userSession)) {
			result = LOGIN;
		}
		log.info("result: " + result);
		log.info("**************** DISCOVERY ID: " + this.discoveryId);
		return result;
	}
	
	public String execute() throws Exception {
		
		log.info("Entered values: ");
		
		log.info("Human Brain Score: " + huBrainScore);
		log.info("Human Peripheral Score: " + huPerScore);
		log.info("Human Gene Association Score: " + huGeneAssocScore);
		log.info("Human Gene CNV: " + huGeneCnvScore);
		log.info("Human Gene Linkage: " + huGeneLinkageScore);
		
		
		log.info("NonHuman Brain Score: " + nhBrainScore);
		log.info("NonHuman Peripheral Score: " + nhPerScore);
		log.info("NonHuman Gene Association Score: " + nhGeneAssocScore);
		log.info("NonHuman Gene CNV: " + nhGeneCnvScore);
		log.info("NonHuman Gene Linkage: " + nhGeneLinkageScore);

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
				List<cfe.enums.prioritization.ScoringWeights> weights = new ArrayList<cfe.enums.prioritization.ScoringWeights>();
				cfe.enums.prioritization.ScoringWeights weight;

				weight = cfe.enums.prioritization.ScoringWeights.HUBRAIN;
				weight.setScore( huBrainScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.HUPER;
				weight.setScore( huPerScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.HUGENEASSOC;
				weight.setScore( huGeneAssocScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.HUGCNV;
				weight.setScore( huGeneCnvScore );
				weights.add(weight);


				weight = cfe.enums.prioritization.ScoringWeights.NHBRAIN;
				weight.setScore( nhBrainScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.NHPER;
				weight.setScore( nhPerScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.NHGENEASSOC;
				weight.setScore( nhGeneAssocScore );
				weights.add(weight);

				weight = cfe.enums.prioritization.ScoringWeights.NHGCNV;
				weight.setScore( nhGeneCnvScore );
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

	
    public void withSession(Map<String, Object> session) {
        this.userSession = session;
    }
    
	
	public double getHuGeneAssocScore() {
		return huGeneAssocScore;
	}
	
	public void setHuGeneAssocScore(double huGeneAssocScore) {
		this.huGeneAssocScore = huGeneAssocScore;
	}

	public void validate() {
		
		boolean res = (huBrainScore < 0.0) || (huPerScore < 0.0) || (huGeneCnvScore < 0.0) || (huGeneAssocScore < 0.0)  
				|| (nhBrainScore < 0.0) || (nhPerScore < 0.0) || (nhGeneCnvScore < 0.0) || (nhGeneAssocScore < 0.0);

		if (res) 
			addActionError( "ERROR: Scores cannot be negative." );
	}
	

	public double getNhBrainScore() {
		return nhBrainScore;
	}
	public void setNhBrainScore(double nhBrainScore) {
		this.nhBrainScore = nhBrainScore;
	}
	public double getNhPerScore() {
		return nhPerScore;
	}
	public void setNhPerScore(double nhPerScore) {
		this.nhPerScore = nhPerScore;
	}
	public double getHuPerScore() {
		return huPerScore;
	}
	public void setHuPerScore(double huPerScore) {
		this.huPerScore = huPerScore;
	}
	public double getHuGeneCnvScore() {
		return huGeneCnvScore;
	}
	public void setHuGeneCnvScore(double huGeneCnvScore) {
		this.huGeneCnvScore = huGeneCnvScore;
	}
	public double getNhGeneCnvScore() {
		return nhGeneCnvScore;
	}
	public void setNhGeneCnvScore(double nhCnvScore) {
		this.nhGeneCnvScore = nhCnvScore;
	}
	public double getNhGeneAssocScore() {
		return nhGeneAssocScore;
	}
	public void setNhGeneAssocScore(double nhGeneAssocScore) {
		this.nhGeneAssocScore = nhGeneAssocScore;
	}

	public double getHuGeneLinkageScore() {
		return huGeneLinkageScore;
	}
	public void setHuGeneLinkageScore(double huGeneLinkageScore) {
		this.huGeneLinkageScore = huGeneLinkageScore;
	}
	public double getNhGeneLinkageScore() {
		return nhGeneLinkageScore;
	}
	public void setNhGeneLinkageScore(double nhGeneLinkageScore) {
		this.nhGeneLinkageScore = nhGeneLinkageScore;
	}
	
	public Long getDiscoveryId() {
        return discoveryId;
    }
	
    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }
    
    public Double getDiscoveryScoreCutoff() {
        return discoveryScoreCutoff;
    }
    
    public void setDiscoveryScoreCutoff(Double discoveryScoreCutoff) {
        this.discoveryScoreCutoff = discoveryScoreCutoff;
    }
    
    
    
    public String getGeneListFileName() {
        return geneListFileName;
    }
    public void setGeneListFileName(String geneListFileName) {
        this.geneListFileName = geneListFileName;
    }

}
