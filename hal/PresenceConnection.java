package com.rockstargames.hal;

import com.google.android.vending.expansion.downloader.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/* loaded from: classes.dex */
public class PresenceConnection implements Runnable {
    private static final int kCONNECTIONTIMEOUT_MS = 30000;
    private static final int kREADTIMEOUT_MS = 120000;
    HttpClient client = null;
    private boolean connectionCancelled;
    private byte[] data;
    private andHttp http;
    private InputStream inputStream;
    private HttpRequestBase request;

    private void pLog(String str) {
    }

    public PresenceConnection(andHttp andhttp, HttpRequestBase httpRequestBase, byte[] bArr) {
        this.connectionCancelled = false;
        this.http = andhttp;
        this.request = httpRequestBase;
        this.data = bArr;
        this.connectionCancelled = false;
        andhttp.setPresenceConnection(this);
    }

    public void cancel() {
        pLog("Cancel requested");
        this.connectionCancelled = true;
        if (this.inputStream != null) {
            this.client.getConnectionManager().shutdown();
        }
    }

    private HttpClient createPresenceHttpClient() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, kCONNECTIONTIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(basicHttpParams, kREADTIMEOUT_MS);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        return new DefaultHttpClient(new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry), basicHttpParams);
    }

    private InputStream executePostRequest(HttpPost httpPost) throws ClientProtocolException, IOException {
        Header[] allHeaders;
        HttpResponse execute = this.client.execute(httpPost);
        pLog("Received response from Presence server");
        final int statusCode = execute.getStatusLine().getStatusCode();
        final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
        final StringBuilder sb = new StringBuilder();
        for (Header header : execute.getAllHeaders()) {
            sb.append(header.getName() + '\n' + header.getValue() + '\n');
        }
        ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.1
            @Override // java.lang.Runnable
            public void run() {
                PresenceConnection.this.http.onReceivedResponse(PresenceConnection.this.http.getId(), statusCode, reasonPhrase, sb.toString());
            }
        });
        if (statusCode != 200) {
            pLog("Response was not 200-OK: " + statusCode + " - " + reasonPhrase);
            if (statusCode < 200 || statusCode >= 300) {
                throw new ClientProtocolException("Presense System Connection Error: " + statusCode + Constants.FILENAME_SEQUENCE_SEPARATOR + reasonPhrase);
            }
        }
        return execute.getEntity().getContent();
    }

    private void readStreamUntilItErrorsOut(BufferedReader bufferedReader) throws Exception {
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                if (readLine.length() > 0) {
                    final byte[] bytes = readLine.getBytes();
                    final int length = readLine.length();
                    pLog(readLine);
                    ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.2
                        @Override // java.lang.Runnable
                        public void run() {
                            PresenceConnection.this.http.onReceivedData(PresenceConnection.this.http.getId(), bytes, length);
                        }
                    });
                }
            } else {
                return;
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Header[] allHeaders;
        pLog("Connecting presence system...");
        this.client = createPresenceHttpClient();
        try {
            HttpPost httpPost = new HttpPost(this.request.getURI());
            for (Header header : this.request.getAllHeaders()) {
                httpPost.addHeader(header.getName(), header.getValue());
            }
            if (this.data != null) {
                httpPost.setEntity(new ByteArrayEntity(this.data));
            }
            this.inputStream = executePostRequest(httpPost);
        } catch (Exception e) {
            pLog("Error connecting to presense system");
            ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.3
                @Override // java.lang.Runnable
                public void run() {
                    PresenceConnection.this.http.onError(PresenceConnection.this.http.getId(), 0);
                }
            });
            e.printStackTrace();
        }
        BufferedReader bufferedReader = null;
        if (this.inputStream != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
            try {
                readStreamUntilItErrorsOut(bufferedReader);
            } catch (SocketTimeoutException e2) {
                if (!this.connectionCancelled) {
                    e2.printStackTrace();
                }
            } catch (IOException e3) {
                if (!this.connectionCancelled) {
                    e3.printStackTrace();
                }
            } catch (Exception e4) {
                ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.4
                    @Override // java.lang.Runnable
                    public void run() {
                        PresenceConnection.this.http.onError(PresenceConnection.this.http.getId(), 0);
                    }
                });
                if (!this.connectionCancelled) {
                    e4.printStackTrace();
                }
            }
            ActivityWrapper.getLayout().postDelayed(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.5
                @Override // java.lang.Runnable
                public void run() {
                    PresenceConnection.this.http.onConnectionFinished(PresenceConnection.this.http.getId());
                }
            }, 500L);
        }
        pLog("Closing Presense connection");
        try {
            if (this.inputStream != null) {
                bufferedReader.close();
                this.inputStream.close();
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        andHttp.removeConnection(this.http.getId());
    }
}
