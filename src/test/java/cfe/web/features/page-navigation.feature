Feature: Page navigation

Background:
    Given I am logged into the CFE Wizard
    
Scenario: Home page
    Then I should see "Home"
    And I should see "Instructions"
    And I should see "Convergent Functional Evidence"
    
Scenario: Instructions page
    When I click on link "Instructions"
    Then I should see "CFE Wizard Instructions"
    
Scenario: CFE Pipeline page
    When I click on link "CFE Pipeline"
    Then I should see "Phenomic Database:"
    And I should see "Ending Step:"

Scenario: Saved Results page
    When I click on link "Saved Results"
    Then I should see "CFE Saved Results"
    And I should see "Order:"
    And I should see "Phene:"

Scenario: Special Functions page
    When I click on link "Special Functions"
    Then I should see "Phenomic Database Check"

Scenario: Admin page
    When I click on link "Admin"
    Then I should see "System Status"
