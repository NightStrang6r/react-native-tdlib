package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleLogin(phoneNumber: String, promise: Promise) {
    try {
        val authPhoneNumber = TdApi.SetAuthenticationPhoneNumber().apply {
            this.phoneNumber = phoneNumber
            this.settings = null
        }

        client?.send(authPhoneNumber) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("LOGIN_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("LOGIN_EXCEPTION", e.message)
    }
}