package com.hoang.animationcustom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    //init values items in listview
    static final String[] _values = new String[20];
    {
        for(int i=0;i<_values.length;i++){
            _values[i] = Integer.toString(i);
        }
    }
    ListView _listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, _values);

        _listView = (ListView) findViewById(R.id.list);
        _defaultListViewPosY = _listView.getTranslationY();
        _listView.setAdapter(adapter);
        _listView.setOnTouchListener(this);
        _listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            boolean mIsScrollingUp;
            int mLastFirstVisibleItem;

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE)
                    Log.i("a", "scrolling stopped...");

                if (view.getId() == _listView.getId()) {
                    final int currentFirstVisibleItem = _listView.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                        Log.i("a", "scrolling down...");
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                        Log.i("a", "scrolling up...");
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });
    }

    float _defaultListViewPosY;// the position of listview when create first time
    private int _yDelta;
    int _actionDownListPosY; // raw position Y when DOWN
    int _curMoveListPosY; // raw position Y when MOVE
    int _preMoveListPosY; // raw position Y when MOVE
    boolean _scrollingList;// detect if list view is scrolling
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!listIsAtTop()){
            _scrollingList =true;
            return false;/*when list not at the top, remove onTouch, keep list scrollable*/
        }

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(_scrollingList ==true) _scrollingList =false;

                _actionDownListPosY= (int)motionEvent.getRawY();
                _preMoveListPosY= (int)motionEvent.getRawY();

                _yDelta = _actionDownListPosY - (int)_listView.getTranslationY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if(_scrollingList) return false;

                _curMoveListPosY = (int)motionEvent.getRawY();

                if(checkAndSetLimitMoveTop()){
                    if(_curMoveListPosY< _preMoveListPosY) return false;

                }

                _preMoveListPosY=_curMoveListPosY;
                _listView.setTranslationY(_curMoveListPosY - _yDelta);
                break;
        }
        return true;
    }

    private boolean listIsAtTop(){
        if (_listView.getChildCount() == 0) return true;
        return _listView.getChildAt(0).getTop() == 0;
    }
    private boolean checkAndSetLimitMoveTop(){
        if(_defaultListViewPosY>=_listView.getTranslationY()){
            _listView.setTranslationY(_defaultListViewPosY);
            return true;
        }

        return false;
    }
}
