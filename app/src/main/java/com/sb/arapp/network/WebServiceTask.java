package com.sb.arapp.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;


import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

//import com.renyoolet.app.RegisterLogin.SessionLogout;

public class WebServiceTask extends AsyncTask<Object, Void, ServerResponse> {

	// private static final String TAG = "WebServiceTask";

	private Context mContext;
	private String mUrl;
	private List<NameValuePair> mData;
	private boolean mIsCancellable;
	private WebServiceTaskListener mCallback;
	private WebClient.WebClientTaskListener mCallback2;
	private ProgressDialog mDialog;
	private int mMethod;
	private String mMessage, mJsonString = "";
	public static final int HTTP_METHOD_GET = WebClient.HTTP_METHOD_GET;
	public static final int HTTP_METHOD_POST = WebClient.HTTP_METHOD_POST;
	public static final int HTTP_METHOD_POST_HEADER = WebClient.HTTP_METHOD_POST_HEADER;
	public static final int HTTP_METHOD_PUT = WebClient.HTTP_METHOD_PUT;
	public static final int HTTP_METHOD_DELETE = WebClient.HTTP_METHOD_DELETE;

	private int tag = -1;
	private boolean mFromService = false;
	WebClient client = null;

	public WebServiceTask(Context context, String url, int method,
                          List<NameValuePair> sendData, boolean isCancellable,
                          WebServiceTaskListener callback) {
		mContext = context;
		mUrl = url;
		mMethod = method;
		mData = sendData;
		mIsCancellable = isCancellable;
		mCallback = callback;
	}

	public WebServiceTask(Context context, String url, int method,
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

	public WebServiceTask(Context context, String url, int method,
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

	public WebServiceTask(Context context, String url, int method,
                          List<NameValuePair> sendData, String jsonString,
                          boolean fromService, boolean isCancellable,
                          WebServiceTaskListener callback, WebClient.WebClientTaskListener callback2) {
		mContext = context;
		mUrl = url;
		mMethod = method;
		mData = sendData;
		mJsonString = jsonString;
		mIsCancellable = isCancellable;
		mCallback = callback;
		mFromService = fromService;
		mCallback2 = callback2;
	}

	public WebServiceTask(Context context, String url, int method,
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

	public WebServiceTask(Context context, String url, String message,
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

	public WebServiceTask(Context context, String url, boolean fromService,
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
	/*	if(mUrl!=null && (mUrl.equalsIgnoreCase(Constants.CHANNEL_LIST_URL) ||
                mUrl.equalsIgnoreCase(Constants.FORCE_UPDATE_CHECK))){
			mFromService=true;
		}*/

		if (!mFromService) {
			if(mDialog!=null && mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog.cancel();
			}
			mDialog = new ProgressDialog(mContext);
			if (mMessage == null) {
				//mMessage = mContext.getResources().getString(
				//		R.string.stringPleaseWait);
                mMessage = "please wait...";
			}
			//mDialog.setMessage(Html.fromHtml(mContext.getResources().getString(R.string.stringPleaseWait)));
            mDialog.setMessage("please wait...");

            mDialog.setCancelable(mIsCancellable);
			mDialog.setOnKeyListener(new ProgressDialog.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
					if(mIsCancellable){
							cancel(true);
						}
					
					return false;
				}
	        });
			mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(mIsCancellable){
						cancel(true);
					}
				}
			});

			if(!((Activity) mContext).isFinishing())
			{
				if(((Activity) mContext)!=null) {
					mDialog.show();
				}
			}

		}
	}

	@Override
	protected ServerResponse doInBackground(Object... params) {
		ServerResponse serverResponse = null;
		
		if (mJsonString.length() != 0) {
			client = new WebClient(mUrl, mJsonString,mCallback2,mContext);
			client.execute(mMethod);

		} else {
			client = new WebClient(mUrl, mData,mContext);
			client.execute(mMethod);
		}
		int responseCode = client.getResponseCode();
		InputStream inputStream = client.getResponse();
		boolean timeout = client.isTimeout();

		Log.e("WebServiceTask","RESPONSE CODE: "+responseCode);


		if (inputStream != null) {
			if (responseCode == 200 || responseCode == 400) {
				serverResponse = new ServerResponse(false, inputStream,
						responseCode, timeout);

			}else if(responseCode == 401){// no valid session found





			/*	Intent intent = new Intent(mContext,
						LoginActivity.class);
				mContext.startActivity(intent);
				((Activity)mContext).finish();*/

				serverResponse = new ServerResponse(true, inputStream,
						responseCode, timeout);
			}else if(responseCode == 503){// service down

				serverResponse = new ServerResponse(false, inputStream,
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

			try {
				if ((mDialog != null) && mDialog.isShowing()) {
					mDialog.dismiss();
					mDialog.cancel();
				}
			}catch (Exception ex){

			}
			//if (mDialog.isShowing())
				//mDialog.dismiss();
		}
		if(result.getSuccessCode() == 503){
			/*AlertManager alr = new AlertManager();
			alr.showNegativeAlertDialog(mContext, "OOPS!!", "Service currently not avilable, please retry after sometimes", false, R.drawable.dialog_failure, true, "OK", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alr.errordialog.dismiss();
				}
			}, "", null, "", null);*/
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
		//public void onTaskCompletePut(HttpPut request);

	}

	public void cancelDialog(){
		try {
			if ((mDialog != null) && mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog.cancel();
			}
		}catch (Exception ex){

		}
	}

	public void loginUser() {

		String jsonString = toJsonLogin();
		Log.e("login", "LOGIN USER:::: " + jsonString);

	/*	fetchTask = new WebServiceTask(this, Constants.LOGIN_USER,
				WebServiceTask.HTTP_METHOD_POST_HEADER,null, jsonString,
				true, true, this);*/

		mContext = mContext;
		mUrl = "";
		mMethod = HTTP_METHOD_POST_HEADER;

		mData = null;
		mJsonString = jsonString;
		mIsCancellable = true;
		mCallback = mCallback;
		mFromService = true;

		if (WebClient.isConnected(mContext)) {
			//fetchTask.setTag(1);
			//fetchTask.execute();
			//setTag(11);
			this.execute();
		} else {
			//Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();

		}
	}

	private String toJsonLogin() {

		String email = "";
		String password = "";
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("email_id", email);
			// if(mPassword!=null && !mPassword.isEmpty()){
			jsonObj.put("password", password);



			return jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
