package vn.dmcl.eagleeyes.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {

    public static void showSimpleAlertDialog(Context activity, String title, String message, String buttonText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    static void showAlertDialog(Activity activity, String title, String message, String buttonText, final onListenerOneButtonClick listenerOneButtonClick) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerOneButtonClick != null) {
                    listenerOneButtonClick.onButtonClick();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showAlertDialog(Activity activity, String title, String message, String buttonText, final onListenerOneButtonClick listenerOneButtonClick, boolean isCanCancel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(isCanCancel);
        alertDialogBuilder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerOneButtonClick != null) {
                    listenerOneButtonClick.onButtonClick();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private static void showAlertDialog(Activity activity, String title, String message, String leftButtonText, String rightButtonText, final onListenerTwoButtonClick listenerTwoButtonClick) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerTwoButtonClick != null) {
                    listenerTwoButtonClick.onLeftButtonClick();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(rightButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerTwoButtonClick != null) {
                    listenerTwoButtonClick.onRightButtonClick();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    static void showAlertDialog(Context context, String title, String message, String leftButtonText, String rightButtonText, final onListenerTwoButtonClick listenerTwoButtonClick) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerTwoButtonClick != null) {
                    listenerTwoButtonClick.onLeftButtonClick();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(rightButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerTwoButtonClick != null) {
                    listenerTwoButtonClick.onRightButtonClick();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showAlertDialog(Activity activity, String title, String message, String leftButtonText, String middleButtonText, String rightButtonText,
                                       final onListenerThreeButtonClick listenerThreeButtonClick) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerThreeButtonClick != null) {
                    listenerThreeButtonClick.onLeftButtonClick();
                }
            }
        });

        alertDialogBuilder.setNeutralButton(middleButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerThreeButtonClick != null) {
                    listenerThreeButtonClick.onMiddleButtonClick();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(rightButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listenerThreeButtonClick != null) {
                    listenerThreeButtonClick.onRightButtonClick();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    public interface onListenerOneButtonClick {
        public void onButtonClick();
    }

    public interface onListenerTwoButtonClick {
        public void onLeftButtonClick();

        public void onRightButtonClick();
    }

    public interface onListenerThreeButtonClick {
        public void onLeftButtonClick();

        public void onMiddleButtonClick();

        public void onRightButtonClick();
    }
}
