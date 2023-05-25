<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Temporary Files</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>
<h2 style="float: left;">Temporary Files</h2>

<h2 style="float: right;">
<span class="help" onclick="window.open('pages/help/temporaryFiles.jsp', '_blank', 'left=440, top=170, width=680, height=200');">?</span>
</h2>            

<div style="clear: both;"></div>

<s:form id ="deleteForm" name="clearForm" theme="simple" action="TemporaryFileDelete" method="post">
  Delete temporary files older than <s:textfield name="deleteAge" style="text-align: right;" size="2" value="7"/> days.
  <s:submit value="Delete" id="deleteButton" style="font-weight: bold; margin-left: 1em;"/>
</s:form>


<div style="clear: both;"></div>

<p>
Temporary Files Directory: <s:property value="tempDir"/>
</p>

<p>
Temporary File Extensions: <s:property value="@cfe.utils.TemporaryFileInfo@fileExtensionsString"/>
</p>

<table class="dataTable" style="margin-bottom: 17px;">
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