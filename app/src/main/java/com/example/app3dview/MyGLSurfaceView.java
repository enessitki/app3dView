package com.example.app3dview;//package trydf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer renderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

//        test();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleTouchPress(x, y);
                        }
                    });
                    break;

                case MotionEvent.ACTION_MOVE:
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleTouchDrag(x, y);
                            requestRender();
                        }
                    });
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
