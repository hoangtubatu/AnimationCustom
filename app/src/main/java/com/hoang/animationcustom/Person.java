package com.hoang.animationcustom;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by Hoang-PC on 12/22/2016.
 */

public class Person {
    Bitmap orginalImage;
    Bitmap cropImageFromOrginal;
    Bitmap circularImageFromCrop;
    Point midFacePoint;
    int radius;
    ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Point getMidFacePoint() {
        return midFacePoint;
    }

    public void setMidFacePoint(Point midFacePoint) {
        this.midFacePoint = midFacePoint;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Bitmap getOrginalImage() {
        return orginalImage;
    }

    public void setOrginalImage(Bitmap orginalImage) {
        this.orginalImage = orginalImage;
    }

    public Bitmap getCropImageFromOrginal() {
        return cropImageFromOrginal;
    }

    public void setCropImageFromOrginal(Bitmap cropImageFromOrginal) {
        this.cropImageFromOrginal = cropImageFromOrginal;
    }

    public Bitmap getCircularImageFromCrop() {
        return circularImageFromCrop;
    }

    public void setCircularImageFromCrop(Bitmap circularImageFromCrop) {
        this.circularImageFromCrop = circularImageFromCrop;
    }
}
