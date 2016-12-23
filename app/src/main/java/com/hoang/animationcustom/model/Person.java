package com.hoang.animationcustom.model;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Hoang-PC on 12/22/2016.
 */

public class Person implements Serializable {
    private static final long serialVersionUID = -6298516694275121291L;

    String tag;
    Bitmap orginalImage;
    Bitmap cropImageFromOrginal;
    Bitmap circularImageFromCrop;
    ImageView imageView;

    Point midFacePoint;
    int radius;

    float startPosY;
    float midPosY;
    float endPosY;

    float xCircular;
    float yCircular;

    public Person(String tag, Bitmap orginalImage, Point midFacePoint, int radius) {
        this.tag = tag;
        this.orginalImage = orginalImage;
        this.midFacePoint = midFacePoint;
        this.radius = radius;
    }

    public float getxCircular() {
        return xCircular;
    }

    public void setxCircular(float xCircular) {
        this.xCircular = xCircular;
    }

    public float getyCircular() {
        return yCircular;
    }

    public void setyCircular(float yCircular) {
        this.yCircular = yCircular;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getStartPosY() {
        return startPosY;
    }

    public void setStartPosY(float startPosY) {
        this.startPosY = startPosY;
    }

    public float getMidPosY() {
        return midPosY;
    }

    public void setMidPosY(float midPosY) {
        this.midPosY = midPosY;
    }

    public float getEndPosY() {
        return endPosY;
    }

    public void setEndPosY(float endPosY) {
        this.endPosY = endPosY;
    }

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

    @Override
    public String toString() {
        return getTag();
    }
}
