package com.example.getMidLandFcst

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.w3c.dom.Element
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGet.setOnClickListener {
            getRouteAcctoBusLcList()
        }
    }

    private fun getRouteAcctoBusLcList() {
        progressBar.visibility = View.VISIBLE
        val request = getRequestUrl()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var itemList: ArrayList<HashMap<String, String>> = ArrayList()

                val body = response.body()?.string()?.byteInputStream()
                val buildFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = buildFactory.newDocumentBuilder()
                val doc = docBuilder.parse(body, null)
                val nList = doc.getElementsByTagName("item")

                for (n in 0 until nList.length) {
                    val element = nList.item(n) as Element
                    val dataHashMap = HashMap<String, String>()
                    dataHashMap.put("address1", getValueFromKey(element, "address1"))
                    dataHashMap.put("address2", getValueFromKey(element, "address2"))
                    dataHashMap.put("libid", getValueFromKey(element, "libid"))
                    dataHashMap.put("libname", getValueFromKey(element, "libname"))
                    dataHashMap.put("position", getValueFromKey(element, "position"))
                    dataHashMap.put("zipno", getValueFromKey(element, "zipno"))
                    itemList.add(dataHashMap)
                }
                runOnUiThread {
                    txtResult.text = itemList.toString()
                    progressBar.visibility = View.GONE
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                val body = e.message
                runOnUiThread {
                    txtResult.text = body
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun getRequestUrl() : Request {



//        var url = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst"
//        var serviceKey = "ufSOsuiA8YGSKWBzLt7yuoA5ixvVhm11UReU6gWQDxhoc%2Bttq27rDUhFVeyiNi%2BJZr%2BkQgS99KfKM3nR3HRdvA%3D%3D"

        var url = "http://dev.ndsl.kr/openapi/service/rest/LbrrySerchInfoService/getLbrryInfoList"
        var serviceKey = "nynKQpN7ybxaSAstA9pWvReQOXQ9pP9ENPUKE%2BmoT%2BOCmvTMUtMhFQNoosQ9sMNvRMGK43nNWoTIcdDFZkUHkg%3D%3D"
        var searchLibName = "서울"

        var httpUrl = HttpUrl.parse(url)
            ?.newBuilder()
            ?.addEncodedQueryParameter("serviceKey", serviceKey)
            ?.addQueryParameter("searchLibName", "1")
            ?.build()

        return Request.Builder()
            .url(httpUrl)
            .addHeader("Content-Type",
                "application/x-www-form-urlencoded; text/xml; charset=utf-8")
            .build()
    }

    private fun getValueFromKey(element: Element, key: String) : String {
        return element.getElementsByTagName(key).item(0).firstChild.nodeValue
    }
}