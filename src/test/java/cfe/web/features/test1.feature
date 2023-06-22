Feature: Page navigation

Scenario: Home page
    Given I am logged into the CFE Wizard
    Then I should see "Home"
    And I should see "Instructions"
    And I should see "Convergent Functional Evidence"
    
Scenario: Instructions page
    Given I am logged into the CFE Wizard
    When I click on link "Instructions"
    Then I should see "CFE Wizard Instructions"
    
Scenario: CFE Pipeline page
    Given I am logged into the CFE Wizard
    When I click on link "CFE Pipeline"
    Then I should see "Phenomic Database:"
    And I should see "Ending Step:"
