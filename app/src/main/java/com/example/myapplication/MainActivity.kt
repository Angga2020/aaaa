package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.Adapters.ProductAdapter
import com.example.myapplication.Models.Data
import com.example.myapplication.Models.Product
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.ProductApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale.filter


class MainActivity : AppCompatActivity(), onProductsItemClickListener {

    lateinit var  mListView : ListView

    private val products : ArrayList<Data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this, AddProduct::class.java)
            startActivity(intent)
        }
        mListView = findViewById(R.id.rvProducts)
//        rvProducts.adapter = ProductAdapter(products,this@MainActivity)




        rvProducts.layoutManager = GridLayoutManager(this, 2 )
        setUpRecyleView()

        //** Set the colors of the Pull To Refresh View
        swipeContainer.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        swipeContainer.setColorSchemeColors(Color.WHITE)

        swipeContainer.setOnRefreshListener {
            products.clear()
            setUpRecyleView()
            swipeContainer.isRefreshing = false
        }

    }
    fun setUpRecyleView() {
        val mainHandler = Handler(getMainLooper())
        var runnable: Runnable = object : Runnable {
            override fun run() {
                //Do your work here !!

        val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)

        apiInterface.getProducts()
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    response.body()?.data?.forEach {
                        products.add(it)
                    }
                    rvProducts.adapter = ProductAdapter(products,this@MainActivity)
                }

                override fun onFailure(call: Call<Product>, t: Throwable){
                    t.printStackTrace()
                }
            })
            }
        }


        mainHandler.postDelayed(runnable, 1000 )
    }
    //delay)
    override fun onItemClick(product:Data, position:Int){
        Toast.makeText(this, product.attributes.name, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailProduct::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.menu_search, menu)
//        val menuItem = menu!!.findItem(R.id.search_menu)
//        var viewSearch = MenuItemCompat.getActionView(menuItem)as SearchView
//        viewSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener
//        {
//            override fun onQueryTextSubmit(query :String?): Boolean{
//                viewSearch.clearFocus()
//                if (products.contains(query))
//                {
//                    ProductAdapter.filter(query)
//                }
//                else{
//                    Toast.makeText(applicationContext,"Not Found", Toast.LENGTH_LONG).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newQuery: String?): Boolean {
////                ProductAdapter.getfilter().filter(newQuery)
//                return false
//            }
//        })
//        return super.onCreateOptionsMenu(menu)
//    }

}
