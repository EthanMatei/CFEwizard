package cfe.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.interceptor.SessionAware;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFile;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Struts2 action for displaying CFE results spreadsheet in .xlsx format
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsFileDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CfeResultsFileDisplayAction.class.getName());
	

    private Map<String, Object> session;

    // Inputs:
    private Long cfeResultsId;
    private String fileType;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
	
    public CfeResultsFileDisplayAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
    }
    
    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
    	this.session = session;
    }    
	
    /**
     * For viewing reports.
     * 
     * @return
     */
    public String view() throws Exception {
        String result = SUCCESS;
        
        log.info("****** FILE TYPE: " + this.fileType);

        if (!Authorization.isLoggedIn(session)) {
            result = LOGIN;
        }
        else {
            //try {
            if (cfeResultsId == null) {
                throw new ActionErrorException("No cfeResultsId argument was specified.");
            }
            
            log.info("cfeResultsId: " + cfeResultsId);
            CfeResults cfeResults = CfeResultsService.get(cfeResultsId);
            
            CfeResultsFile file = cfeResults.getFile(fileType);

            //try {
                
                fileStream = new ByteArrayInputStream( file.getContent() );
                if (fileStream == null) {
                    throw new Exception("The file with results with ID \""
                        + cfeResultsId + "\" and file type \"" + fileType + "\" could not be retrieved.");
                }
            //}
            //catch (Exception exception) {
            //  throw new ActionErrorException( exception.getMessage() );
            //}

            String fileSuffix = file.getFileSuffix();
            fileName = file.getFileType() + fileSuffix;
            fileContentType = file.getMimeType();
            
            log.info("Trying to display file \"" + fileName + "\" with content type \"" + fileContentType + "\".");
        }

        return result;
    }



	//-----------------------------------------
	// Getters and Setters
	//-----------------------------------------
	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

}
