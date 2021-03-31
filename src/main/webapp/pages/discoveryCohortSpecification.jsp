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


<s:form theme="simple">

<p>
Low cutoff (&le;): <s:textfield />
</p>

<p>
High cutoff (&ge;): <s:textfield />
</p>

<br/>

Phenes:
<hr/>
<div id="tree">
<ul>
<s:iterator value="phenes" var="table" status="pstat">
        <li><s:property value="key"/>
            <ul>
            <s:iterator value="value" var="phene">
                <li> <s:property value="phene"/> </li>
            </s:iterator>
            </ul>
        </li>
</s:iterator>
</ul>
</div>


<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
