<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>


<h2>Cohort Creation</h2>
<ul>
    <li><s:a action="DiscoveryDbUpload">Discovery Cohort Creation</s:a></li>
    <li> <s:a action="ClinicalAndTestingCohortsDiscoveryCohortSelection">Validation and Testing Cohorts Creation</s:a> </li>
    <%-- --%>
</ul>

<ul>
    <li>
        <s:a action="ValidationCohortDiscoveryCohortSelection">Validation Cohort Creation</s:a> (WORK IN PROGRESS)
    </li>
    <li>
        <s:a action="TestingCohortsValidationCohortSelection">Testing Cohorts Creation</s:a> (WORK IN PROGRESS)
    </li>
    <%-- --%>
</ul>

<h2>Discovery</h2>
<ul>
    <li><s:a action="DiscoveryCohortSelection">Discovery Scoring</s:a></li>
</ul>

<h2>Prioritization</h2>

<ul>
    <li>
        <s:a action="PrioritizationReport">
            <s:param name="reportName" value="'diseases'" />
            <s:param name="reportFormat" value="'xlsx'" />
            Diseases Report
        </s:a>
    </li>
    
    
    <%-- Only allow admins see and make database uploads --%>
    <s:if test="#session.username==adminUser">
        <%-- <li> <s:a action="DatabaseList">Database Upload Info</s:a> </li> --%>
        <li> <s:a action="PrioritizationDBSelectionInitialize">Upload Databases </s:a></li>
    </s:if>
    
    <li> <s:a action="DatabaseStatusAction">CFE Prioritization Database Status</s:a></li>
    
    <li><s:a action="PrioritizationGeneListUpload">Calculate Scores</s:a></li>
</ul>

<h2>Validation</h2>

<ul>
    <li> <s:a action="ValidationDataSelection">Validation Scoring</s:a></li>
</ul>

<h2>Testing</h2>

<ul>
    <li> <s:a action="TestingDataSelection">Testing Scoring</s:a></li>
</ul>


<h2>Results</h2>
<ul>
    <li><s:a action="CfeResults">Saved Results</s:a></li>
</ul>

<s:if test="#session.username==adminUser">
<hr />
<h3>Admin</h3>
    <ul>
        <%-- <li> <s:a action="DatabaseList">Database Upload Info</s:a> </li> --%>
        <li> <s:a action="SystemStatusAction">System Status</s:a></li>
    </ul>
</s:if>

<%--
<hr/>
<h3>Prototype Process</h3>
<ul>  
 --%>   
 <%-- Only allow admins see and make database uploads --%>
<%--
    <s:if test="#session.username==adminUser">

        <li> <s:a action="DBSelectionInitialize">Upload Databases </s:a></li>
    </s:if>
    
    <li><s:a action="ScoringWeights">Calculate Scores</s:a></li>
    
</ul>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>
