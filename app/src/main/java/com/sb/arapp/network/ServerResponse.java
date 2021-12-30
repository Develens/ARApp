package com.sb.arapp.network;

import java.io.InputStream;

public class ServerResponse {
	private boolean networkIssue;
	private InputStream inputStream;
	private int successCode;
	private int tag;
	private Object additionalData;
	private boolean timeout;
	
	public ServerResponse(boolean networkIssue, InputStream inputStream,
			int successCode, boolean timeout) {
		this.networkIssue = networkIssue;
		this.inputStream = inputStream;
		this.successCode = successCode;
		this.timeout = timeout;

	}

	public boolean isNetworkIssue() {
		return networkIssue;
	}

	public void setNetworkIssue(boolean networkIssue) {
		this.networkIssue = networkIssue;
	}

	public boolean isTimeOut() {
		return timeout;
	}

	public void setTimeOut(boolean timeout) {
		this.timeout = timeout;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(int successCode) {
		this.successCode = successCode;
	}

	public String getResponseAsString() {
		return convertStreamToString(inputStream);

	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public void setAdditionalData(Object additionalData){
		this.additionalData=additionalData;
	}
	
	public Object getAdditionalData(){
		return this.additionalData;
	}
	
	private String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	
}
