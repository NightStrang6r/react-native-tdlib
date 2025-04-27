package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleDownloadFile(fileId: Double, priority: Double?, offset: Double?, limit: Double?, synchronous: Boolean?, promise: Promise) {
    try {
        val request = TdApi.DownloadFile().apply {
            this.fileId = fileId.toInt()
            this.priority = priority?.toInt() ?: 1
            this.offset = offset?.toLong() ?: 0
            this.limit = limit?.toLong() ?: 0
            this.synchronous = synchronous ?: false
        }

        client?.send(request) { obj ->
            when (obj) {
                is TdApi.File -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("EXCEPTION", e.message)
    }
}