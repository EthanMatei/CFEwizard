<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Instructions</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<p>
The CFE Wizard supports calculation of CFE (Convergent Functional Evidence) Scores. Calculation of the CFE Scores consists
of the following steps:
</p>

<ol>
    <li><span style="font-weight: bold">Discovery</span>
        <ol>
            <li>Discovery Cohort Creation</li>
            <li>Discovery Scoring</li>
        </ol>
    </li>
    <li><span style="font-weight: bold">Prioritization</span></li>
    <li><span style="font-weight: bold">Validation</span>
        <ol>
            <li>Validation Cohort Creation</li>
            <li>Validation Scoring</li>
        </ol>
    </li>
    <li><span style="font-weight: bold">Testing</span>
        <ol>
            <li>Testing Cohorts Creation</li>
            <li>Testing Scoring</li>
        </ol>
    </li>   
    
</ol>

</tiles:putAttribute>
</tiles:insertTemplate>
