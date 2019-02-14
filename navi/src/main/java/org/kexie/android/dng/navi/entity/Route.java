package org.kexie.android.dng.navi.entity;

import android.os.Parcelable;

import org.kexie.android.dng.navi.model.Point;

import java.util.List;

public interface Route extends Parcelable
{
    Point getFrom();

    Point getTo();

    List<Point> getPoints();
}
