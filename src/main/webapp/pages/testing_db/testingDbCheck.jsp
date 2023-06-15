<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Phenomic Database Check Results</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h2>Phenomic Database Check Results</h2>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>
        
<s:actionerror />

<pre>
<%-- <s:property value="report"/> --%>
</pre>

<s:iterator value="tableCheckInfos">
    <hr/>
    <div>
        <span style="font-weight: bold;">TABLE "<s:property value="name"/>"</span>
        <ul>
            <s:if test="!columns.isEmpty()">
                <li><span style="font-weight: bold;">COLUMNS:</span> <s:property value="columnsString"/></li>
            </s:if>

            <s:iterator value="errors" var="error">
                <li style="color: red;"><span style="color: red;"><i class="fa fa-exclamation-triangle"></i> ERROR: <s:property value="error"/></span></li>
            </s:iterator>
            
            <s:iterator value="warnings" var="error">
                <li style="color: #FF8000;"><span><i class="fa fa-exclamation-triangle"></i> WARNING: <s:property value="error"/></span></li>
            </s:iterator>
        </ul>
    </div>

</s:iterator>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
