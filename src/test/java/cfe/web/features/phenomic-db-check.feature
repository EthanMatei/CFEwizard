Feature: Phenomic database check

Background:
    Given I am logged into the CFE Wizard
    
Scenario: Phenomic database check
    When I click on link "Special Functions"
    And I click on link "Phenomic Database Check"
    And I specify file "PhenomicDatabase1.accdb" for input "Phenomic Database:"
    
