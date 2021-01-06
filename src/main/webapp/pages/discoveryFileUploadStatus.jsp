<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Discovery File Upload Status</h2>

Discovery CSV File: <s:property value="discoveryCsv" /> <br/>
Discovery Database File: <s:property value="discoveryDb" /> <br/>
<br/>
Script Dir: <s:property value="scriptDir" /> <br/>
Script File: <s:property value="scriptFile" /> <br/>
Script Output: <s:property value="scriptOutput" /> <br/>

<br/>
<br/>
Base directory: <s:property value="baseDir"/> <br/>
</tiles:putAttribute>
</tiles:insertTemplate>
