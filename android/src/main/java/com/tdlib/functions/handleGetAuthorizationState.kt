package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi
import org.json.JSONObject

fun TdlibModule.handleGetAuthorizationState(promise: Promise) {
    try {
        if (client == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
            return
        }

        client?.send(TdApi.GetAuthorizationState()) { obj ->
            when (obj) {
                is TdApi.AuthorizationState -> {
                    try {
                        val originalType = obj::class.simpleName ?: "Unknown"
                        val formattedType = originalType.replaceFirstChar { it.lowercase() }
                        val responseMap = mapOf("@type" to formattedType)

                        promise.resolve(JSONObject(responseMap).toString())
                    } catch (e: Exception) {
                        promise.reject("JSON_CONVERT_ERROR", "Error converting object to JSON: ${e.message}")
                    }
                }
                is TdApi.Error -> promise.reject("AUTH_STATE_ERROR", obj.message)
                else -> promise.reject("AUTH_STATE_UNEXPECTED_RESPONSE", "Unexpected response from TDLib.")
            }
        }
    } catch (e: Exception) {
        promise.reject("GET_AUTH_STATE_EXCEPTION", e.message)
    }
}