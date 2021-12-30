package com.sb.arapp.network;

/**
 * Created by gleecus on 17/3/16.
 */

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.util.List;

//import com.renyoolet.app.RegisterLogin.SessionLogout;


public class WebTask extends AsyncTask<Object, Void, ServerResponse> {

    // private static final String TAG = "WebServiceTask";

    private Context mContext;
    private String mUrl;
    private List<NameValuePair> mData;
    private boolean mIsCancellable;
    private WebServiceTaskListener mCallback;
   // private ProgressDialog mDialog;
    private int mMethod;
    private String mMessage, mJsonString = "";
    public static final int HTTP_METHOD_GET = WebClient.HTTP_METHOD_GET;
    public static final int HTTP_METHOD_POST = WebClient.HTTP_METHOD_POST;
    public static final int HTTP_METHOD_POST_HEADER = WebClient.HTTP_METHOD_POST_HEADER;
    public static final int HTTP_METHOD_PUT = WebClient.HTTP_METHOD_PUT;
    private int tag = -1;
    private boolean mFromService = false;
    WebClient client = null;





    public WebTask(Context context, String url, int method,
                   List<NameValuePair> sendData, boolean isCancellable,
                   WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;
        mIsCancellable = isCancellable;
        mCallback = callback;
    }

    public WebTask(Context context, String url, int method,
                   List<NameValuePair> sendData, String jsonString,
                   boolean isCancellable, WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;
        mJsonString = jsonString;
        mIsCancellable = isCancellable;
        mCallback = callback;
    }

    public WebTask(Context context, String url, int method,
                   List<NameValuePair> sendData, String jsonString,
                   boolean fromService, boolean isCancellable,
                   WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;
        mJsonString = jsonString;
        mIsCancellable = isCancellable;
        mCallback = callback;
        mFromService = fromService;
    }

    public WebTask(Context context, String url, int method,
                   List<NameValuePair> sendData, boolean fromService,
                   boolean isCancellable, WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;

        mIsCancellable = isCancellable;
        mCallback = callback;
        mFromService = fromService;
    }

    public WebTask(Context context, String url, String message,
                   int method, List<NameValuePair> sendData, boolean isCancellable,
                   WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;
        mIsCancellable = isCancellable;
        mCallback = callback;
        mMessage = message;
    }

    public WebTask(Context context, String url, boolean fromService,
                   String message, int method, List<NameValuePair> sendData,
                   boolean isCancellable, WebServiceTaskListener callback) {
        mContext = context;
        mUrl = url;
        mMethod = method;
        mData = sendData;
        mIsCancellable = isCancellable;
        mCallback = callback;
        mMessage = message;
        mFromService = fromService;
    }



    @Override
    protected void onPreExecute() {

      /*  if (!mFromService) {
            mDialog = new ProgressDialog(mContext);
            if (mMessage == null) {
                mMessage = mContext.getResources().getString(
                        R.string.stringPleaseWait);
            }
            //mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mDialog.setMessage(Html.fromHtml(mContext.getResources().getString(R.string.stringPleaseWait)));
            mDialog.setCancelable(mIsCancellable);
            mDialog.setOnKeyListener(new ProgressDialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if (mIsCancellable) {
                        cancel(true);
                    }

                    return false;
                }
            });
            if(!isFinishing())

            mDialog.show();
        }*/
    }

    @Override
    protected ServerResponse doInBackground(Object... params) {
        ServerResponse serverResponse = null;

        if (mJsonString.length() != 0) {
            client = new WebClient(mUrl, mJsonString);
            client.execute(mMethod);

        } else {
            client = new WebClient(mUrl, mData,null);
            client.execute(mMethod);
        }
        int responseCode = client.getResponseCode();
        InputStream inputStream = client.getResponse();
        boolean timeout = client.isTimeout();

        if (inputStream != null) {
            if (responseCode == 200 || responseCode == 400) {
                serverResponse = new ServerResponse(false, inputStream,
                        responseCode, timeout);

            }else if(responseCode == 401){// no valid session found
             //   SessionLogout sl = new SessionLogout(mContext, false);
              //  sl.clearAllSesson();
                serverResponse = new ServerResponse(true, inputStream,
                        responseCode, timeout);

            } else {
                serverResponse = new ServerResponse(true, inputStream,
                        responseCode, timeout);
            }
        } else {
            serverResponse = new ServerResponse(true, inputStream, responseCode, timeout);
        }
        serverResponse.setAdditionalData(params);
        // if(responseCode == 500 ||responseCode == 502){ // rahul
        // serverResponse.setNetworkIssue(true);
        // }
        //
        return serverResponse;
    }

    @Override
    protected void onPostExecute(ServerResponse result) {
        if (!mFromService) {

           /* try {
                if ((mDialog != null) && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog.cancel();
                }
            }catch (Exception ex){

            }
           */
            //if (mDialog.isShowing())
            //mDialog.dismiss();
        }
        result.setTag(tag);
        mCallback.onTaskComplete(result);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public interface WebServiceTaskListener {

        public void onTaskComplete(ServerResponse result);

    }
}
