package samik.app1;

import android.opengl.GLSurfaceView;
//EGLConfig needs to be imported from javax package
//import android.opengl.EGLConfig;
import android.opengl.GLES20;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.util.Log;
import android.opengl.Matrix;

public class App1GLRenderer implements GLSurfaceView.Renderer {
	private static final String logTag = "samik.app1 : ";
	private static final float cube[] = new float[]{
		// Vx     Vy    Vz     Nx     Ny     Nz
		//front
		-1.0f, -1.0f,  1.0f,     0,     0,  1.0f,
		 1.0f, -1.0f,  1.0f,     0,     0,  1.0f,
		 1.0f,  1.0f,  1.0f,     0,     0,  1.0f,
		
		 1.0f,  1.0f,  1.0f,     0,     0,  1.0f,
		-1.0f,  1.0f,  1.0f,     0,     0,  1.0f,
		-1.0f, -1.0f,  1.0f,     0,     0,  1.0f,
		//back
		-1.0f,  1.0f, -1.0f,     0,     0, -1.0f,
		 1.0f,  1.0f, -1.0f,     0,     0, -1.0f,
		 1.0f, -1.0f, -1.0f,     0,     0, -1.0f,
		
		 1.0f, -1.0f, -1.0f,     0,     0, -1.0f,
		-1.0f, -1.0f, -1.0f,     0,     0, -1.0f,
		-1.0f,  1.0f, -1.0f,     0,     0, -1.0f,
		//top
		-1.0f,  1.0f,  1.0f,     0,  1.0f,     0,
		 1.0f,  1.0f,  1.0f,     0,  1.0f,     0,
		 1.0f,  1.0f, -1.0f,     0,  1.0f,     0,
		
		 1.0f,  1.0f, -1.0f,     0,  1.0f,     0,
		-1.0f,  1.0f, -1.0f,     0,  1.0f,     0,
		-1.0f,  1.0f,  1.0f,     0,  1.0f,     0,
		//bottom
		-1.0f, -1.0f, -1.0f,     0, -1.0f,     0,
		 1.0f, -1.0f, -1.0f,     0, -1.0f,     0,
		 1.0f, -1.0f,  1.0f,     0, -1.0f,     0,
		
		 1.0f, -1.0f,  1.0f,     0, -1.0f,     0,
		-1.0f, -1.0f,  1.0f,     0, -1.0f,     0,
		-1.0f, -1.0f, -1.0f,     0, -1.0f,     0,
		//right
		 1.0f, -1.0f,  1.0f,  1.0f,     0,     0,
		 1.0f, -1.0f, -1.0f,  1.0f,     0,     0,
		 1.0f,  1.0f, -1.0f,  1.0f,     0,     0,
		
		 1.0f,  1.0f, -1.0f,  1.0f,     0,     0,
		 1.0f,  1.0f,  1.0f,  1.0f,     0,     0,
		 1.0f, -1.0f,  1.0f,  1.0f,     0,     0,
		//left
		-1.0f,  1.0f,  1.0f, -1.0f,     0,     0,
		-1.0f,  1.0f, -1.0f, -1.0f,     0,     0,
		-1.0f, -1.0f, -1.0f, -1.0f,     0,     0,
		
		-1.0f, -1.0f, -1.0f, -1.0f,     0,     0,
		-1.0f, -1.0f,  1.0f, -1.0f,     0,     0,
		-1.0f,  1.0f,  1.0f, -1.0f,     0,     0	
	};
	private static final String mvUniform = "mvTransform",
		mvpUniform = "mvpTransform", nUniform = "nTransform",
		lampUniform = "lampPos", matColUniform = "materialColor",
		vPosAttrib = "vPos", nPosAttrib = "nPos";
	private static float mvMatrix[] = new float[16],
		mvpMatrix[] = new float[16], nMatrix[] = new float[16], 
		lampPos[] = {5.0f, 5.0f, -5.0f, 1.0f},
		matCol[] = {0.5f, 0.8f, 0.5f};
	static {
		Log.d(logTag, "COMPUTING VIEW AND NOMAL TRANSFORMS");
		//view
		Matrix.setLookAtM(
			mvMatrix, 0,
			5.0f, 5.0f, 10.0f,
			   0,    0,     0,
			   0, 1.0f,     0);
		//normal
		Matrix.invertM(nMatrix, 0, mvMatrix, 0);
	}
	private final String vertexShaderSrc, fragmentShaderSrc;
	private int program, vertexShader, fragmentShader;
	private int vboID[];
	private StringBuffer shaderLog;

	public App1GLRenderer(String v, String f) {
		Log.d(logTag, "FETCHING SHADER SOURCES");
		vertexShaderSrc = v;
		fragmentShaderSrc = f;
		vboID = new int[1];
		shaderLog = new StringBuffer();
	}

