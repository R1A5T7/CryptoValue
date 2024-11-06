    package com.example.project3

    import android.content.Context
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.Spinner
    import android.widget.TextView
    import com.android.volley.Request
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.Volley
    import org.json.JSONException


    class MainActivity : AppCompatActivity(), DataLoadCallback {

        private lateinit var idList: ArrayList<String>
        private lateinit var symbolList: ArrayList<String>
        private lateinit var supplyList: ArrayList<String>
        private lateinit var priceList: ArrayList<String>
        private lateinit var c24List: ArrayList<String>
        private lateinit var currency: TextView
        private lateinit var symbol: TextView
        private lateinit var supply: TextView
        private lateinit var price: TextView
        private lateinit var c24: TextView

        private lateinit var spinner: Spinner

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            currency = findViewById(R.id.currencyTextView)
            symbol = findViewById(R.id.symbolTextView)
            supply = findViewById(R.id.supplyTextView)
            price = findViewById(R.id.priceTextView)
            c24 = findViewById(R.id.c24TextView)

            spinner = findViewById(R.id.spinner)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setData(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
            createList()
            fetchData(this, this)
        }
        override fun onDataLoaded() {

            if(idList.isNotEmpty()){
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, idList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter
            }

            if (priceList.isNotEmpty()) {
                currency.text = idList[0]
            } else {
                currency.text = "No data available"
            }
        }


        override fun onFailed(errorMessage: String) {
            Log.e("DataLoadCallback", "Error: $errorMessage")
        }
        private fun createList(){
            idList = ArrayList()
            symbolList = ArrayList()
            supplyList = ArrayList()
            priceList = ArrayList()
            c24List = ArrayList()

        }

        private fun fetchData(context: Context, callback: DataLoadCallback){
            val url = "https://api.coincap.io/v2/assets"
            val queue = Volley.newRequestQueue(context)
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    for(i in 0 until response.getJSONArray("data").length())
                        try {

                            val data = response.getJSONArray("data").getJSONObject(i)
                            val id = data.getString("id")
                            val symbol = data.getString("symbol")
                            val supply = data.getDouble("supply")
                            val price = data.getDouble("priceUsd")
                            val changePercent24Hr = data.getDouble("changePercent24Hr")

                            idList.add(id)
                            symbolList.add(symbol)
                            supplyList.add(String.format("%.0f", supply))
                            priceList.add(String.format("%.2f", price))
                            c24List.add(String.format("%.2f", changePercent24Hr))

                            callback.onDataLoaded()

                            callback.onDataLoaded()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    { error ->

                        callback.onFailed("Error loading data" + "\n " + error.message)
                    })

            queue.add(jsonObjectRequest)
        }

        private fun setData(id: Int){
            currency.text = idList[id]
            symbol.text = "Symbol: " + symbolList[id]
            supply.text = "Supply: " + supplyList[id]
            price.text = "Price: " + priceList[id]
            c24.text = "% change (24hrs): " + c24List[id]
        }

    }

