package com.example.bookapplication.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookapplication.R
import com.example.bookapplication.util.ConnectionManager
import com.squareup.picasso.Picasso
import android.provider.Settings
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.bookapplication.database.BookDatabase
import com.example.bookapplication.database.BookEntity
import org.json.JSONObject
import java.lang.Exception

lateinit var txtBookName : TextView
lateinit var txtBookAuthor : TextView
lateinit var txtBookPrice : TextView
lateinit var txtBookRating : TextView
lateinit var imgBookImage : ImageView
lateinit var txtBookDesc : TextView
lateinit var btnAddToFav : Button
lateinit var progressBar : ProgressBar
lateinit var progressLayout : RelativeLayout

lateinit var toolbar : Toolbar
var  bookId : String? ="100"
class DescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName =findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.parseColor("#FFE5B4"))
        setSupportActionBar(toolbar)
        supportActionBar?.title= "Book Details"
        if(intent != null)
            bookId = intent.getStringExtra("book_id")
        else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId == "100"){
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue= Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBASyncTask(applicationContext,bookEntity,1).execute()
                            val isFav = checkFav.get()
                            if(isFav){
                                btnAddToFav.text="Remove From Favourites"
                                val favColor = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                btnAddToFav.setBackgroundColor(favColor)
                            }else
                            {
                             btnAddToFav.text = "Add To Favourites"
                             val noFavColor = ContextCompat.getColor(applicationContext, R.color.design_default_color_primary)
                             btnAddToFav.setBackgroundColor(noFavColor)
                            }

                            btnAddToFav.setOnClickListener{

                                if(!DBASyncTask(applicationContext, bookEntity,1).execute().get()){
                                    val async = DBASyncTask(applicationContext,bookEntity,2).execute()
                                    val result = async.get()
                                    if(result){
                                        Toast.makeText(this@DescriptionActivity, "Book Added To Favourites", Toast.LENGTH_SHORT).show()
                                        btnAddToFav.text = "Remove From Favourites"
                                        val favColor = ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                        btnAddToFav.setBackgroundColor(favColor)
                                    }
                                    else{
                                        Toast.makeText(this@DescriptionActivity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else{
                                    val async = DBASyncTask(applicationContext,bookEntity,3).execute()
                                    val result = async.get()
                                    if(result){
                                        Toast.makeText(this@DescriptionActivity, "Book Removed From Favourites", Toast.LENGTH_SHORT).show()
                                        btnAddToFav.text = "Add To Favourites"
                                        val favColor = ContextCompat.getColor(applicationContext,R.color.design_default_color_primary)
                                        btnAddToFav.setBackgroundColor(favColor)
                                    }
                                    else
                                        Toast.makeText(this@DescriptionActivity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some Error Occurred !!",
                                Toast.LENGTH_SHORT
                            ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some Error Occurred !!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {

                    Toast.makeText(
                        this@DescriptionActivity,
                        "Volley Error $it Occurred",
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "62b086128aa3aa"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Is Not Found !!")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    class DBASyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) : AsyncTask<Void,Void,Boolean>(){

        /*
        Mode 1 -> Check DB if the book is favourite or not.
        MOde 2 -> Save the book into DB as favourite.
        Mode 3 -> Remove the favourite book.
         */

        val db = Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 ->{
                    val book: BookEntity? = db.BookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }
                2 -> {
                    db.BookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.BookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}