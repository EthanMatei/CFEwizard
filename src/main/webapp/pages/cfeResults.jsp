<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Results</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    $( document ).ready(function() {
    	$("#cfeResultsTypesSelect").on("change", function() {
    		$("#cfeResultsForm").submit();
    	});
    	
    	$("#cfeResultsOrderSelect").on("change", function() {
    		$("#cfeResultsForm").submit();
    	});
    
    	$("#cfeResultsPheneSelect").on("change", function() {
    		$("#cfeResultsForm").submit();
    	});
    	
        $('a.deleteCfeResults').on('click', function () {
            return confirm('Delete CFE results?');
        });
    });
</script>

<%-- User: <s:property value="#session.username" /> --%>

<h2>CFE Saved Results</h2>

<s:include value="/pages/error_include.jsp"/>
<%--        
<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
        <s:if test="exceptionStack != null && exceptionStack != ''">
            <p>
                <s:property value="exceptionStack" />
            </p>
        </s:if>
    </div>
</s:if>
--%>

<div style="margin-bottom: 22px; border: 1px solid #000000; border-radius: 7px; padding: 7px; background-color: #F5F5FF;">
    <s:form theme="simple" id="cfeResultsForm" action="CfeResults" method="post">
        <span style="font-weight: bold;">Order:</span>
        <s:select id="cfeResultsOrderSelect" list="resultsOrders" name="resultsOrder" value="resultsOrder">
        </s:select>
        
        <span style="font-weight: bold; margin-left: 1em;">Phene:</span> 
        <s:select id="cfeResultsPheneSelect" list="resultsPhenes" name="resultsPhene" value="resultsPhene">
        </s:select>
                
        <span style="font-weight: bold; margin-left: 1em;">Type:</span> 
        <s:select id="cfeResultsTypesSelect" list="cfeResultsTypes" name="cfeResultsType" value="cfeResultsType">
        </s:select>
    </s:form>
</div>

<table class="dataTable">
    <tr>
        <th>ID</th>
        <th>Results</th> <th>Results Type</th> <th>Details</th>
        <th>Time Generated</th>
        <th>Uploaded</th>
        <%--<th>Uploaded</th>--%>
        <th>Discovery Phene</th> <th>Discovery Phene<br/>Low Cutoff</th> <th>Discovery Phene<br/>High Cutoff</th>
        <s:if test="#session.username==adminUser">
            <th>Delete</th>
        </s:if>
    </tr>
    <s:iterator value="cfeResults" var="result">
        <tr>
            <td> <s:property value="cfeResultsId"/> </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    results.xlsx
                 </s:a>
            </td>
            <td> <s:property value="resultsType"/> </td>
            <td>
                <s:a action="CfeResultsDetailAction" title="CFE Results Detail">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    details
                 </s:a>
            </td>
            <td> <s:date name="generatedTime" format="MM/dd/yyyy HH:mm"/> </td>
            <td> <s:property value="uploaded"/> </td>
            <%--
            <td>
                <s:if test="uploaded == true">
                    yes
                </s:if>
                <s:else>
                    &nbsp;
                </s:else>
            </td>
            --%>
            <td> <s:property value="phene"/> </td>
            <td style="text-align: right;"> <s:property value="lowCutoff"/> </td>
            <td style="text-align: right;"> <s:property value="highCutoff"/> </td>
            <s:if test="#session.username==adminUser">
                <td style="text-align: center;">
                    <s:url var="deleteUrl" action="CfeResultsDelete">
                        <s:param name="cfeResultsId" value="cfeResultsId"/>
                        <s:param name="resultsOrder" value="resultsOrder"/>
                        <s:param name="resultsPhene" value="resultsPhene"/>
                        <s:param name="cfeResultsType" value="cfeResultsType"/>
                    </s:url>
                    <s:a class="deleteCfeResults linkButtonThin" href="%{deleteUrl}">
                        <i class="fa fa-remove" style="padding-left: 1em; padding-right: 1em;"></i>
                    </s:a>
                </td>
            </s:if>
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
