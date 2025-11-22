Feature: HU02 - Detalle de Álbumes

  Background:
    Given the app is launched

  Scenario: HU02 - Abrir detalle del primer álbum
    When I tap the first album in the list
    Then I should see the album name "Buscando América"
    And I should see the album description
    And I should see the album tracks section
