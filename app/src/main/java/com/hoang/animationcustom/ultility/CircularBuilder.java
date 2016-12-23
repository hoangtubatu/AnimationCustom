package com.hoang.animationcustom.ultility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hoang.animationcustom.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoang-PC on 12/23/2016.
 */

public class CircularBuilder {
    private static CircularBuilder instance;

    private CircularBuilder(){
    }

    public static CircularBuilder getInstance(){
        if (instance == null){
            synchronized(CircularBuilder.class){
                if (instance == null){
                    instance = new CircularBuilder();
                }
            }
        }

        return instance;
    }

    public interface OnClickListener{
        void onClick(View v, Person p);
    }

    public List<Person> _listPerson;
    public Context _context;
    public ViewGroup _parent;
    public CircularBuilder.OnClickListener _listener = new CircularBuilder.OnClickListener() {
        @Override
        public void onClick(View v, Person p) {

        }
    };

    public Person getPersonByTag(String tag){
        for (Person p : _listPerson){
            if(p.getTag().equals(tag))
                return p;
        }
        return null;
    }

    public CircularBuilder Builder(Context context, ViewGroup parent){
        _listPerson = new ArrayList<>();
        _context = context;
        _parent = parent;

        return this;
    }

    public List<Person> get_listPerson(){
        return _listPerson;
    }

    public CircularBuilder addPerson(Person p){
        _listPerson.add(p);

        return this;
    }

    public CircularBuilder addOnClickListener(CircularBuilder.OnClickListener listener){
        _listener = listener;

        return this;
    }

    public CircularBuilder Build(){
        productBitmap();
        addCircularToScreen();
        handleOnClickListener();

        return this;
    }

    private void productBitmap(){
        BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig= Bitmap.Config.RGB_565;//must use RGB_565 for Face Detect

        for (Person p : _listPerson){
            p.setCropImageFromOrginal(scaleBitmapFitShowArea(p.getOrginalImage(),_parent.getWidth()));
            p.setCircularImageFromCrop(detectAndCutFace(p));
        }
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

        int x = p.getMidFacePoint().x - p.getRadius();
        int y = p.getMidFacePoint().y - p.getRadius();
        int width = 2 * p.getRadius();
        Bitmap newRecCutBitmap = Bitmap.createBitmap(p.getCropImageFromOrginal(), x, y, width, width);

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

    private void addCircularToScreen(){
        for(int i = 0; i < _listPerson.size(); i++){
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(i*(int)(_parent.getWidth()/5), i*(int)(_parent.getWidth()/2), 0, 0);
            lp.gravity = 0;

            final ImageView imageView = new ImageView(_context);
            imageView.setLayoutParams(lp);
            imageView.setImageBitmap(_listPerson.get(i).getCircularImageFromCrop());

            _parent.addView(imageView);

            _listPerson.get(i).setImageView(imageView);
            _listPerson.get(i).setxCircular(imageView.getX());
            _listPerson.get(i).setyCircular(imageView.getY());
            final int finalI = i;
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {//run when layout show
                    _listPerson.get(finalI).setxCircular(imageView.getX());
                    _listPerson.get(finalI).setyCircular(imageView.getY());
                    if (Build.VERSION.SDK_INT < 16) {
                        imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });

        }
    }

    private void handleOnClickListener(){
        for(final Person p : _listPerson)
            p.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _listener.onClick(v, p);
                }
            });
    }
}
