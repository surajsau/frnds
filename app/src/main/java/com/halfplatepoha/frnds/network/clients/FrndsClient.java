package com.halfplatepoha.frnds.network.clients;

import com.halfplatepoha.frnds.network.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.network.models.request.RegisterRequest;
import com.halfplatepoha.frnds.network.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.network.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.network.models.response.RegisterResponse;
import com.halfplatepoha.frnds.network.models.response.UpdateTrackResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public interface FrndsClient {

    @POST("v0/registerGCM")
    Observable<RegisterGCMResponse> updateGCM(@Body RegisterGCMRequest request);

    @POST("v0/register")
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @POST("v0/updateTrack")
    Observable<UpdateTrackResponse> updateTrack(@Body UpdateTrackRequest request);

}
