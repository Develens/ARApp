package com.sb.arapp.network;

import android.content.Context;
import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebClient {


    private String TAG = "WebClient";
    public static final int HTTP_METHOD_GET = 1;
    public static final int HTTP_METHOD_POST = 2;
    public static final int HTTP_METHOD_POST_HEADER = 3;
    public static final int HTTP_METHOD_PUT = 4;
    public static final int HTTP_METHOD_DELETE = 5;
    private WebClientTaskListener mCallback;
    // Set the timeout in milliseconds until a connection is established
    public static final int CONNECTION_TIME_OUT = 1 * 30 * 1000; // 30 sec

    // Set the default socket timeout (SO_TIMEOUT)
    // in milliseconds which is the timeout for waiting for data.
    public static final int SOCKET_TIME_OUT = 1 * 30 * 1000; //30 sec

    private List<NameValuePair> params;

    private String url, jsonString;
    private Context mcontext;

    private int responseCode;
    private boolean timeout;
    private String httpmessage;

    private InputStream response;

    public InputStream getResponse() {
        return response;
    }

    public String getHttpMessage() {
        return httpmessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public WebClient(String url, List<NameValuePair> mData, Context context) {
        this.url = url;
        this.mcontext = context;
        if (mData == null)
            this.params = new ArrayList<NameValuePair>();
        else
            this.params = mData;

    }

    public WebClient(String url, String mJsonString) {
        this.url = url;
        this.jsonString = mJsonString;
        this.params = new ArrayList<NameValuePair>();



    }
    public WebClient(String url, String mJsonString, WebClientTaskListener mCallback, Context context) {
        this.url = url;
        this.jsonString = mJsonString;
        this.params = new ArrayList<NameValuePair>();
        this.mCallback = mCallback;
        this.mcontext = context;

    }

    public interface WebClientTaskListener {

        public void onTaskComplete(HttpPost request);

        public void onTaskCompletePut(HttpPut request);

    }

    public void execute(int method) {
        switch (method) {
            case HTTP_METHOD_GET: {
                // add parameters
                String combinedParams = "";
                if (!params.isEmpty()) {
                    combinedParams += "?";
                    for (NameValuePair p : params) {
                        String paramString = null;
                        try {
                            paramString = p.getName() + "="
                                    + URLEncoder.encode(p.getValue(), "UTF-8");
                            System.out.println("paramString  " + paramString);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (combinedParams.length() > 1) {
                            combinedParams += "&" + paramString;
                        } else {
                            combinedParams += paramString;
                        }
                    }
                }

                //getWebResponse(url + combinedParams);
                HttpGet request = new HttpGet(url + combinedParams);
                executeRequest(request, url);
                break;
            }
            case HTTP_METHOD_POST: {
                HttpPost request = new HttpPost(url);

                if (!params.isEmpty()) {
                    try {
                        request.setEntity(new UrlEncodedFormEntity(params,
                                HTTP.UTF_8));

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (params.isEmpty() && jsonString.length() != 0) {

                    try {
                        request.setEntity(new StringEntity(jsonString,
                                HTTP.UTF_8));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                if (mCallback != null) {
                    mCallback.onTaskComplete(request);
                }

                executeRequest(request, url);
                break;
            }
            case HTTP_METHOD_POST_HEADER: {
                Log.e("VF", "EXECUTE HEADER:");
                HttpPost request = new HttpPost(url);

                if (!params.isEmpty()) {
                    try {
                        request.setEntity(new UrlEncodedFormEntity(params,
                                HTTP.UTF_8));

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (params.isEmpty() && jsonString.length() != 0) {

                    try {
                        request.setEntity(new StringEntity(jsonString,
                                HTTP.UTF_8));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                if (mCallback != null) {
                    mCallback.onTaskComplete(request);
                }

                executeRequest1(request, url);
                break;
            }

            case HTTP_METHOD_PUT: {
                HttpPut request = new HttpPut(url);

                if (!params.isEmpty()) {
                    try {
                        request.setEntity(new UrlEncodedFormEntity(params,
                                HTTP.UTF_8));

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (params.isEmpty() && jsonString.length() != 0) {

                    try {
                        request.setEntity(new StringEntity(jsonString,
                                HTTP.UTF_8));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                if (mCallback != null) {
                    mCallback.onTaskCompletePut(request);
                }

                executeRequest(request, url);
                break;
            }

            case HTTP_METHOD_DELETE: {
                // add parameters
                String combinedParams = "";
                if (!params.isEmpty()) {
                    combinedParams += "?";
                    for (NameValuePair p : params) {
                        String paramString = null;
                        try {
                            paramString = p.getName() + "="
                                    + URLEncoder.encode(p.getValue(), "UTF-8");
                            System.out.println("paramString  " + paramString);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (combinedParams.length() > 1) {
                            combinedParams += "&" + paramString;
                        } else {
                            combinedParams += paramString;
                        }
                    }
                }

                //getWebResponse(url + combinedParams);
                HttpDelete request = new HttpDelete(url + combinedParams);
                executeRequest(request, url);
                break;
            }
        }

    }


    public void getWebResponse(String sAddress) {

        try {
            URL url = new URL(sAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //String userCredentials = "username:password";
            //String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            //con.setRequestProperty ("Authorization", basicAuth);
            //con.setRequestMethod("POST");
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //con.setRequestProperty("Content-Length", "" + postData.getBytes().length);
            //con.setRequestProperty("Content-Language", "en-US");
            //con.setUseCaches(false);
            //con.setDoInput(true);
            //con.setDoOutput(true);
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("x-rnyoo-client", "RnyooAndroid");
            //con.setRequestProperty("Accept", "application/vnd.rnyoo+json; version=1");

            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            responseCode = con.getResponseCode();
            httpmessage = con.getResponseMessage();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = con.getInputStream().read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            response = new ByteArrayInputStream(buffer.toByteArray());


        } catch (Exception e) {

        }

        //return sb.toString();
    }


    // ADD AUTH HEADER HERE
    private void executeRequest1(HttpUriRequest request, String url) {
        //Add the headers explicitly
        request.addHeader("Accept", "*/*");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Content-Type", "application/json");
        //request.addHeader("x-rnyoo-client", "RnyooAndroid");
        // request.addHeader("Accept", "application/vnd.rnyoo+json; version=1");
        //"Accept: application/vnd.rnyoo+json; version=1"

//	 System.out.println("request is : "+request.getRequestLine());
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,
                CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIME_OUT);
        // Declaring the client
        DefaultHttpClient client = new DefaultHttpClient(httpParams);


        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            httpmessage = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(entity);

           // Page-Number

          //  Header header1 = httpResponse.getFirstHeader("Page-Number");

           // header1.getName()



           // Header header = httpResponse.g

            if (buf != null) {

                response = buf.getContent();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //System.out.println("UnsupportedEncodingException");
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            //System.out.println("ClientProtocolException");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            //System.out.println("SocketTimeoutException");
        } catch (ConnectTimeoutException e) {
            //e.printStackTrace();
            timeout=true;
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            //System.out.println("IOException");
        }
    }

    private void executeRequest(HttpUriRequest request, String url) {

       String mToken ="eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjoic3B5a2VidXJuMDI4IiwiZXhwIjoxNTUzMDg0MTE5fQ.LzqWmtVAXXGV6dGpKaTIuxAimKhAQFfvVdFuwpZsAsccvsFK_nrpupc1Qr4TYYSP_OdNjZUQ9DT-IXkBOAPG0Q";


       // String mToken = "Token eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyLW5hbWUiOiJzcHlrZWJ1cm55IiwiZXhwIjoxNTY0NDkxMDU1fQ.VETPe3IeMI81P9j6hKsnYrdyJondQ4zevf5EY53GlrBEn4oL-NUjbGVJo3DNx_kj8_s8sfUJ_iHVLIXEzYEnoA";
        Log.e(TAG, "Token " +mToken);

        //Add the headers explicitly
        request.addHeader("Accept", "*/*");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Content-Type", "application/json");
        if(mToken!=null) {
            if (mToken.length() > 1 && !mToken.isEmpty()) {
                request.addHeader("Authorization",
                        "Token " + mToken);
            }
        }
       /* else{
            request.addHeader("Authorization",
                    "Token eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjoic3B5a2VidXJuMDI4IiwiZXhwIjoxNTUzMDg0MTE5fQ.LzqWmtVAXXGV6dGpKaTIuxAimKhAQFfvVdFuwpZsAsccvsFK_nrpupc1Qr4TYYSP_OdNjZUQ9DT-IXkBOAPG0Q");
            }*/

        //request.addHeader("x-rnyoo-client", "RnyooAndroid");
       // request.addHeader("Accept", "application/vnd.rnyoo+json; version=1");
        //"Accept: application/vnd.rnyoo+json; version=1"

      // System.out.println("request is : "+request.getRequestLine());
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,
                CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIME_OUT);
        // Declaring the client
        DefaultHttpClient client = new DefaultHttpClient(httpParams);


        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            httpmessage = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(entity);

            if (buf != null) {

                response = buf.getContent();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //System.out.println("UnsupportedEncodingException");
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            //System.out.println("ClientProtocolException");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            //System.out.println("SocketTimeoutException");
        } catch (ConnectTimeoutException e) {
            //e.printStackTrace();
            timeout=true;
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
            //System.out.println("IOException");
        }
    }

    public static boolean isConnected(Context context) {

        // TODO Auto-generated method stub
//		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
//
//		//For 3G check
//		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//		            .isConnectedOrConnecting();
//		//For WiFi Check
//		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//		            .isConnectedOrConnecting();
//
//		if (!is3g && !isWifi) 
//		{ 
//			return false;
//		} 
//		 else 
//		{ 
//			 return true;
//		} 
        return Connectivity.isConnectedFast(context);
    }


}
