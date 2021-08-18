<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Discovery Scoring Specification</title>
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

<h2>Discovery Scoring Specification</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<table class="dataTable">
  <tr> <th>Data</th> <th>Inputs</th> <th>Stats</th> </tr>
  <tr>
    <td>
      <s:a action="XlsxDisplay" title="Cohort Spreadsheet">
        <s:param name="spreadsheetFilePath" value="cohortXlsxFile" />
        <div>
          <img border="0"
               style="margin-top: 2px;"
               src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
               alt="Report" /> <br />
          Discovery&nbsp;Cohort
        </div>
      </s:a>
    </td>
    
    <td>
      Phene Table: <s:property value="pheneTable" /> <br />
      Phene: <s:property value="pheneSelection" /> <br />
      Low Cutoff: <s:property value="lowCutoff" /> <br />
      High Cutoff: <s:property value="highCutoff" /> <br />
      Microarray Table: <s:property value="microarrayTable" />
    </td>
    
    <td style="vertical-align: top;">
      Number of Cohort Subjects: <s:property value="numberOfSubjects" /> <br />
      Number of Low Visits for Cohort Subjects: <s:property value="lowVisits" /> <br />
      Number of High Visits for Cohort Subjects: <s:property value="highVisits" />
    </td>
  </tr>
</table>


<%--
<s:property value="cohortCsv" />
--%>

<hr/>

<h2>Discovery Processing</h2>


<s:form theme="simple" action="DiscoveryCalculate" method="post" enctype="multipart/form-data">
 
    <s:hidden name="lowCutoff" />
    <s:hidden name="highCutoff" />
    <s:hidden name="cohortCsvFile" />
    <s:hidden name="cohortDataCsvFile" />
    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryCsvTempFileName" />
    <s:hidden name="discoveryDbTempFileName" />
    <s:hidden name="pheneSelection" />
    <s:hidden name="pheneTable" />
    <s:hidden name="microarrayTable" />
    <s:hidden name="cohortGeneratedTime" />
    <s:hidden name="discoveryId" />
    
    <div id="container">
        <div id="input-files" style="float: left;">
                    
            <p>
            Phene Database: <s:file name="discoveryDb"/>
            </p>
            
            <p>    
            Gene Expression CSV File: <s:file name="discoveryCsv"/>
            </p>
    
            <p>
            Probeset to Gene Mapping Database: <s:file name="probesetMappingDb"/>
            </p>

        </div> <%-- input-file --%>
    
        <div style="margin-left: 4em; font-weight: bold; float: left;">
            <s:submit value="Process" id="processDiscoveryButton" style="font-weight: bold; padding-left: 2em; padding-right: 2em;"/>
        </div>
    
        <div style="clear: both;"></div>
    </div> <%-- container --%>
    

    
    <h3>Diagnosis</h3>

    <table class="dataTable" style="margin-top: 1em;">
        <tr>
            <th> Diagnosis&nbsp;Code </th>
            <th> Examples </th>
        </tr>
        <tr>
            <td> <s:radio name="diagnosisCode" list="{'All'}" checked="true" style="fontweight:bold;"/> </td>
            <td> (All diagnosis codes will be processed) </td>
        </tr>
        <s:iterator value="diagnosisCodes">
            <tr>
                <td> <s:radio name="diagnosisCode" list="{key}" /> </td>
                <td> <s:property value="value"/> </td>
            </tr>
        </s:iterator>
    </table>


    
    <s:token />
</s:form>

<p>&nbsp;</p>



</tiles:putAttribute>
</tiles:insertTemplate>
