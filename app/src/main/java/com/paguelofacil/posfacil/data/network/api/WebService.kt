package com.paguelofacil.posfacil.data.network.api

import retrofit2.Response
import retrofit2.http.*


/**
 * Rest API service used with Retrofit
 *
 * @constructor Create empty Rest a p i
 */
interface WebService {

    /**
     * Get method service
     *
     * @param url
     * @param queryMap params to be passed as url-encoded
     * @return
     */
    @GET("{url}")
    suspend fun get(
        @Path("url", encoded = true) url: String,
        @QueryMap queryMap: HashMap<String, Any> = HashMap(),
    ): Response<BaseResponse<Any>>

    /**
     * Post method service
     *
     * @param url
     * @param body key value params to be passed in the body
     * @return
     */
    @POST("{url}")
    suspend fun post(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>,
    ): Response<BaseResponse<Any>>

    /**
     * Put method service
     *
     * @param url
     * @param body key value params to be passed in the body
     * @return
     */
    @PUT("{url}")
    suspend fun put(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>,
    ): Response<BaseResponse<Any>>

    /**
     * Patch method
     *
     * @param url
     * @param body key value params to be passed in the body
     * @return
     */
    @PATCH("{url}")
    suspend fun patch(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>,
    ): Response<BaseResponse<Any>>

    /**
     * Delete method
     *
     * @param url
     * @param body key value params to be passed in the body
     * @return
     */
    @DELETE("{url}")
    suspend fun delete(
        @Path("url", encoded = true) url: String,
        @QueryMap body: HashMap<String, Any>,
    ): Response<BaseResponse<Any>>

}