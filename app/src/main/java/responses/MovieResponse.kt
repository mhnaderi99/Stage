package responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class MovieResponse(
    val id: Int,
    val title: String,
    val category_id: Int,
    val director: String,
    val year: Number) {

    class Deserializer: ResponseDeserializable<Array<MovieResponse>> {
        override fun deserialize(content: String): Array<MovieResponse>? = Gson().fromJson(content, Array<MovieResponse>::class.java)
    }
}
