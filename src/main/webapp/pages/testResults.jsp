<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Test</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Test Results</h2>

<s:form action="TestProcess" theme="simple" method="post">
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

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>