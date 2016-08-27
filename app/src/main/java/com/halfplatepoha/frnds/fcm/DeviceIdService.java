package com.halfplatepoha.frnds.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.halfplatepoha.frnds.FrndsLog;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class DeviceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        FrndsLog.i("Refresh token: " +  refreshToken);
    }

    private void sendRegistrationToServer(String token) {

    }
}
