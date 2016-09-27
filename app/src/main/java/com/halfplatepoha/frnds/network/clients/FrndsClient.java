package com.halfplatepoha.frnds.network.clients;

import com.halfplatepoha.frnds.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.models.request.RegisterRequest;
import com.halfplatepoha.frnds.models.request.SendMessageRequest;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.models.response.RegisterResponse;
import com.halfplatepoha.frnds.models.response.UpdateTrackResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Observable<Void> updateTrack(@Body UpdateTrackRequest request);

    @POST("v0/sendMessage")
    Observable<Void> sendMessage(@Body SendMessageRequest request);

}
