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

<h1>Discovery Database Upload</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>
        
<s:actionerror />

<s:form id ="discoveryUploadForm" theme="simple" name="discoveryUploadForm"
        action="DiscoveryDatabaseUpload"
        method="post" enctype="multipart/form-data">

    Database: <s:file name="discoveryDb" label="Discovery Database" />
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
