package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleVerifyPhoneNumber(code: String, promise: Promise) {
    try {
        val checkCode = TdApi.CheckAuthenticationCode().apply {
            this.code = code
        }

        client?.send(checkCode) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("VERIFY_PHONE_NUMBER_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("VERIFY_PHONE_EXCEPTION", e.message)
    }
}
