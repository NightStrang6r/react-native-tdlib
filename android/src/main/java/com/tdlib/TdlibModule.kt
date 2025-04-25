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

    /*override fun td_json_client_execute(request: ReadableMap, promise: Promise) {
        td_json_client_execute(request, promise)
    }

    override fun td_json_client_send(request: ReadableMap, promise: Promise) {
        td_json_client_send(request, promise)
    }*/

    override fun td_json_client_receive(promise: Promise) {
        handle_td_json_client_receive(promise)
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

    override fun login(userDetails: ReadableMap, promise: Promise) {
        handleLogin(userDetails, promise)
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

    // ==================== Helpers ====================
    /*@Suppress("UNCHECKED_CAST")
    private fun convertMapToFunction(requestMap: Map<String, Any>): TdApi.Function {
        val type = requestMap["@type"] as? String
            ?: throw IllegalArgumentException("Missing @type in request")

        val className = "org.drinkless.tdlib.TdApi\$" + type.replaceFirstChar { it.uppercaseChar() }

        val clazz = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedOperationException("Unsupported @type: $type (class $className not found)")
        }

        if (!TdApi.Function::class.java.isAssignableFrom(clazz)) {
            throw UnsupportedOperationException("Class $className is not a TdApi.Function")
        }

        // Попробуем найти конструктор с нужными параметрами и установить поля вручную
        val instance = clazz.getDeclaredConstructor().newInstance()

        requestMap.forEach { (key, value) ->
            if (key == "@type") return@forEach

            try {
                val field = clazz.getField(key)
                val convertedValue = convertValue(value, field.type)
                field.set(instance, convertedValue)
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to set field '$key' on $className: ${e.message}", e)
            }
        }

        return instance as TdApi.Function
    }

    private fun convertValue(value: Any, targetType: Class<*>): Any? {
        return when {
            targetType.isAssignableFrom(value::class.java) -> value
            targetType == String::class.java -> value.toString()
            targetType == Boolean::class.java || targetType == java.lang.Boolean::class.java -> value as Boolean
            targetType == Int::class.java || targetType == java.lang.Integer::class.java -> (value as Number).toInt()
            targetType == Long::class.java || targetType == java.lang.Long::class.java -> (value as Number).toLong()
            targetType == Double::class.java || targetType == java.lang.Double::class.java -> (value as Number).toDouble()
            targetType.isArray && value is List<*> -> {
                val componentType = targetType.componentType
                java.lang.reflect.Array.newInstance(componentType, value.size).also { array ->
                    value.forEachIndexed { i, v ->
                        java.lang.reflect.Array.set(array, i, convertValue(v!!, componentType))
                    }
                }
            }
            else -> throw IllegalArgumentException("Unsupported field type: ${targetType.name} for value $value")
        }
    }*/
}