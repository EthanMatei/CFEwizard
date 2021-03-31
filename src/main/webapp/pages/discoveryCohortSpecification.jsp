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
<s:iterator value="phenes" var="table" status="pstat">
        <s:property value="key"/> <br />
        <s:iterator value="value" var="phene">
            &nbsp;&nbsp;<s:property value="phene"/> <br />
        </s:iterator>
</s:iterator>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
