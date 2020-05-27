package cfg.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfg.action.CalculateScores;
import cfg.dao.DaoException;
import cfg.dao.NhBrainGexDao;
import cfg.model.GeneListInput;
import cfg.model.ModelInterface;
import cfg.model.NhBrainGex;
import cfg.model.disease.DiseaseSelection;
import cfg.utils.HibernateUtils;

/**
 * Non-human brain gene expression service class.
 */
public class NhBrainGexService {
	
	private static final Log log = LogFactory.getLog(NhBrainGexService.class);
	
	public static List<NhBrainGex> getAll() throws ServiceException {

		List<NhBrainGex> nhBrainGenes = new ArrayList<NhBrainGex>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhBrainGexDao nhBrainGeneDao = new NhBrainGexDao(session, tx);

		try	{
			nhBrainGenes = nhBrainGeneDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException(exception.getMessage());
		} 
		finally	{
			session.close();
		}

		return nhBrainGenes;
	}

	public static List<NhBrainGex> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhBrainGex> allNhBrainGenes = getAll();
		List<NhBrainGex> selectedNhBrainGenes = new ArrayList<NhBrainGex>();

		for (NhBrainGex nhBrainGene: allNhBrainGenes) {
			log.info("NHBRAINGENE: " + nhBrainGene.getGenecardSymbol() + " " + nhBrainGene.getPsychiatricDomain() 
					+ " " + nhBrainGene.getSubDomain() + " " + nhBrainGene.getRelevantDisorder());
			if (diseaseSelection.isSelected(nhBrainGene.getPsychiatricDomain(), nhBrainGene.getSubDomain(), nhBrainGene.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhBrainGene.getGenecardSymbol() ) ) {
						selectedNhBrainGenes.add( nhBrainGene );
					}
				}
				else {
					selectedNhBrainGenes.add( nhBrainGene );
				}
			}
			
		}
		return selectedNhBrainGenes;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhBrainGex> selectedNhBrainGenes = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhBrainGex nhBrainGene: selectedNhBrainGenes) {
			ModelInterface modelInterface = nhBrainGene;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}