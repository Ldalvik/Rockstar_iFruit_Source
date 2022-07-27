package com.rockstargames.hal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes.dex */
public class andFacebook {
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private static String applicationId;
    private static andFacebook instance;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    static boolean test = true;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private boolean pendingPublishReauthorization = false;

    native void facebookClosed();

    native void facebookOpenedSucessfully(String str);

    native void facebookPostFailed();

    native void facebookPostSuccess();

    native void facebookPublishPermissionGranted();

    native void facebookPublishPermissionRefused();

    public void onCreate() {
    }

    public static andFacebook getInstance() {
        if (instance == null) {
            instance = new andFacebook();
        }
        return instance;
    }

    public void onStop() {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.isOpened()) {
            return;
        }
        closeSession();
    }

    public static boolean isSessionOpen() {
        Session activeSession = Session.getActiveSession();
        return activeSession != null && activeSession.isOpened() && !activeSession.isClosed();
    }

    public static boolean hasPermissions(String str) {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null) {
            return false;
        }
        return activeSession.getPermissions().contains(str);
    }

    public static void requestPermissions(String str) {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null && !hasPermissions(str)) {
            getInstance().pendingPublishReauthorization = true;
            activeSession.requestNewPublishPermissions(new Session.NewPermissionsRequest(ActivityWrapper.getActivity(), PERMISSIONS));
        }
    }

    public static void setApplicationID(String str) {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null && str != activeSession.getApplicationId()) {
            closeAndClearTokenInformation();
        }
        applicationId = str;
    }

    public static void openSession() {
        openSession(false);
    }

    public static void openSession(boolean z) {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null) {
            if (activeSession == null) {
                activeSession = new Session.Builder(ActivityWrapper.getActivity()).setApplicationId(applicationId).build();
            }
            Session.setActiveSession(activeSession);
        }
        if (!z || activeSession.getState() == SessionState.CREATED_TOKEN_LOADED) {
            if (!activeSession.isOpened()) {
                Session.OpenRequest openRequest = new Session.OpenRequest(ActivityWrapper.getActivity());
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
                openRequest.setCallback((Session.StatusCallback) null);
                ArrayList arrayList = new ArrayList();
                arrayList.add("public_profile");
                arrayList.add("publish_actions");
                openRequest.setPermissions((List<String>) arrayList);
                activeSession.openForPublish(openRequest);
                activeSession.addCallback(getInstance().statusCallback);
                return;
            }
            Session.openActiveSession(ActivityWrapper.getActivity(), true, getInstance().statusCallback);
        }
    }

    public static void openSessionFromCache() {
        if (Session.getActiveSession() == null) {
            openSession(true);
        }
    }

    public static void closeSession() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            activeSession.close();
            Session.setActiveSession(null);
        }
    }

    public static void closeAndClearTokenInformation() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            activeSession.closeAndClearTokenInformation();
            activeSession.close();
            Session.setActiveSession(null);
        } else if (applicationId == null) {
        } else {
            Session build = new Session.Builder(ActivityWrapper.getActivity()).setApplicationId(applicationId).build();
            if (build.getState() == SessionState.CREATED_TOKEN_LOADED) {
                build.closeAndClearTokenInformation();
            }
            build.close();
            Session.setActiveSession(null);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            Session.getActiveSession().onActivityResult(ActivityWrapper.getActivity(), i, i2, intent);
            if (!getInstance().pendingPublishReauthorization) {
                return;
            }
            if (hasPermissions("publish_actions")) {
                facebookPublishPermissionGranted();
            } else {
                facebookPublishPermissionRefused();
            }
            getInstance().pendingPublishReauthorization = false;
        } catch (Exception unused) {
        }
    }

    private boolean isSubsetOf(Collection<String> collection, Collection<String> collection2) {
        for (String str : collection) {
            if (!collection2.contains(str)) {
                return false;
            }
        }
        return true;
    }

    public static void postMessage(String str, String str2, String str3, String str4) {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            if (!getInstance().isSubsetOf(PERMISSIONS, activeSession.getPermissions())) {
                getInstance().pendingPublishReauthorization = true;
                activeSession.requestNewPublishPermissions(new Session.NewPermissionsRequest(ActivityWrapper.getActivity(), PERMISSIONS));
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("message", str);
            bundle.putString("link", str4);
            new RequestAsyncTask(new Request(activeSession, "me/feed", bundle, HttpMethod.POST, new Request.Callback() { // from class: com.rockstargames.hal.andFacebook.1
                @Override // com.facebook.Request.Callback
                public void onCompleted(Response response) {
                    if (response != null) {
                        try {
                            try {
                                response.getGraphObject().getInnerJSONObject().getString("id");
                            } catch (JSONException e) {
                                Log.i("Facebook", "JSON error " + e.getMessage(), e);
                            }
                            if (response.getError() == null) {
                                andFacebook.getInstance().facebookPostSuccess();
                                return;
                            }
                        } catch (Exception e2) {
                            Log.e("andFacebook", "Error receiving Facebook response.", e2);
                        }
                    }
                    andFacebook.getInstance().facebookPostFailed();
                }
            })).execute(new Void[0]);
        }
    }

    /* loaded from: classes.dex */
    public class SessionStatusCallback implements Session.StatusCallback {
        private SessionStatusCallback() {
            andFacebook.this = r1;
        }

        @Override // com.facebook.Session.StatusCallback
        public void call(Session session, SessionState sessionState, Exception exc) {
            if (exc != null) {
                Log.i("Facebook", "Facebook error");
            }
            int i = C05042.$SwitchMap$com$facebook$SessionState[sessionState.ordinal()];
            if (i == 1) {
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() { // from class: com.rockstargames.hal.andFacebook.SessionStatusCallback.1
                    @Override // com.facebook.Request.GraphUserCallback
                    public void onCompleted(GraphUser graphUser, Response response) {
                        if (graphUser != null) {
                            andFacebook.this.facebookOpenedSucessfully(graphUser.getName());
                        }
                    }
                });
            } else if (i == 2) {
                andFacebook.this.facebookClosed();
                session.removeCallback(andFacebook.getInstance().statusCallback);
            } else if (i != 3) {
            } else {
                andFacebook.closeSession();
                session.removeCallback(andFacebook.getInstance().statusCallback);
            }
        }
    }

    /* renamed from: com.rockstargames.hal.andFacebook$2 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C05042 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$SessionState;

        static {
            int[] iArr = new int[SessionState.values().length];
            $SwitchMap$com$facebook$SessionState = iArr;
            try {
                iArr[SessionState.OPENED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$SessionState[SessionState.CLOSED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$SessionState[SessionState.CLOSED_LOGIN_FAILED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }
}
