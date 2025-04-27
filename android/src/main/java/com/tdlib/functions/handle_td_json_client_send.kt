package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.JsonClient
import java.util.HashMap

fun TdlibModule.td_json_client_send(request: String, promise: Promise) {
    try {
        if (jsonClientId == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib jsonClient is not initialized")
            return
        }

        JsonClient.send(jsonClientId!!, request)
        promise.resolve("OK")
    } catch (e: Exception) {
        promise.reject("SEND_EXCEPTION", e.message)
    }
}