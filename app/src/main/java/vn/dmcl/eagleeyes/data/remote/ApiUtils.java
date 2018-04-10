package vn.dmcl.eagleeyes.data.remote;

public class ApiUtils {

    public ApiUtils() {
    }

    private static final String BASE_URL = "http://112.78.12.251:12345/api/";

    private static final String MAP_REALTIME_URL = "https://eagleeyerealtime.herokuapp.com/";

    public static ApiService getAPIBase() {
        return RetrofitClient.getClientBase(BASE_URL).create(ApiService.class);
    }

    public static ApiService getAPIMap() {
        return RetrofitClient.getClientMap(MAP_REALTIME_URL).create(ApiService.class);
    }
}