	//called when OpenGL context is first created
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.5f, 0.2f, 0.5f, 1);
		Log.d(logTag, "STARTING OPENGL TASKS, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		Log.d(logTag, "FACE CULLING=" + 
			GLES20.glIsEnabled(GLES20.GL_CULL_FACE));
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		Log.d(logTag, "ENABLED DEPTH TEST, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		GLES20.glGenBuffers(1, vboID, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID[0]);
		//allocate a direct native byte buffer
		Log.d(logTag, "UPLOADING CUBE DATA, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		ByteBuffer cubeByteBuf = ByteBuffer.allocateDirect(
			cube.length * Float.SIZE / 8)
			.order(ByteOrder.nativeOrder());
		FloatBuffer cubeFloatBuf = cubeByteBuf.asFloatBuffer();
		cubeFloatBuf.put(cube).position(0);
		GLES20.glBufferData(
			GLES20.GL_ARRAY_BUFFER,
			cubeFloatBuf.capacity() * Float.SIZE / 8,
			cubeFloatBuf, GLES20.GL_STATIC_DRAW);
		Log.d(logTag, "UPLOADED CUBE DATA, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		program = GLES20.glCreateProgram();
		vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		fragmentShader = GLES20.glCreateShader(
			GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(vertexShader, vertexShaderSrc);
		GLES20.glShaderSource(fragmentShader, fragmentShaderSrc);
		GLES20.glCompileShader(vertexShader);

		shaderLog.append("vertex shader compilation log:\n");
		shaderLog.append(GLES20.glGetShaderInfoLog(vertexShader));
		Log.v(logTag, shaderLog.toString());
		shaderLog.setLength(0);

		GLES20.glCompileShader(fragmentShader);

		shaderLog.append("fragment shader compilation log:\n");
		shaderLog.append(GLES20.glGetShaderInfoLog(fragmentShader));
		Log.v(logTag, shaderLog.toString());
		shaderLog.setLength(0);

		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);
		GLES20.glUseProgram(program);
		Log.d(logTag, "SHADER PROGRAM COMPLETE, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		int mv = GLES20.glGetUniformLocation(program, mvUniform);
		int norm = GLES20.glGetUniformLocation(program, nUniform);
		int lamp = GLES20.glGetUniformLocation(program, lampUniform);
		int mCol = GLES20.glGetUniformLocation(program, matColUniform);
		int vPos = GLES20.glGetAttribLocation(program, vPosAttrib);
		int nPos = GLES20.glGetAttribLocation(program, nPosAttrib);
		Log.d(logTag, "UPLOADING UNIFORMS, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		GLES20.glUniformMatrix4fv(mv, 1, false, mvMatrix, 0);
		GLES20.glUniformMatrix4fv(norm, 1, false, nMatrix, 0);
		GLES20.glUniform4fv(lamp, 1, lampPos, 0);
		GLES20.glUniform3fv(mCol, 1, matCol, 0);
		Log.d(logTag, "UPLOADED UNIFORMS, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		GLES20.glEnableVertexAttribArray(vPos);
		GLES20.glEnableVertexAttribArray(nPos);
		GLES20.glVertexAttribPointer(vPos, 3, GLES20.GL_FLOAT, false,
			6 * Float.SIZE / 8, 0);
		GLES20.glVertexAttribPointer(nPos, 3, GLES20.GL_FLOAT, false,
			6 * Float.SIZE / 8, 3 * Float.SIZE / 8);
	}
	//called per frame
	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
			| GLES20.GL_DEPTH_BUFFER_BIT);
		Log.d(logTag, "DRAWING, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
	}
	//called when viewport is resized
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		Log.d(logTag, "UPLOADING MVP MATRIX, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
		float tmp[] = new float[16];
		Matrix.perspectiveM(tmp, 0, 45, 
			(float)width / (float)height, 0.1f, 100);
		//model view projection matrix
		Matrix.multiplyMM(mvpMatrix, 0, tmp, 0, mvMatrix, 0);
		int mvp = GLES20.glGetUniformLocation(program, mvpUniform);
		GLES20.glUniformMatrix4fv(mvp, 1, false, mvpMatrix, 0);
		Log.d(logTag, "UPLOADED MVP MATRIX, GL code=0x" + 
			Integer.toHexString(GLES20.glGetError()));
	}
	//call this manually when activity is destroyed(not required?)
	/*public void onDestroy() {
		GLES20.glDeleteBuffers(1, vboID, 0);
		GLES20.glDetachShader(program, vertexShader);
		GLES20.glDetachShader(program, fragmentShader);
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		GLES20.glDeleteProgram(program);
	}*/
}
