Feature: HU08 - Asociar canciones a álbum

  Background:
    Given the app is launched

  Scenario: HU08 - Crear un track desde el álbum
    When I tap the first album in the list
    And I tap the plus floating button
    And I tap the add track option
    And I enter the track name "Christmas Tree"
    And I enter the track duration "02:50"
    And I tap the save track button
    Then I should see the track added toast
    And I should see the track name "Christmas Tree" in the tracks list
