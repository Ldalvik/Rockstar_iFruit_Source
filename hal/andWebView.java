package com.rockstargames.hal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.lang.reflect.Constructor;

/* loaded from: classes.dex */
public class andWebView extends andView {
    static int staticCount;

    public native void onLoaded(int i);

    public andWebView(int i) {
        super(i);
        setView(new andWebViewImp(this));
        staticCount++;
    }

    public void destroy() {
        getImpl().loadUrl("about:blank");
        getImpl().onPause();
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public void setUrl(String str) {
        getImpl().setUrl(str);
    }

    public void navigateBack() {
        getImpl().goBack();
    }

    public boolean canNavigateBack() {
        return getImpl().canGoBack();
    }

    public void navigateForward() {
        getImpl().goForward();
    }

    public boolean canNavigateForward() {
        return getImpl().canGoForward();
    }

    public void refresh() {
        getImpl().reload();
    }

    public static void clearCookies() {
        CookieSyncManager.createInstance(ActivityWrapper.getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
    }

    public void addiFruitUserAgentString() {
        getImpl().addiFruitUserAgent();
    }

    /* loaded from: classes.dex */
    public static class NewApiWrapper {
        NewApiWrapper() {
        }

        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }

    public static String getUserAgent() {
        if (Build.VERSION.SDK_INT >= 17) {
            return NewApiWrapper.getDefaultUserAgent(ActivityWrapper.getApplicationContext());
        }
        try {
            Constructor declaredConstructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            declaredConstructor.setAccessible(true);
            String userAgentString = ((WebSettings) declaredConstructor.newInstance(ActivityWrapper.getApplicationContext(), null)).getUserAgentString();
            declaredConstructor.setAccessible(false);
            return userAgentString;
        } catch (Exception unused) {
            return new WebView(ActivityWrapper.getApplicationContext()).getSettings().getUserAgentString();
        }
    }

    public static andWebView createView(int i) {
        return new andWebView(i);
    }

    private andWebViewImp getImpl() {
        return (andWebViewImp) getView();
    }

    /* loaded from: classes.dex */
    public class andWebViewImp extends WebView {
        boolean loading = true;
        String originalAgentString = null;
        andWebView parent;
        private String url;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andWebViewImp(andWebView andwebview) {
            super(ActivityWrapper.getApplicationContext());
            andWebView.this = r3;
            this.parent = null;
            setId(andViewManager.genID());
            this.parent = andwebview;
            setWebChromeClient(new WebChromeClient() { // from class: com.rockstargames.hal.andWebView.andWebViewImp.1
            });
            setWebViewClient(new WebViewClient() { // from class: com.rockstargames.hal.andWebView.andWebViewImp.2
                @Override // android.webkit.WebViewClient
                public void onLoadResource(WebView webView, String str) {
                    super.onLoadResource(webView, str);
                }

                @Override // android.webkit.WebViewClient
                public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                    super.onPageStarted(webView, str, bitmap);
                    Log.i("andWebView", "Page started: " + str);
                }

                @Override // android.webkit.WebViewClient
                public void onPageFinished(WebView webView, String str) {
                    super.onPageFinished(webView, str);
                    andWebViewImp.this.parent.onLoaded(andWebViewImp.this.parent.handle);
                    andWebViewImp.this.loading = false;
                    Log.i("andWebView", "Page finished: " + str);
                }

                @Override // android.webkit.WebViewClient
                public void onReceivedError(WebView webView, int i, String str, String str2) {
                    super.onReceivedError(webView, i, str, str2);
                    Log.i("andWebView", "Received error " + i + ": " + str + " (" + str2 + ")");
                }
            });
            setWebChromeClient(new WebChromeClient() { // from class: com.rockstargames.hal.andWebView.andWebViewImp.3
                @Override // android.webkit.WebChromeClient
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    consoleMessage.messageLevel();
                    return false;
                }
            });
            getSettings().setJavaScriptEnabled(true);
            getSettings().setBuiltInZoomControls(true);
            getSettings().setSupportZoom(true);
            setInitialScale(100);
            getSettings().setUseWideViewPort(true);
            getSettings().setAppCacheEnabled(true);
            getSettings().setDomStorageEnabled(true);
            getSettings().setAppCachePath(ActivityWrapper.getActivity().getApplicationContext().getDatabasePath("myAppCache").getAbsolutePath());
            getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            if (Build.VERSION.SDK_INT >= 19) {
                setWebContentsDebuggingEnabled(true);
            }
        }

        public void setUrl(String str) {
            this.url = str;
        }

        @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            loadUrl(this.url);
            andWebView andwebview = this.parent;
            andwebview.onLoaded(andwebview.handle);
        }

        public void navigateBack() {
            goBack();
        }

        public boolean canNavigateBack() {
            return canGoBack();
        }

        public void navigateForward() {
            goForward();
        }

        public boolean canNavigateForward() {
            return canGoForward();
        }

        public void refresh() {
            reload();
        }

        public andWebView getWebView() {
            return this.parent;
        }

        public void addiFruitUserAgent() {
            if (this.originalAgentString == null) {
                this.originalAgentString = getSettings().getUserAgentString();
                WebSettings settings = getSettings();
                settings.setUserAgentString(this.originalAgentString + " iFruitUASS");
            }
        }
    }
}
