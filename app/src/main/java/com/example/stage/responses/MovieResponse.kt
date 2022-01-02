package com.example.stage.responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class MovieResponse(
    val id: Int,
    val title: String,
    val director: String,
    val year: Number,
    val summary: String,
    val length: Number,
    val category: String,
) {

    class Deserializer : ResponseDeserializable<Array<MovieResponse>> {
        override fun deserialize(content: String): Array<MovieResponse>? =
            Gson().fromJson(content, Array<MovieResponse>::class.java)
    }
}
