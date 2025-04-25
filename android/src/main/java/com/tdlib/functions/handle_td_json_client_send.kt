/*package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.Client
import java.util.HashMap

fun TdlibModule.td_json_client_send(request: ReadableMap, promise: Promise) {
    try {
        if (client == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
            return
        }

        val requestMap = request.toHashMap()
        val function = convertMapToFunction(requestMap)

        client?.send(function) { obj ->
            promise.resolve(gson.toJson(obj))
        }
    } catch (e: Exception) {
        promise.reject("SEND_EXCEPTION", e.message)
    }
}*/