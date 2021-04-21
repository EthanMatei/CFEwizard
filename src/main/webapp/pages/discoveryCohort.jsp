<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Discovery Cohort</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    function submitForm() {
    	document.getElementById("uploadButton").disabled = true;
    	document.body.style.cursor='wait';
    	document.uploadForm.submit();
    }
</script>

<h1>Discovery Cohort</h1>

<s:actionerror />

Phene Table: <s:property value="pheneTable" /> <br />
Phene: <s:property value="pheneSelection" /> <br />
Low Cutoff: <s:property value="lowCutoff" /> <br />
High Cutoff: <s:property value="highCutoff" />

<s:a action="CsvDisplay" title="Download Excel spreadsheet">
    <s:param name="csvFilePath" value="cohortCsvFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="Report" /> <br />
    Cohort Data
    </div>
</s:a>

</tiles:putAttribute>
</tiles:insertTemplate>
