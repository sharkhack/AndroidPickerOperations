//
//  Created by Azer Bulbul on 12/29/13.
//  Copyright (c) 2013 Azer Bulbul. All rights reserved.
//

package com.azer.camera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import com.adobe.fre.FREBitmapData;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;

public class CameraSaveExtensionContext extends FREContext {

	@Override
	public void dispose() 
	{
		cleardata();
		CameraSaveExtension.context = null;

	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("saveBitmapData", new CameraSaveFunction());
		functionMap.put("isImagePickerAvailable", new IsImagePickerAvailableFunction());
		functionMap.put("isCameraAvailable", new IsCameraAvailableFunction());
		functionMap.put("deleteTempFile", new DeleteTempFileFunction());
		functionMap.put("browseForImage", new browseForImage());
		return functionMap;
	}

	public void cleardata(){
		if(bm!=null){
			try{bm.recycle();} 
			catch (Exception e) {e.printStackTrace();} 
			catch (Error e){e.printStackTrace(); }
		}

		if(inputValue!=null){
			try{inputValue.release();}
			catch(IllegalStateException e){e.printStackTrace();}
			catch (Exception e) {e.printStackTrace();} 
			catch (Error e){e.printStackTrace();}
		}
		
		bm = null;
		inputValue = null;
	}

	/*save image functions*/
	public Bitmap bm =null;
	public FREBitmapData inputValue = null;
	public MyMediaConnectorClient myscanner = null;

	private boolean resetExternalStorageMedia() {
		Boolean ret = true;
		try{
			Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
			Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
			getActivity().sendBroadcast(intent);
			ret = true;
		}
		catch (Exception e) {e.printStackTrace(); ret = false;}
		catch (Error e){e.printStackTrace(); ret = false;}
		
		return (ret); 
	}
	
	private void notifyMediaScannerService(String path) {
		MediaScannerConnection.scanFile(getActivity(),
	            new String[] { path }, null,
	            new MediaScannerConnection.OnScanCompletedListener() {
		        	public void onScanCompleted(String path, Uri uri) {}
	    		}
	    );
	}

	
	public void writeToCustomPath(){

		File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES + File.separator + "MyFolder" + File.separator);
	    File file = new File(path, String.valueOf(System.currentTimeMillis())+".jpg");
		
		if(!path.exists())
		{
			path.mkdirs();
		}
		
		String eventCode = "ok";

		OutputStream os_ = null;
		try{
			os_ = new FileOutputStream(file);
			this.bm.compress(Bitmap.CompressFormat.JPEG, 90, os_);
			os_.flush();
			os_.close();
			
			eventCode = "ok";
		}catch (FileNotFoundException e){
			e.printStackTrace();
			eventCode = "err";
		}catch (Exception e) {
			e.printStackTrace();
			eventCode = "err";
		}catch (Error e){
			e.printStackTrace();
			eventCode = "err";
		}finally{
			try {this.inputValue.release();} 
			catch (Exception e) {e.printStackTrace();} 
			catch (Error e) {e.printStackTrace();}
			finally{this.inputValue = null;}
		}

		if(eventCode != "err"){
			try{
				MediaScannerConnection.scanFile(getActivity(),
		                new String[] { file.toString() }, null,
		                new MediaScannerConnection.OnScanCompletedListener() {
		            public void onScanCompleted(String path, Uri uri) {
		                //Log.i("ExternalStorage", "Scanned " + path + ":");
		                //Log.i("ExternalStorage", "-> uri=" + uri);
		            }
		        });
			}
			catch (Exception e) {e.printStackTrace();}
			catch (Error e){e.printStackTrace();}
		}
		
		cleardata();

