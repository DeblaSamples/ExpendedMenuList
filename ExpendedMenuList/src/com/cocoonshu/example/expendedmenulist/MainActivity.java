
package com.cocoonshu.example.expendedmenulist;

import com.cocoonshu.example.expendedmenulist.DummyMenu.OnItemExpendedListener;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView  mTxvTitle        = null;
    private ListView  mLstExpenedMenu  = null;
    private DummyMenu mDummyMenu       = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        initializeControls();
    }

    private void initializeViews() {
        mDummyMenu      = new DummyMenu();
        mTxvTitle       = (TextView) findViewById(R.id.TextView_Title);
        mLstExpenedMenu = (ListView) findViewById(R.id.ListView_ExpendedMenu);
        mDummyMenu.setHostListView(mLstExpenedMenu);
    }

    private void initializeControls() {
        mDummyMenu.setOnItemExpenededListener(new OnItemExpendedListener() {
            
            @Override
            public void onItemExpended(int position) {
                // TODO Auto-generated method stub
            }
            
        });
    }

}
