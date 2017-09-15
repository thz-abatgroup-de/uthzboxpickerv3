package com.abat.us.boxpicker_v01;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ThomasZochler on 8/18/2017.
 */

public class MainActivityModel {

    public enum State {
        INIT,
        SCAN_SHELF,
        SCAN_SHELF_ERR,
        SCAN_SHELF_OK,
        SCAN_PRODUCT,
        SCAN_PRODUCT_ERR,
        SCAN_PRODUCT_OK,
        CONFIRM_PRODUCT,
        SCAN_COMPLETE,
    }

    private final String TAG = "M300_MAIN_MODEL";

    private AppCompatActivity mActivity;
    private MainActivityController mController;
    private MainActivityView mView;

    private State mState;
    private ProductOnShelf mProductToPick;

    private boolean pConfirmShelfId;
    private boolean pConfirmProductId;

    private int mMaxNumRows;
    private String[] mColumnInfo;

    private ProductOnShelfManager mProductOnShelfManager;

    MainActivityModel(@NonNull AppCompatActivity pActivity) {
        this.mActivity = pActivity;
    }
    void setController(MainActivityController pController) {
        this.mController = pController;
    }
    void setView(MainActivityView pView) {

        this.mView       = pView;
    }

    void initModel(){

        mState = State.INIT;

        Resources lRes  = mActivity.getResources();

        mMaxNumRows      = lRes.getInteger(R.integer.shelf_rows_max);

        pConfirmShelfId = lRes.getBoolean(R.bool.confirm_shelf_id);
        pConfirmProductId = lRes.getBoolean(R.bool.confirm_product_id);

        if(mProductOnShelfManager == null) mProductOnShelfManager = new ProductOnShelfManager();
        mProductOnShelfManager.reset();

        int lResIdShelfColumns                 = R.array.shelf_columns;
        TypedArray lShelfColumns               = lRes.obtainTypedArray(lResIdShelfColumns);

        mColumnInfo = new String[lShelfColumns.length()];
        for (int i = 0; i < lShelfColumns.length(); i++) {
            mColumnInfo[i] = lShelfColumns.getString(i);
        }

        int lResIdProductIds                   = R.array.product_ids;
        int lResIdProductPics                  = R.array.product_pics;
        int lResIdProductRotations             = R.array.product_rotations;
        int lResIdShelfIds                     = R.array.shelf_ids;
        int lResIdShelfContainsProductIds      = R.array.shelf_contains_products;
        int lResIdShelfChoosingProbabilities   = R.array.shelf_choosing_probability;
        TypedArray lProductIds                 = lRes.obtainTypedArray(lResIdProductIds);
        TypedArray lProductPics                = lRes.obtainTypedArray(lResIdProductPics);
        TypedArray lProductRotations           = lRes.obtainTypedArray(lResIdProductRotations);
        TypedArray lShelfIds                   = lRes.obtainTypedArray(lResIdShelfIds);
        TypedArray lShelfContainsProductIds    = lRes.obtainTypedArray(lResIdShelfContainsProductIds);
        TypedArray lShelfChoosingProbabilities = lRes.obtainTypedArray(lResIdShelfChoosingProbabilities);

        for (int i = 0; i < lProductIds.length(); i++) {
            Product lProduct = new Product(
                    lProductIds.getString(i),
                    lProductPics.getDrawable(i),
                    lProductRotations.getInteger(i,0));
            mProductOnShelfManager.addProduct(lProduct);
        }

        for (int i = 0; i < lShelfIds.length(); i++) {
            Shelf lShelf = new Shelf(lShelfIds.getString(i));
            mProductOnShelfManager.addShelf(lShelf);
            if( !lShelfContainsProductIds.getString(i).equals("") ){
                String[] lProductOnShelfIds = lShelfContainsProductIds.getString(i).split(",");
                String[] lProductOnShelfProbabilities = lShelfChoosingProbabilities.getString(i).split(",");
                for (int j = 0; j < lProductOnShelfIds.length && j < lProductOnShelfProbabilities.length; j++) {
                    Product lProduct = mProductOnShelfManager.getProductById(lProductOnShelfIds[j]);
                    int lProbability = Integer.valueOf(lProductOnShelfProbabilities[j]);
                    mProductOnShelfManager.addProductOnShelf(lProduct, lShelf, lProbability);
                }
            }
        }

        lShelfColumns.recycle();
        lProductIds.recycle();
        lProductPics.recycle();
        lProductRotations.recycle();
        lShelfIds.recycle();
        lShelfContainsProductIds.recycle();
        lShelfChoosingProbabilities.recycle();

    }

    State getState() { return mState; }
    void setState(State pNewState) {
        if (mState == pNewState) return;
        State lOldState = mState;
        mState = pNewState;
        this.onStateChange(lOldState, pNewState);
        if( mView != null ) mView.onStateChange(lOldState, pNewState);
        if( mController != null ) mController.onStateChange(lOldState, pNewState);
    }

