package com.example.movies.common.utils

import android.Manifest
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.example.movies.network.HttpRequestItem
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object NetworkUtils {

    val HTTP_GET = "GET"
    /**
     * HTTP_POST for [HttpURLConnection.setRequestMethod]
     */
    val HTTP_POST = "POST"

    /**
     * HTTP_GET for [HttpURLConnection.setRequestMethod]
     */
    val HTTP_PUT = "PUT"
    /**
     * HTTP_POST for [HttpURLConnection.setRequestMethod]
     */
    val HTTP_DELETE = "DELETE"
    /**
     * HTTP_MULTIPART for [HttpURLConnection.setRequestMethod]
     */
    val HTTP_MULTIPART = "PROFILE_IMAGE"
    /**
     * Default error code for exception at any level/method while parsing/create HTTP response
     */
    private val HTTP_EXCEPTION = -1
    /**
     * HTTP timeout value
     */
    val HTTP_TIMEOUT = 30000 // milli seconds
    /**
     * HTTP Multipart timeout value
     */
    val HTTP_MULTIPART_TIMEOUT = 60000 // milli seconds
    /**
     * Max retries for given HTTP request
     */
    val MAX_RETRY_CONNECTIONS = 2
    /**
     * Charset for
     */
    val CHARSET = "UTF-8"


    @WorkerThread
    fun executeNetworkRequest(params: HttpRequestItem): String {
        val TAG = "NetworkUtils.executeNetworkRequest"

        // region HTTP_TIMEOUT
        // Create ExecutorService from a cachedThreadPool and submit a Callable/Future reference.
        val executor = Executors.newCachedThreadPool(Executors.defaultThreadFactory())

        val httpFuture = executor.submit(Callable {
            processHttpRequest(params, 0)
        })


        // endregion

        // getWalletAmount service/api name from HttpRequestItem
        var serviceName = params.getHttpRequestUrl()
        // extract api/service name
        serviceName = if (AppUtils.ifNotNullEmpty(serviceName)) "\"" + serviceName!!.substring(
            serviceName.lastIndexOf("/") + 1, serviceName.length
        ) + "\"" else ""

        // default message
        var formattedResponse =
            getResponseMessage("Request time out", HttpURLConnection.HTTP_CLIENT_TIMEOUT)
        try {
            Logger.info(TAG, "getting response using future for $serviceName")
            // wait for maximum timeout for current request and read data/response from HttpURLConnection
            formattedResponse = httpFuture.get(
                (if (params.getHttpRequestTimeout() == 0L) {
                    HTTP_TIMEOUT.toLong()
                } else params.getHttpRequestTimeout()),
                TimeUnit.MILLISECONDS
            )
            Logger.info(TAG, "we have http response using future for $serviceName Yey")

        } catch (e: Exception) {
            Logger.error(false, e)
        } finally {
            // cancel future task
            try {
                httpFuture.cancel(true)
            } catch (e: Exception) {
                Logger.error(false, e)
            }

            // shutdown executor service
            try {
                executor.shutdownNow()
            } catch (e: Exception) {
                Logger.error(false, e)
            }

        }
        return formattedResponse
    }

    private fun processHttpRequest(params: HttpRequestItem, retryCount: Int): String {
        var connection: HttpURLConnection? = null
        val TAG = "InternetUtils.processHttpRequest"
        // default/error response
        var response = ""
        try {
            Logger.info(TAG, "initializing HttpURLConnection")
            // initializing HttpURLConnection with given params and charset
            connection = initHttpURLConnection(params)

            Logger.info(TAG, "initialized http connection, now getting http response...")
            // read http response from HttpURLConnection#getInputStream and covert this stream into string
            response = getHttpURLConnectionResponse(connection)
            // Logger.info(TAG, "we have http response, Yey");
        } catch (exception: Exception) {
            Logger.error(true, exception)
            // retry this whole request
            if (exception is EOFException && retryCount <= MAX_RETRY_CONNECTIONS) {
                Logger.error(TAG, "Retries $retryCount, $exception")
                disconnectHttpURLConnection(connection)
                connection = null
                return processHttpRequest(params, retryCount + 1)
            }
            // there is some error either from server connectivity, HTTP response parsing, and/or input stream exception.
            // return with HTTP_EXCEPTION a
            response = getResponseMessage("Invalid HTTP response", HTTP_EXCEPTION)
        } finally {
            // all we'r doing is disconnecting HttpURLConnection
            disconnectHttpURLConnection(connection)
        }
        return response
    }


    @Throws(IOException::class)
    private fun getHttpURLConnectionResponse(connection: HttpURLConnection): String {
        var response: String
        var inputStream: InputStream? = null
        val TAG = "NetworkUtils.getHttpURLConnectionResponse"
        Logger.info(TAG, "executing network request")
        try {
            Logger.info(TAG, "connecting HttpURLConnection")
            connection.connect()
            Logger.info(TAG, "connected HttpURLConnection")

            // read response from HttpURLConnection
            val responseCode = connection.responseCode
            var cookie = ""
            if (connection.headerFields.containsKey("set-cookie") &&
                connection.getHeaderField("set-cookie") != null &&
                connection.getHeaderField("set-cookie") != ""
            ) {
                cookie = connection.getHeaderField("set-cookie")
                cookie = cookie.substring(0, cookie.indexOf(';'))
                Logger.info("Cookie", "" + cookie)
                //SharedPreferenceManager.getInstance().save(PreferenceUtils.COOKIE, cookie)
            }
            // if response code from one of below, consider them as error, return HttpURLConnection error code
            // and custom error message.

            // NOTE:
            // we can add as many error(s) here
            when (responseCode) {
                HttpURLConnection.HTTP_INTERNAL_ERROR -> return getResponseMessage(
                    "No server found",
                    HttpURLConnection.HTTP_INTERNAL_ERROR
                )
                HttpURLConnection.HTTP_CLIENT_TIMEOUT -> return getResponseMessage(
                    "Request time out",
                    HttpURLConnection.HTTP_CLIENT_TIMEOUT
                )
                HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> return getResponseMessage(
                    "Request time out",
                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT
                )
                HttpURLConnection.HTTP_UNAUTHORIZED -> return getResponseMessage(
                    "Session Expired",
                    HttpURLConnection.HTTP_UNAUTHORIZED
                )
            }

            Logger.info(TAG, "getting InputStream")
            inputStream = connection.inputStream

            Logger.info(TAG, "converting stream to readable format")
            response = convertStream(inputStream)!!

            // check for response nullity
            if (AppUtils.ifNotNullEmpty(response))
                response = getResponseMessage(response, HttpURLConnection.HTTP_OK)
            else
                response = getResponseMessage(
                    "No service response",
                    HttpURLConnection.HTTP_LENGTH_REQUIRED
                )

        } /*catch (Exception e) {
            // there is some error either from server connectivity, HTTP response parsing, and/or input stream exception.
            // return with HTTP_EXCEPTION a
            response = getResponseMessage("Invalid HTTP response", HTTP_EXCEPTION);
            Logger.error(true, e);
        } */
        finally {
            // close stream
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Logger.error(false, e)
            }

        }
        return response
    }


    @Throws(Exception::class)
    private fun initHttpURLConnection(params: HttpRequestItem): HttpURLConnection {
        val TAG = "InternetUtils.initHttpURLConnection"

        val isGetRequest = isGetRequest(params)
        val queryParams: String? = getQueryParams(params.getParams(), CHARSET)

        val url: URL
        if (isGetRequest)
            //url = URL(params.getHttpRequestUrl() + queryParams?.let { "?$it" })
       url= URL(params.getHttpRequestUrl() + if (!queryParams.isNullOrEmpty()) "?$queryParams" else "")

        else
            url = URL(params.getHttpRequestUrl())

        // create HttpURLConnection from given URL
        val httpURLConnection = url.openConnection() as HttpURLConnection

        httpURLConnection.apply {
            connectTimeout = HTTP_TIMEOUT
            readTimeout = HTTP_TIMEOUT
            doInput = true
            //setRequestProperty("content-type", "application/json;charset=utf-8")
            setRequestProperty("Accept-Charset", CHARSET)
        }
        //        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        setHeaderParams(httpURLConnection, params.getHeaderParams())

        if (isGetRequest) {
            httpURLConnection.setRequestProperty(
                "Content-Type",
                "application/json;charset=utf-8"
//                "application/x-www-form-urlencoded"
            )
            httpURLConnection.requestMethod = params.getHttpRequestType()
        } else {
            if (params.getHttpRequestType() == HTTP_MULTIPART) {
                addMultipartEntity(params, httpURLConnection)
            } else {
                //            if (AppUtils.ifNotNullEmpty(queryParams)) {
                httpURLConnection.doOutput = true
                httpURLConnection.requestMethod = params.getHttpRequestType()
                //                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=utf-8"
                )
                //                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(queryParams.length()));
                val dataOutputStream = DataOutputStream(httpURLConnection.outputStream)
                val jsonObject = JSONObject()
                val map = params.getParams()
                map?.forEach {
                    jsonObject.put(it.key, it.value)
                }
                dataOutputStream.write(jsonObject.toString().toByteArray(charset(CHARSET)))

//                if (map != null && map.isNotEmpty()) {
//                    for ((key, value) in map) {
//                        jsonObject.put(key, value)
//                    }
//                    dataOutputStream.write(jsonObject.toString().toByteArray(charset(CHARSET)))
//                }
                dataOutputStream.apply {
                    flush()
                    close()
                }
                //            } else
                //                Logger.warn(TAG, "No request params for HTTP POST");
            }
        }
        return httpURLConnection
    }

    @Throws(Exception::class)
    private fun addMultipartEntity(item: HttpRequestItem, connection: HttpURLConnection) {
        var outputStream: DataOutputStream? = null

        val twoHyphens = "--"
        val boundary = "*****"
        val lineEnd = "\r\n"

        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 2 * 1024 * 1024

        val file = File(item.getParams()!!["path"].toString())
        Log.e("File", "" + file.length() / 1024)
        BitmapUtils.compressBitmap(file)
        Log.e("File", "" + file.length() / 1024)
        val fileInputStream = FileInputStream(file)

        with(connection) {
            connectTimeout = HTTP_MULTIPART_TIMEOUT
            readTimeout = HTTP_MULTIPART_TIMEOUT
            doOutput = true
            useCaches = false

            useCaches = false
            setRequestProperty("Connection", "Keep-Alive")
            setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        }

        outputStream = DataOutputStream(connection.outputStream)

        with(outputStream) {
            writeBytes(twoHyphens + boundary + lineEnd)
            writeBytes("Content-Disposition: form-data; name=\"" + "image" + "\"; filename=\"" + file.getAbsolutePath() + "\"" + lineEnd)
            writeBytes("Content-Type: image/jpeg$lineEnd")
            writeBytes("Content-Transfer-Encoding: binary$lineEnd")
            writeBytes(lineEnd)
        }

        bytesAvailable = fileInputStream.available()
        bufferSize = Math.min(bytesAvailable, maxBufferSize)
        buffer = ByteArray(bufferSize)

        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        outputStream.apply {
            writeBytes(lineEnd)
            writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        }

        fileInputStream.close()

        outputStream.apply {
            flush()
            close()
        }
    }

    private fun disconnectHttpURLConnection(connection: HttpURLConnection?) {
        try {
            connection?.disconnect()
            Logger.info("disconnectHttpURLConnection", "HttpURLConnection is disconnected")
        } catch (e: Exception) {
            Logger.error(true, e)
        }

    }

    private fun isGetRequest(params: HttpRequestItem): Boolean {
        return params.getHttpRequestType() == HTTP_GET
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getQueryParams(map: Map<String, Any>?, charset: String): String? {
        if (map != null && map.isNotEmpty()) {
            var params = StringBuilder()
            for ((key, value) in map)

                params.append(
                    String.format(
                        "%s=%s&", key,
                        //                        entry.getValue().toString()));
                        URLEncoder.encode(value.toString(), charset)
                    )
                )
            params = StringBuilder(params.substring(0, params.lastIndexOf("&")))
            return params.toString()
        } else
            return null



    }


    @Throws(IOException::class)
    private fun convertStream(stream: InputStream?): String? {
        var line: String? = null
        stream?.let {
            // var bufferedReader: BufferedReader? = null

            val builder = StringBuilder()
            try {
//                bufferedReader = BufferedReader(InputStreamReader(stream, "UTF-8"))
//                while ( bufferedReader.readLine() != null) {
//                    builder.append(bufferedReader.readLine()).append("\n")
//                }
                line = it.run {
                    bufferedReader().use(BufferedReader::readText)
                }
            } finally {
                try {
                    it.close()
                } catch (e: IOException) {
                    Logger.error(false, e)
                }

//                if (bufferedReader != null) {
//                    try {
//                        bufferedReader.close()
//                    } catch (e: IOException) {
//                        Logger.error(false, e)
//                    }
//
//                }
            }

        }
        return line
    }

    @Throws(UnsupportedEncodingException::class)
    private fun setHeaderParams(connection: HttpURLConnection, map: Map<String, String>?) {
        if (map != null && map.isNotEmpty())
            for ((key, value) in map)
                connection.addRequestProperty(key, value)
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun hasNetworkConnection(context: Context?, includeMobile: Boolean): Boolean {
        if (context == null) return false
        val manager =
            context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        activeNetwork?.let {
            if (it.type == ConnectivityManager.TYPE_WIFI)
                return it.isConnected
            if (includeMobile && it.type == ConnectivityManager.TYPE_MOBILE)
                return it.isConnected
        }
//        if (activeNetwork != null) {
//            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
//                return activeNetwork.isConnected
//            if (includeMobile && activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
//                return activeNetwork.isConnected
//        }
        return false
    }

    private fun getResponseMessage(data: String?, responseCode: Int): String {
        try {
            val json = JSONObject().apply {
                put("data", data)
                put("response_code", responseCode)
            }
            return json.toString()
        } catch (e: JSONException) {
            Logger.error(false, e)
        }

        return ""
    }

}