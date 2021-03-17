<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Discovery File Upload Status</h2>

<table class="dataTable">
    <tr>
        <th> Cohort </th>
        <td> <s:property value="cohort"/> </td>
    </tr>
    <tr>
        <th> Diagnosis Code </th>
        <td> <s:property value="diagnosisCode"/> </td>
    </tr>
    <tr>
        <th>Database File</th>
        <td><s:property value="discoveryDbFileName" /></td>
    </tr>
    <tr>
        <th>CSV File</th>
        <td><s:property value="discoveryCsvFileName" /></td>
</table>

<br/>
DB temp file name: <s:property value="discoveryDbTempFileName" />
<br/>
CSV temp file name: <s:property value="discoveryCsvTempFileName" />

<hr />

<br/>
Script Dir: <s:property value="scriptDir" /> <br/>
Script File: <s:property value="scriptFile" /> <br/>
Script Output: <s:property value="scriptOutput" escapeHtml="false"/> <br/>


<br/>
<br/>
Base directory: <s:property value="baseDir"/> <br/>

<hr/>

<p>
Output file: <s:property value="outputFile"/>
</p>

<p>
Report file: <s:property value="reportFile"/>
</p>

<s:a action="CsvDisplay" title="Download Excel spreadsheet">
    <s:param name="csvFilePath" value="outputFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Output" /> <br />
    <s:property value="outputFileName"/>
    </div>
</s:a>

<br/>

<s:a action="CsvDisplay" title="Download Excel spreadsheet">
    <s:param name="csvFilePath" value="reportFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Report" /> <br />
    <s:property value="reportFileName"/>
    </div>
</s:a>
</tiles:putAttribute>
</tiles:insertTemplate>
