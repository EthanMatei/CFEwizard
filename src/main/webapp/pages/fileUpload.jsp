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

<h1>Database Upload<br> 
</h1>
<p>
Please select the database(s) to upload:
</p>
 <!-- http://stackoverflow.com/questions/5633949/jquery-solution-for-dynamically-uploading-multiple-files -->
 <!-- http://stackoverflow.com/questions/8906910/struts2-dynamically-add-remove-list-of-objects-from-page -->
<s:actionerror />
<s:form id ="uploadForm" name="uploadForm" action="FileUpload" method="post" enctype="multipart/form-data">
	<s:iterator value="#session.dbnames" var="dbname">
    	<s:file name="upload" label="%{dbname}" />
	</s:iterator>
	<s:submit value="Upload" id="uploadButton" onclick="submitForm();" />
<s:token />

<%-- Store that selected database name values on the page --%>
<s:iterator value="dbnames" status="stat">
    <s:hidden name="dbnames[%{#stat.index}]" />
</s:iterator>

</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
