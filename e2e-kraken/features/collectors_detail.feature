Feature: HU06 - Detalle de Coleccionista

  Background:
    Given the app is launched

  Scenario: HU06 - Abrir detalle del primer coleccionista
    When I tap Collectors menu
    And I tap the first collector in the list
    Then I should see the collector detail
    And I should see the collector name from list
    And I should see the collector avatar
    And I should see the collector contact information
    And I should see the collector comments section
    And I should see the collector favorite performers section

