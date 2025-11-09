package com.team3.vinyls.viewmodels

import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.models.MusicianDto
import com.team3.vinyls.ui.mapper.toUi
import org.junit.Assert.*
import org.junit.Test

class MusicianUiMapperTest {

    @Test
    fun `toUi uses description when present`() {
        val dto = MusicianDto(id = 1, name = "A", image = null, description = "Desc", birthDate = null, albums = emptyList(), performerPrizes = null)
        val ui = dto.toUi()
        assertEquals("Desc", ui.subtitle)
    }

    @Test
    fun `toUi falls back to first album genre when description missing`() {
        val album = AlbumDto(id = 1, name = "T", cover = "", releaseDate = "", description = "", genre = "Rock", recordLabel = "")
        val dto = MusicianDto(id = 1, name = "A", image = null, description = null, birthDate = null, albums = listOf(album), performerPrizes = null)
        val ui = dto.toUi()
        assertEquals("Rock", ui.subtitle)
    }

    @Test
    fun `toUi fallback unknown when no description and no album genre`() {
        val dto = MusicianDto(id = 1, name = "A", image = null, description = null, birthDate = null, albums = null, performerPrizes = null)
        val ui = dto.toUi()
        assertEquals("Género desconocido", ui.subtitle)
    }

    @Test
    fun `toUi image fallback to album cover`() {
        val album = AlbumDto(id = 1, name = "T", cover = "http://cover", releaseDate = "", description = "", genre = "Rock", recordLabel = "")
        val dto = MusicianDto(id = 1, name = "A", image = "", description = null, birthDate = null, albums = listOf(album), performerPrizes = null)
        val ui = dto.toUi()
        assertEquals("http://cover", ui.image)
    }
}
