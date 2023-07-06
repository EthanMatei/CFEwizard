Feature: Phenomic database check

Background:
    Given I am logged into the CFE Wizard
    
Scenario: Phenomic database check
    When I click on link "Special Functions"
    And I click on link "Phenomic Database Check"
    And I specify file "PhenomicDatabase1.accdb" for input "Phenomic Database:"
    And I click on submit "Check"
    Then I should see "TABLE \"Demographics\""
    And I should see "COLUMNS: \"PheneVisit\", \"Gender(M/F)\", \"Age at testing (Years)\", \"Age at Onset of Illness\", \"Race/Ethnicity\""
    And I should see "ERROR: Phene visit \"phchp378V1\" on line 875 has an incorrect format."
    And I should see "ERROR: Phene visit \"phchp416V1\" on line 928 has an incorrect format."
    
