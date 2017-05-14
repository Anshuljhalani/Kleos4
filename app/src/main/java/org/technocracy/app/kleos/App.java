package org.technocracy.app.kleos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import org.technocracy.app.kleos.activity.LoginActivity;
import org.technocracy.app.kleos.helper.DatabaseHandler;
import org.technocracy.app.kleos.helper.SessionManager;

public class App {
    //EndPoints
    public static final String URL = "http://kleos.pe.hu/kleos_testing/";
    public static final String REGISTER_URL = URL+"register.php";
    public static final String LOGIN_URL = URL+"login.php";
    public static final String QUESTION_URL = URL+"getQuestion.php";
    public static final String ANSWER_URL = URL+"checkAnswer.php";
    public static final String IMAGE1_URL = URL+"getImage1.php?username=";
    public static final String IMAGE2_URL = URL+"getImage2.php?username=";
    public static final String IMAGE3_URL = URL+"getImage3.php?username=";
    public static final String LEADERBOARD_URL = URL+"leaderboard.php";
    public static final String UPDATES_URL = URL+"getUpdates.php";
    public static final String REGISTER_FCM_TOKEN_URL = URL+"registerFCMToken.php";

    //Notifications
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final int lastLevel = 15;
    public static final String gameCompletedMessage="Congratulations! You have completed the game. " +
            "Keep an eye on Leaderboard and Updates section for further updates.";

    /*static final int LOGO_COLORS[] = {R.color.blue_dextra_logo, R.color.purple_dextra_logo,
            R.color.red_dextra_logo, R.color.orange_dextra_logo, R.color.yellow_dextra_logo, R.color.green_dextra_logo};*/

    public static final int SWIPE_REFRESH_COLORS[] = {R.color.colorPrimary, R.color.colorAccent};


    public static void logoutUser(SessionManager session, DatabaseHandler db, Activity activity) {
        session.setLogin(false);
        db.deleteUser();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static float getScreenWidth(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }


    public static void showProgressBar(ProgressBar progressBar) {
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(ProgressBar progressBar) {
        if (progressBar.getVisibility() != View.GONE
                || progressBar.getVisibility() != View.INVISIBLE)
            progressBar.setVisibility(View.GONE);
    }
}
