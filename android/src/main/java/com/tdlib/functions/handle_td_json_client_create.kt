package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.JsonClient
import android.util.Log

fun TdlibModule.handle_td_json_client_create(promise: Promise) {
    try {
        if (jsonClientId == null) {
            // set log message handler to handle only fatal errors (0) and plain log messages (-1)
            JsonClient.setLogMessageHandler(-1, object : JsonClient.LogMessageHandler {
                override fun onLogMessage(verbosityLevel: Int, message: String) {
                    Log.d("TDLib", "Log message: $message")
                }
            })

            jsonClientId = JsonClient.createClientId()

            // send first request to activate the client
            JsonClient.send(jsonClientId!!, "{\"@type\":\"getOption\",\"name\":\"version\"}");

            promise.resolve("TDLib jsonClient created")
        } else {
            promise.resolve("TDLib jsonClient already exists")
        }
    } catch (e: Exception) {
        promise.reject("CREATE_CLIENT_ERROR", e.message)
    }
}

