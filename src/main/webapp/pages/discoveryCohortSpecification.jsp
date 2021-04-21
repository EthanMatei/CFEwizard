<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database Upload</title>
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

<h1>Discovery Cohort Specification</h1>

<s:actionerror />


<s:form theme="simple" action="DiscoveryCohortSpecification">

<s:hidden name="discoveryDbTempFileName"/>

<p>
Low cutoff (&le;): <s:textfield name="lowCutoff"/>
</p>

<p>
High cutoff (&ge;): <s:textfield name="highCutoff"/>
</p>

<p>
Microarray table <s:select name="microarrayTable" list="microarrayTables"/>
</p>

<br/>



Phenes:
<hr/>
<div id="tree">
<s:iterator value="phenes" var="table" status="pstat">
        <s:property value="key"/> <br/>
        <s:iterator value="value" var="pheneValue">
            <s:radio name="pheneSelection" list="#{#pheneValue.tableAndColumnName:#pheneValue.columnName}" /> <br/>
        </s:iterator>
</s:iterator>
</div>


<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

<hr />

<s:submit value="Process"/>

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
