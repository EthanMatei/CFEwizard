<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<p style="font-size: 120%; font-weight: bold;">
<s:a action="BatchInitialization">CFE Pipeline</s:a>
</p>

<fieldset>
<legend>Discovery</legend>

<%--
<div style="display: inline-block; vertical-align: top; margin-top: 0; padding-top: 0;">
    <ol type="A" style="margin-top: 0;">
        <li> <s:a action="DiscoveryDbUpload">Discovery Cohort Creation</s:a> </li>
        <li> <s:a action="DiscoveryCohortSelection">Discovery Scoring</s:a> </li>
    </ol>
</div>
--%>

<div style="display: inline-block; vertical-align: top;">
    <ul style="margin-top: 0;">
        <li> <s:a action="TestingDbUploadInit">Testing Database Check</s:a></li>
    </ul>
</div>
</fieldset>

<fieldset>
<legend>Prioritization</legend>

<div style="display: inline-block; vertical-align: top; margin-top: 0; padding-top: 0;">
    <ul>        
        <li><s:a action="PrioritizationGeneListUpload">Prioritization Scoring</s:a></li>
    </ul>
</div>

<div style="display: inline-block; vertical-align: top;">
    <ul style="margin-top: 0;">
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
    </ul>
</div>
</fieldset>


<fieldset>
<legend>Validation</legend>

<%--
<div style="display: inline-block; vertical-align: top; margin-top: 0; padding-top: 0;">
    <ol type="A" style="margin-top: 0;">
        <li> <s:a action="ValidationCohortDataSelection">Validation Cohort Creation</s:a> </li>
        <li> <s:a action="ValidationDataSelection">Validation Scoring</s:a></li>
    </ol>
</div>
--%>

<div style="display: inline-block; vertical-align: top;">
    <ul style="margin-top: 0;">
        <li> <s:a action="ValidationDataMergeSelection">Merge Priority Scores with Discovery Scores</s:a></li>
    </ul>
</div>

</fieldset>

<%--
<h2>4. Testing</h2>
<ol type="A">
    <li> <s:a action="TestingCohortsDataSelection">Testing Cohorts Creation</s:a> </li>
    <li> <s:a action="TestingDataSelection">Testing Scoring</s:a></li>
</ol>
--%>





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
        <li> <s:a action="TestAction">Test Page</s:a></li>
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
