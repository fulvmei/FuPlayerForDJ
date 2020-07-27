package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TimingOff implements Parcelable {

    public static final int TIMING_OFF_MODE_OFF = 0;
    public static final int TIMING_OFF_MODE_ONE = 1;
    public static final int TIMING_OFF_MODE_TIME = 2;

    private int mode;
    private int second;
    private boolean checked;
    private int finishedSecond;
    private String tag;


    protected TimingOff(Parcel in) {
        mode = in.readInt();
        second = in.readInt();
        checked = in.readByte() != 0;
        finishedSecond = in.readInt();
        tag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mode);
        dest.writeInt(second);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeInt(finishedSecond);
        dest.writeString(tag);
    }

    public static final Creator<TimingOff> CREATOR = new Creator<TimingOff>() {
        @Override
        public TimingOff createFromParcel(Parcel in) {
            return new TimingOff(in);
        }

        @Override
        public TimingOff[] newArray(int size) {
            return new TimingOff[size];
        }
    };

    public static String toJson(TimingOff timingOff) {
        JSONObject jo = new JSONObject();
        if (timingOff == null) {
            return jo.toString();
        }
        try {
            jo.put("mode", timingOff.mode);
            jo.put("second", timingOff.second);
            jo.put("checked", timingOff.checked);
            jo.put("finishedSecond", timingOff.finishedSecond);
            jo.put("tag", timingOff.tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }

    public static TimingOff fromJson(String json) {
        TimingOff timingOff = TimingOff.defaultTimingOff();
        try {
            JSONObject jo = new JSONObject(json);
            timingOff.setMode(jo.optInt("mode"));
            timingOff.setSecond(jo.optInt("second"));
            timingOff.setChecked(jo.optBoolean("checked"));
            timingOff.setFinishedSecond(jo.optInt("finishedSecond"));
            timingOff.setTag(jo.optString("tag"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return timingOff;
    }

    public static TimingOff defaultTimingOff() {
        return new TimingOff(TIMING_OFF_MODE_OFF, 0, false);
    }

    public static boolean areItemsTheSame(TimingOff o1, TimingOff o2) {
        return o1 != null && o2 != null && o1.mode == o2.mode && o1.second == o2.second;
    }

    public TimingOff() {
    }

    public TimingOff(int timingOffMode, int timingOffSecond, boolean checked) {
        this.mode = timingOffMode;
        this.second = timingOffSecond;
        this.checked = checked;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getFinishedSecond() {
        return finishedSecond;
    }

    public void setFinishedSecond(int finishedSecond) {
        this.finishedSecond = finishedSecond;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "TimingOff{" +
                "timingOffMode=" + mode +
                ", timingOffSecond=" + second +
                ", checked=" + checked +
                '}';
    }

}
