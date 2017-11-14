package vn.dmcl.eagleeyes.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.dto.ConfigDTO;
import vn.dmcl.eagleeyes.dto.SessionDTO;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class UserAccountHelper {
    @SuppressLint("StaticFieldLeak")
    private static UserAccountHelper _intance;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static SharedPreferences mPrefs;

    public static UserAccountHelper getIntance() {
        if (_intance == null)
            _intance = new UserAccountHelper();
        return _intance;
    }

    private UserAccountHelper() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void initContext(Context _context) {
        context = _context;
    }

    public static Context getContext() {
        return context;
    }

    public void setSecureKey(String key) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("Key", key);
        prefsEditor.apply();
    }

    public String getSecureKey() {
        return mPrefs.getString("Key", "");
    }

    public void setLogId(String logId) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("LogID", logId);
        prefsEditor.apply();
    }

    public String getLogId() {
        return mPrefs.getString("LogID", "");
    }

    public void setUserType(int userType) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt("UserType", userType);
        prefsEditor.apply();
    }

    public void setPhoneNumber(String Phone) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("Phone", Phone);
        prefsEditor.apply();
    }

    public String getPhoneNumber() {
        return mPrefs.getString("Phone", "");
    }

    public int getUserType() {
        return mPrefs.getInt("UserType", 0);
    }

    public void updateConfig(List<ConfigDTO> list) {
        try {
            for (ConfigDTO item :
                    list) {
                if (item.getName().equals("MaxDistance"))
                    AppConst.MaxDistances = Float.parseFloat(item.getValue());
                if (item.getName().equals("MaxTimeRecall"))
                    AppConst.MaxTimeUpdateLocation = Integer.parseInt(item.getValue());
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateConfig: " + ex);
        }
    }
}
