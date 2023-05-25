<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>


<div style="width: 50%;">

<p>
The Convergent Functional Evidence (CFE) Wizard is a pipeline for discovering, prioritizing, validating,
and testing blood biomarkers for mental health and related disorders. 
Based on the approach developed by Dr. Alexander B. Niculescu and colleagues.
Requires databases of phenotypic data, expression data, and clinical outcomes. 
</p>

<p>
Niculescu AB, Le-Niculescu H. Precision medicine in psychiatry: biomarkers to the forefront.
<i>Neuropsychopharmacology</i>. 2022  Jan 47(1):422-423. doi: 10.1038/s41386-021-01183-3. PMID: 34584209
</p>

</div>

</tiles:putAttribute>
</tiles:insertTemplate>
