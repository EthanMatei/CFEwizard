<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Test Results</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>


<s:form action="TestProcessAction" theme="simple" method="post">
<table class="dataTable" id="scoreTable">
    <tr>
        <th>Percentile Range</th> <th>Score</th>
    </tr>
    
    <s:iterator value="data" var="datum" status="status">
        <tr>
            <td>
                <s:textfield size="7" readonly="true" cssStyle="text-align: right;margin-left: 1em"/>
                &le; x &lt;
                <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                    name="data[%{#status.index}]"
                />
            </td>
            <td>
                &nbsp;
            </td>
        </tr>
    </s:iterator>
</table>

<p>
<input type="button" value="add row" id="addRow" onclick="addTableRow()"/>
</p>

<script>
    function addTableRow() {
        var scoreTable = document.getElementById('scoreTable');
        var lastRowIndex = scoreTable.rows.length - 2;  // Subtract 1 for header row and 1 for zero-indexing
        var newRowIndex  = lastRowIndex + 1;
        var lastRow = scoreTable.rows[ lastRowIndex + 1 ];
        var tr = document.createElement('tr');
        //alert("LAST ROW INDEX: " + lastRowIndex);
        //alert("NEW ROW INDEX: " + newRowIndex);
        tr.innerHTML = lastRow.innerHTML
            .replace('[' + lastRowIndex + ']', '[' + newRowIndex + ']')
            .replace('_' + lastRowIndex, '_' + newRowIndex);
        alert(tr.innerHTML); 
        //'<td>'
        //    + '<s:textfield size="7" cssStyle="text-align: right;margin-left: 1em;" name="data[%{' + scoreTable.rows.length + '}]" value="1.0"/>'
        //    + '</td>'
        scoreTable.appendChild(tr);
    }
    // ' name="data[%{' + scoreTable.rows.length + '}]"
</script>

<p>
<s:submit value="Process"/>
</p>

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>