package com.halfplatepoha.frnds.network.clients;

import com.halfplatepoha.frnds.network.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.network.models.response.RegisterGCMResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public interface FrndsClient {

    @POST("registerGCM")
    Observable<RegisterGCMResponse> updateGCM(@Body RegisterGCMRequest request);

}
