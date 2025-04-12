package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.Client
import android.util.Log

fun TdlibModule.handle_td_json_client_create(promise: Promise) {
    try {
        if (client == null) {
            client = Client.create(
                { obj -> Log.d(TAG, "Global Update: ${gson.toJson(obj)}") },
                null,
                null
            )
            promise.resolve("TDLib client created")
        } else {
            promise.resolve("TDLib client already exists")
        }
    } catch (e: Exception) {
        promise.reject("CREATE_CLIENT_ERROR", e.message)
    }
}