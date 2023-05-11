<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<h1>Manually Created Spreadsheet Help</h1>

<p>
For the CFE Wizard to get the discovery phene information from an uploaded spreadsheet,
it needs to contain a sheet named 
<strong>"<s:property value="@cfe.model.CfeResultsSheets@DISCOVERY_COHORT_INFO"/>"</strong>
that contains the following information (example phene name and cutoffs shown):
</p>

<table border="1">
    <tr>
        <th>attribute</th>	<th>value</th>
    </tr>
    
    <tr>
        <td>Phene</td> <td>PANSS.P1 Delusions (1-7) </td>
    </tr> 
    <tr>
        <td>Low Cutoff</td>	<td style="text-align: right;">1</td>
    </tr>
    <tr>
        <td>High Cutoff</td> <td style="text-align: right;">4</td>
    </tr>
</table>

<p>
Note: the phene name needs to contain the phene's database table name ("PANSS" in the example above)
and phene name separated by a "."
</p>