    int getMaxNumRows() { return mMaxNumRows; }
    String[] getColumnInfo() { return mColumnInfo; }

    boolean getConfirmShelfId() { return pConfirmShelfId; }
    boolean getConfirmProductId() { return pConfirmProductId; }

    String getProductIdToPick() {
        if (mProductToPick != null) {
            Product lProductToPick = mProductToPick.getProduct();
            if (lProductToPick != null) {
                return lProductToPick.getProductId();
            }
        }
        return "";
    }
    Drawable getProductPicToPick() {
        if (mProductToPick != null) {
            Product lProductToPick = mProductToPick.getProduct();
            if (lProductToPick != null) {
                return lProductToPick.getProductPic();
            }
        }
        return null;
    }
    int getProductPicRotationToPick() {
        if (mProductToPick != null) {
            Product lProductToPick = mProductToPick.getProduct();
            if (lProductToPick != null) {
                return lProductToPick.getProductRotation();
            }
        }
        return 0;
    }
    String getShelfIdToPick() {
        if (mProductToPick != null) {
            Shelf lShelf = mProductToPick.getShelf();
            if (lShelf != null) {
                return lShelf.getShelfId();
            }
        }
        return "";
    }

    private void onStateChange( final MainActivityModel.State pOldState, final MainActivityModel.State pNewState ) {
        if( pOldState == pNewState ) return;
        switch (pNewState) {
            case SCAN_SHELF:
            case SCAN_PRODUCT:
            case CONFIRM_PRODUCT:
                if( mProductToPick == null )
                    mProductToPick = mProductOnShelfManager.getNextPick();
                break;
            case INIT:
                mProductToPick = null;
        }
    }

    // ===============================================================
    private class Product {
    // ===============================================================

        public Product(String pProductId, Drawable pProductPic, int pProductRotation) {
            mProductId  = pProductId;
            mProductPic = pProductPic;
            mProductRotation = pProductRotation;
        }
        private String mProductId;
        private Drawable mProductPic;
        private int mProductRotation;
        String getProductId() { return mProductId; }
        Drawable getProductPic() { return mProductPic; }
        int getProductRotation() { return mProductRotation; }

    }

    // ===============================================================
    private class Shelf {

        Shelf(String pShelfId) {
            mShelfId = pShelfId;
        }
        private String mShelfId;
        String getShelfId() { return mShelfId; }

    }

    // ===============================================================
    private class ProductOnShelf {

        ProductOnShelf(Product pProduct, Shelf pShelf, int pProbability) {
            mProduct     = pProduct;
            mShelf       = pShelf;
            mProbability = pProbability;
        }
        private Product mProduct;
        private Shelf mShelf;
        private int mProbability;
        Product getProduct() { return mProduct; }
        Shelf getShelf() { return mShelf; }
        int getProbability() { return mProbability; }
    }

    // ===============================================================
    private class ProductOnShelfManager {
        private ArrayList<Product> mProducts;
        private ArrayList<Shelf> mShelfs;
        private ArrayList<ProductOnShelf> mProductOnShelfs;
        private int mMaxProbability;
        private Random mRand;

        ProductOnShelfManager() {
            mProducts = new ArrayList<>();
            mShelfs = new ArrayList<>();
            mProductOnShelfs = new ArrayList<>();
            mRand = new Random();
        }
        void reset(){
            mProducts.clear();
            mShelfs.clear();
            mProductOnShelfs.clear();
        }

        void addProduct(@NonNull Product pProduct) {
            mProducts.add(pProduct);
        }
        void addShelf(@NonNull Shelf pShelf) {
            mShelfs.add(pShelf);
        }
        void addProductOnShelf(@NonNull Product pProduct,@NonNull Shelf pShelf, int pProbability) {
            ProductOnShelf lProductOnShelf = new ProductOnShelf(pProduct,pShelf,pProbability);
            mProductOnShelfs.add(lProductOnShelf);
            mMaxProbability += pProbability;
        }

        ProductOnShelf getNextPick() {
            int lRand = mRand.nextInt(mMaxProbability);
            for (ProductOnShelf lProductOnShelf: mProductOnShelfs ) {
                if( lRand < lProductOnShelf.getProbability())
                    return lProductOnShelf;
                lRand -= lProductOnShelf.getProbability();
            }
            return mProductOnShelfs.get(mRand.nextInt(mProductOnShelfs.size()));

        }
        Product getProductById(String pProductId) {
            for (Product pProduct: mProducts) {
                if( pProduct.getProductId().equals(pProductId) ) {
                    return pProduct;
                }
            }
            return null;
        }
        Shelf getShelfById(String pShelfId) {
            for (Shelf pShelf: mShelfs) {
                if( pShelf.getShelfId().equals(pShelfId) ) {
                    return pShelf;
                }
            }
            return null;
        }

    }

}
