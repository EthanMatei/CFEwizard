<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Prioritization Database Upload Results</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<s:include value="/pages/prioritization/dbUploadSteps.jsp"/>

<h2>Database Upload Results</h2>
<s:property value="display" />

<s:if test="%{#session.uploaded}">
    <p>The following databases were uploaded successfully:</p>
    <ul>
	    <s:iterator value="#session.uploaded" var="uploaded" >
		    <li><s:property value="#uploaded" /></li>
	    </s:iterator>
    </ul>
</s:if>

<p>
Upload Time: <s:property value="uploadTime" />
</p>

<s:if test="errorMessage != ''">
    <div style="color: #CC0000; border: 1px solid #CC0000; padding: 7px;">
        ERROR: <s:property value="errorMessage" />
    </div>
    <p style="margin-top: 7px; margin-bottom: 14px;">
        <span style="font-weight: bold;">Note:</span> if a row number is provided
        in the error message above, it may be referring to the row of the batch
        of data being uploaded, and not the row in the database table being uploaded.
    </p>
</s:if>

<h4>Database Processing Details</h4>
<s:iterator value="parseResults" status="pstat">
    
    <div style="border: 1px solid black; padding: 7px; margin-bottom: 20px; width 100%;">
        <div style="font-weight: bold; margin-bottom: 4px;"> <s:property value="fileName" /> </div>

        <table class ="dataTable">
            <tr>
                <th>Table</th> <th>Status</th> <th> Issues </th>
            </tr>
            <s:iterator value="tableParseResults" status="prstat">
                <tr>
                    <td> <s:property value="name"/> </td>
                    <td>
                        <s:if test="status.equals('processed')"> 
                            <span style="color: green;">&#10004;</span>&nbsp;Processed
                        </s:if>
                        <s:if test="status.equals('ignored')">
                            <span style="color: red;">&#8856;</span>&nbsp;Ignored
                        </s:if>
                        <s:if test="status.equals('error')">
                            <span style="color: #CC0000; font-weight: bold;">!</span>&nbsp;<span style="color: #CC0000;">Error</span>
                        </s:if>
                    </td>
                    <td>
                        <ul style="margin-left: 16px; margin-top: 2px; margin-bottom: 2px; padding: 0;">
                        <s:iterator value="issues">
                            <li> <s:property/> </li>
                        </s:iterator>
                        </ul> 
                    </td>
                </tr>
            </s:iterator>
        </table>
    </div>
    
</s:iterator>

    
</tiles:putAttribute>
</tiles:insertTemplate>