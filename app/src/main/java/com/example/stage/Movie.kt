package com.example.stage

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Movie(
    val title: String,
    val year: Number,
    val director: String) {

    class Deserializer: ResponseDeserializable<Array<Movie>> {
        override fun deserialize(content: String): Array<Movie>? = Gson().fromJson(content, Array<Movie>::class.java)
    }
}