package samik.app1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

class App1GLSurfaceView extends GLSurfaceView {
	private final App1GLRenderer app1GLRenderer;

	//constructor(Context) can't be used since it is initialized from 
	//layout XML file, must use constructor(Context, AttributeSet)
	public App1GLSurfaceView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		setEGLContextClientVersion(2);
		app1GLRenderer = new App1GLRenderer();
		setRenderer(app1GLRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}
