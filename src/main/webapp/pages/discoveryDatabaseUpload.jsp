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

<h1>Discovery<br> 
</h1>
<p>
Please select the files to upload:
</p>

<s:actionerror />

<s:form id ="discoveryUploadForm" theme="simple" name="discoveryUploadForm"
        action="DiscoveryDatabaseUpload"
        method="post" enctype="multipart/form-data">

    Discovery database: <s:file name="discoveryDb" label="Discovery Database" />
    <s:hidden name="discoveryDbTempFileName" /> 
    
    <br />
    <!-- <s:submit value="Upload" id="uploadButton" onclick="submitForm();" /> -->
    <s:submit value="Upload" id="uploadButton" />
    <s:token />

</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
