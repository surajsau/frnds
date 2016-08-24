package com.halfplatepoha.frnds;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public interface SoundCloudClient {

    @GET("tracks/{id}")
    Observable<TrackResponse> getTrackDetails(@Path("id") String id);
}
