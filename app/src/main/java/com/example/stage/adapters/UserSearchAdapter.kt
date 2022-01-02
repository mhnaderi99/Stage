package com.example.stage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.json.JSONTokener
import com.example.stage.responses.UserResponse

import com.example.stage.activities.TimelineActivity
import com.example.stage.fragments.ProfileFragment
import com.example.stage.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserSearchAdapter(
    private val dataSet: ArrayList<UserResponse>
    ) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {


    fun getDataSet(): ArrayList<UserResponse> {
        return dataSet
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val username: TextView = view.findViewById(R.id.username)
        val userImage: ImageView = view.findViewById(R.id.userImage)


        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_row, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.username.text = dataSet[position].username

        Picasso.get()
            .load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${dataSet[position].id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(viewHolder.userImage, object : Callback {
                override fun onSuccess() {
                    println("success")
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

        with(viewHolder.itemView) {
            setOnClickListener {
                val userId = dataSet[position].id
                val username = dataSet[position].username

                Fuel.get("${GlobalVariables.getActiveURL()}/user/checkFollow?id=${userId}")
                    .authentication()
                    .basic(AppPreferences.email, AppPreferences.password)
                    .response { result ->
                        val (bytes, error) = result
                        if (bytes != null) {
                            val res = String(bytes)
                            val jsonResult = JSONTokener(res).nextValue() as JSONObject
                            val followed = jsonResult.getInt("count") > 0
                            val self = AppPreferences.password.toInt() == userId

                            println("$followed $self")

                            val manager = (context as TimelineActivity).supportFragmentManager
                            val profileFragment = ProfileFragment(self, userId, username, followed)
                            manager.beginTransaction().replace(com.example.stage.R.id.flFragment, profileFragment)
                                .addToBackStack("xyz")
                                .commit()
                        }
                    }

            }
        }
    }


    fun update(users: ArrayList<UserResponse>) {
        dataSet.clear()
        dataSet.addAll(users)

        notifyDataSetChanged()
    }

    fun addToList(user: UserResponse) {
        dataSet.add(0, user)
        notifyItemInserted(0)
    }

    fun removeFromList(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}