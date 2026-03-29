package io.github.komposeCharts.core.animation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChartAnimationTest {

    @Test
    fun default_isEnabled() {
        assertTrue(ChartAnimation.Default.enabled)
    }

    @Test
    fun default_durationIs600ms() {
        assertEquals(600, ChartAnimation.Default.durationMs)
    }

    @Test
    fun default_staggerIs80ms() {
        assertEquals(80, ChartAnimation.Default.staggerMs)
    }

    @Test
    fun none_isDisabled() {
        assertFalse(ChartAnimation.None.enabled)
    }

    @Test
    fun none_durationIsZero() {
        assertEquals(0, ChartAnimation.None.durationMs)
    }

    @Test
    fun fast_durationIs300ms() {
        assertEquals(300, ChartAnimation.Fast.durationMs)
    }

    @Test
    fun fast_staggerIs40ms() {
        assertEquals(40, ChartAnimation.Fast.staggerMs)
    }

    @Test
    fun custom_storesFieldsCorrectly() {
        val anim = ChartAnimation(enabled = true, durationMs = 1200, staggerMs = 100)
        assertTrue(anim.enabled)
        assertEquals(1200, anim.durationMs)
        assertEquals(100, anim.staggerMs)
    }

    @Test
    fun copy_overridesFieldCorrectly() {
        val modified = ChartAnimation.Default.copy(durationMs = 900)
        assertEquals(900, modified.durationMs)
        assertEquals(ChartAnimation.Default.staggerMs, modified.staggerMs)
        assertTrue(modified.enabled)
    }
}
