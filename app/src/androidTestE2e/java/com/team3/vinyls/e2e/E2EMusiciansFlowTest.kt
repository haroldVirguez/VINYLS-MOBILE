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
class E2EMusiciansFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var recyclerIdling: RecyclerViewItemCountIdlingResource? = null

    @Before
    fun setUp() {
        // No registrar IdlingResource aquí: la vista puede no estar aún adjuntada.
    }

    @After
    fun tearDown() {
        recyclerIdling?.let {
            IdlingRegistry.getInstance().unregister(it)
            // También limpiar referencia interna si el recurso lo expone
            try {
                it.unregister()
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    /**
     * HUxx: Consultar catálogo de músicos
     * HUyy: Consultar detalle de músico
     *
     * Precondición: Ejecutar el mock API Express en http://10.0.2.2:3000 y usar el flavor e2e.
     */
    @Test
    fun listMusicians_and_openMusicianDetail_displaysMockData() {
        // Comprueba que la lista está visible
        onView(withId(R.id.recyclerMusicians))
            .check(matches(isDisplayed()))

        // Registrar el IdlingResource ahora que la vista está visible
        activityRule.scenario.onActivity { activity ->
            val rv: RecyclerView? = activity.findViewById(R.id.recyclerMusicians)
            if (rv == null) {
                throw AssertionError("RecyclerView with id R.id.recyclerMusicians not found after view isDisplayed().")
            }
            recyclerIdling = RecyclerViewItemCountIdlingResource(rv)
            IdlingRegistry.getInstance().register(recyclerIdling)
        }

        // Matcher para un item con nombre no vacío
        // Evitar el matcher deprecado isEmptyOrNullString: comprobar que el texto no es la cadena vacía
        val itemMatcher = hasDescendant(allOf(withId(R.id.txtName), withText(not(equalTo("")))))

        // Haz scroll hasta el primer item que tenga nombre no vacío
        onView(withId(R.id.recyclerMusicians))
            .perform(
                RecyclerViewActions.scrollTo<MusiciansAdapter.MusicianViewHolder>(
                    itemMatcher
                )
            )

        // Verifica que el nombre está presente y no es vacío
        // Comprobar el texto del TextView usando withText(...)
        onView(allOf(withId(R.id.txtName), isDescendantOfA(withId(R.id.recyclerMusicians))))
            .check(matches(withText(not(equalTo("")))))

        // Verifica que el subtítulo (género/descripcion) existe para el mismo item
        onView(allOf(withId(R.id.txtSubtitle), isDescendantOfA(withId(R.id.recyclerMusicians))))
            .check(matches(isDisplayed()))

        // Realiza un click en el mismo item encontrado (menos frágil que usar posición fija)
        onView(withId(R.id.recyclerMusicians))
            .perform(
                RecyclerViewActions.actionOnItem<MusiciansAdapter.MusicianViewHolder>(
                    itemMatcher,
                    click()
                )
            )
    }
}
