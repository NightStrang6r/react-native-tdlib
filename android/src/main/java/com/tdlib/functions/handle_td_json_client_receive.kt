package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import org.drinkless.tdlib.TdApi
import org.drinkless.tdlib.JsonClient
import java.util.concurrent.TimeUnit

fun TdlibModule.handle_td_json_client_receive(timeout: Double?, promise: Promise) {
    try {
        if (jsonClientId == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib jsonClient is not initialized")
            return
        }

        val response = JsonClient.receive(timeout ?: 10.0)

        if (response != null) {
            promise.resolve(gson.toJson(response))
        } else {
            promise.reject("RECEIVE_ERROR", "No response from TDLib")
        }
    } catch (e: Exception) {
        promise.reject("RECEIVE_EXCEPTION", e.message)
    }
}