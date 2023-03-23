<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Temporary Files</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>
<h2>Temporary Files</h2>

<div style="float: right;">
  <s:form id ="deleteForm" name="clearForm" action="TemporaryFileDelete" method="post">
    <p>
    Delete temporary files older than <s:textfield name="deleteAge" value="7"/>
    <s:submit value="Delete" id="deleteButton" style="font-weight: bold;"/>
    </p>
  </s:form>
</div>

<div style="clear: both;"></div>

<p>
Temporary Files Directory: <s:property value="tempDir"/>
</p>

<p>
Temporary File Extensions: <s:property value="@cfe.utils.TemporaryFileInfo@fileExtensionsString"/>
</p>

<table class="dataTable">
    <caption style="margin-bottom: 7px; font-weight: bold;">Temporary Files</caption>
    <tr>
        <th>File Name</th> <th>Size</th> <th>Age in Days</th>
    </tr>
    <s:iterator value="tempFileInfos">
        <tr>
            <td>
                <s:property value="name"/>
            </td>
            <td style="text-align: right;">
                <s:property value="sizeInMegabytesFormatted"/> MB
            </td>
            <td style="text-align: right;">
                <s:property value="ageInDaysFormatted"/>
            </td>            
        </tr>
    </s:iterator>
</table>


</tiles:putAttribute>
</tiles:insertTemplate>