Feature: Literature database

Background:
    Given I am logged into the CFE Wizard
    
Scenario: Phenomic database check
    When I click on link "Special Functions"
    And I click on link "Upload Literature Databases"
    Then I should see "Database Selection"
    And I should see "Database Upload"
    And I should see "Database Upload Results"
    
