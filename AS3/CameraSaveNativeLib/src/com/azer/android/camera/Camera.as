package com.azer.android.camera
{
	import flash.display.BitmapData;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	
	import mx.graphics.shaderClasses.ExclusionShader;

	public class Camera  extends EventDispatcher implements IEventDispatcher
	{
		private static var extContext:ExtensionContext = null;
		
		public function Camera(target:*=null)
		{
			if ( !extContext )
			{
				extContext = ExtensionContext.createExtensionContext("com.azer.android.camera",null);
			}
		}
		public function bmpSave(b:BitmapData,isWriteCustomFolder:Boolean = true):void{
			extContext.addEventListener( StatusEvent.STATUS, statusHandler);
			extContext.call("saveBitmapData",b,isWriteCustomFolder);
		}
	
		public function isImagePickerAvailable() : Boolean
		{
			return extContext.call("isImagePickerAvailable");
		}
		public function isCameraAvailable() : Boolean
		{
			return extContext.call("isCameraAvailable");
		}
		
		public function browseForImage(isCameraCapture:Boolean = false):Boolean{
			var available:Boolean = false;
			
			if(isCameraCapture){
				available = isCameraAvailable();
			} else {
				available = isImagePickerAvailable();
			}
			
			if(available){
				extContext.addEventListener( StatusEvent.STATUS, statusHandler);
				extContext.call("browseForImage",allowVideoCapture);
			}
			return available;
		}
		
		public function deleteTemporaryFile(path:String = null):void{
			
			extContext.call("deleteTempFile",path);
		}
		
		private function statusHandler(e:StatusEvent):void
		{
			dispatchEvent(e);
		}
		
		public function dispose():void{
			try{
				extContext.dispose();
			} catch(e:*){}
			extContext = null;
		}
	}
}