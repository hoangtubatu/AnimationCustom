package com.hoang.animationcustom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.hoang.animationcustom.ultility.CircularBuilder;
import com.hoang.animationcustom.model.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    private static final String TAG = "HOANG";
    private static final float OFFSET = 20f;
    private static final boolean INVERT = false;
    List<Person> _listPerson;

    @BindView(R.id.showimage) FrameLayout _showimage;
    @BindView(R.id.list) ListView _listView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String[] $values = new String[20];
        for(int i=0;i<$values.length;i++){
            $values[i] = Integer.toString(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, $values);

        _listView.setAdapter(adapter);
        _listView.setOnTouchListener(this);
        _listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//run when layout show
                _limitTopLv = (int)_listView.getTranslationY();
                _limitBotLv = _showimage.getHeight()- _showimage.getHeight()/8;
                if (Build.VERSION.SDK_INT < 16) {
                    _listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    _listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                createCircularAnimation();
            }
        });
    }

    private void createCircularAnimation(){
        int width = _showimage.getWidth();
        BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;//must use RGB_565 for Face Detect

        Person p1 = new Person("Huyen My",
                BitmapFactory.decodeResource(getResources(), R.drawable.image1, bitmapFatoryOptions),
                new Point(width/2,(int)(width/2.6)),
                width/4);

        Person p2 = new Person("Khoi My",
                BitmapFactory.decodeResource(getResources(), R.drawable.image2, bitmapFatoryOptions),
                new Point(width/2,(int)(width/2.1)),
                width/3);

        CircularBuilder cb = CircularBuilder.getInstance()
                .Builder(MainActivity.this, _showimage)
                .addPerson(p1).addPerson(p2)
                .addOnClickListener(new CircularBuilder.OnClickListener() {
                    @Override
                    public void onClick(View v, Person p) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("tag",p.getTag());

                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                })
                .Build();
        _listPerson = cb.get_listPerson();
    }

    int _limitTopLv;// the position of listview when create first time
    int _limitBotLv; // the position of listview when move to bottom
    private int _yDelta;
    int _actionDownListPosY; // raw position Y when DOWN
    int _curMoveListPosY; // raw position Y when MOVE
    int _preMoveListPosY; // raw position Y when MOVE
    boolean _scrollingList;// detect if list view is scrolling
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
         if(!listIsAtTop()){
            _scrollingList = true;

            return false;/*when list not at the top, remove onTouch, keep list scrollable*/
        }

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(true == _scrollingList) _scrollingList = false;

                _actionDownListPosY= (int)motionEvent.getRawY();
                _preMoveListPosY= (int)motionEvent.getRawY();

                _yDelta = _actionDownListPosY - (int)_listView.getTranslationY();

                for(int i=0;i<_listPerson.size();i++) {
                    _listPerson.get(i).setStartPosY(_listPerson.get(i).getImageView().getY() + OFFSET);
                    _listPerson.get(i).setMidPosY(_listPerson.get(i).getImageView().getY() + _listPerson.get(i).getRadius() + OFFSET);
                    _listPerson.get(i).setEndPosY(_listPerson.get(i).getImageView().getY() + 2 * _listPerson.get(i).getRadius() + OFFSET);
                }

                break;

            case MotionEvent.ACTION_UP:
                if(_listView.getY() <= _listView.getHeight()/2){
                    _listView.animate().setDuration(500).translationY(_limitTopLv).setInterpolator(new OvershootInterpolator()).start();
                    for (Person p : _listPerson){
                        p.getImageView().animate().scaleX(0f).scaleY(0f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
                    }
                }else{
                    _listView.animate().setDuration(500).translationY(_limitBotLv).setInterpolator(new OvershootInterpolator()).start();
                    for (Person p : _listPerson){
                        p.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if(_scrollingList) return false;

                _curMoveListPosY = (int)motionEvent.getRawY();

                if(checkAndSetLimitMoveTop()){
                    if(_curMoveListPosY< _preMoveListPosY) return false;
                }

                _preMoveListPosY=_curMoveListPosY;
                _listView.setTranslationY(_curMoveListPosY - _yDelta);

                //caculate position of listview to scale image
                for (Person $p : _listPerson){
                    float $lvPosY = _listView.getY();

                    if($lvPosY <= $p.getStartPosY()){
                        if(!INVERT){
                            $p.getImageView().setScaleX(0);
                            $p.getImageView().setScaleY(0);
                        }
                    }else {
                        if($p.getStartPosY() <= $lvPosY && $lvPosY <= $p.getMidPosY()){
                            float $scale = INVERT?($p.getMidPosY()-$lvPosY)/$p.getRadius():0f;
                            $p.getImageView().setScaleX($scale);
                            $p.getImageView().setScaleY($scale);
                        }else{
                            if($p.getMidPosY() < $lvPosY && $lvPosY <= $p.getEndPosY()){
                                float $scale = ($lvPosY-$p.getMidPosY())/$p.getRadius();
                                $p.getImageView().setScaleX($scale);
                                $p.getImageView().setScaleY($scale);
                            }
                        }
                    }
                }

                break;

            default :
                break;
        }

        return true;
    }

    private boolean listIsAtTop(){
        if (_listView.getChildCount() == 0) return true;

        return _listView.getChildAt(0).getTop() == 0;
    }

    private boolean checkAndSetLimitMoveTop(){
        if(_limitTopLv >=_listView.getTranslationY()){
            _listView.setTranslationY(_limitTopLv);
            return true;
        }

        return false;
    }
}
