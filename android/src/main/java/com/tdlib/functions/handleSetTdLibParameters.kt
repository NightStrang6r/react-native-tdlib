package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.TdApi
import com.facebook.react.bridge.ReactApplicationContext

import android.util.Log

fun TdlibModule.handleSetTdLibParameters(reactApplicationContext: ReactApplicationContext, parameters: ReadableMap, promise: Promise) {
    try {
        val tdlibParameters = TdApi.SetTdlibParameters().apply {
            databaseDirectory = "${reactApplicationContext.filesDir.absolutePath}/tdlib"
            filesDirectory = "${reactApplicationContext.filesDir.absolutePath}/tdlib"
            useChatInfoDatabase = true
            useMessageDatabase = true
            useFileDatabase = true
            useSecretChats = false
            apiId = parameters.getInt("api_id")
            apiHash = parameters.getString("api_hash")
            systemLanguageCode = parameters.getString("system_language_code") ?: "en"
            deviceModel = parameters.getString("device_model") ?: "React Native"
            systemVersion = parameters.getString("system_version") ?: "1.0"
            applicationVersion = parameters.getString("application_version") ?: "1.0"
        }

        client?.send(tdlibParameters) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("TDLIB_PARAMS_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("TDLIB_PARAMS_EXCEPTION", e.message)
    }
}