package com.team3.vinyls.e2e

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.MainActivity
import com.team3.vinyls.R
import com.team3.vinyls.e2e.util.RecyclerViewItemCountIdlingResource
import com.team3.vinyls.ui.adapters.MusiciansAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class E2EMusiciansSimpleFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var recyclerIdling: RecyclerViewItemCountIdlingResource? = null

    @Before
    fun setUp() {
        // No registrar aquí; se registrará en el test después de comprobar que la vista está visible.
    }

    @After
    fun tearDown() {
        // Intentar limpiar IdlingRegistry de forma segura
        try {
            recyclerIdling?.unregister()
        } catch (_: Exception) {
            // ignore
        }
        try {
            recyclerIdling?.let { IdlingRegistry.getInstance().unregister(it) }
        } catch (_: Exception) {
            // ignore
        }
    }

    /**
     * Prueba E2E sencilla para músicos: navega a la pestaña Artistas, comprueba que la lista se muestra,
     * hace click en el primer elemento y valida que el nombre y subtítulo están presentes.
     * Usa el mock API en http://10.0.2.2:3000.
     */
    @Test
    fun listMusicians_and_clickFirst_displaysMockDataInList() {
        // Asegurar que el botón de navegación a 'Artistas' está visible y luego navegar
        onView(withId(R.id.nav_artists)).check(matches(isDisplayed())).perform(click())

        // Comprueba que la lista de músicos está visible
        onView(withId(R.id.recyclerMusicians)).check(matches(isDisplayed()))

        // Registrar IdlingResource ahora que la vista está visible
        activityRule.scenario.onActivity { activity ->
            val rv: RecyclerView? = activity.findViewById(R.id.recyclerMusicians)
            if (rv == null) {
                throw AssertionError("RecyclerView with id R.id.recyclerMusicians not found after navigation to Artistas.")
            }
            recyclerIdling = RecyclerViewItemCountIdlingResource(rv)
            try {
                IdlingRegistry.getInstance().register(recyclerIdling)
            } catch (_: IllegalArgumentException) {
                // ya registrado
            }
        }

        // Haz scroll (si es necesario) y hacer click en el primer elemento (posición 0)
        onView(withId(R.id.recyclerMusicians)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MusiciansAdapter.MusicianViewHolder>(0, click())
        )

        // Verificar que el primer item muestra un nombre no vacío y que el subtítulo está presente
        onView(allOf(withId(R.id.txtName), isDescendantOfA(withId(R.id.recyclerMusicians))))
            .check(matches(withText(not(equalTo("")))))

        onView(allOf(withId(R.id.txtSubtitle), isDescendantOfA(withId(R.id.recyclerMusicians))))
            .check(matches(isDisplayed()))
    }
}
