package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleLogin(userDetails: ReadableMap, promise: Promise) {
    try {
        val authPhoneNumber = TdApi.SetAuthenticationPhoneNumber().apply {
            phoneNumber = userDetails.getString("countrycode") + userDetails.getString("phoneNumber")
        }

        client?.send(authPhoneNumber) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve("Phone number set successfully")
                is TdApi.Error -> promise.reject("LOGIN_ERROR", obj.message)
            }
        }
    } catch (e: Exception) {
        promise.reject("LOGIN_EXCEPTION", e.message)
    }
}