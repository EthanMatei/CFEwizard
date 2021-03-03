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
Script Output: <s:property value="scriptOutput" /> <br/>


<br/>
<br/>
Base directory: <s:property value="baseDir"/> <br/>
</tiles:putAttribute>
</tiles:insertTemplate>
