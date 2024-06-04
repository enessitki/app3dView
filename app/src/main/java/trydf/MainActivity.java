//package com.example.app3dview;
//
//import android.content.Context;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.Matrix;
//import android.os.Bundle;
//import android.view.MotionEvent;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//import java.nio.ShortBuffer;
//import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//public class MainActivity extends AppCompatActivity {
//
//    private GLSurfaceView mGLView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mGLView = new MyGLSurfaceView(this);
//        setContentView(mGLView);
//
//
//    }
//
//    class MyGLSurfaceView extends GLSurfaceView {
//
//        private final MyGLRenderer renderer;
//
//        public MyGLSurfaceView(Context context) {
//            super(context);
//            setEGLContextClientVersion(2);
//            renderer = new MyGLRenderer(context);
//            setRenderer(renderer);
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//            test();
//        }
//
//        private void test(){
//            Thread videoThread = new Thread(() -> {
//                while (true) {
//                    try {
//
//                        runOnUiThread(this::requestRender);
//
//                        TimeUnit.MILLISECONDS.sleep(10);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            videoThread.start();
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            if (event != null) {
//                final float x = event.getX();
//                final float y = event.getY();
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                renderer.handleTouchPress(x, y);
//                            }
//                        });
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                renderer.handleTouchDrag(x, y);
//                                requestRender();
//                            }
//                        });
//                        break;
//                }
//                return true;
//            } else {
//                return super.onTouchEvent(event);
//            }
//        }
//    }
//
//    class MyGLRenderer implements GLSurfaceView.Renderer {
//
//        private final Context context;
//
//        private FloatBuffer vertexBuffer;
//        private ShortBuffer drawListBuffer;
//
//        private float[] projectionMatrix = new float[16];
//        private float[] viewMatrix = new float[16];
//        private float[] modelMatrix = new float[16];
//        private float[] mvpMatrix = new float[16];
//        private float[] rotationMatrix = new float[16];
//
//        private int mProgram;
//        private int mPositionHandle;
//        private int mMVPMatrixHandle;
//
//        private int vertexCount;
//
//        private float angleX = -40.0f;
//        private float angleY = 0.0f;
//
//        public MyGLRenderer(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//
//            loadOBJ(R.raw.cow);
//
//            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
//            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
//
//            mProgram = GLES20.glCreateProgram();
//            GLES20.glAttachShader(mProgram, vertexShader);
//            GLES20.glAttachShader(mProgram, fragmentShader);
//            GLES20.glLinkProgram(mProgram);
//
//            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//
//            test();
//        }
//
//        @Override
//        public void onSurfaceChanged(GL10 gl, int width, int height) {
//            GLES20.glViewport(0, 0, width, height);
//            float ratio = (float) width / height;
//            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 20);
//        }
//
//        @Override
//        public void onDrawFrame(GL10 gl) {
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//            GLES20.glUseProgram(mProgram);
//
//            Matrix.setIdentityM(modelMatrix, 0);
//            Matrix.rotateM(modelMatrix, 0, angleX, 1.0f, 0.0f, 0.0f); // X ekseninde döndürme
//            Matrix.rotateM(modelMatrix, 0, angleY, 0.0f, 1.0f, 0.0f); // Y ekseninde döndürme
//
//            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
//            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//            Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
//
//            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
//
//            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//            GLES20.glEnableVertexAttribArray(mPositionHandle);
//
//            GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexCount, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
//
//            GLES20.glDisableVertexAttribArray(mPositionHandle);
//        }
//
//        private void loadOBJ(int objResourceId) {
//            InputStream inputStream = context.getResources().openRawResource(objResourceId);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            ArrayList<Float> vertices = new ArrayList<>();
//            ArrayList<Short> faces = new ArrayList<>();
//
//            String line;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    String[] parts = line.split("\\s+");
//                    switch (parts[0]) {
//                        case "v":
//                            vertices.add(Float.parseFloat(parts[1]));
//                            vertices.add(Float.parseFloat(parts[2]));
//                            vertices.add(Float.parseFloat(parts[3]));
//                            break;
//                        case "f":
//                            for (int i = 1; i < parts.length; i++) {
//                                String[] subParts = parts[i].split("/");
//                                faces.add((short) (Short.parseShort(subParts[0]) - 1));
//                            }
//                            break;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            vertexCount = faces.size();
//
//            float[] vertexArray = new float[vertices.size()];
//            for (int i = 0; i < vertices.size(); i++) {
//                vertexArray[i] = vertices.get(i);
//            }
//
//            short[] faceArray = new short[faces.size()];
//            for (int i = 0; i < faces.size(); i++) {
//                faceArray[i] = faces.get(i);
//            }
//
//            ByteBuffer bb = ByteBuffer.allocateDirect(vertexArray.length * 4);
//            bb.order(ByteOrder.nativeOrder());
//            vertexBuffer = bb.asFloatBuffer().put(vertexArray);
//            vertexBuffer.position(0);
//
//            ByteBuffer dlb = ByteBuffer.allocateDirect(faceArray.length * 2);
//            dlb.order(ByteOrder.nativeOrder());
//            drawListBuffer = dlb.asShortBuffer();
//            drawListBuffer.put(faceArray);
//            drawListBuffer.position(0);
//        }
//
//        private int loadShader(int type, String shaderCode) {
//            int shader = GLES20.glCreateShader(type);
//            GLES20.glShaderSource(shader, shaderCode);
//            GLES20.glCompileShader(shader);
//            return shader;
//        }
//        private void test(){
//            Thread videoThread = new Thread(() -> {
//                while (true) {
//                    try {
//                        angleY+=1;
//
//                        runOnUiThread(() -> {
//
//                        });
//
//                        TimeUnit.MILLISECONDS.sleep(10);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            videoThread.start();
//        }
//        private final String vertexShaderCode =
//                "attribute vec4 vPosition;" +
//                        "uniform mat4 uMVPMatrix;" +
//                        "void main() {" +
//                        "  gl_Position = uMVPMatrix * vPosition;" +
//                        "}";
//
//        private final String fragmentShaderCode =
//                "precision mediump float;" +
//                        "void main() {" +
//                        "  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
//                        "}";
//
//        private float previousX;
//        private float previousY;
//        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
//
//        public void handleTouchPress(float x, float y) {
//            previousX = x;
//            previousY = y;
//        }
//
//        public void handleTouchDrag(float x, float y) {
//            float dx = x - previousX;
//            float dy = y - previousY;
//
//            angleX += dy * TOUCH_SCALE_FACTOR;
//            angleY += dx * TOUCH_SCALE_FACTOR;
//
//            previousX = x;
//            previousY = y;
//        }
//    }
//}