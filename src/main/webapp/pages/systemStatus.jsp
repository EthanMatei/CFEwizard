<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>System Status</h2>

<%-- Only allow admins see and make database uploads --%>
<s:if test="#session.username==adminUser">

  <h3>Application Properties</h3>
  <table class="dataTable">
  <tr> <th>Property</th> <th>Value</th> </tr>
  <s:iterator value="applicationProperties">
    <tr>
      <td><s:property value="key"/></td>
      <td><s:property value="value"/></td>
    </tr>
  </s:iterator>
  </table>
  
  <hr />
  
  <h3>Java System Properties</h3>
  <table class="dataTable">
  <tr> <th>Property</th> <th>Value</th> </tr>
  <s:iterator value="systemProperties">
    <tr>
      <td><s:property value="key"/></td>
      <td><s:property value="value"/></td>
    </tr>
  </s:iterator>
  </table>
  
  <hr />
  
  <h3>Environment Variables</h3>
  <table class="dataTable" style="margin-bottom: 14px;">
  <tr> <th>Variable</th> <th>Value</th> </tr>
  <s:iterator value="environmentVariables">
    <tr>
      <td><s:property value="key"/></td>
      <td><s:property value="value"/></td>
    </tr>
  </s:iterator>
  </table>
  
  

</s:if>
<s:else>
    <p>ADMIN ACCESS ONLY</p>
</s:else>

</tiles:putAttribute>
</tiles:insertTemplate>
