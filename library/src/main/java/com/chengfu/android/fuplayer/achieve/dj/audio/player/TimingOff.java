package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import java.io.Serializable;

public class TimingOff implements Serializable {
    public static final int TIMING_OFF_MODE_OFF = 0;
    public static final int TIMING_OFF_MODE_ONE = 1;
    public static final int TIMING_OFF_MODE_TIME = 2;

    private int mode;
    private int second;
    private boolean checked;
    private int finishedSecond;
    private String tag;


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
