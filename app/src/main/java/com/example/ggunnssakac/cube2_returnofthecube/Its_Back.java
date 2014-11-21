package com.example.ggunnssakac.cube2_returnofthecube;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


public class Its_Back extends Activity {

    GLSurfaceView glView;
    ChildRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView = new GLSurfaceView(this);
        renderer = new ChildRenderer(this);

        glView.setEGLContextClientVersion(2);
        glView.setRenderer(renderer);

        glView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        renderer.isTouched = true;
                        renderer.VELOCITY_SCALE += 1F;
                        break;
                    case MotionEvent.ACTION_UP:
                        renderer.isTouched = false;
                        renderer.VELOCITY_SCALE = 1F;
                        break;

                }

                return false;
            }
        });

        setContentView(glView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.its__back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
