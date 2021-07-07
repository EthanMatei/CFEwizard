<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Discovery Results</h2>


<table class="dataTable">
    <tr> <th>Results</th> <th>Time Generated</th> <th>Phene</th> <th>Phene Low Cutoff</th> <th>Phene High Cutoff</th> </tr>
    <s:iterator value="discoveryResults" var="result">
        <tr>
            <td>
                <s:a action="DiscoveryResultsXlsxDisplay" title="Discovery Results">
                    <s:param name="discoveryResultsId" value="discoveryResultsId" />
                    discovery-results.xlsx
                 </s:a>
            </td>
            <td> <s:date name="generatedTime" format="MM/dd/yyyy hh:mm"/> </td>
            <td> <s:property value="phene"/> </td>
            <td style="text-align: right;"> <s:property value="lowCutoff"/> </td>
            <td style="text-align: right;"> <s:property value="highCutoff"/> </td>
        </tr>
    </s:iterator>
</table>

<br/>

<%--
<s:iterator value="fileNames" var="file">
    <s:a action="XlsxDisplay" title="Discovery Results">
        <s:param name="spreadsheetFilePath" value="file" />
        <s:property value="file"/>
    </s:a>
    <br />
</s:iterator>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>
