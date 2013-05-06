package me.kennydude.sphere;

import android.opengl.GLSurfaceView;
import android.util.Log;
import com.threed.jpct.*;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: kennydude
 * Date: 05/05/13
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
public class SphereRenderer implements GLSurfaceView.Renderer {
	public static final int SPHERE_RADIUS = 100;

	private RGBColor back = new RGBColor(50, 50, 100);
	public float touchTurn, touchTurnUp;

	FrameBuffer fb;
	Object3D mSphere;
	World mWorld;

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);

		//Texture texture = new Texture(4, 4, RGBColor.BLACK);
		//TextureManager.getInstance().addTexture("sphere", texture);

		//setupWorld();
	}

	public void setupWorld(){
		mSphere = Primitives.getSphere(SPHERE_RADIUS);
		mSphere.setCulling(false);
		mSphere.setTexture("sphere");
		mSphere.build();

		mWorld = new World();
		mWorld.setAmbientLight(200,200,200);

		mWorld.addObject(mSphere);

		Camera camera = mWorld.getCamera();
		camera.moveCamera( Camera.CAMERA_MOVEOUT, 30 );
		//camera.setPosition( mSphere.getTransformedCenter() );
		camera.lookAt( mSphere.getTransformedCenter() );
		//camera.moveCamera(Camera.CAMERA_MOVEIN, 5);

		MemoryHelper.compact();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		fb.clear(back);

		if(mWorld != null){
			if (touchTurn != 0) {
				mSphere.rotateZ(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				mSphere.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}
			mWorld.renderScene(fb);
			mWorld.draw(fb);
		}
		fb.display();
	}

	public void updateTextures() {
		setupWorld();
	}
}
