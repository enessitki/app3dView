//package trydf;
//
//import android.content.Context;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.Matrix;
//
//import com.example.app3dview.R;
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
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//public class MyGLRenderer implements GLSurfaceView.Renderer {
//
//    private final Context context;
//    private FloatBuffer vertexBuffer;
//    private ShortBuffer drawListBuffer;
//    private int mProgram;
//    private int vertexCount;
//
//    private final float[] modelMatrix = new float[16];
//    private final float[] viewMatrix = new float[16];
//    private final float[] projectionMatrix = new float[16];
//    private final float[] mvpMatrix = new float[16];
//
//    public MyGLRenderer(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//
//        // OBJ dosyasını yükle
//        loadOBJ(R.raw.cow);
//
//        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
//        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
//
//        mProgram = GLES20.glCreateProgram();
//        GLES20.glAttachShader(mProgram, vertexShader);
//        GLES20.glAttachShader(mProgram, fragmentShader);
//        GLES20.glLinkProgram(mProgram);
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//        GLES20.glUseProgram(mProgram);
//
//        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//
//        GLES20.glEnableVertexAttribArray(positionHandle);
//        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//
//        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//
//        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
//        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
//        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
//
//        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
//
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
//
//        GLES20.glDisableVertexAttribArray(positionHandle);
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        GLES20.glViewport(0, 0, width, height);
//
//        float ratio = (float) width / height;
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//    }
//
//    private void loadOBJ(int objResourceId) {
//        InputStream inputStream = context.getResources().openRawResource(objResourceId);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        ArrayList<Float> vertices = new ArrayList<>();
//        ArrayList<Float> normals = new ArrayList<>();
//        ArrayList<Float> textures = new ArrayList<>();
//        ArrayList<Short> faces = new ArrayList<>();
//
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split("\\s+");
//                switch (parts[0]) {
//                    case "v":
//                        vertices.add(Float.parseFloat(parts[1]));
//                        vertices.add(Float.parseFloat(parts[2]));
//                        vertices.add(Float.parseFloat(parts[3]));
//                        break;
//                    case "vn":
//                        normals.add(Float.parseFloat(parts[1]));
//                        normals.add(Float.parseFloat(parts[2]));
//                        normals.add(Float.parseFloat(parts[3]));
//                        break;
//                    case "vt":
//                        textures.add(Float.parseFloat(parts[1]));
//                        textures.add(Float.parseFloat(parts[2]));
//                        break;
//                    case "f":
//                        for (int i = 1; i < parts.length; i++) {
//                            String[] subParts = parts[i].split("/");
//                            faces.add((short) (Short.parseShort(subParts[0]) - 1));
//                        }
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        float[] vertexArray = new float[vertices.size()];
//        for (int i = 0; i < vertices.size(); i++) {
//            vertexArray[i] = vertices.get(i);
//        }
//
//        vertexBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(vertexArray);
//        vertexBuffer.position(0);
//
//        short[] faceArray = new short[faces.size()];
//        for (int i = 0; i < faces.size(); i++) {
//            faceArray[i] = faces.get(i);
//        }
//
//        drawListBuffer = ByteBuffer.allocateDirect(faceArray.length * 2)
//                .order(ByteOrder.nativeOrder())
//                .asShortBuffer()
//                .put(faceArray);
//        drawListBuffer.position(0);
//    }
//
//    private int loadShader(int type, String shaderCode) {
//        int shader = GLES20.glCreateShader(type);
//        GLES20.glShaderSource(shader, shaderCode);
//        GLES20.glCompileShader(shader);
//
//        return shader;
//    }
//
//    private final String vertexShaderCode =
//            "uniform mat4 uMVPMatrix;" +
//                    "attribute vec4 vPosition;" +
//                    "void main() {" +
//                    "  gl_Position = uMVPMatrix * vPosition;" +
//                    "}";
//
//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "void main() {" +
//                    "  gl_FragColor = vec4(0.6, 0.6, 0.6, 1.0);" +
//                    "}";
//}
