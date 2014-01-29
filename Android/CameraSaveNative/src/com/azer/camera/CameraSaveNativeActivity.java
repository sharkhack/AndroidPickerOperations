//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class CameraSaveNativeActivity extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(CameraSaveExtension.context!=null){
			CameraSaveExtension.context.onCreatePickerActivity(this);
		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
            super.onActivityResult(requestCode, resultCode, data);
            
            if(CameraSaveExtension.context!=null){
            	CameraSaveExtension.context.onPickerActivityResult(requestCode, resultCode, data);
            }
            
    }
	
	@Override
    protected void onDestroy()
    {
           super.onDestroy();
    }
	
}