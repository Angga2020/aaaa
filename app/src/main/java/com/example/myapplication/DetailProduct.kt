package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Models.Data
import com.example.myapplication.Models.Product
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.ProductApi
import com.example.myapplication.utils.GlideApp
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.detail_product.*
import kotlinx.android.synthetic.main.product_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailProduct : AppCompatActivity(){


    internal lateinit var myDialog: Dialog
    internal lateinit var txt : TextView
    internal lateinit var cancel : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_product)

        val product : Data = intent.getParcelableExtra("product")

        if (!product.attributes.images.isEmpty()) {
            GlideApp.with(this)
                .load(product.attributes.images.first().imageUrl)
                .placeholder(R.drawable.noimage)
                .into(ivDetailProductImage)
        }else {
            GlideApp.with(this)
                .load(R.drawable.noimage)
                .into(ivDetailProductImage)
        }
        tvDetailProductName.setText(product.attributes.name)
        tvDetailProductDesc.setText(product.attributes.description)
        tvDetailProductColor.setText(product.attributes.color)
        tvDetailProductSize.setText(product.attributes.size)
        tvDetailProductPrice.setText(product.attributes.price.toString())
        tvDetailProductStock.setText(product.attributes.inStock.toString())


        updateBtn.setOnClickListener {
            val intent = Intent(this, EditProduct::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            ShowDialog()
        }
    }


    fun ShowDialog(){
        myDialog = Dialog(this)
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        myDialog.setContentView(R.layout.activity_confirm_delete)
        myDialog.setTitle("My PopUp")

        txt = myDialog.findViewById(R.id.tvbYes) as TextView
        txt.isEnabled = true
        txt.setOnClickListener{
            val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)
            val productId = intent.getStringExtra("productId")
            apiInterface.deleteProduct(productId.toInt())
                .enqueue(object : Callback<Product> {

                    override fun onResponse(call: Call<Product>, response: Response<Product>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DetailProduct,"Product was successfully deleted",Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(this@DetailProduct, response.message(),Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Product>, t: Throwable) = t.printStackTrace()

                })
            myDialog.dismiss()

        }

        txt = myDialog.findViewById(R.id.tvbCancel) as TextView
        txt.isEnabled = true
        txt.setOnClickListener{
            myDialog.cancel()
        }
        myDialog.show()
    }

}