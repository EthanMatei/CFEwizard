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

<s:a action="CsvDisplay" title="Download Excel spreadsheet">
    <s:param name="csvFilePath" value="cohortDataCsvFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Report" /> <br />
    Cohort Data
    </div>
</s:a>

<s:a action="CsvDisplay" title="Download Excel spreadsheet">
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

<s:form theme="simple" action="DiscoveryCalculate">
    Discovery CSV file: <s:file name="discoveryCsv"
    cssStyle="margin-bottom: 12px;"/>
     
    <s:hidden name="diagnosisCode" />
    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryCsvFileName" />
    <s:hidden name="pheneSelection" />
    
    <h2>Diagnosis</h2>
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
