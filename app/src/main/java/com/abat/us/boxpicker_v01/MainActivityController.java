package com.abat.us.boxpicker_v01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ThomasZochler on 8/18/2017.
 */

class MainActivityController {

    private final String TAG = "M300_MAIN_CONTROLLER";
    private final String BARCODE_SYMBOL = "##";

    private AppCompatActivity mActivity;
    private MainActivityModel mModel;
    private MainActivityView mView;

    private IntentIntegrator mScanner;

    MainActivityController( @NonNull AppCompatActivity pActivity,
                                   @NonNull MainActivityModel pModel ) {
        this.mActivity = pActivity;
        this.mModel    = pModel;
        this.mModel.setController(this);

        this.mScanner = new IntentIntegrator(mActivity);
    }
    void setView( @NonNull MainActivityView pView) {
        this.mView = pView;
    }

    void initController() {
        askForPermissions();
    }

    void onKeyNext() {
        switch (mModel.getState()) {
            case INIT:
                if( mModel.getConfirmShelfId() )
                    mModel.setState(MainActivityModel.State.SCAN_SHELF );
                else if( mModel.getConfirmProductId() )
                    mModel.setState(MainActivityModel.State.SCAN_PRODUCT );
                else
                    mModel.setState(MainActivityModel.State.CONFIRM_PRODUCT );
                break;
            case SCAN_SHELF:
            case SCAN_SHELF_ERR:
            case SCAN_PRODUCT:
            case SCAN_PRODUCT_ERR:
                triggerScan();
                break;
            case CONFIRM_PRODUCT:
                mModel.setState(MainActivityModel.State.SCAN_COMPLETE );
        }
    }
    void onKeyBack() {
        mModel.setState(MainActivityModel.State.INIT);
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK ) {
            IntentResult lBarcodeResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String lBarcode = lBarcodeResult.getContents();
            validateBarcode(lBarcode);
            return;
        }
        if( requestCode == IntentIntegrator.REQUEST_CODE ) {
            onKeyBack();
        }
    }

    void onStateChange( final MainActivityModel.State pOldState, final MainActivityModel.State pNewState ) {
        switch (pNewState) {
            case SCAN_SHELF_OK:
                if( mModel.getConfirmProductId() )
                    doDelayedStateChange(MainActivityModel.State.SCAN_PRODUCT);
                else
                    doDelayedStateChange(MainActivityModel.State.SCAN_COMPLETE);
                break;
            case SCAN_PRODUCT_OK:
                doDelayedStateChange(MainActivityModel.State.SCAN_COMPLETE);
                break;
            case SCAN_COMPLETE:
                doDelayedStateChange(MainActivityModel.State.INIT);
                break;
        }
    }

    private void triggerScan() {
        if( 1 == 1 ) {
            mScanner.initiateScan();
        } else {
            switch (mModel.getState()) {
                case SCAN_SHELF:
                    validateBarcode("99");
                    break;
                case SCAN_SHELF_ERR:
                    validateBarcode(mModel.getShelfIdToPick());
                    break;
                case SCAN_PRODUCT:
                    validateBarcode("99");
                    break;
                case SCAN_PRODUCT_ERR:
                    validateBarcode(mModel.getProductIdToPick());
                    break;
            }
        }
    }

    private void validateBarcode(String pBarcode) {
        String lBarcode = pBarcode;
        if( lBarcode.startsWith(BARCODE_SYMBOL)){
            lBarcode = lBarcode.substring(0 + BARCODE_SYMBOL.length(), lBarcode.length());
        }
        if( lBarcode.endsWith(BARCODE_SYMBOL)){
            lBarcode = lBarcode.substring(0, lBarcode.length()-BARCODE_SYMBOL.length());
        }
        switch (mModel.getState()) {
            case SCAN_SHELF:
            case SCAN_SHELF_ERR:
                if( lBarcode.equals(mModel.getShelfIdToPick()) )
                    mModel.setState(MainActivityModel.State.SCAN_SHELF_OK);
                else
                    mModel.setState(MainActivityModel.State.SCAN_SHELF_ERR);
                break;
            case SCAN_PRODUCT:
            case SCAN_PRODUCT_ERR:
                if( lBarcode.equals(mModel.getProductIdToPick()) )
                    mModel.setState(MainActivityModel.State.SCAN_PRODUCT_OK);
                else
                    mModel.setState(MainActivityModel.State.SCAN_PRODUCT_ERR);
                break;
        }
    }

    private void askForPermissions(){
        if (ContextCompat.checkSelfPermission( mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( mActivity, new String[]{ Manifest.permission.CAMERA }, 0 );
        }
    }

    private void doDelayedStateChange(final MainActivityModel.State pNextState) {
        new DelayExecutor( 2000 ) {
            @Override
            public void taskPostExecute() {
                mModel.setState(pNextState);
            }
        }.execute();
    }

    private abstract class DelayExecutor extends AsyncTask<Void, Void, Void> {
        private final int delay;

        protected DelayExecutor(int delay) {
            this.delay = delay;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            taskPostExecute();
        }

        public abstract void taskPostExecute();

        public void taskInBackground(){}
    }

}
