package cfg.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;




import cfg.model.GeneList;
import cfg.model.GeneListInput;
import cfg.utils.Authorization;

/**
 * Action class for uploading a custom gene list.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class GeneListUpload extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 6507655467803486283L;
	private static final Log log = LogFactory.getLog(GeneListUpload.class);
	private Map<String, Object> session;
	
	private File file;
    @SuppressWarnings("unused")
	private String contentType;
    @SuppressWarnings("unused")
	private String filename;
    
    
    public void setUpload(File file) {
       this.file = file;
    }

    public void setUploadContentType(String contentType) {
       this.contentType = contentType;
    }

    public void setUploadFileName(String filename) {
       this.filename = filename;
    }

    public String initialize() {
        String result = SUCCESS;
        return result;
    }
    
    public String execute() throws IOException  {
		String result = SUCCESS;

		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {
			GeneListInput geneListInput = new GeneListInput();

			//GeneListService.reset();

			if (file != null)	{
				//ArrayList<GeneList> genes = processFile(file.getPath());
				//GeneListService.save(genes);

				try {
					geneListInput = processFile2(file.getPath());
				}
				catch (Exception exception) {
					log.error("************ GENE LIST UPLOAD ERROR: " + exception.getMessage());
	    			this.setErrorMessage( exception.getMessage() );
					result = ERROR;
				}

			}

			session.put("geneListInput",  geneListInput);
		}
		
    	log.info("gene list update process result: " + result);
    	return result;
    }
    
    
    /**
     * An attacker might try to embed commands in the file 
     * http://owasp-esapi-java.googlecode.com/svn/trunk_doc/latest/index.html
     * @throws IOException 
     */
	private ArrayList<GeneList> processFile (String filename) throws IOException   {
    	ArrayList<GeneList> genes = new ArrayList<GeneList>(50);
    	
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	String line, _gene;

    	while ((line = br.readLine()) != null) {
    		 _gene = line.trim();
    		 
    		 if (cfg.secure.Security.passesGeneWhiteList(_gene))	{
    			 GeneList gene = new GeneList();
    			 gene.setGenecardSymbol(_gene);
    			 genes.add(gene);
    		 } else {
    			 log.warn("Invalid gene " + _gene + " in file " + filename);
    		 }
    	}
    	
    	br.close();
    	return genes;    	
    }
	
	private GeneListInput processFile2(String filename) throws Exception {
		GeneListInput geneList = new GeneListInput();
    	
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	String line, geneCardSymbol;

    	while ((line = br.readLine()) != null) {
    		 geneCardSymbol = line.trim();
    		 
    		 if (geneCardSymbol.equals("")) {
    			 ; // skip blank lines
    		 }
    		 else if (cfg.secure.Security.passesGeneWhiteList(geneCardSymbol))	{
    			 geneList.add(geneCardSymbol);
    		 } 
    		 else {
    			 String message = "Invalid gene name \"" + geneCardSymbol + "\" in gene list.";
    			 log.warn( message  + " File: " + filename);
    			 br.close();
    			 throw new Exception( message );
    		 }
    	}
    	
    	br.close();		
		return geneList;
	}
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
