<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Instructions</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>
<h2>CFE Wizard Instructions</h2>

<div style="width: 50%">

<h3>CFE Wizard Processing Steps</h3>
<p>
The CFE Wizard supports calculation of CFE (Convergent Functional Evidence) Scores. Calculation of the CFE Scores consists
of the following steps:
</p>

<ol>
    <li><span style="font-weight: bold">Discovery</span>
        <ol>
            <li>Discovery Cohort Creation</li>
            <li>Discovery Scoring</li>
        </ol>
    </li>
    <li><span style="font-weight: bold">Prioritization</span></li>
    <li><span style="font-weight: bold">Validation</span>
        <ol>
            <li>Validation Cohort Creation</li>
            <li>Validation Scoring</li>
        </ol>
    </li>
    <li><span style="font-weight: bold">Testing</span>
        <ol>
            <li>Testing Cohorts Creation</li>
            <li>Testing Scoring</li>
        </ol>
    </li>   
    
</ol>

<hr/>

<h3>CFE Pipeline</h3>

<p>
The CFE Pipeline allows you to calculate the above steps in sequential order. The CFE Pipeline saves the results
of each step, and these results can be accessed by clicking on <b>Saved Results</b>.
</p>

<p>
By default, the CFE Pipeline runs all steps, however, you can start the process at a middle step by specifying
previously saved results or a manually created spreadsheet. If a manually created spreadsheet is used, it
needs to follow the format used by the CFE Wizard. In addition to changing the start point, you
can also specify a different end point (as long as it is after the specified start point).
Ending the CFE Pipeline calculations at a middle step can be useful if you need to modify the results
that are generated before you proceed.
</p>

<hr/>

<h3>Prioritization</h3>

<p>
In addition to being able to change the starting and ending steps of the CFE Pipeline, it is also possible to run
the Prioritization step on its own. To run this, click on <b>Other Functions -> (Prioritization) Scoring</b>.
If you specify a manually uploaded gene list or all genes in the first step, prioritization scores will
be calculated that do not have associated discovery scores. If you want to use these 
prioritization scores in the CFE Pipeline,
you will need to merge them with previously calculated discovery scores by clicking on
<b>Other Functions -> (Prioritization) Merge with Discovery Scores</b>
</p>

<p>
The Prioritization phase uses a literature database, and the data for this need to be uploaded to the CFE Wizard.
MS Access literature databases are uploaded and combined into a single CFE Wizard MySQL literature database.
To upload MS Access literature databases, click on <b>Other Functions -> (Prioritization) Upload Literature Databases</b>.
</p>

<p>
To download a spreadsheet of the diseases in the CFE Wizard literature database, click on
<b>Other Functions -> (Prioritization) Diseases Report</b>
</p>

<p>
To see information on the CFE Wizard literature database tables, click on 
<b>Other Functions -> (Prioritization) CFE Database Status</b>.
This function also provides a button (<b>Clear All</b>) that will delete
all data currently stored in the CFE literature database.
</p>

<hr/>

<h3>Admin Pages</h3>

There are a few admin (administrator) pages that can be accessed by clicking on <b>Admin</b>:
<ul>
    <li> <b>System Status.</b> The system status page provides various system property values
    that can be helpful for problem solving. This page is primarily intended for developers.
    </li>
    
    <li> <b>Temporary Files.</b> The temporary files page can be used to see and delete
    temporary files that are created by the CFE Wizard. These files could eventually fill
    up the disk space on the computer where the CFE Wizard is running.
    </li>
    
    <li> <b>Test Page.</b> The test page is used by developers for testing.
    </li>
</ul>

</div>

</tiles:putAttribute>
</tiles:insertTemplate>
