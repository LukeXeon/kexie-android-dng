package org.kexie.android.dng.navi.entity;

/**
 * Created by Luke on 2018/12/27.
 */

//public final class SdkRoute
//        implements Parcelable,
//        LiteRoute
//{
//    protected SdkRoute(Parcel in)
//    {
//        from = in.readParcelable(JsonPoint.class.getClassLoader());
//        to = in.readParcelable(JsonPoint.class.getClassLoader());
//        path = in.readParcelable(DrivePath.class.getClassLoader());
//    }
//
//    public static final Creator<SdkRoute> CREATOR = new Creator<SdkRoute>()
//    {
//        @Override
//        public SdkRoute createFromParcel(Parcel in)
//        {
//            return new SdkRoute(in);
//        }
//
//        @Override
//        public SdkRoute[] newArray(int size)
//        {
//            return new SdkRoute[size];
//        }
//    };
//
//    private SdkRoute(Builder builder)
//    {
//        from = builder.from;
//        to = builder.to;
//        path = builder.path;
//    }
//
//    @Override
//    public int describeContents()
//    {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags)
//    {
//        dest.writeParcelable(from, flags);
//        dest.writeParcelable(to, flags);
//        dest.writeParcelable(path, flags);
//    }
//
//    public static class LiteStep
//    {
//        public final DriveStep driveStep;
//
//        private LiteStep(Builder builder)
//        {
//            driveStep = builder.driveStep;
//        }
//
//        public String getAction()
//        {
//            return driveStep.getAction();
//        }
//
//        public int getActionRes()
//        {
//            String actionName = driveStep.getAction();
//            if (actionName == null || actionName.equals(""))
//            {
//                return R.mipmap.dir3;
//            }
//            if ("左转".equals(actionName))
//            {
//                return R.mipmap.dir2;
//            }
//            if ("右转".equals(actionName))
//            {
//                return R.mipmap.dir1;
//            }
//            if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName))
//            {
//                return R.mipmap.dir6;
//            }
//            if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName))
//            {
//                return R.mipmap.dir5;
//            }
//            if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName))
//            {
//                return R.mipmap.dir7;
//            }
//            if ("向右后方行驶".equals(actionName))
//            {
//                return R.mipmap.dir8;
//            }
//            if ("直行".equals(actionName))
//            {
//                return R.mipmap.dir3;
//            }
//            if ("减速行驶".equals(actionName))
//            {
//                return R.mipmap.dir4;
//            }
//            return R.mipmap.dir3;
//        }
//
//        public static final class Builder
//        {
//            private DriveStep driveStep;
//
//            public Builder()
//            {
//            }
//
//            public Builder driveStep(DriveStep val)
//            {
//                driveStep = val;
//                return this;
//            }
//
//            public LiteStep build()
//            {
//                return new LiteStep(this);
//            }
//        }
//    }
//
//    public final JsonPoint from;
//    public final JsonPoint to;
//    public final DrivePath path;
//
//    @Override
//    public JsonPoint getFrom()
//    {
//        return from;
//    }
//
//    @Override
//    public JsonPoint getTo()
//    {
//        return to;
//    }
//
//    @Override
//    public List<JsonPoint> getPoints()
//    {
//        List<DriveStep> driveSteps = path.getSteps();
//        List<JsonPoint> points = new ArrayList<>(driveSteps.size());
//        for (DriveStep step : driveSteps)
//        {
//            points.add(new JsonPoint(step.getPolyline().get(0)));
//        }
//        return points;
//    }
//

//
//
//    public List<LiteStep> getSteps()
//    {
//        List<DriveStep> driveSteps = path.getSteps();
//        List<LiteStep> steps = new ArrayList<>(driveSteps.size());
//        for (DriveStep driveStep : driveSteps)
//        {
//            steps.add(new LiteStep.Builder().driveStep(driveStep).build());
//        }
//        return steps;
//    }
//
//    public static final class Builder
//    {
//        private JsonPoint from;
//        private JsonPoint to;
//        private DrivePath path;
//
//        public Builder()
//        {
//        }
//
//        public Builder from(JsonPoint val)
//        {
//            from = val;
//            return this;
//        }
//
//        public Builder to(JsonPoint val)
//        {
//            to = val;
//            return this;
//        }
//
//        public Builder path(DrivePath val)
//        {
//            path = val;
//            return this;
//        }
//
//        public SdkRoute build()
//        {
//            return new SdkRoute(this);
//        }
//    }
//}
