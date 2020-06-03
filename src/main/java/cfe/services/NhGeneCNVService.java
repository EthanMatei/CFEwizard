package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.NhGeneCNVDao;
import cfe.model.GeneListInput;
import cfe.model.ModelInterface;
import cfe.model.NhGeneCNV;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhGeneCNVService {

	public static List<NhGeneCNV> getAll() throws ServiceException {

		List<NhGeneCNV> nhGeneCNVs = new ArrayList<NhGeneCNV>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhGeneCNVDao nhGeneCNVDao = new NhGeneCNVDao(session, tx);

		try	{
			nhGeneCNVs = nhGeneCNVDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhGeneCNVs;
	}

	

	public static List<NhGeneCNV> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput)  throws ServiceException {
		
		List<NhGeneCNV> allNhGeneCNVs = getAll();
		List<NhGeneCNV> selectedNhGeneCNVs = new ArrayList<NhGeneCNV>();

		for (NhGeneCNV nhGeneCNV: allNhGeneCNVs) {
			if (diseaseSelection.isSelected(nhGeneCNV.getPsychiatricDomain(), nhGeneCNV.getSubDomain(), nhGeneCNV.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhGeneCNV.getGenecardSymbol() ) ) {
						selectedNhGeneCNVs.add( nhGeneCNV );
					}
				}
				else {
					selectedNhGeneCNVs.add( nhGeneCNV );
				}
			}
			
		}
		return selectedNhGeneCNVs;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput)  throws ServiceException {
		List<NhGeneCNV> selectedNhGeneCNVs = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhGeneCNV nhGeneCNV: selectedNhGeneCNVs) {
			ModelInterface modelInterface = nhGeneCNV;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}