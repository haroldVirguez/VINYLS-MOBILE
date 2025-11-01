package com.team3.vinyls.e2e

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.team3.vinyls.MainActivity
import com.team3.vinyls.R
import com.team3.vinyls.e2e.util.RecyclerViewItemCountIdlingResource
import com.team3.vinyls.ui.adapters.AlbumsAdapter
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumDetailLikeSampleTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var recyclerIdling: RecyclerViewItemCountIdlingResource? = null

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            val rv = activity.findViewById<RecyclerView>(R.id.recyclerAlbums)
            recyclerIdling = RecyclerViewItemCountIdlingResource(rv)
            IdlingRegistry.getInstance().register(recyclerIdling)
        }

        onView(withId(R.id.recyclerAlbums))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlbumsAdapter.AlbumViewHolder>(
                    0, click()
                )
            )

        onView(allOf(withId(R.id.txtTitle), isDisplayed()))
            .check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        recyclerIdling?.let { IdlingRegistry.getInstance().unregister(it) }
        // Cleanup internal observer to avoid leaks in the RecyclerView
        recyclerIdling?.unregister()
    }

    @Test
    fun verifyDetailElements() {
        onView(withId(R.id.txtTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.txtSubtitle)).check(matches(isDisplayed()))
        onView(withId(R.id.imgAlbumCover)).check(matches(isDisplayed()))
        onView(withId(R.id.txtReleaseDate)).check(matches(isDisplayed()))
        onView(withId(R.id.tracksContainer)).check(matches(isDisplayed()))
        onView(withId(R.id.txtDescription)).check(matches(isDisplayed()))
    }

    @Test
    fun comeBackToTheList() {
        pressBack()
        onView(withId(R.id.recyclerAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun compareTitleListAndDetail() {
        // Volver a la lista
        pressBack()

        var listTitle = ""
        onView(withId(R.id.recyclerAlbums))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlbumsAdapter.AlbumViewHolder>(
                    0,
                    object : ViewAction {
                        override fun getConstraints() = null
                        override fun getDescription() = "Obtener texto del título del álbum en lista"
                        override fun perform(uiController: UiController?, view: View?) {
                            val tv = view?.findViewById<TextView>(R.id.txtTitle)
                            listTitle = tv?.text?.toString().orEmpty()
                        }
                    }
                )
            )

        // Entra a detalle nuevamente
        onView(withId(R.id.recyclerAlbums))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlbumsAdapter.AlbumViewHolder>(0, click())
            )

        var detailTitle = ""
        onView(withId(R.id.txtTitle)).check { view, _ ->
            detailTitle = (view as TextView).text.toString()
        }

        assert(listTitle == detailTitle) {
            "Títulos diferentes: detalle='$detailTitle', lista='$listTitle'"
        }
    }

    @Test
    fun descriptionIsNotEmpty() {
        onView(withId(R.id.txtDescription)).check { view, _ ->
            val description = (view as TextView).text.toString()
            assert(description.isNotBlank()) { "La descripción del álbum está vacía" }
        }
    }

    @Test
    fun validateReleaseDateFormat() {
        onView(withId(R.id.txtReleaseDate)).check { view, _ ->
            val release = (view as TextView).text.toString()
            val regex = Regex("""Lanzado en \d{4}""")
            assert(regex.matches(release)) { "Formato de fecha inválido: $release" }
        }
    }

    @Test
    fun validGenre() {
        val allowed = listOf("Salsa", "Rock", "Pop", "Jazz")
        onView(withId(R.id.txtSubtitle)).check { view, _ ->
            val subtitle = (view as TextView).text.toString()
            val genre = subtitle.substringBefore(" • ")
            assert(allowed.contains(genre)) { "Género inesperado: $genre" }
        }
    }
}
