package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;
import androidx.annotation.XmlRes;

public class PackageValidator {

    public PackageValidator(Context context, @XmlRes int xmlResId) {

    }

    public boolean isKnownCaller(String callingPackage, int callingUid) {
        return true;
    }
}
