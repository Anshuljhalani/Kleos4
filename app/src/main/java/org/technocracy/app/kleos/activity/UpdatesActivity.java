package org.technocracy.app.kleos.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.technocracy.app.kleos.App;
import org.technocracy.app.kleos.R;
import org.technocracy.app.kleos.api.User;
import org.technocracy.app.kleos.helper.AppController;
import org.technocracy.app.kleos.helper.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdatesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseHandler db;
    private static final String TAG = UpdatesActivity.class.getSimpleName();
    private RecyclerView updatesRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HashMap<String,String>> updatesList;
    private UpdatesRecyclerViewAdapter updatesAdapter;
    private String currentDateString;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateString = dateFormat.format(Calendar.getInstance().getTime());

        db = new DatabaseHandler(getApplicationContext());
        final User user = db.getUser();

        updatesList = new ArrayList<HashMap<String, String>>();
        updatesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(updatesRecyclerView.getContext());
        updatesRecyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeResources(App.SWIPE_REFRESH_COLORS);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUpdates(user.getAccesstoken(),user.getUsername());
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getUpdates(user.getAccesstoken(),user.getUsername());
            }
        });
        //getUpdates(user.getAccesstoken(),user.getUsername());
    }



    private void getUpdates(final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_string_req = "req_updates";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                App.UPDATES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Updates Response: " + response.toString());
                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // show dialog box for next question or home.
                        JSONArray updates = jsonObject.getJSONArray("updates");
                        updatesList.clear();
                        for(int i = 0; i < updates.length(); i++){
                            JSONObject updatesObject = updates.getJSONObject(i);
                            HashMap<String,String> updatesEntry = new HashMap<>();
                            updatesEntry.put("update",updatesObject.getString("update"));
                            updatesEntry.put("update_detail",updatesObject.getString("update_detail"));
                            updatesEntry.put("posted_at",updatesObject.getString("posted_at"));
                            updatesList.add(i,updatesEntry);
                        }
                        updatesAdapter = new UpdatesRecyclerViewAdapter(UpdatesActivity.this,updatesList);
                        updatesRecyclerView.setAdapter(updatesAdapter);
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
                Log.e(TAG, "Updates Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
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


    class UpdatesRecyclerViewAdapter extends RecyclerView.Adapter<UpdatesRecyclerViewAdapter.UpdatesViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        ArrayList<HashMap<String,String>> updatesList;

        public UpdatesRecyclerViewAdapter(Context context, ArrayList<HashMap<String,String>> updatesList) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            this.updatesList = updatesList;
        }

        @Override
        public UpdatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_updates, parent, false);
            return new UpdatesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final UpdatesViewHolder holder, final int position) {
            holder.boundUpdate = updatesList.get(position);
            holder.updateCard.setBackgroundColor(getResources().getColor(R.color.white));
            SpannableString spanString = new SpannableString("update");
           // spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            holder.update.setText(Html.fromHtml(holder.boundUpdate.get("update")));

            holder.updateDetail.setText(Html.fromHtml(holder.boundUpdate.get("update_detail")));
            holder.updateTime.setText(Html.fromHtml(holder.boundUpdate.get("posted_at")));

            setUpdateTime(holder.updateTime,holder.boundUpdate.get("posted_at"));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }

            });
        }

        @Override
        public int getItemCount() {
            return updatesList.size();
        }

        public class UpdatesViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final CardView updateCard;
            public final TextView update;
            public final TextView updateDetail;
            public final TextView updateTime;
            public HashMap<String,String> boundUpdate;

            public UpdatesViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                updateCard = (CardView) itemView.findViewById(R.id.updates_card);
                update = (TextView) itemView.findViewById(R.id.update_name_textView);
                updateDetail = (TextView) itemView.findViewById(R.id.update_detail_textView);
                updateTime = (TextView) itemView.findViewById(R.id.posted_on_textView);
            }
        }
    }

    private void setUpdateTime(TextView updateTimeTextView, String updateTime) {
        try {
            Date currentDate = dateFormat.parse(currentDateString);
            Date updateDate = dateFormat.parse(updateTime);
            long differenceInMS = currentDate.getTime() - updateDate.getTime();
            long differenceInSecs = TimeUnit.MILLISECONDS.toSeconds(differenceInMS);
            if (differenceInSecs < 60) {
                updateTimeTextView.setText("Just now");
            } else if (differenceInSecs < 3600) {
                long differenceInMins = TimeUnit.SECONDS.toMinutes(differenceInSecs);
                if (differenceInMins == 1)
                    updateTimeTextView.setText(differenceInMins + " minute ago");
                else
                    updateTimeTextView.setText(differenceInMins + " minutes ago");
            } else if (differenceInSecs < 86400) {
                long differenceInHrs = TimeUnit.SECONDS.toHours(differenceInSecs);
                if (differenceInHrs == 1)
                    updateTimeTextView.setText("about an hour ago");
                else
                    updateTimeTextView.setText(differenceInHrs + " hours ago");
            } else if (differenceInSecs < 604800) {
                long differenceInDays = TimeUnit.SECONDS.toDays(differenceInSecs);
                String time = new SimpleDateFormat("h:mm aa").format(updateDate);
                if (differenceInDays == 1)
                    updateTimeTextView.setText("Yesterday at " + time);
                else {
                    Calendar c = Calendar.getInstance();
                    c.setTime(updateDate);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    switch (dayOfWeek) {
                        case 1:
                            updateTimeTextView.setText("Mon at " + time);
                            break;
                        case 2:
                            updateTimeTextView.setText("Tue at " + time);
                            break;
                        case 3:
                            updateTimeTextView.setText("Wed at " + time);
                            break;
                        case 4:
                            updateTimeTextView.setText("Thu at " + time);
                            break;
                        case 5:
                            updateTimeTextView.setText("Fri at " + time);
                            break;
                        case 6:
                            updateTimeTextView.setText("Sat at " + time);
                            break;
                        case 7:
                            updateTimeTextView.setText("Sun at " + time);
                            break;
                    }
                }
            } else {
                String time = new SimpleDateFormat("h:mm aa").format(updateDate);
                String date = new SimpleDateFormat("d MMM").format(updateDate);
                updateTimeTextView.setText(date + " at " + time);
            }

        } catch (ParseException e) {
            updateTimeTextView.setText(updateTime);
            e.printStackTrace();
        }
    }

}
