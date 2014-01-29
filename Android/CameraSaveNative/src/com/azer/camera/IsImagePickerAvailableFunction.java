//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

public class IsImagePickerAvailableFunction implements FREFunction
{
    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
            try
            {
                 return FREObject.newObject(CameraSaveExtension.context.isImagePickerAvailable());
            }
            catch (FREWrongThreadException exception)
            {
            	return null;
            }
    }
}