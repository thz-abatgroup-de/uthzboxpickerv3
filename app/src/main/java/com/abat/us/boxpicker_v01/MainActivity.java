package com.abat.us.boxpicker_v01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.zxing.integration.android.IntentIntegrator;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "M300_MAIN_ACTIVITY";

    protected MainActivityModel mModel;
    protected MainActivityController mController;
    protected MainActivityView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG,"onCreate() entered ...");
        super.onCreate(savedInstanceState);

        mModel      = new MainActivityModel(this);
        mController = new MainActivityController(this,mModel);
        mView       = new MainActivityView(this,mModel,mController);

        mModel.initModel();
        mView.initView();
        mController.initController();

        mView.updateView();

    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart() entered ...");
        super.onStart();
    }


    @Override
    protected void onResume() {
        Log.d(TAG,"onResume() entered ...");
        super.onResume();
    }


    @Override
    protected void onPause() {
        Log.d(TAG,"onPause() entered ...");
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.d(TAG,"onStop() entered ...");
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        Log.d( TAG,"onDestroy() entered ..." );
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.d( TAG, "onKeyDown:" + keycode + " " + event.toString() );
        switch (keycode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mController.onKeyNext();
                break;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d( TAG, "onBackPressed called." );
        if (mModel.getState() != MainActivityModel.State.INIT){
            mController.onKeyBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d( TAG, "onActivityResult:" + requestCode + " " + resultCode + ( data == null ? "null" : data.toString() ) );
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            mController.onActivityResult( requestCode, resultCode, data );
        }
    }

}
