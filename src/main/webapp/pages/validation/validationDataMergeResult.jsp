<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation - Validation Input Data Merge Result</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Validation - Validation Input Data Merge</h2>


<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>


<p>
Data merge results:
</p>

<s:set var="mergeFileName" value="'discovery-prioritization-merge.xlsx'"/>

<table class="dataTable">
    <tr>
        <th>ID</th> <th>File</th>
    </tr>
    <tr>
        <td> <s:property value="cfeResultsId"/> </td>
        <td>
            <s:a action="CfeResultsXlsxDisplay" title="Discovery Prioritization Merge">
                <s:param name="cfeResultsId" value="cfeResultsId" />
                <s:param name="fileName" value="mergeFileName" />
                <s:property value="mergeFileName"/>
            </s:a>
        </td>
    </tr>
</table>

<s:if test="true">
    <br/>
    <s:a action="ValidationCohortSpecification" title="Validation Cohort Creation" class="linkButton" style="margin-left: 2em;">
        <s:param name="discoveryId" value="cfeResultsId" />
        Validation Cohort Creation
    </s:a>
    <br/>
</s:if>                          
</tiles:putAttribute>
</tiles:insertTemplate>
