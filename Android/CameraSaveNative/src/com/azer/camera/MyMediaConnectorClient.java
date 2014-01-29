//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class MyMediaConnectorClient implements MediaScannerConnectionClient {

	String _fisier;
	MediaScannerConnection MEDIA_SCANNER_CONNECTION;

	public MyMediaConnectorClient(String nume) {
		Log.i("bfn","MyMediaConnectorClient:"+nume);
	    _fisier = nume;
	}
	
	public void setScanner(Context ctx){
		if(MEDIA_SCANNER_CONNECTION!=null) MEDIA_SCANNER_CONNECTION.disconnect(); 
		
		MEDIA_SCANNER_CONNECTION = new MediaScannerConnection(ctx,this); 
		MEDIA_SCANNER_CONNECTION.connect();
	}
	
	@Override
	public void onScanCompleted(String path, Uri uri) {
		//if(path.equals(_fisier))
		Log.i("bfn", "Scanned " + path + ":");
        Log.i("bfn", "-> uri=" + uri);
	    
        MEDIA_SCANNER_CONNECTION.disconnect();
	}
	
	@Override
	public void onMediaScannerConnected() {
			Log.i("bfn","onMediaScannerConnected:"+_fisier);
			try{
			MEDIA_SCANNER_CONNECTION.scanFile(_fisier, null);
			} catch (java.lang.IllegalStateException e){
		       }
	}

	public void dispose(){
		try{
			if(MEDIA_SCANNER_CONNECTION!=null) MEDIA_SCANNER_CONNECTION.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e){
			e.printStackTrace(); 
		}
		finally{}
		
		MEDIA_SCANNER_CONNECTION = null;
	}
}
