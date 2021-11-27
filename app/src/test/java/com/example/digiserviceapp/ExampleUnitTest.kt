package com.example.digiserviceapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val range = "nama nama nama nama nama"
        println(range.slice(0..16) + "...")
        assertEquals(4, 2 + 2)
    }
}