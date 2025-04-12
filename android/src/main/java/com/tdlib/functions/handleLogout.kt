package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleLogout(promise: Promise) {
    try {
        client?.send(TdApi.LogOut()) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve("Logout successful")
                is TdApi.Error -> promise.reject("LOGOUT_ERROR", obj.message)
            }
        }

        client = null
    } catch (e: Exception) {
        promise.reject("LOGOUT_EXCEPTION", e.message)
    }
}