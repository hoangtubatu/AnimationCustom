package com.hoang.animationcustom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    //init values items in listview
    static final String[] _values = new String[20];
    private static final String TAG = "HOANG";

    {
        for(int i=0;i<_values.length;i++){
            _values[i] = Integer.toString(i);
        }
    }
    @BindView(R.id.showimage) FrameLayout _showimage;
    @BindView(R.id.list) ListView _listView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, _values);

        _listView.setAdapter(adapter);
        _listView.setOnTouchListener(this);
        _listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                _limitTopLv = (int)_listView.getTranslationY();
                _limitBotLv = _listView.getHeight()- _listView.getHeight()/8;
                if (Build.VERSION.SDK_INT < 16) {
                    _listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    _listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                decodeBitmap(_showimage.getWidth());
                addCircularToScreen(_showimage.getWidth());
            }
        });
    }

    private void addCircularToScreen(double width){
        for(int i = 0; i < _listPerson.size(); i++){
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(i*(int)(width/5), i*(int)(width/2), 0, 0);
            lp.gravity = 0;

            ImageView im = new ImageView(this);
            im.setLayoutParams(lp);
            im.setImageBitmap(_listPerson.get(i).getCircularImageFromCrop());

            _showimage.addView(im);

            _listPerson.get(i).setImageView(im);
        }
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
            _scrollingList =true;
            return false;/*when list not at the top, remove onTouch, keep list scrollable*/
        }

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(true == _scrollingList) _scrollingList =false;

                _actionDownListPosY= (int)motionEvent.getRawY();
                _preMoveListPosY= (int)motionEvent.getRawY();

                _yDelta = _actionDownListPosY - (int)_listView.getTranslationY();
                break;
            case MotionEvent.ACTION_UP:
                if(_listView.getY() <= _listView.getHeight()/2){
                    _listView.animate().setDuration(500).translationY(_limitTopLv).setInterpolator(new OvershootInterpolator());
                }else{
                    _listView.animate().setDuration(500).translationY(_limitBotLv).setInterpolator(new OvershootInterpolator());
                }
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
        if(_limitTopLv >=_listView.getTranslationY()){
            _listView.setTranslationY(_limitTopLv);
            return true;
        }

        return false;
    }

    List<Person> _listPerson;
    private void decodeBitmap(double width){
        _listPerson= new ArrayList<>();

        BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;//must use RGB_565 for Face Detect

        Person p1 = new Person();
        p1.setOrginalImage(BitmapFactory.decodeResource(getResources(), R.drawable.image1, bitmapFatoryOptions));
        p1.setCropImageFromOrginal(scaleBitmapFitShowArea(p1.getOrginalImage(),width));
        p1.setMidFacePoint(new Point((int)(width/2),(int)(width/2.6)));
        p1.setRadius((int)(width/2));
        p1.setCircularImageFromCrop(detectAndCutFace(p1));

        Person p2 = new Person();
        p2.setOrginalImage(BitmapFactory.decodeResource(getResources(), R.drawable.image2, bitmapFatoryOptions));
        p2.setCropImageFromOrginal(scaleBitmapFitShowArea(p2.getOrginalImage(),width));
        p2.setMidFacePoint(new Point((int)width/2,(int)(width/2.1)));
        p2.setRadius((int)(width/1.5));
        p2.setCircularImageFromCrop(detectAndCutFace(p2));

        _listPerson.add(p1);
        _listPerson.add(p2);
    }

    private Bitmap scaleBitmapFitShowArea(Bitmap bitmap, double width){
        double scaleFactor=bitmap.getWidth()/width;
        return Bitmap.createScaledBitmap(bitmap,(int)width,(int)(bitmap.getHeight()/scaleFactor),false);
    }

    private Bitmap detectAndCutFace(Person p){
/* google Face Detector not work, just skip it
            FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .build();

            Detector<Face> safeDetector = new SafeFaceDetector(detector);

            Frame frame = new Frame.Builder().setBitmap(b).build();
            SparseArray<Face> faces = safeDetector.detect(frame);

            if (!safeDetector.isOperational()) {
                safeDetector.release();
                // Note: The first time that an app using face API is installed on a device, GMS will
                // download a native library to the device in order to do detection.  Usually this
                // completes before the app is run for the first time.  But if that download has not yet
                // completed, then the above call will not detect any faces.
                //
                // isOperational() can be used to check if the required native library is currently
                // available.  The detector will automatically become operational once the library
                // download completes on device.
                Log.w(TAG, "Face detector dependencies are not yet available.");

                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, "low_storage_error", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "low_storage_error");
                }
                return;
            }

            Face face = faces.valueAt(0);
            PointF myMidPoint = new PointF();
            PointF myEye = new PointF();
            List<Landmark> listLandmark = face.getLandmarks();
            for (Landmark lm:listLandmark){
                if(lm.getType()== Landmark.NOSE_BASE){
                    myMidPoint=lm.getPosition();
                }
                if(lm.getType()== Landmark.LEFT_EYE){
                    myEye=lm.getPosition();
                }
                if(lm.getType()== Landmark.RIGHT_EAR){
                    myEye=lm.getPosition();
                }
            }
            int width = (int)Math.abs((myMidPoint.y - myEye.y) * 2);
            _listBitmapFace.add(getCroppedBitmap(b, width));

            safeDetector.release();
*/

        int x = p.getMidFacePoint().x - p.getRadius()/2;
        int y = p.getMidFacePoint().y - p.getRadius()/2;
        Bitmap newRecCutBitmap=Bitmap.createBitmap(p.getCropImageFromOrginal(),x,y,p.getRadius(),p.getRadius());
        return getCircularFromRect(newRecCutBitmap);
    }

    private Bitmap getCircularFromRect(Bitmap bitmap){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
