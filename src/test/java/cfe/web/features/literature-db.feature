Feature: Literature database

Background:
    Given I am logged into the CFE Wizard
    When I click on link "Other Functions"
    
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

Scenario: Literature database upload
    When I click on link "Upload Literature Databases"
    And I check checkbox with value "HUBRAIN"
    And I check checkbox with value "HUGENE"
    And I check checkbox with value "HUPER"
    And I check checkbox with value "NHBRAIN"
    And I check checkbox with value "NHGENE"
    And I check checkbox with value "NHPER"
    And I click on submit "Next"
    And I specify file "ms-access/HUBRAIN (NJ 10-30-2013).accdb" for input "Human Brain:"
    And I specify file "ms-access/HUGEN (NJ 11-1-2013).accdb" for input "Human Genetic:"
    And I specify file "ms-access/HUPER (NJ 11-1-2013).accdb" for input "Human Peripheral:"
    And I specify file "ms-access/NHBRAIN (NJ 11-1-2013).accdb" for input "NonHuman Brain:"
    And I specify file "ms-access/NHGEN (NJ 11-1-2013).accdb" for input "NonHuman Genetic:"
    And I specify file "ms-access/NHPER (NJ 10-30-2013).accdb" for input "NonHuman Peripheral:"
    And I click on submit "Upload"
    When I click on link "CFE Database Status"
    Then I should see table
        | MS Access Table   | CFE MySQL Table | CFE MySQL Table Rows |
        | HUBRAIN-GEX       | hubraingex      |                    0 |
        | HUBRAIN-MET       | hubrainmet      |                    1 |
        | HUBRAIN-PROT      | hubrainprot     |                  605 |
        | HUGEN-ASSOCIATION | hugeneassoc     |                8,091 |
        | HUGEN-CNV         | hugenecnv       |                  699 |
        | HUGEN-LINKAGE     | hugenelinkage   |                    0 |
        | HUPER-GEX         | hupergex        |                    0 |
        | HUPER-MET         | hupermet        |                    1 |
        | HUPER-PROT        | huperprot       |                  459 |
        | NHBRAIN-GEX       | nhbraingex      |                    0 |
        | NHBRAIN-MET       | nhbrainmet      |                    1 |
        | NHBRAIN-PROT      | nhbrainprot     |                  119 |
        | NHGEN-ASSOCIATION | nhgeneassoc     |                  243 |
        | NHGEN-CNV         | nhgenecnv       |                    1 |
        | NHGEN-LINKAGE     | nhgenelinkage   |                    0 |
        | NHPER-GEX         | nhpergex        |                    0 |
        | NHPER-MET         | nhpermet        |                    1 |
        | NHPER-PROT        | nhperprot       |                   47 |
    But I should not see "Error:"


