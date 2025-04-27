package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.JsonClient
import java.util.HashMap
import org.drinkless.tdlib.TdApi

fun TdlibModule.td_json_client_execute(request: String, promise: Promise) {
    try {
        if (jsonClientId == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib jsonClient is not initialized")
            return
        }

        JsonClient.execute(request)
        promise.resolve("OK")
    } catch (e: Exception) {
        promise.reject("EXECUTE_EXCEPTION", e.message)
    }
}