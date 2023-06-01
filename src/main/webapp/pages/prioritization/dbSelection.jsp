<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>Prioritization - Database Selection</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<s:include value="/pages/prioritization/dbUploadSteps.jsp"/>

<!-- http://stackoverflow.com/questions/4148499/how-to-style-checkbox-using-css -->
<h2>Database(s) Selection</h2>
<s:actionerror />
<p>Note: Support for HuGen Linkage is currently disabled.</p>
Please select the database(s):<br />


<s:form action="PrioritizationDBSelection" theme="simple">

  <s:iterator value="@cfe.enums.prioritization.Databases@values()" var="dbvalue" status="stat" >
    <s:checkbox name="dbnames" fieldValue="%{dbvalue}"/> <s:property value="label"/> <br />
  </s:iterator>

<!-- 
<s:checkboxlist
	label="Please select the database(s)"
	list="@cfe.enums.prioritization.Databases@values()"
	listValue="label"
	name="dbnames" 
/> 
-->

  <p>
  <s:submit class="submit" value="Next"/>
  </p>
  
  <s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>