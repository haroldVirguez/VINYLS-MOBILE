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
import com.team3.vinyls.ui.adapters.AlbumsAdapter
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class E2EAlbumsFlowTest {

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
    }

    @After
    fun tearDown() {
        recyclerIdling?.let { IdlingRegistry.getInstance().unregister(it) }
    }

    /**
     * HU01: Consultar catálogo de álbumes
     * HU02: Consultar detalle de álbum
     *
     * Precondición: Ejecutar el mock API Express en http://10.0.2.2:3000 y usar el flavor e2e.
     */
    @Test
    fun listAlbums_and_openAlbumDetail_displaysMockData() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerAlbums))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlbumsAdapter.AlbumViewHolder>(
                    0,
                    click()
                )
            )

        onView(allOf(withId(R.id.txtTitle), isDisplayed()))
            .check(matches(withText("Mock Album 1")))

        onView(withId(R.id.txtDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.tracksContainer)).check(matches(isDisplayed()))
    }
}


