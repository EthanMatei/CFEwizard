<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFG Wizard - Database Upload Success</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<H1>Success!</H1>
<s:property value="display" />

<s:if test="%{#session.uploaded}">
    <p>The following databases were uploaded successfully:</p>
    <ul>
	    <s:iterator value="#session.uploaded" var="uploaded" >
		    <li><s:property value="#uploaded" /></li>
	    </s:iterator>
    </ul>
    

</s:if>


<h4>Parse Results</h4>
<s:iterator value="parseResults" status="pstat">
    
    <div style="border: 1px solid black; padding: 4px; width 100%; font-weight: bold; background-color: #CAE1F9; margin: 10px 0px;">
        <s:property value="fileName" />
    </div>
    
    <table class ="dataTable">
        <tr>
            <th>Table</th> <th>Status</th>
        </tr>
        <s:iterator value="tablesProcessed" status="prstat">
            <tr> <td> <s:property/> </td> <td> <span style="color: green;">&#10004;</span> Processed </td> </tr>
        </s:iterator>
        <s:iterator value="TablesIgnored" status="istat">
            <tr> <td> <s:property/> </td> <td> <span style="color: red;">&#8856;</span> Ignored </td> </tr>
        </s:iterator>
    </table>

</s:iterator>


<s:if test="%{#session.vMsgs}">
<p>The following possible issues were detected during the upload:</p>
<ul>
	<s:iterator value="#session.vMsgs" var="_vMsgs" >
		<li><s:property value="#_vMsgs" /></li>
	</s:iterator>
</ul>
</s:if>

<p>
Upload Time: <s:property value="uploadTime" />
</p>
    
</tiles:putAttribute>
</tiles:insertTemplate>