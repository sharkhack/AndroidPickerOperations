//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;

public class DeleteTempFileFunction  implements FREFunction {

	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{

		String filepath = null;
		try {
			filepath = passedArgs[0].getAsString();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FRETypeMismatchException e) {
			e.printStackTrace();
		} catch (FREInvalidObjectException e) {
			e.printStackTrace();
		} catch (FREWrongThreadException e) {
			e.printStackTrace();
		}
		
		if(filepath!=null){
			CameraSaveExtension.context.deleteTemporaryImageFile(filepath);
			
		} else if(CameraSaveExtension.context._cameraOutputPath!=null){
			CameraSaveExtension.context.deleteTemporaryImageFile(CameraSaveExtension.context._cameraOutputPath);
			
		}

		
		return null;
	}
}
