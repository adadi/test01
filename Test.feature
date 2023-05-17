Feature: Testing CaseService

  Scenario: Retrieving cases between two booking dates
    Given I have the start booking date "2023-01-01"
    And I have the end booking date "2023-02-01"
    When I call the getCases method
    Then I should receive a list of CaseExternalDto

  Scenario: Retrieving cases by IDs
    Given I have the following case IDs:
      | 1 |
      | 2 |
      | 3 |
    When I call the getCaseByIds method
    Then I should receive a list of CaseExternalDto

  Scenario: Retrieving last update
    When I call the getLastUpdate method
    Then I should receive a LastUpdateDto
