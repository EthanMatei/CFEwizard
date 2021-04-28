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

Phene Table: <s:property value="pheneTable" /> <br />
Phene: <s:property value="pheneSelection" /> <br />
Low Cutoff: <s:property value="lowCutoff" /> <br />
High Cutoff: <s:property value="highCutoff" /> <br />
Microarray Table: <s:property value="microarrayTable" />

<s:a action="XlsxDisplay" title="Cohort Data Spreadsheet">
    <s:param name="spreadsheetFilePath" value="cohortDataXlsxFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Report" /> <br />
    Cohort Data
    </div>
</s:a>

<s:a action="CsvDisplay" title="Cohort Spreadsheet">
    <s:param name="csvFilePath" value="cohortCsvFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Report" /> <br />
    Cohort
    </div>
</s:a>
<%--
<s:property value="cohortCsv" />
--%>

<hr/>

<h2>Discovery Processing</h2>

<s:form theme="simple" action="DiscoveryCalculate" method="post" enctype="multipart/form-data">
 
    <s:hidden name="lowCuttof" />
    <s:hidden name="highCutoff" />
    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryCsvTempFileName" />
    <s:hidden name="discoveryDbTempFileName" />
    <s:hidden name="pheneSelection" />
    <s:hidden name="pheneTable" />
    
    <div>    
    Gene Expression CSV File: <s:file name="discoveryCsv"/>
    </div>
        
    <h3>Diagnosis</h3>

    <table class="dataTable" style="margin-top: 1em;">
        <tr>
            <th> Diagnosis&nbsp;Code </th>
            <th> Examples </th>
        </tr>
        <s:iterator value="diagnosisCodes">
            <tr>
                <td> <s:radio name="diagnosisCode" list="{key}" /> </td>
                <td> <s:property value="value"/> </td>
            </tr>
        </s:iterator>
    </table>

    <div style="margin-top: 1em; font-weight: bold;">
        <s:submit value="Process" id="processDiscoveryButton" />
    </div>

    
    <s:token />
</s:form>

<p>&nbsp;</p>



</tiles:putAttribute>
</tiles:insertTemplate>
