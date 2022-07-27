package com.rockstargames.hal;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.BufferedInputStream;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
public class andHttp {
    private static SparseArray<andHttp> connections = new SparseArray<>();
    private boolean cancelled = false;

    /* renamed from: id */
    private int f35id;
    private PresenceConnection presenceConnection;
    private HttpRequestBase request;

    public static void log(String str) {
    }

    public native void onConnectionFinished(int i);

    public native void onError(int i, int i2);

    public native void onReceivedData(int i, byte[] bArr, int i2);

    public native void onReceivedResponse(int i, int i2, String str, String str2);

    public static void HEAD(int i, String str) {
        log("HEAD: " + str);
        processRequest(i, new HttpHead(str), false);
    }

    public static void GET(int i, String str, String str2) {
        log("GET: " + str);
        HttpGet httpGet = new HttpGet(str);
        log("Header string: " + str2);
        StringTokenizer stringTokenizer = new StringTokenizer(str2, ":\n");
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            String nextToken2 = stringTokenizer.nextToken();
            log("    " + nextToken + ": " + nextToken2);
            httpGet.addHeader(nextToken, nextToken2);
        }
        processRequest(i, httpGet, false);
    }

    public static void AsyncGET(int i, String str, String str2) {
        log("AsyncGET: " + str);
        HttpGet httpGet = new HttpGet(str);
        log("Header string: " + str2);
        StringTokenizer stringTokenizer = new StringTokenizer(str2, ":\n");
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            String nextToken2 = stringTokenizer.nextToken();
            log("    " + nextToken + ": " + nextToken2);
            httpGet.addHeader(nextToken, nextToken2);
        }
        processRequest(i, httpGet, true);
    }

    public static void POST(int i, String str, String str2, byte[] bArr) {
        log("POST: " + str);
        HttpPost httpPost = new HttpPost(str);
        log("Header string: " + str2);
        StringTokenizer stringTokenizer = new StringTokenizer(str2, ":\n");
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            String nextToken2 = stringTokenizer.nextToken();
            log("    " + nextToken + ": " + nextToken2);
            httpPost.addHeader(nextToken, nextToken2);
        }
        if (!str.contains("Presence.asmx/WaitMessage")) {
            log("with data:");
            logDataSafe(bArr);
            httpPost.setEntity(new ByteArrayEntity(bArr));
            processRequest(i, httpPost, false);
            return;
        }
        andHttp andhttp = new andHttp(i, httpPost);
        addConnection(i, andhttp);
        new Thread(new PresenceConnection(andhttp, httpPost, bArr), "Presence Connection Thread").start();
    }

    public static boolean IsNetworkReachable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) ActivityWrapper.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void cancelConnection(final int i) {
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andHttp.1
            @Override // java.lang.Runnable
            public void run() {
                andHttp connection = andHttp.getConnection(i);
                if (connection != null) {
                    andHttp.log("Cancelling connection " + i);
                    connection.cancelled = true;
                    if (connection.presenceConnection != null) {
                        connection.presenceConnection.cancel();
                    }
                    andHttp.removeConnection(i);
                    return;
                }
                andHttp.log("Can't find connection " + i + " to cancel.");
            }
        }).start();
    }

    public static void logDataSafe(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length && i < 4096; i++) {
            char c = (char) bArr[i];
            if (c != '\r') {
                if (c == '\n') {
                    log("    " + sb.toString());
                    sb.setLength(0);
                } else if ((c < ' ' && c != '\t') || c >= 127) {
                    sb.append("{" + c + "}");
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 100) {
                log("    " + sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) {
            log("    " + sb.toString());
            if (bArr.length <= 4096) {
                return;
            }
            log("    ...plus " + (bArr.length - 4096) + " more bytes.");
        }
    }

    private static synchronized void addConnection(int i, andHttp andhttp) {
        synchronized (andHttp.class) {
            connections.put(i, andhttp);
        }
    }

    public static synchronized void removeConnection(int i) {
        synchronized (andHttp.class) {
            connections.remove(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized andHttp getConnection(int i) {
        andHttp andhttp;
        synchronized (andHttp.class) {
            andhttp = connections.get(i);
        }
        return andhttp;
    }

    private static void processRequest(int i, HttpRequestBase httpRequestBase, boolean z) {
        andHttp andhttp = new andHttp(i, httpRequestBase);
        addConnection(i, andhttp);
        if (z) {
            andhttp.executeAsync();
        } else {
            andhttp.execute();
        }
    }

    public andHttp(int i, HttpRequestBase httpRequestBase) {
        this.f35id = i;
        this.request = httpRequestBase;
    }

    public int getId() {
        return this.f35id;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setPresenceConnection(PresenceConnection presenceConnection) {
        this.presenceConnection = presenceConnection;
    }

    public void execute() {
        onPreExecute();
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andHttp.2
            @Override // java.lang.Runnable
            public void run() {
                final Pair<HttpResponse, byte[]> doInBackground = andHttp.this.doInBackground();
                ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        andHttp.this.onPostExecute(doInBackground);
                    }
                });
            }
        }).start();
    }

    public void executeAsync() {
        onPreExecute();
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3
            @Override // java.lang.Runnable
            public void run() {
                Header[] allHeaders;
                BasicHttpParams basicHttpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
                try {
                    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(basicHttpParams);
                    andHttp.log("Executing request...");
                    BasicHttpResponse basicHttpResponse = (BasicHttpResponse) defaultHttpClient.execute(andHttp.this.request);
                    andHttp.log("Response obtained.");
                    andHttp.log("Status line: " + basicHttpResponse.getStatusLine());
                    StringBuilder sb = new StringBuilder();
                    for (Header header : basicHttpResponse.getAllHeaders()) {
                        sb.append(header.getName() + '\n' + header.getValue() + '\n');
                    }
                    StatusLine statusLine = basicHttpResponse.getStatusLine();
                    final int statusCode = statusLine.getStatusCode();
                    final String reasonPhrase = statusLine.getReasonPhrase();
                    final String sb2 = sb.toString();
                    if (statusCode >= 200 && statusCode < 300) {
                        ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                andHttp.this.onReceivedResponse(andHttp.this.f35id, statusCode, reasonPhrase, sb2);
                            }
                        });
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(basicHttpResponse.getEntity().getContent());
                        byte[] bArr = new byte[2048];
                        do {
                            final int read = bufferedInputStream.read(bArr, 0, 2048);
                            if (read == -1) {
                                break;
                            } else if (read > 0) {
                                final byte[] bArr2 = (byte[]) bArr.clone();
                                ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3.3
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        andHttp.this.onReceivedData(andHttp.this.f35id, bArr2, read);
                                    }
                                });
                            }
                        } while (!andHttp.this.cancelled);
                        ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3.4
                            @Override // java.lang.Runnable
                            public void run() {
                                andHttp.this.onConnectionFinished(andHttp.this.f35id);
                            }
                        });
                        return;
                    }
                    ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3.2
                        @Override // java.lang.Runnable
                        public void run() {
                            andHttp.this.onError(andHttp.this.f35id, statusCode);
                        }
                    });
                } catch (Exception e) {
                    ActivityWrapper.handleException(e);
                    ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.3.5
                        @Override // java.lang.Runnable
                        public void run() {
                            andHttp.this.onError(andHttp.this.f35id, -1);
                        }
                    });
                }
            }
        }).start();
    }

    protected void onPreExecute() {
        Header[] allHeaders;
        log("Starting HTTP request: " + this.request.getRequestLine());
        for (Header header : this.request.getAllHeaders()) {
            log("    " + header.getName() + ": " + header.getValue());
        }
    }

    protected Pair<HttpResponse, byte[]> doInBackground() {
        Header[] allHeaders;
        try {
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            if (!this.request.getRequestLine().getUri().contains("Presence.asmx/WaitMessage")) {
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
            } else {
                HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
            }
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(basicHttpParams);
            log("Executing request...");
            BasicHttpResponse basicHttpResponse = (BasicHttpResponse) defaultHttpClient.execute(this.request);
            log("Response obtained.");
            log("Status line: " + basicHttpResponse.getStatusLine());
            for (Header header : basicHttpResponse.getAllHeaders()) {
                log("    " + header.getName() + " : " + header.getValue());
            }
            HttpEntity entity = basicHttpResponse.getEntity();
            byte[] byteArray = entity != null ? EntityUtils.toByteArray(entity) : new byte[0];
            logDataSafe(byteArray);
            if (this.cancelled) {
                return null;
            }
            return new Pair<>(basicHttpResponse, byteArray);
        } catch (SSLPeerUnverifiedException e) {
            Log.e(HttpVersion.HTTP, "Exception!", e);
            ActivityWrapper.handleException(e);
            return new Pair<>(null, "0x80131500".getBytes(Charset.forName(HTTP.UTF_8)));
        } catch (Exception e2) {
            Log.e(HttpVersion.HTTP, "Exception!", e2);
            ActivityWrapper.handleException(e2);
            return null;
        }
    }

    protected void onPostExecute(Pair<HttpResponse, byte[]> pair) {
        Header[] allHeaders;
        if (pair == null) {
            onError(this.f35id, -1);
        } else if (pair.first == null) {
            if (new String((byte[]) pair.second, Charset.forName(HTTP.UTF_8)).equals("0x80131500")) {
                onError(this.f35id, -2146233088);
            } else {
                onError(this.f35id, -1);
            }
        } else {
            StatusLine statusLine = ((HttpResponse) pair.first).getStatusLine();
            StringBuilder sb = new StringBuilder();
            for (Header header : ((HttpResponse) pair.first).getAllHeaders()) {
                sb.append(header.getName() + '\n' + header.getValue() + '\n');
            }
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < ((byte[]) pair.second).length && i < 1024; i++) {
                byte b = ((byte[]) pair.second)[i];
                if (b >= 32 && b < 128) {
                    sb2.append((char) b);
                } else {
                    sb2.append((int) b);
                    sb2.append(" ");
                }
                if (sb2.length() > 100) {
                    sb2.setLength(0);
                }
            }
            if (sb2.length() > 0) {
                sb2.setLength(0);
            }
            onReceivedResponse(this.f35id, statusLine.getStatusCode(), statusLine.getReasonPhrase(), sb.toString());
            onReceivedData(this.f35id, (byte[]) pair.second, ((byte[]) pair.second).length);
            onConnectionFinished(this.f35id);
        }
        removeConnection(getId());
    }
}
