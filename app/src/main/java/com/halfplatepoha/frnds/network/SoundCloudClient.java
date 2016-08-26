package com.halfplatepoha.frnds.network;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public interface SoundCloudClient {

    @GET("tracks/{id}")
    Observable<TrackResponse> getTrackDetails(@Path("id") String id);

    @GET("tracks")
    Observable<List<TrackDetails>> getSearchResult(@Query("q") String searchString);
}
