package cfg.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfg.dao.HuBrainGexDao;
import cfg.model.GeneListInput;
import cfg.model.HuBrainGex;
import cfg.model.ModelInterface;
import cfg.model.Research;
import cfg.model.ResearchData;
import cfg.model.disease.DiseaseSelection;
import cfg.utils.HibernateUtils;


public class HuBrainGexService {
	
	private static final Log log = LogFactory.getLog(HuBrainGexService.class);
	
	public static List<HuBrainGex> getAll() throws ServiceException {

		List<HuBrainGex> huBrainGexs = new ArrayList<HuBrainGex>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuBrainGexDao huBrainGexDao = new HuBrainGexDao(session, tx);

		try	{
			huBrainGexs = huBrainGexDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huBrainGexs;
	}
	
	
	/**
	 * Gets only the data records that have a selected disorder and a gene specified (if no gene list
	 * is specified, then all genes that have a selected disorder are returned).
	 * 
	 * @param diseaseSelection the disease selection criteria
	 * @param geneListInput the gene list to use (if there any)
	 * @return
	 */
	public static List<HuBrainGex> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuBrainGex> allHuBrainGexs = getAll();
		List<HuBrainGex> selectedHuBrainGexs = new ArrayList<HuBrainGex>();
		
		for (HuBrainGex huBrainGex: allHuBrainGexs) {
			
			if (diseaseSelection.isSelected(huBrainGex.getPsychiatricDomain(), huBrainGex.getSubDomain(), huBrainGex.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huBrainGex.getGenecardSymbol() ) ) {
						selectedHuBrainGexs.add( huBrainGex );
					}
				}
				else {
					selectedHuBrainGexs.add( huBrainGex );
				}
			}
			
		}
		return selectedHuBrainGexs;
	}
	
	/**
	 * Gets ModelInterface objects for all records that match the selection criteria.
	 * @param diseaseSelection
	 * @param geneListInput
	 * @return
	 * @throws ServiceException
	 */
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuBrainGex> selectedHuBrainGexs = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuBrainGex huBrainGex: selectedHuBrainGexs) {
			ModelInterface modelInterface = huBrainGex;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}


}