AndroidPickerOperations NativeExtension
===============

Air Native Extension for mobile camera and gallery features (Android picker and save gallery)

- This is an Air native extension that allows you to display native UI to pick images from the gallery or take a picture with the camera on Android.

- This project supports Google+ & Picasa Photos

- Save BitmapData to Gallery or Custom gallery (android)

- Supports create custom gallery folder

- Supports media connection scanner…

It has been developed by Azer Bulbul


USAGE

  ```actionscript
//Camera or Gallery
 	var androidCamera:Camera = new Camera();
	androidCamera.addEventListener(StatusEvent.STATUS, onResult);
	//if camera image capture = true … if open gallery image picker capture=false
	var isCameraCapture:boolean = false; 
	var issupprt:Boolean =androidCamera.browseForImage(isCameraCapture);
	if(issupprt == false){/*not supported code here*/}
	protected function onResult(e:StatusEvent):void{
  		trace("event:"+e.code + " - level:"+e.level);
  		if(e.code == 'IMAGEPATH' && e.level != ""){
  			var androidimagefilepath:String =  e.level;
			// now call the file read code….
  		} else{ 
  			//error code here....
  		}
 	 }
  
  //save BitmapData
  
  	var originalBd:BitmapData = new BitmapData(100,100,false,0x000000);

	// I change it because java bitmapdata channels are different from the ones on AS3

	var bd = new bitmapdata(100,100,false,0x000000);
  	bd.copyChannel(originalBd,originalBd.rect,new Point(0,0),BitmapDataChannel.BLUE,BitmapDataChannel.RED);
	bd.copyChannel(originalBd,originalBd.rect,new Point(0,0),BitmapDataChannel.GREEN,BitmapDataChannel.GREEN);
	bd.copyChannel(originalBd,originalBd.rect,new Point(0,0),BitmapDataChannel.RED,BitmapDataChannel.BLUE);
	
	var androidCamera:Camera = new Camera();
	androidCamera.addEventListener(StatusEvent.STATUS, onResult);
	var isWriteToCustomFolder:boolean = false; 
	androidCamere.bmpSave(bd,isWriteToCustomFolder);
  
  	protected function onResult(e:StatusEvent):void{
  		trace("event:"+e.code + " - level:"+e.level);
  		if(e.code == 'err'){
  			//didnt save image...
  		} else{ 
  			//saved ok code here....
  		}
  	}
