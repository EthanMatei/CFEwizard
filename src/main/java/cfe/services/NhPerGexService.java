package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.NhPerGexDao;
import cfe.model.GeneListInput;
import cfe.model.ModelInterface;
import cfe.model.NhPerGex;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhPerGexService {

	public static List<NhPerGex> getAll() throws ServiceException {

		List<NhPerGex> nhPerGenes = new ArrayList<NhPerGex>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhPerGexDao nhPerGeneDao = new NhPerGexDao(session, tx);

		try	{
			nhPerGenes = nhPerGeneDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhPerGenes;
	}
	
	

	public static List<NhPerGex> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhPerGex> allNhPerGenes = getAll();
		List<NhPerGex> selectedNhPerGenes = new ArrayList<NhPerGex>();

		for (NhPerGex nhPerGene: allNhPerGenes) {
			if (diseaseSelection.isSelected(nhPerGene.getPsychiatricDomain(), nhPerGene.getSubDomain(), nhPerGene.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhPerGene.getGenecardSymbol() ) ) {
						selectedNhPerGenes.add( nhPerGene );
					}
				}
				else {
					selectedNhPerGenes.add( nhPerGene );
				}
			}
			
		}
		return selectedNhPerGenes;
	}

	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhPerGex> selectedNhPerGenes = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhPerGex nhPerGene: selectedNhPerGenes) {
			ModelInterface modelInterface = nhPerGene;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}