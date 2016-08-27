package com.halfplatepoha.frnds.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IPrefConstants;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class DeviceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        FrndsLog.i("Refresh token: " +  refreshToken);

        FrndsPreference.setInPref(IPrefConstants.FCM_REFRESH_TOKEN, refreshToken);
    }
}
