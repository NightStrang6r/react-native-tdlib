package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi
import com.google.gson.Gson

fun TdlibModule.handleGetProfile(promise: Promise) {
    try {
        client?.send(TdApi.GetMe()) { obj ->
            when (obj) {
                is TdApi.User -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("GET_PROFILE_ERROR", obj.message)
            }
        }
    } catch (e: Exception) {
        promise.reject("GET_PROFILE_EXCEPTION", e.message)
    }
}