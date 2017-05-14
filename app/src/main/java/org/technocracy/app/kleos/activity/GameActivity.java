package org.technocracy.app.kleos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.technocracy.app.kleos.App;
import org.technocracy.app.kleos.R;
import org.technocracy.app.kleos.api.Question;
import org.technocracy.app.kleos.api.User;
import org.technocracy.app.kleos.helper.AppController;
import org.technocracy.app.kleos.helper.DatabaseHandler;
import org.technocracy.app.kleos.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView levelTextView;
    private ScrollView questionScrollView;
    private LinearLayout questionLayout;
    private TextView questionPart1TextView;
    private TextView questionPart2TextView;
    private SubsamplingScaleImageView imagePart1ImageView;
    private SubsamplingScaleImageView  imagePart2ImageView;
    private SubsamplingScaleImageView  imagePart3ImageView;
    private LinearLayout answerLayout;
    private EditText answerEditText;
    private Button submitButton;
    private ProgressBar loadingProgressBar;
    private ProgressBar image1ProgressBar;
    private ProgressBar image2ProgressBar;
    private ProgressBar image3ProgressBar;
    private SessionManager session;
    private DatabaseHandler db;
    private static final String TAG = GameActivity.class.getSimpleName();
    private User user;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        levelTextView = (TextView) findViewById(R.id.level_textView);
        questionScrollView = (ScrollView) findViewById(R.id.question_scroll_view);
        questionLayout = (LinearLayout) findViewById(R.id.question_layout);
        questionPart1TextView = (TextView) findViewById(R.id.question_part1_textView);
        questionPart2TextView = (TextView) findViewById(R.id.question_part2_textView);
        imagePart1ImageView = (SubsamplingScaleImageView ) findViewById(R.id.image_part1_imageView);
        imagePart2ImageView = (SubsamplingScaleImageView ) findViewById(R.id.image_part2_imageView);
        imagePart3ImageView = (SubsamplingScaleImageView ) findViewById(R.id.image_part3_imageView);
        answerLayout = (LinearLayout) findViewById(R.id.answer_layout);
        answerEditText = (EditText) findViewById(R.id.answer_editText);
        submitButton = (Button) findViewById(R.id.submit_button);
        loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        image1ProgressBar = (ProgressBar) findViewById(R.id.image1_progressBar);
        image2ProgressBar = (ProgressBar) findViewById(R.id.image2_progressBar);
        image3ProgressBar = (ProgressBar) findViewById(R.id.image3_progressBar);

        db = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            App.logoutUser(session, db, GameActivity.this);
        }
        user = db.getUser();

        getQuestion(user.getAccesstoken(), user.getUsername());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String submittedAnswer = answerEditText.getText().toString().trim();
                if (!submittedAnswer.isEmpty() && submittedAnswer.length() > 0) {
                    checkAnswer(submittedAnswer, user.getAccesstoken(), user.getUsername());
                }
            }
        });

    }

    private void getQuestion(final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_string_req = "req_question";
        App.showProgressBar(loadingProgressBar);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                App.QUESTION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Question Response: " + response.toString());
                App.hideProgressBar(loadingProgressBar);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // Creating Question Object
                        int level = jsonObject.getInt("level");
                        if (level == App.lastLevel) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                            builder.setTitle("Awesome!");
                            builder.setMessage(App.gameCompletedMessage);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    finish();
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();
                        } else {
                            JSONObject questionObject = jsonObject.getJSONObject("question");

                            question = new Question(questionObject.getString("question_part1"),
                                    questionObject.getString("question_part2"),
                                    (questionObject.getString("ratio1") != null && !questionObject.getString("ratio1").equals("null")) ? Double.parseDouble(questionObject.getString("ratio1")) : 0,
                                    (questionObject.getString("ratio2") != null && !questionObject.getString("ratio2").equals("null")) ? Double.parseDouble(questionObject.getString("ratio2")) : 0,
                                    (questionObject.getString("ratio3") != null && !questionObject.getString("ratio3").equals("null")) ? Double.parseDouble(questionObject.getString("ratio3")) : 0);
                            if (question.getQuestionPart1() != null && !question.getQuestionPart1().equals("null")) {
                                Spanned questionText = Html.fromHtml(question.getQuestionPart1());
                                questionPart1TextView.setText(questionText);
                                questionPart1TextView.setVisibility(View.VISIBLE);
                            }
                            if (question.getQuestionPart2() != null && !question.getQuestionPart2().equals("null")) {
                                Spanned questionText = Html.fromHtml(question.getQuestionPart2());
                                questionPart2TextView.setText(questionText);
                                questionPart2TextView.setVisibility(View.VISIBLE);
                            }
                            if (question.getRatio1() != 0.0) {
                                int width = Math.round(App.getScreenWidth(GameActivity.this) - 2 * getResources().getDimensionPixelSize(R.dimen.question_image_horizontal_margin));
                                int height = (int) Math.round(width * question.getRatio1());
                                height = height / 2;

                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                paint.setColor(ContextCompat.getColor(GameActivity.this, R.color.colorPrimaryDark));
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawPaint(paint);
                                imagePart1ImageView.setImage(ImageSource.bitmap(bitmap));
                                imagePart1ImageView.setVisibility(View.VISIBLE);

                                getImage1(width, height, accessToken, username);
                            }
                            if (question.getRatio2() != 0.0) {
                                int width = Math.round(App.getScreenWidth(GameActivity.this) - 2 * getResources().getDimensionPixelSize(R.dimen.question_image_horizontal_margin));
                                int height = (int) Math.round(width * question.getRatio1());
                                height = height / 2;

                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                paint.setColor(ContextCompat.getColor(GameActivity.this, R.color.colorPrimaryDark));
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawPaint(paint);
                                imagePart2ImageView.setImage(ImageSource.bitmap(bitmap));
                                imagePart2ImageView.setVisibility(View.VISIBLE);

                                getImage2(width, height, accessToken, username);
                            }
                            if (question.getRatio3() != 0.0) {
                                int width = Math.round(App.getScreenWidth(GameActivity.this) - 2 * getResources().getDimensionPixelSize(R.dimen.question_image_horizontal_margin));
                                int height = (int) Math.round(width * question.getRatio1());
                                height = height / 2;

                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                paint.setColor(ContextCompat.getColor(GameActivity.this, R.color.colorPrimaryDark));
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawPaint(paint);
                                imagePart3ImageView.setImage(ImageSource.bitmap(bitmap));
                                imagePart3ImageView.setVisibility(View.VISIBLE);

                                getImage3(width, height, accessToken, username);
                            }
                            //Toast.makeText(getApplicationContext(),"Values of ratios\n"+question.getRatio1()+"\n"+question.getRatio2()+"\n"+question.getRatio3(),Toast.LENGTH_LONG).show();
                            answerLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Json error: ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getQuestion Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                App.hideProgressBar(loadingProgressBar);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getImage1(final int width, final int height,
                           final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_image_req = "req_image1";
        Log.e("Image 1", " Fetching");
        App.showProgressBar(image1ProgressBar);

        ImageRequest imgReq = new ImageRequest(App.IMAGE1_URL + username, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.e("Image 1", " Fetched");
                imagePart1ImageView.setImage(ImageSource.bitmap(response));
                imagePart1ImageView.setVisibility(View.VISIBLE);
                App.hideProgressBar(image1ProgressBar);
            }
        }, width, height, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getImage1 Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                App.hideProgressBar(image1ProgressBar);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(imgReq, tag_image_req);
    }

    private void getImage2(final int width, final int height,
                           final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_image_req = "req_image2";
        Log.e("Image 2", " Fetching");
        App.showProgressBar(image2ProgressBar);

        ImageRequest imgReq = new ImageRequest(App.IMAGE2_URL + username, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.e("Image 2", " Fetched");
                imagePart2ImageView.setImage(ImageSource.bitmap(response));
                imagePart2ImageView.setVisibility(View.VISIBLE);
                App.hideProgressBar(image2ProgressBar);
            }
        }, width, height, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getImage2 Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                App.hideProgressBar(image2ProgressBar);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(imgReq, tag_image_req);
    }

    private void getImage3(final int width, final int height,
                           final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_image_req = "req_image3";
        Log.e("Image 3", " Fetching");
        App.showProgressBar(image3ProgressBar);

        ImageRequest imgReq = new ImageRequest(App.IMAGE3_URL + username, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.e("Image 3", " Fetched");
                imagePart3ImageView.setImage(ImageSource.bitmap(response));
                imagePart3ImageView.setVisibility(View.VISIBLE);
                App.hideProgressBar(image3ProgressBar);
            }
        }, width, height, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getImage3 Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                App.hideProgressBar(image3ProgressBar);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(imgReq, tag_image_req);
    }

    private void checkAnswer(final String submittedAnswer, final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_string_req = "req_check_answer";
        App.showProgressBar(loadingProgressBar);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                App.ANSWER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Answer Response: " + response.toString());
                App.hideProgressBar(loadingProgressBar);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // show dialog box for next question or home.
                        String timeWhenAnswered = jsonObject.getString("status");
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                        builder.setTitle("Correct Answer");
                        builder.setMessage("Bravo! You have " + timeWhenAnswered + ".");
                        builder.setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent toNextQuestion = new Intent(GameActivity.this, GameActivity.class);
                                startActivity(toNextQuestion);
                                finish();
                            }
                        });
                        builder.setNegativeButton("Go back to Home Screen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                finish();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    } else {
                        // Error. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Json error: ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "checkAnswer Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                App.hideProgressBar(loadingProgressBar);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("answer", submittedAnswer);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
