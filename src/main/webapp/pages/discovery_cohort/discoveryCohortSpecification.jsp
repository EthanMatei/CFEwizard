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

<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<s:form theme="simple" action="DiscoveryCohort">

    <s:hidden name="discoveryDbFileName" />
    <s:hidden name="discoveryDbTempFileName"/>

    <p>
    Low cutoff (&le;): <s:textfield size="6" style="text-align: right;" name="lowCutoff"/>
    <span style="margin-left: 1em;">Comparison threshold:</span>
    <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="discoveryCohortComparisonThreshold"/>
    </p>

    <p>
    High cutoff (&ge;): <s:textfield size="6" style="text-align: right;" name="highCutoff"/>
    </p>

             
    <p>
    Genomics table <s:select name="genomicsTable" list="genomicsTables"/>
    </p>

    <br/>

    <h3>Phenes:</h3>

    <div class="scrollable" style="height: 240px;">
        <div id="tree">
            <s:iterator value="phenes" var="table" status="pstat">
                <span style="font-weight: bold;"><s:property value="key"/></span> <br/>
                <s:iterator value="value" var="pheneValue">
                    <s:radio name="pheneSelection" list="#{#pheneValue.tableAndColumnName:#pheneValue.columnName}" /> <br/>
                </s:iterator>
            </s:iterator>
        </div>
    </div>

    <s:iterator value="validationMsgs" status="vstat">
        <s:property value="validationMsgs[%{#vstat.index}]" />
    </s:iterator>
    
    
    <s:submit style="margin-top: 12px;" value="Process"/>

</s:form>

<p>&nbsp;</p>

</tiles:putAttribute>
</tiles:insertTemplate>
