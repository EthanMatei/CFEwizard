<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Database Status</h2>

<div style="float: right;">
  <s:form id ="clearForm" name="clearForm" action="DatabaseClear" method="post">
    <s:submit value="Clear All" id="clearAllButton"/>
  </s:form>
</div>

<div style="clear: both;"></div>
 
<%-- Only allow admins see and make database uploads --%>
<s:if test="#session.username==adminUser">

  <p>
  <span style="font-weight: bold;">Database Host:</span> <s:property value="dbHost"/>
  </p>

  <p>
  <span style="font-weight: bold;">Database User:</span> <s:property value="dbUser"/>
  </p>

  <h3>Database Table Map</h3>
  
  <table class="dataTable" style="margin-bottom: 14px;">
  <tr> <th>MS Access Table</th> <th>CFE MySQL Table</th> <th>CFE MySQL Table Rows</th> </tr>
  <s:iterator value="tableMap">
    <tr>
      <td><s:property value="key"/></td>
      <td><s:property value="value.tableName"/></td>
      <td style="text-align: right"><s:number name="value.count" groupingUsed="true"/></td>
    </tr>
  </s:iterator>
  </table>
  
  <p>
  Note: MS Access tables not shown in the database table map
  above, and the linkage tables shown above, are ignored by the CFE Wizard.
  </p>
  
</s:if>
<s:else>
    <p>ADMIN ACCESS ONLY</p>
</s:else>

</tiles:putAttribute>
</tiles:insertTemplate>
