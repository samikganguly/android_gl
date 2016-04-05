package samik.app1;

import android.opengl.GLSurfaceView;
//EGLConfig needs to be imported from javax package
//import android.opengl.EGLConfig;
import android.opengl.GLES20;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class App1GLRenderer implements GLSurfaceView.Renderer {
	//called when OpenGL context is first created
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.5f, 0.2f, 0.5f, 1);
	}
	//called per frame
	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
	//called when viewport is resized
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}
}
