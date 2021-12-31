package responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class UserResponse(
    val username: String,
    val id: Int) {

    class Deserializer: ResponseDeserializable<Array<UserResponse>> {
        override fun deserialize(content: String): Array<UserResponse>? = Gson().fromJson(content, Array<UserResponse>::class.java)
    }
}
