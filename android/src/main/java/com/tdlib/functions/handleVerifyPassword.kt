package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleVerifyPassword(password: String, promise: Promise) {
    try {
        val checkPassword = TdApi.CheckAuthenticationPassword().apply {
            this.password = password
        }

        client?.send(checkPassword) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("PASSWORD_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("PASSWORD_EXCEPTION", e.message)
    }
}