package com.shanan.lufthansa.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.shanan.lufthansa.BuildConfig
import com.shanan.lufthansa.model.Airport
import com.shanan.lufthansa.model.AirportsResponse
import com.shanan.lufthansa.model.AuthResponse
import com.shanan.lufthansa.model.Names
import com.shanan.lufthansa.utils.Constants
import com.shanan.lufthansa.utils.Constants.BEARER
import com.shanan.lufthansa.utils.NamesClassJsonDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


private const val TAG = "LufthansaService"
private const val IN_QUALIFIER = "in:name,description"

/**
 * Get a list of airports.
 * Trigger a request to the Lufthansa open api with the following params:
 * @param token authToken
 * @param offset Number of records skipped
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of airports received
 * @param onError function that defines how to handle request failure
 */
fun getAirports(
        service: LufthansaService,
        token: String,
        offset: Int,
        limit: Int,
        lang: String,
        onSuccess: (airports: List<Airport>, totalCount: Int) -> Unit,
        onError: (error: String) -> Unit) {
    Log.d(TAG, "query: $token, page: $offset, itemsPerPage: $limit")

//    val apiQuery = token + IN_QUALIFIER

    service.getAirports(BEARER.plus(token), offset, limit, lang).enqueue(
            object : Callback<AirportsResponse> {
                override fun onResponse(call: Call<AirportsResponse>, response: Response<AirportsResponse>) {
                    if (response.isSuccessful) {
                        val airports = response.body()?.airportResource?.airports?.airport
                                ?: emptyList()
                        onSuccess(airports, response.body()?.airportResource?.meta?.totalCount
                                ?: offset+limit)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<AirportsResponse>, t: Throwable) {
                    onError(t.message ?: "unknown error")
                }
            }
    )
}

fun requestAccessToken(
        service: LufthansaService,
        onSuccess: (auth: AuthResponse?) -> Unit,
        onError: (error: String) -> Unit) {

    service.authenticate(BuildConfig.LH_CLIENT_ID,
            BuildConfig.LH_CLIENT_SECRET,
            Constants.LH_GRANT_TYPE).enqueue(
            object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        onSuccess(authResponse)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.d(TAG, "got a response ${t.message}")
                    onError(t.message ?: "unknown error")
                }
            }
    )
}

/**
 * Lufthansa API communication setup via Retrofit.
 */
interface LufthansaService {
    /**
     * Get access token
     */
    @FormUrlEncoded
    @POST("oauth/token")
    fun authenticate(@Field("client_id") clientId: String,
                     @Field("client_secret") clientSecret: String,
                     @Field("grant_type") grantType: String):
            Call<AuthResponse>

    /**
     * Get a list of airports
     */
    @GET("references/airports")
    fun getAirports(@Header(Constants.AUTHORIZATION_HEADER) token: String,
                    @Query("offset") offset: Int,
                    @Query("limit") limit: Int,
                    @Query("limit") lang: String):
            Call<AirportsResponse>


    companion object {

        fun create(): LufthansaService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BODY

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()

            val gson = GsonBuilder()
                    .registerTypeAdapter(Names::class.java, NamesClassJsonDeserializer())
                    .setLenient()
                    .create()


            return Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(LufthansaService::class.java)
        }
    }
}