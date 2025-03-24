package com.tdlib

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import com.google.gson.Gson
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.Map
import java.util.HashMap
import org.json.JSONObject
import org.json.JSONException
import android.util.Log

@ReactModule(name = TdlibModule.NAME)
class TdlibModule(reactContext: ReactApplicationContext) : NativeTdlibSpec(reactContext) {
  private var TAG = "TdLibModule"
  private var client: Client? = null
  private var gson = Gson()

  companion object {
    const val NAME = "Tdlib"
  }

  override fun getName(): String {
    return NAME
  }

  override fun td_json_client_create(promise: Promise) {
    try {
        if (client == null) {
            client = Client.create(
                { obj -> Log.d(TAG, "Global Update: ${gson.toJson(obj)}") },
                null,
                null
            )
            promise.resolve("TDLib client created")
        } else {
            promise.resolve("TDLib client already exists")
        }
    } catch (e: Exception) {
        promise.reject("CREATE_CLIENT_ERROR", e.message)
    }
  }

  /*override fun td_json_client_execute(request: ReadableMap, promise: Promise) {
    try {
        if (client == null) {
          promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
          return
        }

        val requestMap: Map<String, Object> = request.toHashMap()
        val function: TdApi.Function = convertMapToFunction(requestMap)

        val response: TdApi.Object? = Client.execute(function)
        if (response != null) {
          promise.resolve(gson.toJson(response))
        } else {
          promise.reject("EXECUTE_ERROR", "No response from TDLib")
        }
    } catch (e: Exception) {
      promise.reject("EXECUTE_EXCEPTION", e.message)
    }
  }*/

  /*override fun td_json_client_send(request: ReadableMap, promise: Promise) {
      try {
          if (client == null) {
              promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
              return
          }

          val requestMap = request.toHashMap()
          val function = convertMapToFunction(requestMap)

          client?.send(function) { obj ->
              promise.resolve(gson.toJson(obj))
          }
      } catch (e: Exception) {
          promise.reject("SEND_EXCEPTION", e.message)
      }
  }*/

  /*override fun td_json_client_receive(promise: Promise) {
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
  }*/


  // ==================== High-Level API ====================
  override fun startTdLib(parameters: ReadableMap, promise: Promise) {
    try {
        if (client != null) {
            promise.resolve("TDLib already started")
            return
        }

        client = Client.create(
            { obj -> Log.d(TAG, "Global Update: $obj") },
            null,
            null
        )

        Client.execute(TdApi.SetLogVerbosityLevel(0))
        setTdLibParameters(parameters, promise)
    } catch (e: Exception) {
        promise.reject("TDLIB_START_ERROR", e.message)
    }
  }

  override fun getAuthorizationState(promise: Promise) {
    try {
        if (client == null) {
            promise.reject("CLIENT_NOT_INITIALIZED", "TDLib client is not initialized")
            return
        }

        client?.send(TdApi.GetAuthorizationState()) { obj ->
            when (obj) {
                is TdApi.AuthorizationState -> {
                    try {
                        val originalType = obj::class.simpleName ?: "Unknown"
                        val formattedType = originalType.replaceFirstChar { it.lowercase() }
                        val responseMap = mapOf("@type" to formattedType)

                        promise.resolve(JSONObject(responseMap).toString())
                    } catch (e: Exception) {
                        promise.reject("JSON_CONVERT_ERROR", "Error converting object to JSON: ${e.message}")
                    }
                }
                is TdApi.Error -> promise.reject("AUTH_STATE_ERROR", obj.message)
                else -> promise.reject("AUTH_STATE_UNEXPECTED_RESPONSE", "Unexpected response from TDLib.")
            }
        }
    } catch (e: Exception) {
        promise.reject("GET_AUTH_STATE_EXCEPTION", e.message)
    }
  }

  override fun login(userDetails: ReadableMap, promise: Promise) {
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

  override fun verifyPhoneNumber(code: String, promise: Promise) {
      try {
          val checkCode = TdApi.CheckAuthenticationCode().apply {
              this.code = code
          }

          client?.send(checkCode) { obj ->
              when (obj) {
                  is TdApi.Ok -> promise.resolve("Verification successful")
                  is TdApi.Error -> promise.reject("VERIFY_PHONE_NUMBER_ERROR", obj.message)
              }
          }
      } catch (e: Exception) {
          promise.reject("VERIFY_PHONE_EXCEPTION", e.message)
      }
  }

  override fun verifyPassword(password: String, promise: Promise) {
      try {
          val checkPassword = TdApi.CheckAuthenticationPassword().apply {
              this.password = password
          }

          client?.send(checkPassword) { obj ->
              when (obj) {
                  is TdApi.Ok -> promise.resolve("Password verification successful")
                  is TdApi.Error -> promise.reject("PASSWORD_ERROR", obj.message)
              }
          }
      } catch (e: Exception) {
          promise.reject("PASSWORD_EXCEPTION", e.message)
      }
  }

  override fun logout(promise: Promise) {
      try {
          client?.send(TdApi.LogOut()) { obj ->
              when (obj) {
                  is TdApi.Ok -> promise.resolve("Logout successful")
                  is TdApi.Error -> promise.reject("LOGOUT_ERROR", obj.message)
              }
          }
      } catch (e: Exception) {
          promise.reject("LOGOUT_EXCEPTION", e.message)
      }
  }

  override fun getProfile(promise: Promise) {
      try {
          client?.send(TdApi.GetMe()) { obj ->
              when (obj) {
                  is TdApi.User -> promise.resolve(Gson().toJson(obj))
                  is TdApi.Error -> promise.reject("GET_PROFILE_ERROR", obj.message)
              }
          }
      } catch (e: Exception) {
          promise.reject("GET_PROFILE_EXCEPTION", e.message)
      }
  }

  // ==================== Helpers ====================
  private fun setTdLibParameters(parameters: ReadableMap, promise: Promise) {
    try {
        val tdlibParameters = TdApi.SetTdlibParameters().apply {
            databaseDirectory = "${reactApplicationContext.filesDir.absolutePath}/tdlib"
            useMessageDatabase = true
            useSecretChats = true
            apiId = parameters.getInt("api_id")
            apiHash = parameters.getString("api_hash")
            systemLanguageCode = parameters.getString("system_language_code") ?: "en"
            deviceModel = parameters.getString("device_model") ?: "React Native"
            systemVersion = parameters.getString("system_version") ?: "1.0"
            applicationVersion = parameters.getString("application_version") ?: "1.0"
            useFileDatabase = true
        }

        client?.send(tdlibParameters) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve("TDLib parameters set successfully")
                is TdApi.Error -> promise.reject("TDLIB_PARAMS_ERROR", obj.message)
            }
        }
    } catch (e: Exception) {
        promise.reject("TDLIB_PARAMS_EXCEPTION", e.message)
    }
  }

  // ==================== Helpers ====================
  /*private fun convertMapToFunction(requestMap: Map<String, Any>): TdApi.Function {
    // TODO: Implement conversion logic based on TdApi request types
    throw UnsupportedOperationException("Conversion not implemented")
  }*/
}