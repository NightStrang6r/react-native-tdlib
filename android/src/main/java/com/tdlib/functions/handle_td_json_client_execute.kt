package com.tdlib.functions

/*import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.Client
import java.util.HashMap
import org.drinkless.tdlib.TdApi

fun TdlibModule.td_json_client_execute(request: ReadableMap, promise: Promise) {
    try {
        if (client == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
            return
        }

        val requestMap: Map<String, Object> = request.toHashMap()
        val function: TdApi.Function = convertMapToFunction(requestMap)

        val response: TdApi.Object? = Client.execute(function)
        if (response != null) {
            promise.resolve(gson.toJson(response))
        } else {
            promise.reject("EXECUTE_ERROR", "No response from TDLib")
        }
    } catch (e: Exception) {
        promise.reject("EXECUTE_EXCEPTION", e.message)
    }
}*/