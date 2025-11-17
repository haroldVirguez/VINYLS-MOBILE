Feature: HU07 - Crear Album

  Background:
    Given the app is launched
    

  Scenario: HU07 - Crear un nuevo album
    When I tap the create album button
    And I fill the album creation form
    And I submit the album creation form
    Then I should see the album name "Buscando América Prueba"
