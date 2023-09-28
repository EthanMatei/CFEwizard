<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>Prioritization - Disease Selection</title>
    <s:head />


<script>

<s:include value="/js/diseases.js"/>

</script>

</tiles:putAttribute>
<tiles:putAttribute name="content">

<s:include value="/pages/prioritization/prioritizationSteps.jsp"/>

<div style="width:100%">
    <%--
    <div style="float:left">
        <h1>Prioritization - Disease Selection</h1>
    </div>
    --%>
    
    <div style="float:right">
        <s:a action="PrioritizationReport">
            <s:param name="reportName" value="'diseases'" />
            <s:param name="reportFormat" value="'xlsx'" />
            <div style="text-align:center">
                <img border="0"
                     style="margin-top: 2px;"
                     src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
                     alt="SCORES" />
            </div>
            Diseases
        </s:a>
    </div>
</div>

<div style="clear:both">
</div>


<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        ERROR: <s:property value="errorMessage" />
        <p>
        <s:if test="exceptionStack != ''">
            <s:property value="exceptionStack" />
        </s:if>
        </p>
    </div>
</s:if>

<s:actionerror />

<p>
Import a diseases CSV file with columns "Domain", "SubDomain", "Relevant Disorder", "Coefficient". <b>OR</b>,
manually select diseases and enter coefficients.
</p>

<hr/>

<%-- Diseases import form --%>
<h3>Diseases - CSV File Import</h3>
<div style="border: 1px solid #222222; padding: 4px; margin-bottom: 32px;">

<s:form id ="importDiseases" theme="simple" action="PrioritizationDiseasesImport" method="post" enctype="multipart/form-data">
    <s:hidden name="discoveryId"/>
    <s:hidden name="discoveryScoreCutoff"/>
    <s:hidden name="geneListFileName"/>
    <s:file name="diseasesImport" label="Disease Selection CSV File" />
    <s:submit value="Import" style="font-weight: bold;"/>
    <s:token />
</s:form>
</div>

<hr/>

<h3>Diseases - Manual Entry</h3>

<s:form action="PrioritizationDiseaseSelectionProcess" theme="simple" cssStyle="margin-bottom: 10px;">
    <s:hidden name="score" />
    <s:hidden name="otherCompleted" />
    <s:hidden name="discoveryId"/>
    <s:hidden name="discoveryScoreCutoff"/>
    <s:hidden name="geneListFileName"/>

    <s:include value="/pages/diseases_include.jsp"/>
<!-- 
<s:set var="previousDomain" value="" />
<s:set var="previousSubDomain" value="" />

<div class="scrollable">

<div>
<s:iterator value="diseaseSelectors" var="dSelectors" status="status">
	

    <%-- If this is a new psychiatric domain (or the first one), start a new table --%>
    <s:if test="#previousDomain!=psychiatricDomain">
        <table border="0">
            <thead>
                <tr><th>Psychiatric Domain</th><th>SubDomain</th><th>Relevant Disorder</th><th style="margin-left: 1em">Coefficient</th></tr>
            </thead>
            <tbody>    
    </s:if>                      

    <tr>
    
        <%-- Pyschiatric Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricDomain" />
        <s:if test="#previousDomain==psychiatricDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'DOMAIN:' + psychiatricDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricDomain"/>
            </td>
        </s:else>

        <%-- Psychiatric Sub Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricSubDomain" />
        <s:if test="#previousSubDomain==psychiatricSubDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'SUBDOMAIN:' + psychiatricDomain + '|' + psychiatricSubDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricSubDomain"/>
            </td>
        </s:else>
        
        <%-- Relevant Disorder --%>
        <td>
            <s:checkbox name="diseaseSelectors[%{#status.index}].relevantDisorderSelected"
                        id="%{'DISORDER:' + psychiatricDomain + '|' + psychiatricSubDomain + '|' + relevantDisorder}"
                        onclick="selectBox(this);"/>
            <s:property value="relevantDisorder"/>
            <s:hidden name="diseaseSelectors[%{#status.index}].relevantDisorder" />
        </td>
        
        <%-- Coefficient --%>
        <td>
            <s:set var="coefficient" value="diseaseSelectors[%{#status.index}].coefficient"/>
            <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                name="diseaseSelectors[%{#status.index}].coefficient"
                value="%{getText('{0,number,##0.0}',{coefficient})}"
                theme="simple"
            />
        </td>
    </tr>


    <%-- If this is the last of all rows, or the next row has a different domain name,
         end the current table.
     --%>
    <s:if test="#status.index==diseaseSelectors.size-1||!psychiatricDomain.equals(diseaseSelectors[#status.index+1].psychiatricDomain)">
       </tbody>
       </table>
       <hr />
    </s:if>
    
    <s:set var="previousDomain" value="psychiatricDomain" />
    <s:set var="previousSubDomain" value="psychiatricSubDomain" />
</s:iterator>
</div>
</div>
-->

<s:submit value="Next" cssStyle="margin-top: 4px;"/>
  
<s:token />

</s:form>


<s:iterator value="disorders">
<s:property value="domain" />
<s:property value="subdomain" />
<s:property value="relevantDisorder" />
<br />
</s:iterator>


</tiles:putAttribute>
</tiles:insertTemplate>