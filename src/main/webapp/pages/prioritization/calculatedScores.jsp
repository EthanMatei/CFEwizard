<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>Prioritization - Calculated Scores</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


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

<s:url var="download" namespace="/pages" action="downloadScores" ></s:url>

<div style="float:right">
<table style="border:0">
<tr>
<td style="padding-left:1em">
<s:a action="PrioritizationReport" title="Download Excel spreadsheet">
    <s:param name="reportName" value="'scores'" />
    <s:param name="reportFormat" value="'xlsx'" />
    <s:param name="discoveryId" value="discoveryId" />
    <s:param name="discoveryScoreCutoff" value="discoveryScoreCutoff" />
    <s:param name="geneListFileName" value="geneListFileName" />
    <div style="text-align:center">
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="SCORES" /> <br />
    cfg-scores.xlsx
    </div>
</s:a>
</td>
</tr>
</table>
</div>

<div style="clear:both;"></div>



<%--
<s:form action="Report">
    <s:hidden name="reportName" value="scores"/>
    <s:hidden name="reportFormat" value="xlsx"/>
    <s:submit value="Excel"/>
</s:form>
--%>

            
<h1 style="padding-top:0px; margin-top:4px;">Prioritization Scores </h1>
<s:actionerror />

<p>
Discovery ID: <s:property value="discoveryId"/>
</p>
<p>
Discovery Score Cutoff: <s:property values="discoveryScoreCutoff"/>
</p>
<p>
Gene List File Name: <s:property value="geneListFileName"/>
</p>


<%--
<table border="1">
<thead>
<tr><th /><th>Gene</th><th>Score</th><th>Tissue</th><th>Direction Of Change</th><th>PubMed</tr>
</thead>
<tbody>

<s:iterator value="scores" var="_score" status="status">
	<tr>
	<td><s:property value="%{#status.index}" /></td>
	<td><s:property value="%{key}"/></td>
	<td><s:property value="value.score"/></td>
	<td><s:property value="value.tissue"/></td>
	<td><s:property value="value.directionChange"/></td>
	<td>
	<s:iterator value="value.pubMedUrl" var="_pubMedUrl">
		<s:a href="http://www.ncbi.nlm.nih.gov/pubmed/%{_pubMedUrl}" target="_blank"><s:property value="%{_pubMedUrl}"/></s:a><br>
	</s:iterator>
	</td>
	</tr>
</s:iterator>

</tbody>	
</table>


<hr />
<br />
New Results: <br />
--%>

<table class="dataTable">
<tr>
    <th> Gene </th>
    <th> Score </th>
    <th> Direction of Change </th>
    <th> Tissue </th>
    <th> Disorder </th>
    <th> PubMed </th>
</tr>
<s:iterator value="results.results" var="var" status="rstatus">
    <tr>
        <td>

            <%-- <s:a href=""> --%>
                <s:iterator value="%{results.geneNames[key]}" var="name" status="nstatus">

                    <s:if test="#nstatus.last"> 
                        <s:property value="name" />
                    </s:if>
                    <s:else>
                        <s:property value="name" />,
                    </s:else>
 
                </s:iterator>
            <%-- </s:a> --%>

        </td>
        <td style="text-align:right"> <s:property value="value.score" /> </td>
        <td>
            <s:iterator value="value.allResearch">
                <s:property value="directionChange" />
                <br />
            </s:iterator>
        </td>
        <td>
            <s:iterator value="value.allResearch">
                <s:property value="tissue" />
                <br />
            </s:iterator>
        </td>
        <td>
            <s:iterator value="value.allResearch">
                <s:property value="psychiatricDomain" />
                <s:property value="subdomain" />
                <s:property value="relevantDisorder" />
                <br />
            </s:iterator>
        </td>                    
        <td>
            <s:iterator value="value.allResearch">
                <s:if test="pubMedId > 0">
                    <s:a href="http://www.ncbi.nlm.nih.gov/pubmed/%{pubMedId}" target="_blank"><s:property value="%{pubMedId}"/></s:a>
                </s:if>
                <s:else>
                    <s:property value="pubMedId" />
                </s:else>
                <br />
            </s:iterator>
        </td>             
    </tr>
</s:iterator>
</table>

<br />
<br />


<%--
<hr />

<table border="1">
<tr>
    <th> Gene </th>
    <th> Score </th>
    <s:iterator value="results.categoryHeaders" var="var">
        <th colspan="2"> <s:property value="var"/> </th>
    </s:iterator>
    
</tr>
<s:iterator value="results.results" var="var" status="rstatus">
    <tr>
        <td> <s:property value="key" /> </td>
        <td> <s:property value="value.score" /> </td>
        <s:iterator value="value.categoryResults">
            <td>
                Score: <s:property value="value.score" /> 
            </td>
            <td>
                <s:iterator value="value.researchList">
                    Database: <s:property value="category" />
                    <br />
                    Table: <s:property value="subcategory" />
                    <br />
                    <s:property value="psychiatricDomain" />&nbsp;<s:property value="subdomain" />&nbsp;<s:property value="relevantDisorder" />
                    <br />
                    <s:property value="tissue" />
                    <s:property value="directionChange" />
                    <br />
                    <s:property value="pubMedId" />
                    <br />
                    <br />
                </s:iterator>
                &nbsp;
            </td>
        </s:iterator>
    </tr>
</s:iterator>
</table>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>