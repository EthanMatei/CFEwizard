<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFG Wizard - Gene List Upload</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h1>Gene List Upload<br></h1>
<p>
(Optional) Upload the Gene List File:
</p>
<p>
If there is no file, just click on Next.
</p>
 <!-- http://stackoverflow.com/questions/5633949/jquery-solution-for-dynamically-uploading-multiple-files -->
 <!-- http://stackoverflow.com/questions/8906910/struts2-dynamically-add-remove-list-of-objects-from-page -->
<s:actionerror />
<s:form id ="uploadGeneForm" action="GeneListUploadProcess" method="post" enctype="multipart/form-data">
    	<s:file name="upload" label="Gene List File" />
	<s:submit value="Next"/>
<s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
