<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Test Results</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<%--
<s:include value="/pages/mainTabs.jsp"/>
--%>

<s:form action="TestProcessAction" theme="simple" method="post">

<%--
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
--%>

<p>
<input type="button" value="insert row" id="insertRow" onclick="insertTableRow()"/>
</p>


<p>
<input type="button" value="insert row 2" id="insertRow2" onclick="insertTableRow2('scoreTable');"/>
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
    
    function insertTableRow() {
        var scoreTable = document.getElementById('scoreTable');
        var tr = scoreTable.insertRow(1);
        tr.innerHTML = scoreTable.rows[2].innerHTML;  // copy first data row;

        
        for (var i = 2; i < scoreTable.rows.length; i++) {
            var row = scoreTable.rows[i];
            row.innerHTML = row.innerHTML
                .replaceAll('[' + (i-2) + ']', '[' + (i-1) + ']')
                .replaceAll('_' + (i-2), '_' + (i-1));
        }
        //alert(scoreTable.innerHTML);
    }
    
        
    function insertTableRow2(tableId) {
        var table = document.getElementById(tableId);
        alert(table.innerHTML);
        /*
        var scoreTable = document.getElementById('scoreTable');
        var tr = scoreTable.insertRow(1);
        tr.innerHTML = scoreTable.rows[2].innerHTML;  // copy first data row;

        
        for (var i = 2; i < scoreTable.rows.length; i++) {
            var row = scoreTable.rows[i];
            row.innerHTML = row.innerHTML
                .replaceAll('[' + (i-2) + ']', '[' + (i-1) + ']')
                .replaceAll('_' + (i-2), '_' + (i-1));
        }
        */
        //alert(scoreTable.innerHTML);
    }
    
    // ' name="data[%{' + scoreTable.rows.length + '}]"
</script>

<%--
<table class="dataTable" id="scoreTable">
    <tr>
        <th>Percentile Range</th> <th>Score</th>
    </tr>
    
    <s:iterator value="perScores" var="percentileScore" status="status">
        <tr>
            <td>
                <span style="text-align: right;margin-left: 1em; width: 7em; display: inline-block;">
                    <s:property value="lowerBound"/>
                </span>
                &le; x &lt;
                <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                    name="upperBound"
                />
            </td>
            <td>
                 <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                    name="score"
                />               
            </td>
        </tr>
    </s:iterator>
</table>
--%>
<p>&nbsp;</p>

<table class="dataTable" id="scoreTable">
    <thead>
    <tr>
        <th>Percentile Range</th> <th>Score</th>
    </tr>
    </thead>
    
    <tbody>
    <s:iterator value="percentileScores.lowerBounds" var="lowerBound" status="status">
        <tr>
            <td>
                <span id="lower<s:property value='#status.index'/>" style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                    <s:property value="lowerBound" />
                </span>
                
                &le; x &lt;
                
                <s:set value="%{'upper' + #status.index}" var="upperId" />
                
                <s:if test="#status.last">
                    <s:textfield size="8" cssStyle="text-align: right;margin-left: 1em" readonly="true"
                        name="percentileScores.upperBounds[%{#status.index}]"
                        id="%{upperId}"
                    />
                </s:if>
                <s:else>
                    <s:textfield size="8" cssStyle="text-align: right;margin-left: 1em"
                        name="percentileScores.upperBounds[%{#status.index}]"
                        id="%{upperId}"
                    />
                </s:else>
            </td>
            <td>
                 <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                    name="percentileScores.scores[%{#status.index}]"
                 />
            </td>
        </tr>
    </s:iterator>
    </tbody>
</table>
<p>

<s:submit value="Process"/>
</p>

</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>