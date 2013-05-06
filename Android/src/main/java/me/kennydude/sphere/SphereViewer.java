package me.kennydude.sphere;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class SphereViewer extends Activity {

	GLSurfaceView mSurfaceView;
	SphereRenderer mSphereRenderer;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSurfaceView = new GLSurfaceView(this);
		mSurfaceView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});

		mSphereRenderer = new SphereRenderer();
		mSurfaceView.setRenderer(mSphereRenderer);

		setContentView(mSurfaceView);

		if( getIntent().getData() != null ){
			// Load from data URI
			new FileDownloaderTask( this, getIntent().getDataString(), new FileDownloaderTask.TaskDoneCallback() {
				@Override
				public void onTaskSuccessful() {
					// Now we need to build the actual bitmap
					new SphereRendererTask(SphereViewer.this, new FileDownloaderTask.TaskDoneCallback() {
						@Override
						public void onTaskSuccessful() {
							mSphereRenderer.updateTextures();
						}

						@Override
						public void onTaskFailure() {
							//To change body of implemented methods use File | Settings | File Templates.
						}
					});
				}

				@Override
				public void onTaskFailure() {
					// TODO
				}
			});
		}
	}

	float xpos, ypos;//, touchTurn, touchTurnUp;

	public boolean onTouchEvent(MotionEvent me) {
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			mSphereRenderer.touchTurn = 0;
			mSphereRenderer.touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			mSphereRenderer.touchTurn = xd / -100f;
			mSphereRenderer.touchTurnUp = yd / -100f;

			mSurfaceView.requestRender();

			Log.d("sphere", "Move sphere");
			return true;
		}

		return true;
	}
}
