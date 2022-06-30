<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Discovery Cohort</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    function submitForm() {
    	document.getElementById("uploadButton").disabled = true;
    	document.body.style.cursor='wait';
    	document.uploadForm.submit();
    }
</script>

<h2>Discovery Cohort</h2>

<s:actionerror />

<table class="dataTable">
  <tr> <th>Data</th> <th>Inputs</th> <th>Stats</th> </tr>
  <tr>
    <td>
    
      <%-- OLD APPROACH:
      <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
        <s:param name="cfeResultsId" value="cfeResultsId" />
          <div>
            <img border="0"
                 style="margin-top: 2px;"
                 src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
                 alt="Report" /> <br />
            discovery-cohort.xlsx
          </div>
      </s:a>
      --%>
      
		<s:a action="CfeResultsFilesXlsxDisplay" title="CFE Results">
		  <s:param name="cfeResultsId" value="cfeResultsId" />
		    <div>
		      <img border="0"
		           style="margin-top: 2px;"
		           src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
		           alt="Report" /> <br />
		      discovery-cohort.xlsx
		    </div>
		</s:a>      
    </td>
    
    <td>
      Phene Table: <s:property value="pheneTable" /> <br />
      Phene: <s:property value="pheneSelection" /> <br />
      Low Cutoff: <s:property value="lowCutoff" /> <br />
      High Cutoff: <s:property value="highCutoff" /> <br />
      Genomics Table: <s:property value="genomicsTable" />
    </td>
    
    <td style="vertical-align: top;">
      Number of Cohort Subjects: <s:property value="numberOfSubjects" /> <br />
      Number of Low Visits for Cohort Subjects: <s:property value="lowVisits" /> <br />
      Number of High Visits for Cohort Subjects: <s:property value="highVisits" />
    </td>
  </tr>
</table>


<br/>

<s:a action="ValidationCohortSpecification" title="Validation Cohort Creation" class="linkButton">
    <s:param name="discoveryId" value="cfeResultsId" />
    Validation Cohort Creation
</s:a>

<s:a action="DiscoveryScoringSpecification" title="Discovery Scoring" class="linkButton" style="margin-left: 2em;">
    <s:param name="discoveryId" value="cfeResultsId" />
    Discovery Scoring
</s:a>

<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
