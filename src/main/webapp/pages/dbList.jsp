<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database List</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Database Uploads</h2>

<table class="dataTable" cellpadding="0" cellspacing="0">
  <thead>
    <tr>
      <th> Database </th>
      <th> File Name </th>
      <th> Upload Time </th>
    </tr>
  </thead>
  <tbody>
    <s:iterator value="databaseUploadInfos" >
      <tr>
        <td> <s:property value="databaseName" />               </td>
        <td> <s:property value="uploadFileName" />             </td>
        <td> <s:date name="uploadTime" format="dd/MM/yyyy HH:mm:ss" />  </td>
      </tr>
    </s:iterator>
  </tbody>
</table>


</tiles:putAttribute>
</tiles:insertTemplate>
