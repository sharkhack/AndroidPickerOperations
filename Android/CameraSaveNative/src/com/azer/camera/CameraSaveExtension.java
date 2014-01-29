//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class CameraSaveExtension implements FREExtension {
	
	public static CameraSaveExtensionContext context;
	
	
	public FREContext createContext(String contextType) {
		return context = new CameraSaveExtensionContext();
	}

	
	public void dispose() {
		
		if(context!=null){
			context.cleardata();
		}
		
		context = null;
	}

	
	public void initialize() {}
	
}
