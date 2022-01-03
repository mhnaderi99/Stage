package com.example.stage.activities

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.example.stage.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject
import org.json.JSONTokener
import android.R.attr.data
import android.database.Cursor
import android.graphics.BitmapFactory
import android.provider.MediaStore.MediaColumns
import android.widget.ImageButton
import com.github.kittinunf.fuel.core.FileDataPart
import java.io.File
import java.io.FileNotFoundException
import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable

import android.R.attr.bitmap

import android.graphics.Bitmap
import android.widget.ImageView


class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var submitButton: Button
    private lateinit var imageView: ImageView
    private lateinit var usernameText: EditText
    private var filePath = ""

    private var activeUrl = GlobalVariables.getActiveURL();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        submitButton = findViewById(R.id.signup)
        usernameText = findViewById(R.id.usernameText)
        imageView = findViewById(R.id.buttonLoadPicture)

        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setAdjustViewBounds(true);

        val emailAddress: String = intent.getStringExtra("email").toString()

        if (askForPermissions()) {
            // Permissions are already granted, do your stuff
        }
        submitButton.setOnClickListener {
            val username = usernameText.text.toString()
            val json = JSONObject()
            json.put("email", emailAddress)
            json.put("username", username)
            Fuel.post("$activeUrl/signup")
                .jsonBody(json.toString())
                .response { result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        val res = String(bytes)
                        val jsonResult = JSONTokener(res).nextValue() as JSONObject

                        val jjson = JSONObject()
                        jjson.put("email", emailAddress)

                        val file = FileDataPart.from(filePath, name = "image")
                        Fuel.upload("${GlobalVariables.getActiveURL()}/uploadUserImage?id=${jsonResult.getInt("id")}")
                            .add(file)
                            .response { result ->
                                val (bytes, error) = result
                                if (bytes != null) {
                                    val res = String(bytes)
                                    println(res)
                                    Fuel.post("$activeUrl/deleteOTP")
                                        .jsonBody(jjson.toString())
                                        .response { result ->
                                            val (bytes, error) = result
                                            if (bytes != null) {
                                                val ress = String(bytes)
                                                println(ress)
                                                AppPreferences.isLogin = true
                                                AppPreferences.email = emailAddress
                                                AppPreferences.username = username
                                                AppPreferences.password = jsonResult.getInt("id").toString()
                                                login(jsonResult.getString("email"), jsonResult.getInt("id").toString())
                                            }
                                        }

                                }
                            }





                    }
                }

        }

        imageView.setOnClickListener {
            openGalleryForImage()
        }
    }

    fun login(email: String, userid: String) {
        val intent = Intent(this@CompleteProfileActivity, TimelineActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            println(data?.data)
            // TODO : upload image to server
            val selectedImage: Uri = data!!.data!!

            filePath = getPath(selectedImage)
            val file_extn = filePath.substring(filePath.lastIndexOf(".") + 1)
            //image_name_tv.setText(filePath)
            try {
                if (file_extn == "img" || file_extn == "jpg" || file_extn == "jpeg" || file_extn == "gif" || file_extn == "png") {
                    //FINE

                    val bmp = BitmapFactory.decodeFile(filePath)
                    //val bitmapScaled = Bitmap.createScaledBitmap(bmp, imageView.width, imageView.height, true)
                    //val drawable: Drawable = BitmapDrawable(bitmapScaled)
                    imageView.setImageBitmap(bmp)

                } else {
                    //NOT IN REQUIRED FORMAT
                }
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
    }

    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaColumns.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        var column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
        cursor.moveToFirst()
        var imagePath = cursor.getString(column_index)
        return imagePath
    }


    fun isPermissionsAllowed(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else true
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }
}