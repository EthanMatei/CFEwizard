<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Results Detail</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>CFE Results Detail</h2>

<table class="dataTable">
    <tr>
        <th>Property</th> <th>Value</th>
    </tr>
    
    <tr>
        <td>ID</td> <td style="text-align: right;"><s:property value="cfeResultsId"/></td>
    </tr>
    
    <tr>
        <td>Discovery Phene</td> <td><s:property value="cfeResults.phene"/></td>
    </tr>
    
    <tr>
        <td>Discovery Phene Low Cutoff</td> <td style="text-align: right;"><s:property value="cfeResults.lowCutoff"/></td>
    </tr>
     
    <tr>
        <td>Discovery Phene High Cutoff</td> <td style="text-align: right;"><s:property value="cfeResults.highCutoff"/></td>
    </tr>
</table>

<table class="dataTable">       
    <s:iterator value="cfeResultsFiles" var="file">
        <tr>
            <td>
                <s:property value="fileType"/>
            </td>
            <%--
                <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    results.xlsx
                 </s:a>
            --%>
        </tr>
    </s:iterator>
</table>

<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
