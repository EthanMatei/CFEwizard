<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database Upload</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    function submitForm() {
    	document.getElementById("uploadButton").disabled = true;
    	document.body.style.cursor='wait';
    	document.uploadForm.submit();
    }
</script>

<h1>Discovery DE<br> 
</h1>

<s:actionerror />

Database File: <s:property value="discoveryDbFileName" /> <br />

<s:form id ="discoveryUploadForm" name="discoveryUploadForm" action="DiscoveryFileUpload" method="post" enctype="multipart/form-data">

    <s:hidden name="discoveryDb" />
    <s:hidden name="discoveryCsv" />
    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryCsvFileName" />
    
    <s:hidden name="dbFileName" />
    
    <s:radio label="Cohort"
              name="cohort"
              list="cohorts"
              required="true"
    />

<hr />
    Cohort: <br/>
    <s:iterator value="cohorts" var="cohort" status="cstatus">
        <s:property value="cohort"/> <br />
    </s:iterator>

<table>
<s:iterator value="diagnosisCodes">
    <tr>
        <td> <s:property value="key"/> </td>
        <td> <s:property value="value"/> </td>
    </tr>
</s:iterator>
</table>

    <s:submit value="Process" id="processDiscoveryDEButton" />
    <s:token />
</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
