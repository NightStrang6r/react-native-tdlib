package com.tdlib

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray

import org.drinkless.tdlib.Client
import org.drinkless.tdlib.JsonClient
import org.drinkless.tdlib.TdApi
import com.google.gson.Gson
import java.util.Map
import java.util.HashMap
import org.json.JSONObject
import org.json.JSONException
import android.util.Log
import java.lang.reflect.Constructor

import com.tdlib.functions.*

@ReactModule(name = TdlibModule.NAME)
class TdlibModule(reactContext: ReactApplicationContext) : NativeTdlibSpec(reactContext) {
    internal var TAG = "TdLibModule"
    internal var client: Client? = null
    internal var jsonClientId: Int? = null
    internal var gson = Gson()
    internal val subscribedEventTypes = mutableSetOf<String>()

    companion object {
        const val NAME = "Tdlib"
    }

    override fun getName(): String {
        return NAME
    }

    override fun td_json_client_create(promise: Promise) {
        handle_td_json_client_create(promise)
    }

    override fun td_json_client_execute(request: String, promise: Promise) {
        td_json_client_execute(request, promise)
    }

    override fun td_json_client_send(request: String, promise: Promise) {
        td_json_client_send(request, promise)
    }

    override fun td_json_client_receive(timeout: Double?, promise: Promise) {
        handle_td_json_client_receive(timeout, promise)
    }

    override fun subscribeToEvents(types: ReadableArray) {
        if (types == null) {
            return
        }

        for (i in 0 until types.size()) {
            val type = types.getString(i)
            if (type != null) subscribedEventTypes.add(type)
        }
    }

    override fun unsubscribeFromEvents(types: ReadableArray?) {
        if (types == null) {
            subscribedEventTypes.clear()
            return
        }

        for (i in 0 until types.size()) {
            val type = types.getString(i)
            if (type != null) subscribedEventTypes.remove(type)
        }
    }

    private fun sendEvent(eventTag: String, eventName: String, payload: TdApi.Object) {
        if (!subscribedEventTypes.contains("tdlibGlobalUpdate") && !subscribedEventTypes.contains(eventName)) {
            return
        }

        val json = JSONObject(gson.toJson(payload))
        try {
            json.put("@type", eventName)
        } catch (e: JSONException) {
            Log.e(TAG, "Error adding @type to JSON object", e)
        }
        val jsonString = json.toString()

        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventTag, jsonString)
    }    

    // ==================== High-Level API ====================
    override fun startTdLib(parameters: ReadableMap, promise: Promise) {
        try {
            if (client != null) {
                promise.resolve("TDLib already started")
                return
            }

            client = Client.create(
                { obj ->
                    //Log.d(TAG, "Global Update: $obj")
                    var eventName = obj.javaClass.simpleName ?: "Unknown"
                    eventName = eventName.replaceFirstChar { it.lowercase() }
                    sendEvent("tdlibGlobalUpdate", eventName, obj)
                },
                null,
                null
            )

            Client.execute(TdApi.SetLogVerbosityLevel(0))
            setTdLibParameters(reactApplicationContext, parameters, promise)
        } catch (e: Exception) {
            promise.reject("TDLIB_START_ERROR", e.message)
        }
    }

    override fun getAuthorizationState(promise: Promise) {
        handleGetAuthorizationState(promise)
    }

    override fun login(phoneNumber: String, promise: Promise) {
        handleLogin(phoneNumber, promise)
    }

    override fun verifyPhoneNumber(code: String, promise: Promise) {
        handleVerifyPhoneNumber(code, promise)
    }

    override fun verifyPassword(password: String, promise: Promise) {
        handleVerifyPassword(password, promise)
    }

    override fun getProfile(promise: Promise) {
        handleGetProfile(promise)
    }

    override fun sendMessage(chatId: Double, message: String, file: String?, promise: Promise) {
        handleSendMessage(reactApplicationContext, chatId, message, file, promise)
    }

    override fun downloadFile(fileId: Double, priority: Double?, offset: Double?, limit: Double?, synchronous: Boolean?, promise: Promise) {
        handleDownloadFile(fileId, priority, offset, limit, synchronous, promise)
    }

    override fun createNewSupergroupChat(
        title: String,
        isForum: Boolean?,
        isChannel: Boolean?,
        description: String?,
        location: ReadableMap?,
        messageAutoDeleteTime: Double?,
        forImport: Boolean?,
        promise: Promise
    ) {
        handleCreateNewSupergroupChat(
            title,
            isForum,
            isChannel,
            description,
            location,
            messageAutoDeleteTime,
            forImport,
            promise
        )
    }

    override fun getChats(limit: Double, promise: Promise) {
        handleGetChats(limit, promise)
    }

    override fun getChat(chatId: Double, promise: Promise) {
        handleGetChat(chatId, promise)
    }

    override fun getChatHistory(chatId: Double, fromMessageId: Double?, offset: Double?, limit: Double?, onlyLocal: Boolean?, promise: Promise) {
        handleGetChatHistory(chatId, fromMessageId, offset, limit, onlyLocal, promise)
    }

    override fun logout(promise: Promise) {
        handleLogout(promise)
    }

    override fun destroy(promise: Promise) {
        handleDestroy(promise)
    }

    // ==================== Helpers ====================
    private fun setTdLibParameters(reactApplicationContext: ReactApplicationContext, parameters: ReadableMap, promise: Promise) {
        handleSetTdLibParameters(reactApplicationContext, parameters, promise)
    }
}