package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleDestroy(promise: Promise) {
    try {
        client?.send(TdApi.Destroy()) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("DESTROY_ERROR", gson.toJson(obj))
            }
        }

        client = null
    } catch (e: Exception) {
        promise.reject("DESTROY_EXCEPTION", e.message)
    }
}