Feature: Literature database

Background:
    Given I am logged into the CFE Wizard
    When I click on link "Special Functions"
    
Scenario: Literature database upload page access
    When I click on link "Upload Literature Databases"
    Then I should see "Database Selection"
    And I should see "Database Upload"
    And I should see "Database Upload Results"
    
Scenario: Literature database clear
    When I click on link "CFE Database Status"
    And I click on submit "Clear All"
    Then I should see table
        | MS Access Table   | CFE MySQL Table | CFE MySQL Table Rows |
        | HUBRAIN-GEX       | hubraingex      |                    0 |
        | HUBRAIN-MET       | hubrainmet      |                    0 |
        | HUBRAIN-PROT      | hubrainprot     |                    0 |
        | HUGEN-ASSOCIATION | hugeneassoc     |                    0 |
        | HUGEN-CNV         | hugenecnv       |                    0 |
        | HUGEN-LINKAGE     | hugenelinkage   |                    0 |
        | HUPER-GEX         | hupergex        |                    0 |
        | HUPER-MET         | hupermet        |                    0 | 
        | HUPER-PROT        | huperprot       |                    0 |
        | NHBRAIN-GEX       | nhbraingex      |                    0 |
        | NHBRAIN-MET       | nhbrainmet      |                    0 |
        | NHBRAIN-PROT      | nhbrainprot     |                    0 |
        | NHGEN-ASSOCIATION | nhgeneassoc     |                    0 |
        | NHGEN-CNV         | nhgenecnv       |                    0 |
        | NHGEN-LINKAGE     | nhgenelinkage   |                    0 |
        | NHPER-GEX         | nhpergex        |                    0 |
        | NHPER-MET         | nhpermet        |                    0 |
        | NHPER-PROT        | nhperprot       |                    0 |
    But I should not see "Error:"

