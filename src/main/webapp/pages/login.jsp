<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard Login</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<div id="login">
<s:form theme="simple" action="LoginProcess">



<s:if test="errorMessage != ''">
    <p>
    <span class="formError"><s:property value="errorMessage"/></span>
    </p>
</s:if>

<table style="cellpadding: 7px;">
<tr>
<td> username: </td> <td> <s:textfield name="username" autocomplete="off" /> </td>
</tr>
<tr>
<td> password: </td> <td> <s:password name="password" autocomplete="off" /> </td>
</tr>
<tr>
  <td colspan="2" style="text-align:center"><s:submit value="Login"/></td>
</tr>
</table>

<s:token />
</s:form>
</div>

</tiles:putAttribute>
</tiles:insertTemplate>
