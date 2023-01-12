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

<h1>Testing Database Check</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
            <s:if test="exceptionStack != null && exceptionStack != ''">
            <br/>
            <pre>
                <s:property value="exceptionStack" />
            </pre>
        </s:if>
</s:if>
        
<s:actionerror />

<s:form id ="testingDbUploadForm" theme="simple" name="testingDbUploadForm"
        action="TestingDbUploadAndCheck"
        method="post" enctype="multipart/form-data">

    Testing Database: <s:file name="testingDb" label="Testing Database" />
    
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
