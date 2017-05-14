package org.technocracy.app.kleos.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import org.technocracy.app.kleos.App;
import org.technocracy.app.kleos.R;
import org.technocracy.app.kleos.api.User;
import org.technocracy.app.kleos.helper.DatabaseHandler;
import org.technocracy.app.kleos.helper.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView kleos;
    private TextView kleosr;

    private DatabaseHandler db;
    private SessionManager session;
    private BoomMenuButton boomMenuButtonInActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);

        FirebaseMessaging.getInstance().subscribeToTopic("kleos");
        FirebaseInstanceId.getInstance().getToken();

        boomMenuButtonInActionBar = (BoomMenuButton) findViewById(R.id.boom);
        boomMenuButtonInActionBar.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override
            public void onClick(int buttonIndex) {
                switch (buttonIndex){
                    case 0:
                        Intent toLeaderboard = new Intent(HomeActivity.this,LeaderboardActivity.class);
                        startActivity(toLeaderboard);
                        break;
                    case 1:
                        Intent toGame = new Intent(HomeActivity.this,GameActivity.class);
                        startActivity(toGame);
                        break;
                    case 2:
                        Intent toUpdates = new Intent(HomeActivity.this,UpdatesActivity.class);
                        startActivity(toUpdates);
                        break;
                    case 3:
                        App.logoutUser(session,db,HomeActivity.this);
                        break;
                }
            }
        });
        //boomMenuButtonInActionBar.setAnimatorListener(this);

        //usernameTextView = (TextView) findViewById(R.id.username_textView);
        //emailTextView = (TextView) findViewById(R.id.email_textView);
        kleos= (TextView) findViewById(R.id.kleos_textView);
         kleos.setText("Kleos is an online event consisting of various levels of puzzles, riddles and brain teasers combined together. This event helps acquire knowledge about solving various types of puzzles and increasing individual’s intellect, logical reasoning and aptitude. The main goal of this game is to reach the next level at any cost. This game will test your power of logic, creativity and your ability to connect between unrelated things. The game is meant to be fun.");
        kleosr= (TextView) findViewById(R.id.kleosr_textView);
        kleosr.setText("1.There are no constraints on the number of attempt.\n2. Marking will be based on the highest level reached and time taken by player to solve last level answered.\n3. Scores of top players will be displayed on the leaderboard.\n4. Google is your best friend.\n5. No caps No spaces No special characters are allowed in answer.\n6. Time spent will be calculated by taking difference between timestamps.\n7. Make sure you have a good internet connection.\n8. There are 14 levels in game.\n9. You are free to move/zoom clue images anyway you want to");
        db = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            App.logoutUser(session,db,HomeActivity.this);
        }
        User user = db.getUser();
        //usernameTextView.setText(user.getUsername());
        //emailTextView.setText(user.getEmail());
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Drawable leaderBoardDrawable=getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        leaderBoardDrawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        Drawable gameDrawable=getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp);
        gameDrawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        Drawable updatesDrawable=getResources().getDrawable(R.drawable.ic_notifications_none_black_24dp);
        updatesDrawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        Drawable logoutDrawable=getResources().getDrawable(R.drawable.ic_account_circle_black_24dp);
        logoutDrawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        int[][] subButtonColors = new int[4][2];
        subButtonColors[0][1] = ContextCompat.getColor(this, R.color.red);
        subButtonColors[0][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);
        subButtonColors[1][1] = ContextCompat.getColor(this, R.color.amber);
        subButtonColors[1][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);
        subButtonColors[2][1] = ContextCompat.getColor(this, R.color.green);
        subButtonColors[2][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);
        subButtonColors[3][1] = ContextCompat.getColor(this, R.color.light_blue);
        subButtonColors[3][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);
        boomMenuButtonInActionBar.init(
                new Drawable[]{leaderBoardDrawable, gameDrawable, updatesDrawable, logoutDrawable}, // The drawables of images of sub buttons. Can not be null.
                new String[]{"Leaderboard", "Game", "Hints/Updates", "Logout"},     // The texts of sub buttons, ok to be null.
                subButtonColors,    // The colors of sub buttons, including pressed-state and normal-state.
                ButtonType.HAM,     // The button type.
                BoomType.HORIZONTAL_THROW,  // The boom type.
                PlaceType.HAM_4_1,  // The place type.
                null,               // Ease type to move the sub buttons when showing.
                null,               // Ease type to scale the sub buttons when showing.
                null,               // Ease type to rotate the sub buttons when showing.
                null,               // Ease type to move the sub buttons when dismissing.
                null,               // Ease type to scale the sub buttons when dismissing.
                null,               // Ease type to rotate the sub buttons when dismissing.
                null                // Rotation degree.
        );
    }
}