		this.inputValue = null;
		this.bm = null;
		dispatchStatusEventAsync(eventCode, "status");
	}
	
	public void writeToDisk(){
		SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
		String format = s.format(new Date());

		ContentValues values = new ContentValues();
		values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(android.provider.MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(android.provider.MediaStore.Images.Media.TITLE, "IMG_"+ format);
		values.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "2020");
		
		Uri uri_ =this.getActivity().getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		

		String eventCode = "ok";

		OutputStream os_ = null;
		try{
			os_ = this.getActivity().getContentResolver().openOutputStream(uri_);
			this.bm.compress(Bitmap.CompressFormat.JPEG, 90, os_);
			os_.flush();
			os_.close();
			
			if(resetExternalStorageMedia() == false){
				notifyMediaScannerService(uri_.getPath());
			}
			eventCode = "ok";
		}catch (FileNotFoundException e){
			e.printStackTrace();
			eventCode = "err";
		}catch (Exception e) {
			e.printStackTrace();
			eventCode = "err";
		}catch (Error e){
			e.printStackTrace();
			eventCode = "err";
		}finally{
			try {this.inputValue.release(); eventCode = "ok";} 
			catch (Exception e) {e.printStackTrace();} 
			catch (Error e) {e.printStackTrace();} 
			finally{this.inputValue = null;}
		}

		
		cleardata();

		this.inputValue = null;
		this.bm = null;
		dispatchStatusEventAsync(eventCode, "status");
	}

	/*browse image functions*/

	public static final int NO_ACTION = -1;
	public static final int GALLERY_IMAGES_ONLY_ACTION = 0;
	public static final int CAMERA_IMAGE_ACTION = 1;

	private int _currentAction = NO_ACTION;


	public Boolean isImagePickerAvailable()
	{
		return isActionAvailable(GALLERY_IMAGES_ONLY_ACTION);
	}

	public void displayImagePicker()
	{
		startPickerActivityForAction(GALLERY_IMAGES_ONLY_ACTION);
	}

	public Boolean isCameraAvailable()
	{
		Boolean hasCameraFeature = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		Boolean hasFrontCameraFeature = getActivity().getPackageManager().hasSystemFeature("android.hardware.camera.front");
		Boolean isAvailable = (hasFrontCameraFeature || hasCameraFeature) && (isActionAvailable(CAMERA_IMAGE_ACTION));
		return isAvailable;
	}


	private Boolean isActionAvailable(int action)
	{
		final PackageManager packageManager = getActivity().getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(getIntentForAction(action), PackageManager.MATCH_DEFAULT_ONLY);

		return list.size() > 0;
	}
	
	private Intent getIntentForAction(int action)
	{
		Intent intent;
		switch (action)
		{
		case GALLERY_IMAGES_ONLY_ACTION:
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			//intent = new Intent(Intent.ACTION_GET_CONTENT);
			//intent.setType("image/*");
			return intent;

		case CAMERA_IMAGE_ACTION:
			return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		default:
			return null;
		}
	}

	public void displayCamera()
	{
		startPickerActivityForAction(CAMERA_IMAGE_ACTION);
	}

	private void handleResultForAction(Intent data, int action)
	{
		if (action == GALLERY_IMAGES_ONLY_ACTION )
		{
			handleResultForGallery(data);
		}
		else if (action == CAMERA_IMAGE_ACTION )
		{
			handleResultForImageCamera(data);
		}

	}

	private String selectedImagePath;
	private void handleResultForGallery(Intent data)
	{
		Uri selectedImageUri = null;

		try{
			selectedImageUri = data.getData();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e){
			e.printStackTrace(); 
		}
		finally{}

		String fileManagerString = null;

		if(selectedImageUri!=null){
			try{		
				fileManagerString = selectedImageUri.getPath();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e){
				e.printStackTrace(); 
			}
			finally{}

			try{
				selectedImagePath = getPath(selectedImageUri);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e){
				e.printStackTrace(); 
			}
			finally{}
			
			if(selectedImagePath == null){
				if(fileManagerString!=null){
					
					selectedImagePath = fileManagerString;
				}
				
			}
		}

		if (selectedImagePath.startsWith("http")) {
			//
		}
		else if (selectedImagePath.startsWith("content://com.google.android.gallery3d")) {
            try {
				processPicasaMedia(selectedImagePath, ".jpg");
				selectedImagePath = null;
				
			} catch (Exception e) {
				selectedImagePath = null;
				e.printStackTrace();
			} finally{
				
				selectedImagePath = _cameraOutputPath;
			}
        } else if (selectedImagePath.startsWith("content://com.google.android.apps.photos.content")
                || selectedImagePath.startsWith("content://com.android.providers.media.documents")) {
            try {
				processGooglePhotosMedia(selectedImagePath, ".jpg");
				selectedImagePath = null;
			} catch (Exception e) {
				selectedImagePath = null;
				e.printStackTrace();
			} finally{
				
				selectedImagePath = _cameraOutputPath;
			}
        }
		

		if(selectedImagePath!=null){
			dispatchResultEvent("IMAGEPATH", selectedImagePath);
		}
		else if(fileManagerString!=null){

			dispatchResultEvent("IMAGEPATH", fileManagerString);
		}
		else if(_cameraOutputPath!=null){

			dispatchResultEvent("IMAGEPATH", _cameraOutputPath);
		}
		else {
			dispatchResultEvent("IMAGEPATH", "");

		}
	}

	private void handleResultForImageCamera(Intent data)
	{
		if(_cameraOutputPath!=null){
			dispatchResultEvent("IMAGEPATH", _cameraOutputPath);
		}
		else {
			dispatchResultEvent("IMAGEPATH", "");

		}

		//deleteTemporaryImageFile(_cameraOutputPath);
	}

	CameraSaveNativeActivity _pickerActivity;

	private void startPickerActivityForAction(int action)
	{
		_currentAction = action;
		Intent intent = new Intent(getActivity().getApplicationContext(), CameraSaveNativeActivity.class);
		getActivity().startActivity(intent);
	}

	public void onCreatePickerActivity(CameraSaveNativeActivity pickerActivity)
	{
		if (_currentAction != NO_ACTION)
		{
			Intent intent = getIntentForAction(_currentAction);
			prepareIntentForAction(intent, _currentAction);
			_pickerActivity = pickerActivity;
			_pickerActivity.startActivityForResult(intent, _currentAction);
		}

	}

	private void prepareIntentForAction(Intent intent, int action)
	{
		if (action == CAMERA_IMAGE_ACTION)
		{
			prepareIntentForPictureCamera(intent);
		}

	}

	public void onPickerActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (requestCode == _currentAction && resultCode == Activity.RESULT_OK)
		{
			handleResultForAction(data, _currentAction);
		}
		else
		{
			dispatchResultEvent("DID_CANCEL");
		}

	}

	private void dispatchResultEvent(String eventName, String message)
	{
		_currentAction = NO_ACTION;
		if (_pickerActivity != null)
		{
			_pickerActivity.finish();
		}

		dispatchStatusEventAsync(eventName, message);
	}


	private void dispatchResultEvent(String eventName)
	{
		dispatchResultEvent(eventName, "OK");
	}


	private String getPath(Uri selectedImage)
	{
		final String[] filePathColumn = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
		Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

		// Some devices return an URI of com.android instead of com.google.android
		if (selectedImage.toString().startsWith("content://com.android.gallery3d.provider"))
		{
			selectedImage = Uri.parse( selectedImage.toString().replace("com.android.gallery3d", "com.google.android.gallery3d") );
		}

		if (cursor != null)
		{
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);

			// if it is a picassa image on newer devices with OS 3.0 and up
			if (selectedImage.toString().startsWith("content://com.google.android.gallery3d")
					|| selectedImage.toString().startsWith("content://com.google.android.apps.photos.content")
					|| selectedImage.toString().startsWith("content://com.android.providers.media.documents")
					)
			{
				columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
				return selectedImage.toString();
			}
			else
			{
				return cursor.getString(columnIndex);
			}
		}
		else if ( selectedImage != null && selectedImage.toString().length() > 0 )
		{
			return selectedImage.toString();
		}
		else return null;
	}


	protected void processPicasaMedia(String path, String extension) throws Exception {

		try {
			InputStream inputStream = getActivity().getContentResolver().openInputStream(Uri.parse(path));

			File tempFile = getTemporaryImageFile(extension);
			_cameraOutputPath = tempFile.getAbsolutePath();

			BufferedOutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(_cameraOutputPath));
			byte[] buf = new byte[2048];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outStream.write(buf, 0, len);
			}
			inputStream.close();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	protected void processGooglePhotosMedia(String path, String extension) throws Exception {

		String retrievedExtension = checkExtension(Uri.parse(path));
		if (retrievedExtension != null && !TextUtils.isEmpty(retrievedExtension)) {
			extension = "." + retrievedExtension;
		}
		try {
			File tempFile = getTemporaryImageFile(extension);
			_cameraOutputPath = tempFile.getAbsolutePath();

			ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver()
					.openFileDescriptor(Uri.parse(path), "r");

			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

			InputStream inputStream = new FileInputStream(fileDescriptor);

			BufferedInputStream reader = new BufferedInputStream(inputStream);

			BufferedOutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(_cameraOutputPath));
			byte[] buf = new byte[2048];
			int len;
			while ((len = reader.read(buf)) > 0) {
				outStream.write(buf, 0, len);
			}
			outStream.flush();
			outStream.close();
			inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	public String _cameraOutputPath = null;

	private void prepareIntentForPictureCamera(Intent intent)
	{

		File tempFile = getTemporaryImageFile(".jpg");
		_cameraOutputPath = tempFile.getAbsolutePath();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

	}

	private File getTemporaryImageFile( String extension )
	{
		// Get or create folder for temp files
		File tempFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"AzrImagePicker");
		if (!tempFolder.exists())
		{
			tempFolder.mkdir();
			try
			{
				new File(tempFolder, ".nomedia").createNewFile();
			}
			catch (Exception e) {}
		}

		// Create temp file
		return new File(tempFolder, String.valueOf(System.currentTimeMillis())+extension);
	}

	public void deleteTemporaryImageFile(String filePath)
	{
		try{
			new File(filePath).delete();
		} catch(Exception e) {}
	}


	public String checkExtension(Uri uri) {

		String extension = "";

		// The query, since it only applies to a single document, will only
		// return
		// one row. There's no need to filter, sort, or select fields, since we
		// want
		// all fields for one document.
		Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

		try {
			// moveToFirst() returns false if the cursor has 0 rows. Very handy
			// for
			// "if there's anything to look at, look at it" conditionals.
			if (cursor != null && cursor.moveToFirst()) {

				// Note it's called "Display Name". This is
				// provider-specific, and might not necessarily be the file
				// name.
				String displayName = cursor.getString(cursor
						.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				int position = displayName.indexOf(".");
				extension = displayName.substring(position + 1);

				//int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
				// If the size is unknown, the value stored is null. But since
				// an
				// int can't be null in Java, the behavior is
				// implementation-specific,
				// which is just a fancy term for "unpredictable". So as
				// a rule, check if it's null before assigning to an int. This
				// will
				// happen often: The storage API allows for remote files, whose
				// size might not be locally known.
				//String size = null;
				//if (!cursor.isNull(sizeIndex)) {
					// Technically the column stores an int, but
					// cursor.getString()
					// will do the conversion automatically.
					//size = cursor.getString(sizeIndex);
				//} else {
				//	size = "Unknown";
				//}
			}
		} finally {
			cursor.close();
		}
		return extension;
	}

	public static String getDirectory(String foldername) {
		//      if (!foldername.startsWith(".")) {
		//          foldername = "." + foldername;
		//      }
		File directory = null;
		directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + foldername);
		if (!directory.exists()) {
			directory.mkdir();
			
			try
			{
				new File(directory, ".nomedia").createNewFile();
			}
			catch (Exception e) {}
			
		}
		return directory.getAbsolutePath();
	}

	public static String getFileExtension(String filename) {
		String extension = "";
		try {
			extension = filename.substring(filename.lastIndexOf(".") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extension;
	}
}
