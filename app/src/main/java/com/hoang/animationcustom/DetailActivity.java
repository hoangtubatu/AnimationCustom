package com.hoang.animationcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.hoang.animationcustom.model.Person;
import com.hoang.animationcustom.view.CircleOverlayView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hoang-PC on 12/23/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.imageView) ImageView _imageView;
    @BindView(R.id.circleView) ImageView _circleView;
    @BindView(R.id.cicleOverlay)
    CircleOverlayView _circleOverlayView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);
        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Person person = (Person) bundle.getSerializable("person");

        _imageView.setImageBitmap(person.getCropImageFromOrginal());
        _circleView.setImageBitmap(person.getCircularImageFromCrop());
        _circleView.setX(person.getxCircular());
        _circleView.setY(person.getyCircular());
        _circleOverlayView.setCircle(person.getMidFacePoint(),person.getRadius());
        _circleOverlayView.invalidate();
    }
}
