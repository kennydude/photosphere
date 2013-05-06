package me.kennydude.sphere;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Renders the image used on the sphere
 */
public class SphereRendererTask {
	public static final String SPHERE_NS = "http://ns.google.com/photos/1.0/panorama/";

	Activity mContext;
	ProgressDialog mDialog;

	public SphereRendererTask(Activity c, final FileDownloaderTask.TaskDoneCallback callback){
		mDialog = new ProgressDialog(c);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setMessage( c.getString(R.string.please_wait) );
		mDialog.show();

		mContext = c;

		new Thread(new Runnable() {

			public Integer getXMPValue( String xmp, String name ){
				int x = xmp.indexOf( name + "=\"" ) + name.length() + 2;
				int e = xmp.indexOf( '"', x );
				Log.d("xmp", x + "- " + e);
				return Integer.parseInt( xmp.substring( x, e ) );
			}

			@Override
			public void run() {

				try{
					// Step 1: Get the metadata
					File mFile = new File( mContext.getCacheDir(), "temp" );
					BufferedReader mReader = new BufferedReader( new FileReader( mFile ) );

					String mXMP = "";
					while(true){

						mXMP += mReader.readLine( );
						if(mXMP.contains("</x:xmpmeta")){
							break;
						}
					}

					mXMP = mXMP.substring( mXMP.indexOf("<x:xmpmeta"), mXMP.indexOf("</x:xmpmeta>") );
					Log.d("xmp", mXMP);

					int fullWidth = getXMPValue(mXMP, "GPano:FullPanoWidthPixels"),
						fullHeight = getXMPValue(mXMP, "GPano:FullPanoHeightPixels"),
						cropWidth = getXMPValue(mXMP, "GPano:CroppedAreaImageWidthPixels"),
						cropHeight = getXMPValue(mXMP, "GPano:CroppedAreaImageHeightPixels"),
						x = getXMPValue(mXMP, "GPano:CroppedAreaLeftPixels"),
						y = getXMPValue(mXMP, "GPano:CroppedAreaTopPixels");

					// TODO: Find out image width/height and decode properly

					mReader.close();
					Bitmap mSource = BitmapFactory.decodeFile( mFile.getAbsolutePath() );

					Bitmap mTarget = Bitmap.createBitmap(fullWidth, fullHeight, Bitmap.Config.ARGB_4444);
					Canvas mCanvas = new Canvas(mTarget);

					mCanvas.drawRGB( 200, 0, 0 );

					Paint mPaint = new Paint();
					mPaint.setColor( Color.WHITE );
					mPaint.setStrokeWidth(1);
					for(int i = 0; i < fullHeight; i += 200){
						mCanvas.drawLine( 0, i, fullWidth, i, mPaint );
					}

					mCanvas.drawBitmap(mSource, null, new Rect(
							x, y, cropWidth, cropHeight
					), null);
					mCanvas.save();

					mTarget.compress( Bitmap.CompressFormat.PNG, 100, mContext.openFileOutput("re.png", Context.MODE_PRIVATE) );

					Texture mTexture = new Texture( mTarget );
					//mTexture.setClamping(true);
					TextureManager.getInstance().addTexture( "sphere", mTexture );

					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mDialog.dismiss();
							callback.onTaskSuccessful();
						}
					});
				} catch (Exception e){
					e.printStackTrace();
				}

			}
		}).start();

	}

}
