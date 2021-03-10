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

<h1>Discovery Parameters<br> 
</h1>

<s:actionerror />

<%-- Database File: <s:property value="discoveryDbFileName" /> <br /> --%>

<s:form id ="discoveryUploadForm" name="discoveryUploadForm" action="DiscoveryFileUpload"
        theme="simple" method="post" enctype="multipart/form-data">

    <s:hidden name="discoveryDb" />
    <s:hidden name="discoveryCsv" />
    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryCsvFileName" />
    
    <s:hidden name="discoveryDbTempFileName" />
    <s:hidden name="discoveryCsvTempFileName" />
        
    <s:hidden name="dbFileName" />
    
    <h2>Cohort</h2>
    <s:radio label="Cohort"
              name="cohort"
              list="cohorts"
              required="true"
    />


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
    <s:submit value="Process" id="processDiscoveryDEButton" />
    </div>
    <s:token />
</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
