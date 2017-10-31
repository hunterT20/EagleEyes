package vn.dmcl.eagleeyes.common;

public class AppConst {
    public static String ServiceUrl = "http://112.78.12.251:12345/api/";

    public static float MaxDistances = 10;
    public static float MaxDistancesApproximately = 100;
    public static int MaxTimeUpdateLocation = 120000;
    public static int PerLocaRequestCode = 10;
    public static int SessionEnded = 300;
    public static int CameraRequestCode = 9999;

    public static enum AsyncMethod {
        POST, GET
    }

    public static class UserType {
        public static final int FLYER = 0;
        public static final int DCHECK = 1;
    }

    public static class AreaStatus {
        public static final int None = 0;
        public static final int Started = 1;
        public static final int Ended = 2;
    }
}
