Feature: HU04 - Detalle de Artista

  Background:
    Given the app is launched

  Scenario: HU04 - Abrir detalle del primer artista
    When I tap Artist menu
    And I tap the first artist in the list
    Then I should see the artist name "Rubén"
    And I should see the artist description
    And I should see the artist albums section
    And I should see the artist prizes section