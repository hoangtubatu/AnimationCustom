package com.hoang.animationcustom;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.hoang.animationcustom.model.Person;
import com.hoang.animationcustom.ultility.CircularBuilder;
import com.hoang.animationcustom.view.CircleOverlayView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hoang-PC on 12/23/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.imageView) ImageView _imageView;
    @BindView(R.id.circleView) ImageView _circleView;
    @BindView(R.id.cicleOverlay) CircleOverlayView _circleOverlayView;
    Person _person;
    int _radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);
        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        String tag = intent.getStringExtra("tag");
        _person = CircularBuilder.getInstance().getPersonByTag(tag);
        if(_person==null){
            Log.v("tag","person null");

            return;
        }

        _radius = _person.getRadius();

        _imageView.setImageBitmap(_person.getCropImageFromOrginal());
        _circleView.setImageBitmap(_person.getCircularImageFromCrop());
        _circleView.setX(_person.getxCircular());
        _circleView.setY(_person.getyCircular());
        _circleOverlayView.setCircle(_person.getMidFacePoint(),_radius);
        //_circleOverlayView.invalidate();

        _circleView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // remove previous listener
                _circleView.getViewTreeObserver().removeOnPreDrawListener(this);

                // prep the scene
                prepareScene();

                // run the animation
                runEnterAnimation();
                return true;
            }
        });
    }
    float _deltaX;
    float _deltaY;
    private void prepareScene() {
        // caculate distance translate
        _deltaX = _person.getMidFacePoint().x - _person.getRadius() ;
        _deltaY = _person.getMidFacePoint().y - _person.getRadius() ;

        // reposition the circle image
        _circleView.setX(_person.getxCircular());
        _circleView.setY(_person.getyCircular());
    }

    private void runEnterAnimation() {
        // We can now make it visible
        _circleView.setVisibility(View.VISIBLE);
        // finally, run the animation
        _circleView.animate()
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .x(_deltaX)
                .y(_deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        _circleView.setVisibility(View.INVISIBLE);
                        _imageView.setVisibility(View.VISIBLE);
                        _circleOverlayView.setVisibility(View.VISIBLE);
                        _circleOverlayView.animate()
                                .setDuration(500)
                                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        _radius += 30;
                                        _circleOverlayView.setCircle(_person.getMidFacePoint(), _radius);
                                        _circleOverlayView.invalidate();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }
}
