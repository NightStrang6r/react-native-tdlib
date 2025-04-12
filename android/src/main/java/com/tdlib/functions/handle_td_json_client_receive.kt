package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import org.drinkless.tdlib.TdApi
import java.util.concurrent.TimeUnit

fun TdlibModule.handle_td_json_client_receive(promise: Promise) {
    try {
        if (client == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
            return
        }

        val latch = CountDownLatch(1)
        val responseRef = AtomicReference<TdApi.Object?>()

        client?.send(null) { obj ->
            responseRef.set(obj)
            latch.countDown()
        }

        val awaitSuccess = latch.await(10, TimeUnit.SECONDS)
        if (awaitSuccess && responseRef.get() != null) {
            promise.resolve(gson.toJson(responseRef.get()))
        } else {
            promise.reject("RECEIVE_ERROR", "No response from TDLib")
        }
    } catch (e: Exception) {
        promise.reject("RECEIVE_EXCEPTION", e.message)
    }
}