<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFG Wizard - Database Selection</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


<!-- http://stackoverflow.com/questions/4148499/how-to-style-checkbox-using-css -->
<h1>Database(s) Input Selection</h1>
<s:actionerror />
<p>Note: Support for HuGen Linkage is currently disabled.</p>
Please select the database(s):<br />


<s:form action="DBSelection" theme="simple">

  <s:iterator value="@cfg.enums.Databases@values()" var="dbvalue" status="stat" >
    <s:checkbox name="dbnames" fieldValue="%{dbvalue}"/> <s:property value="label"/> <br />
  </s:iterator>

<!-- 
<s:checkboxlist
	label="Please select the database(s)"
	list="@cfg.enums.Databases@values()"
	listValue="label"
	name="dbnames" 
/> 
-->

  <s:submit value="Next" />
  <s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>