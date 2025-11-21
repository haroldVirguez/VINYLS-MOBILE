package com.team3.vinyls

import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.ui.mapper.toUi
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectorUiMapperTest {

    @Test
    fun toUi_prefersEmail_thenTelephone_thenDefault() {
        val dtoWithEmail = CollectorDto(1, "John", "300", "john@example.com", null, null, null)
        val ui1 = dtoWithEmail.toUi()
        assertEquals("john@example.com", ui1.subtitle)

        val dtoWithPhone = CollectorDto(2, "Jane", "310", null, null, null, null)
        val ui2 = dtoWithPhone.toUi()
        assertEquals("310", ui2.subtitle)

        val dtoEmpty = CollectorDto(3, "NoInfo", "", "", null, null, null)
        val ui3 = dtoEmpty.toUi()
        assertEquals("Ubicación desconocida", ui3.subtitle)
    }
}

