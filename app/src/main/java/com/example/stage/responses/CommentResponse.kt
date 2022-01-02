package com.example.stage.responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class CommentResponse(
    val id: Int,
    val title: String,
    val username: String,
    val comment_text: String,
    val user_id: Int
) {

    class Deserializer: ResponseDeserializable<Array<CommentResponse>> {
        override fun deserialize(content: String): Array<CommentResponse>? = Gson().fromJson(content, Array<CommentResponse>::class.java)
    }
}