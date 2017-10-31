package vn.dmcl.eagleeyes.helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import vn.dmcl.eagleeyes.common.AppConst;

public class UrlGenerationHelper {
    private static JSONObject map;
    String functionName = "";

    private static UrlGenerationHelper _UrlGenerationHelper;

    public static UrlGenerationHelper getInstance() {
        if (_UrlGenerationHelper == null)
            _UrlGenerationHelper = new UrlGenerationHelper();
        return _UrlGenerationHelper;
    }

    UrlGenerationHelper() {
        map = new JSONObject();
    }

    public void setMap(JSONObject hmap) {
        map = hmap;
    }

    public void putData(String key, Object value) {
        try {
            map.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ClearData() {
        if (map != null) map = new JSONObject();
    }

    String generateUrl() {
        StringBuilder value = new StringBuilder(AppConst.ServiceUrl + functionName);
        try {
            Iterator iter = map.keys();
            if (iter != null)
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    if (value.toString().contentEquals(AppConst.ServiceUrl + functionName))
                        value.append("?");
                    else value.append("&");
                    value.append(key).append("=").append(map.get(key).toString());
                }
        } catch (Exception ignored) {
        }

        Log.d("mapi", value.toString());
        return value.toString();
    }

    public String generateJsonString() {
        String value = "{}";
        /*try{
            if(map.size() > 0)
                value = new Gson().toJson(map);
        } catch (Exception ex){}*/
        return value;
    }


}
