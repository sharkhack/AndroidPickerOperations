//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;
import com.adobe.fre.FREBitmapData;


public class CameraSaveFunction implements FREFunction {

	public static final String NAME = "invertBitmapData";

	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{	
		
		Boolean isWriteCustomFolder = true;
		try {
			isWriteCustomFolder = passedArgs[1].getAsBool();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FRETypeMismatchException e) {
			e.printStackTrace();
		} catch (FREInvalidObjectException e) {
			e.printStackTrace();
		} catch (FREWrongThreadException e) {
			e.printStackTrace();
		}
		
		try {

			CameraSaveExtension.context.inputValue = (FREBitmapData)passedArgs[0];
			CameraSaveExtension.context.inputValue.acquire();
			int srcWidth = CameraSaveExtension.context.inputValue.getWidth();
			int srcHeight =CameraSaveExtension.context.inputValue.getHeight();
			
			CameraSaveExtension.context.inputValue.release();
			CameraSaveExtension.context.inputValue.acquire();
			
			if(srcWidth>0){
				CameraSaveExtension.context.bm = Bitmap.createBitmap(srcWidth, srcHeight, Config.ARGB_8888);
				CameraSaveExtension.context.bm.copyPixelsFromBuffer( CameraSaveExtension.context.inputValue.getBits() );
				if(isWriteCustomFolder == true){
					
					CameraSaveExtension.context.writeToCustomPath();
				} else {
					CameraSaveExtension.context.writeToDisk();
					
				}
				
			} else {
				
				CameraSaveExtension.context.cleardata();
			}

		} catch (Exception e) {
			e.printStackTrace();
			CameraSaveExtension.context.cleardata();
			CameraSaveExtension.context.dispatchStatusEventAsync("err", "status");
		}
		catch (Error e){
			
			e.printStackTrace(); 
			CameraSaveExtension.context.cleardata();
			CameraSaveExtension.context.dispatchStatusEventAsync("err", "status");
		}
		finally{
			                       
		}

		
		return null;
	}
	
}
