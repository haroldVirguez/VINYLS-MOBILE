Feature: HU01/HU02 - Álbumes lista y detalle

  Background:
    Given the app is launched

  Scenario: HU01 - Ver la lista de álbumes
    Then I should see the albums list

  Scenario: HU02 - Abrir detalle del primer álbum
    When I tap the first album in the list
    Then I should see the album name "Buscando América Prueba"
    And I should see the album description
    And I should see the album tracks section
