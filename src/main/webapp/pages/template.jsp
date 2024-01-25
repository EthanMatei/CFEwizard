<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE HTML>
      
<%-- 
CFE Template
--%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

 <html lang="en">
    <head>
            <s:if test="#session.username != null & #session.username != ''" >
        <link rel="stylesheet" href="js/jquery-ui.min.css">
        <link rel="stylesheet" href="js/jquery-ui.structure.min.css">
        <link rel="stylesheet" href="js/jquery-ui.theme.min.css">
        <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
        <script src="<s:url includeParams='none' value='/js/jquery-ui.min.js'/>"></script> 
          <script>
  $( function() {
    $( "#tabs" ).tabs();
  } );
  </script>
  </s:if>
        <link rel="stylesheet" type="text/css" href="<s:url includeParams="none" value='/fontawesome/css/all.min.css'/>" />
        <link rel="stylesheet" type="text/css" href="<s:url includeParams="none" value='/css/cfe.css'/>" />

        <tiles:insertAttribute name="header" />
    </head>
    <body>
        <%-- Page Header --%>

        <div id="logo">
            <%--
            <h2 style="margin-bottom: 4px; margin-top: 4px;"><i class="fa fa-hat-wizard"></i>CFE Wizard Scoring Program</h2>
            --%>
            <h2 style="margin-bottom: 4px; margin-top: 4px;">CFE Wizard Scoring Program</h2>
            <h4 style="margin-top:2px; margin-bottom:4px">version <s:property value="@cfe.model.VersionNumber@VERSION_NUMBER" /> </h4>
        </div>
        <div style="clear: both;"></div>
        
        
        <s:if test="#session.username != null & #session.username != ''" >

            <%-- =========================================================================== --%>
            <%-- TABS                                                                        --%>
            <%-- =========================================================================== --%>
            <div class="mainTabsContainer">
            <div class="mainTabs" style="float: left;">

                <s:if test="currentTab.equals('Home')">
                    <s:a class="selectedMainTab" action="Home"><i class="fa fa-house"></i> Home</s:a>
                </s:if>
                <s:else>
                    <s:a action="Home"><i class="fa fa-house"></i> Home</s:a>
                </s:else>
                
                &nbsp;|&nbsp;
 
                 <s:if test="currentTab.equals('Instructions')">
                    <s:a class="selectedMainTab" action="InstructionsAction"><i class="fa fa-circle-info"></i> Instructions</s:a>
                </s:if>
                <s:else>
                    <s:a action="InstructionsAction"><i class="fa fa-circle-info"></i> Instructions</s:a>
                </s:else>
                
                &nbsp;|&nbsp;
                               
                <%--
                <s:a action=""><i class="fa fa-circle-info"></i> Info</s:a>
                                
                &nbsp;|&nbsp;
                --%>
                
                <s:if test="currentTab.equals('CFE Pipeline')">
                    <s:a class="selectedMainTab" action="BatchInitialization"><i class="fa fa-calculator"></i> CFE Pipeline</s:a>
                </s:if>
                <s:else>
                    <s:a action="BatchInitialization"><i class="fa fa-calculator"></i> CFE Pipeline</s:a>
                </s:else>
                
                &nbsp;|&nbsp;
                
                <s:if test="currentTab.equals('Saved Results')">
                    <s:a class="selectedMainTab" action="CfeResults"><i class="fa fa-table"></i> Saved Results</s:a>
                </s:if>
                <s:else>
                    <s:a action="CfeResults"><i class="fa fa-table"></i> Saved Results</s:a>
                </s:else>
                
                &nbsp;|&nbsp;
                
                <s:if test="currentTab.equals('Other Functions')">
                    <s:a class="selectedMainTab" action="TestingDbUploadInit"><i class="fa fa-toolbox"></i> Other Functions</s:a>
                </s:if>
                <s:else>
                    <s:a action="TestingDbUploadInit"><i class="fa fa-toolbox"></i> Other Functions</s:a>
                </s:else>              
                
                <s:if test="#session.username==adminUser">
                                
                    &nbsp;|&nbsp;
                    
                    <s:if test="currentTab.equals('Admin')">
                        <s:a class="selectedMainTab" action="SystemStatusAction"><i class="fa fa-gear"></i> Admin</s:a></li>
                    </s:if>
                    <s:else>
                        <s:a action="SystemStatusAction"><i class="fa fa-screwdriver-wrench"></i> Admin</s:a>
                    </s:else>
                </s:if>
                
            </div>
            <div class="mainTabs" style="float: right;"> <s:a action="LogoutAction"><i class="fa fa-sign-out"></i> Logout</s:a> </div>
            <div style="clear: both;"></div>
            </div>
            
            <%-- Sub Tabs --%>
            <div class="subTabs">
                <s:if test="currentTab.equals('Admin')">
                    <s:if test="currentSubTab.equals('System Status')">
                        <s:a class="selectedSubTab" action="SystemStatusAction">System Status</s:a>
                    </s:if>
                    <s:else>
                        <s:a action="SystemStatusAction">System Status</s:a>
                    </s:else>
                    
                    &nbsp;|&nbsp;
                    
                    <s:if test="currentSubTab.equals('Temporary Files')">
                        <s:a class="selectedSubTab" action="TemporaryFilesAction">Temporary Files</s:a>
                    </s:if>
                    <s:else>
                        <s:a action="TemporaryFilesAction">Temporary Files</s:a>
                    </s:else>
                    
                    &nbsp;|&nbsp;
                    <s:if test="currentSubTab.equals('Test Page')">
                        <s:a  class="selectedSubTab" action="TestAction">Test Page</s:a>
                    </s:if>
                    <s:else>
                        <s:a action="TestAction">Test Page</s:a>
                    </s:else>
                </s:if>
                
                <s:if test="currentTab.equals('Other Functions')">
                    
                    <fieldset style="float: left;">
                        <legend style="font-weight: bold;">Discovery</legend>
                    <s:if test="currentSubTab.equals('Phenomic Database Check')">
                        <s:a class="selectedSubTab" action="TestingDbUploadInit">Phenomic Database Check</s:a>
                    </s:if>
                    <s:else>
                        <s:a action="TestingDbUploadInit">Phenomic Database Check</s:a>
                    </s:else>
                    </fieldset>
                    
                    <fieldset style="float: left;">
                        <legend style="font-weight: bold;">Prioritization</legend>
                        
                        <s:if test="currentSubTab.equals('Scoring')">
                            <s:a class="selectedSubTab" action="PrioritizationGeneListUpload">Scoring</s:a>
                        </s:if>
                        <s:else>
                            <s:a action="PrioritizationGeneListUpload">Scoring</s:a>
                        </s:else>

                        &nbsp;|&nbsp;
                        
                        <s:if test="currentSubTab.equals('Merge with Discovery Scores')">
                            <s:a class="selectedSubTab" action="ValidationDataMergeSelection">Merge with Discovery Scores</s:a>
                        </s:if>
                        <s:else>
                            <s:a action="ValidationDataMergeSelection">Merge with Discovery Scores</s:a>
                        </s:else>           
                                     
                        &nbsp;|&nbsp;
                                           
                        <s:if test="#session.username==adminUser">
                           <s:if test="currentSubTab.equals('Upload Literature Databases')">
                              <s:a class="selectedSubTab" action="PrioritizationDBSelectionInitialize">Upload Literature Databases</s:a>
                           </s:if>
                           <s:else>
                               <s:a action="PrioritizationDBSelectionInitialize">Upload Literature Databases</s:a>
                            </s:else>
                        </s:if>
                        
                        &nbsp;|&nbsp;
                                            
                        <s:a action="PrioritizationReport">
                            <s:param name="reportName" value="'diseases'" />
                            <s:param name="reportFormat" value="'xlsx'" />
                            Diseases Report
                        </s:a>
                        
                        &nbsp;|&nbsp;
                        
                        <s:if test="currentSubTab.equals('CFE Database Status')">
                            <s:a class="selectedSubTab" action="DatabaseStatusAction">CFE Database Status</s:a>                 
                        </s:if>
                        <s:else>
                            <s:a action="DatabaseStatusAction">CFE Database Status</s:a>
                        </s:else>

                        
                    </fieldset>
                    
                    <div style="clear: both;"></div>
                    
                    <%-- &nbsp;|&nbsp; --%>

                </s:if>
            </div>
            
        </s:if>
        
        
        
        <hr style="margin-bottom: 0px;"/>

            
        <%-- Content --%>
    	<div class="content">
            <tiles:insertAttribute name="content" />
        </div>
    </body>
</html>
