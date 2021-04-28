<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Discovery Results</h2>

Diagnosis Code:<s:property value="diagnosisCode"/> <br />
        
<hr/>

<h3>Inputs</h3>
<table class="dataTable">
    <tr>
        <th> Phene </th>
        <td> <s:property value="pheneSelection"/> </td>
    </tr>
    <tr>
        <th> Phene Table </th>
        <td> <s:property value="pheneTable"/> </td>
    </tr>
    <tr>
        <th>Phene Low Cutoff</th>
        <td> <s:property value="lowCutoff" /> </td>
    </tr>
    <tr>
        <th>Phene High Cutoff</th>
        <td> <s:property value="highCutoff" /> </td>
    </tr>
    <tr>
        <th> Diagnosis Code </th>
        <td> <s:property value="diagnosisCode"/> </td>
    </tr>
    <tr>
        <th>Phene Vist Database File</th>
        <td><s:property value="discoveryDbFileName" /></td>
    </tr>
    <tr>
        <th>Gene Expression CSV File</th>
        <td><s:property value="discoveryCsvFileName" /></td>
</table>

<hr style="margin-top: 12px;"/>

<h3>Results</h3>


<p>
Output file: <s:property value="outputFile"/>
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


<p>
Report file: <s:property value="reportFile"/>
</p>

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


<br/>
Script Output: <s:property value="scriptOutput" escapeHtml="false"/> <br/>

<hr style="margin-top: 12px;"/>

<h3>System Information</h3>

<h3>Inputs</h3>
<table class="dataTable">
    <tr>
        <th>Phene Visit DB temp file</th>
        <td> <s:property value="discoveryDbTempFileName" /> </td>
    </tr>
    <tr>
        <th>Gene Expression CSV temp file</th>
        <td> <s:property value="discoveryCsvTempFileName" /> </td>
    </tr>
    <tr>
        <th>CFE Wizard Base directory</th>
        <td> <s:property value="baseDir"/> </td>
    </tr>
    <tr>
        <th>R Script Directory</th>
        <td> <s:property value="scriptDir" /> </td>
    <tr>
        <th>R Script File</th>
        <td> <s:property value="scriptFile" /> </td>
    </tr>
</table>



<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
