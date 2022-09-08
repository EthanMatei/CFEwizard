<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Test</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Test Results</h2>

<%--
<table class="dataTable">
    <s:iterator value="data" var="datum" status="status">
        <tr>
            <td>
                <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                    name="data[%{#status.index}]"
                    theme="simple"
                />
            </td>
        </tr>
    </s:iterator>
</table>

<hr/>
--%>

<p>Table:</p>
<table class="dataTable">
    <tr>
        <th>Lower Bound</th>
        <th>Upper Bound</th>
        <th>Score</th>
    </tr>
    <s:iterator value="percentileScores.lowerBounds" var="lowerBound" status="status">
        <s:if test="#status.last == true">
        </s:if>
        <tr>
            <%--
            <td> <s:property value="#status.index"/> </td>
            <td> <s:property value="%{#status.index}"/>
            --%>
            <td>
                <s:property value="lowerBound"/>
            </td>
            <td>
                <s:property value="percentileScores.upperBounds[#status.index]"/>
            </td>
            <td>
                <s:property value="percentileScores.scores[#status.index]"/>
            </td>
        </tr>
    </s:iterator>
</table>

</tiles:putAttribute>
</tiles:insertTemplate>