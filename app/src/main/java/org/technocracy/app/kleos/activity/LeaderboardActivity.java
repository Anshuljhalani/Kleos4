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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseHandler db;
    private static final String TAG = LeaderboardActivity.class.getSimpleName();
    private RecyclerView leaderboardRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HashMap<String,String>> leaderBoardList;
    private LeaderBoardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        db = new DatabaseHandler(getApplicationContext());
        final User user = db.getUser();

        leaderboardRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(leaderboardRecyclerView.getContext());
        leaderboardRecyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeResources(App.SWIPE_REFRESH_COLORS);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLeaderboard(user.getAccesstoken(),user.getUsername());
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getLeaderboard(user.getAccesstoken(),user.getUsername());
            }
        });
        //getLeaderboard(user.getAccesstoken(),user.getUsername());

    }

    private void getLeaderboard(final String accessToken, final String username) {
        // Tag used to cancel the request
        String tag_string_req = "req_leaderboard";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                App.LEADERBOARD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Leaderboard Response: " + response.toString());
                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // show dialog box for next question or home.
                        JSONArray leaderboard = jsonObject.getJSONArray("leaderboard");
                        leaderBoardList = new ArrayList<HashMap<String, String>>();
                        for(int i = 0; i < leaderboard.length(); i++){
                            JSONObject leaderboardObject = leaderboard.getJSONObject(i);
                            HashMap<String,String> leaderboardEntry = new HashMap<>();
                            leaderboardEntry.put("username",leaderboardObject.getString("username"));
                            leaderboardEntry.put("level",leaderboardObject.getString("level"));
                            leaderboardEntry.put("answered_at",leaderboardObject.getString("answered_at"));
                            leaderBoardList.add(i,leaderboardEntry);
                        }
                        leaderboardAdapter = new LeaderBoardAdapter(LeaderboardActivity.this,leaderBoardList);
                        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

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
                Log.e(TAG, "Leaderboard Request Error: " + error.getMessage());
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

    class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderboardViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        ArrayList<HashMap<String,String>> leaderboardList;

        public LeaderBoardAdapter(Context context, ArrayList<HashMap<String,String>> leaderboardList) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            this.leaderboardList = leaderboardList;
        }

        @Override
        public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_leaderboard, parent, false);
            return new LeaderboardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LeaderboardViewHolder holder, final int position) {
            holder.boundLeader = leaderboardList.get(position);
            holder.leaderCard.setBackgroundColor(getResources().getColor(R.color.white));
            holder.leaderUsername.setText(Html.fromHtml(holder.boundLeader.get("username")));
            holder.leaderLevel.setText(Html.fromHtml(holder.boundLeader.get("level")));
            holder.answered_at.setText(Html.fromHtml(holder.boundLeader.get("answered_at")));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }

            });
        }

        @Override
        public int getItemCount() {
            return leaderboardList.size();
        }

        public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final CardView leaderCard;
            public final TextView leaderUsername;
            public final TextView leaderLevel;
            public final TextView answered_at;
            public HashMap<String,String> boundLeader;

            public LeaderboardViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                leaderCard = (CardView) itemView.findViewById(R.id.leaderboard_card);
                leaderUsername = (TextView) itemView.findViewById(R.id.username_leaderboard);
                leaderLevel = (TextView) itemView.findViewById(R.id.user_level_leaderboard);
                answered_at= (TextView) itemView.findViewById(R.id.answered_at);
            }
        }
    }
}
