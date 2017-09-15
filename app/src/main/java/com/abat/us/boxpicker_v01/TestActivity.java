package com.abat.us.boxpicker_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app01_testview_v01);

        MainActivityModel lModel = new MainActivityModel(this);
        lModel.initModel();

        {
            String lDummy = null;
            for (int i = 0; i < 10; i++) {
                lModel.setState(MainActivityModel.State.SCAN_SHELF);
                String lProductIdToPick = lModel.getProductIdToPick();
                String lShelfIdToPick = lModel.getShelfIdToPick();
                lModel.setState(MainActivityModel.State.INIT);
                if(lDummy==null) lDummy = ""; else lDummy += "\n";
                lDummy +=  "Product to pick: " + lProductIdToPick + "\n";
                lDummy +=  "Shelf to pick from: " + lShelfIdToPick;
            }
            ((TextView)findViewById(R.id.text01)).setText(lDummy);
        }

        {
            HashMap<String,Integer> lMap = new HashMap<>();
            for( int i = 0; i < 10000; i++) {
                lModel.setState(MainActivityModel.State.SCAN_SHELF);
                String lProductIdToPick = lModel.getProductIdToPick();
                String lShelfIdToPick = lModel.getShelfIdToPick();
                String lKey = "Prod: " + lProductIdToPick + " / Shelf: " + lShelfIdToPick;
                Integer lValue = lMap.get(lKey);
                if(lValue == null) lValue = 1; else lValue = lValue + 1;
                lMap.put(lKey,lValue);
                lModel.setState(MainActivityModel.State.INIT);
            }

            String lDummy = null;
            for( String lKey : lMap.keySet() ) {
                if(lDummy==null) lDummy = ""; else lDummy += "\n";
                lDummy += lKey + " occurred " + lMap.get(lKey) + " times!";
            }

            ((TextView)findViewById(R.id.text02)).setText(lDummy);

        }

    }

}
