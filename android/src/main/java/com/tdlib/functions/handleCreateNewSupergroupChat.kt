package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleCreateNewSupergroupChat(
    title: String,
    isForum: Boolean?,
    isChannel: Boolean?,
    description: String?,
    location: ReadableMap?,
    messageAutoDeleteTime: Double?,
    forImport: Boolean?,
    promise: Promise
) {
    try {
        val createNewSupergroupChat = TdApi.CreateNewSupergroupChat().apply {
            this.title = title
            this.isForum = isForum ?: false
            this.isChannel = isChannel ?: false
            this.description = description ?: ""
            this.location = if (location != null) {
                TdApi.ChatLocation().apply {
                    this.location = TdApi.Location().apply {
                        this.latitude = location.getDouble("latitude")
                        this.longitude = location.getDouble("longitude")
                    }
                    this.address = location.getString("address") ?: ""
                }
            } else null
            this.messageAutoDeleteTime = messageAutoDeleteTime?.toInt() ?: 0
            this.forImport = forImport ?: false
        }

        client?.send(createNewSupergroupChat) { obj ->
            when (obj) {
                is TdApi.Chat -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("CREATE_NEW_SUPERGROUP_CHAT_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("CREATE_NEW_SUPERGROUP_CHAT_EXCEPTION", e.message)
    }
}