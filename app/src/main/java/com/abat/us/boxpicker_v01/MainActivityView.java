package com.abat.us.boxpicker_v01;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThomasZochler on 8/18/2017.
 */

class MainActivityView {

    private final String TAG = "M300_MAIN_VIEW";

    private AppCompatActivity mActivity;
    private MainActivityModel mModel;
    private MainActivityController mController;

    private Map<String,Integer> mViewIdMap;

    MainActivityView( @NonNull AppCompatActivity pActivity,
                             @NonNull MainActivityModel pActModel,
                             @NonNull MainActivityController pActController) {
        this.mActivity   = pActivity;
        this.mModel      = pActModel;
        this.mController = pActController;
        mModel.setView(this);
        mController.setView(this);
    }

    void initView(){

        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mActivity.setContentView(R.layout.app01_mainview_v01);

        LinearLayout lShelf = (LinearLayout) mActivity.findViewById(R.id.areaShelf);
        lShelf.removeAllViews();

        lShelf.setPadding(3,3,3,3);

        mViewIdMap = new HashMap<>();
        int lRowMax = mModel.getMaxNumRows();
        String[] lColumnInfo = mModel.getColumnInfo();
        for (int lRow = 0; lRow < lRowMax && lRow < lColumnInfo.length; lRow++) {

            LinearLayout lViewRow = new LinearLayout(mActivity);
            lViewRow.setOrientation(LinearLayout.HORIZONTAL);
            lViewRow.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));

            LinearLayout.LayoutParams lViewRowLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1.0f);

            lShelf.addView(lViewRow, lViewRowLayout);

            String[] lColumnsPerRow = lColumnInfo[lRow].split(",");

            for (int lCol = 0; lCol < lColumnsPerRow.length; lCol++) {

                String[] lColDetails = lColumnsPerRow[lCol].split(":");
                String lShelfNo = lColDetails[0];
                int lId = View.generateViewId();

                LinearLayout.LayoutParams lViewCellLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
                lViewCellLayout.setMargins(3, 3, 3, 3);

                TextView lViewCell = new TextView(mActivity.getBaseContext());
                lViewCell.setGravity(Gravity.CENTER);
                lViewCell.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                lViewCell.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
                lViewCell.setText(lShelfNo );
                lViewCell.setId(lId);

                mViewIdMap.put(lShelfNo,lId);
                lViewRow.addView(lViewCell, lViewCellLayout);

            }
        }

    }

    void updateView() {
        fillShelfToSelect();
        fillProductPicture();
        fillInfoText();
    }

    void onStateChange( final MainActivityModel.State pOldState, final MainActivityModel.State pNewState ) {
        updateView();
    }


    private void fillShelfToSelect(){
        for( Integer lId: mViewIdMap.values() ) {
            ((TextView) mActivity.findViewById( lId )).setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
        }
        String lShelfToPick = mModel.getShelfIdToPick();
        if( !lShelfToPick.equals("")) {
            ((TextView) mActivity.findViewById( mViewIdMap.get(lShelfToPick))).setBackgroundColor(ContextCompat.getColor(mActivity, R.color.blueabat));
        }
    }

    private void fillProductPicture(){
        ImageView lImageView = ((ImageView) mActivity.findViewById(R.id.imageControls));
        Drawable lDrawable;
        switch(mModel.getState()) {
            case INIT:
                lDrawable = ContextCompat.getDrawable(mActivity,R.drawable.ic_scan_white_48dp);
                break;
            default:
                lDrawable = mModel.getProductPicToPick();
                break;
        }
        if( lDrawable != null ) lImageView.setImageDrawable( lDrawable );
    }

    private void fillInfoText(){
        TextView lTextView = (TextView) mActivity.findViewById(R.id.textControls);

        lTextView.setTextColor( ContextCompat.getColor(mActivity, R.color.white) );

        switch(mModel.getState()) {
            case INIT:
                lTextView.setText(
                        mActivity.getResources().getString(R.string.txtInit));
                break;
            case SCAN_SHELF:
                lTextView.setText(
                        String.format(mActivity.getResources().getString(R.string.txtScanShelf),mModel.getShelfIdToPick()) +
                        "\r\n\n" +
                        mActivity.getResources().getString(R.string.txtStartScan));
                break;
            case SCAN_SHELF_ERR:
                lTextView.setTextColor( ContextCompat.getColor(mActivity, R.color.red) );
                lTextView.setText(
                        String.format(mActivity.getResources().getString(R.string.txtScanShelfErr),mModel.getShelfIdToPick()) +
                         "\r\n\n" +
                         mActivity.getResources().getString(R.string.txtStartScan));
                break;
            case SCAN_SHELF_OK:
                lTextView.setTextColor( ContextCompat.getColor(mActivity, R.color.green) );
                lTextView.setText(
                        mActivity.getResources().getString(R.string.txtScanShelfOK));
                break;
            case SCAN_PRODUCT:
                lTextView.setText(
                        String.format(mActivity.getResources().getString(R.string.txtScanProduct),mModel.getProductIdToPick()) +
                                "\r\n\n" +
                                mActivity.getResources().getString(R.string.txtStartScan));
                break;
            case SCAN_PRODUCT_ERR:
                lTextView.setTextColor( ContextCompat.getColor(mActivity, R.color.red) );
                lTextView.setText(
                        String.format(mActivity.getResources().getString(R.string.txtScanProductErr),mModel.getProductIdToPick()) +
                                "\r\n\n" +
                                mActivity.getResources().getString(R.string.txtStartScan));
                break;
            case SCAN_PRODUCT_OK:
                lTextView.setTextColor( ContextCompat.getColor(mActivity, R.color.green) );
                lTextView.setText(
                        mActivity.getResources().getString(R.string.txtScanProductOK));
                break;
            case SCAN_COMPLETE:
                lTextView.setText(
                        mActivity.getResources().getString(R.string.txtScanComplete));
                break;
        }
    }
}
