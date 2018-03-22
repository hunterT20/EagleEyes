package vn.dmcl.eagleeyes.helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class JsonHelper {

    private static JsonHelper _intance;

    public static JsonHelper getIntance() {
        if (_intance == null) _intance = new JsonHelper();
        return _intance;
    }

    private JsonHelper() {
    }

    public JSONObject GetListConfig(int pageIndex, int pageSize, String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("pageIndex", pageIndex);
            temp.put("pageSize", pageSize);
            temp.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject CheckKey(String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject UploadLocationPhoto(String logId, double lat, double lng, String base64Photo, String description, String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("logId", logId);
            temp.put("lat", lat);
            temp.put("lng", lng);
            temp.put("base64Photo", base64Photo);
            temp.put("description", description);
            temp.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject GetListLocation(String areaId, String key, long flyerId) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
            temp.put("areaId", areaId);
            temp.put("flyerId", flyerId);
            Log.e(TAG, "GetListLocation: " + " " + areaId + " " + key + " " + flyerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject GetListUser(String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }


    public JSONObject StopLog(String logId, double lat, double lng, String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
            temp.put("logId", logId);
            temp.put("lat", lat);
            temp.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }


    public HashMap Login(String phoneNumber, String otp) {
        HashMap<String,String> param = new HashMap<>();
        param.put("otp", otp);
        param.put("phoneNumber", phoneNumber);
        return param;
    }

    public JSONObject SendLocation(String logId, String key, double lat, double lng) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
            temp.put("logId", logId);
            temp.put("lat", lat);
            temp.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject StartLog(long UserID,String areaId, double lat, double lng, String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("flyerId",UserID);
            temp.put("key", key);
            temp.put("areaId", areaId);
            temp.put("lat", lat);
            temp.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public JSONObject GetListArea(String key) {
        JSONObject temp = new JSONObject();
        try {
            temp.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }


    public static String fixJsonString(String json) {
        return "{\"list\":" + json + "}";
    }

    public static String fixJsonString(String json, String strList) {
        return "{\"" + strList + "\":" + json + "}";
    }

    static String fixStringToJson(String json) {
        return "{" + json + "}";
    }
}
