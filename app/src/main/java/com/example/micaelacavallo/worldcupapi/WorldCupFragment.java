package com.example.micaelacavallo.worldcupapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorldCupFragment extends ListFragment {

    TextView mTextViewCountryCode;
     MatchAdapter mAdapter;
    final static String LOG_TAG = WorldCupFragment.class.getSimpleName();

    public WorldCupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        wireUpViews(rootView);
        prepareButton(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void wireUpViews(View rootView) {
        mTextViewCountryCode = (TextView) rootView.findViewById(R.id.edit_text_country_code);
    }

    private void prepareButton(View rootView) {
        Button buttonGetTeams = (Button) rootView.findViewById(R.id.button_get_teams);
        buttonGetTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mTextViewCountryCode.getText().toString();
                fetchReposInQueue(code);

            }
        });
    }

    private void fetchReposInQueue(String code) {
        try {
            URL url = constructURLQuery(code);
            Request request = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<Match> listOfMatches = parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfMatches);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void prepareListView() {
        List<Match> matches = new ArrayList<>();
        mAdapter = new MatchAdapter(getActivity(), matches);
        setListAdapter(mAdapter);
    }

    private URL constructURLQuery(String country) throws MalformedURLException {
        final String WORLD_CUP_BASE_URL = "worldcup.sfg.io";
        final String MATCHES_PATH = "matches";
        final String COUNTRY_PATH = "country";
        final String RESULT_ENDPOINT = "fifa_code";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(WORLD_CUP_BASE_URL).
                appendPath(MATCHES_PATH).
                appendPath(COUNTRY_PATH).
                appendQueryParameter(RESULT_ENDPOINT, country);
        Uri uri = builder.build();
        Log.d(LOG_TAG, "Built URI: " + uri.toString());
        return new URL(uri.toString());
    }

    private List<Match> parseResponse (String response) {
        final String HOME_TEAM= "home_team";
        final String AWAY_TEAM= "away_team";
        final String CODE = "code";
        final String GOALS = "goals";
        final String LOCATION = "location";
        List<Match> matches = new ArrayList<>();
        Match match;
        try {
            JSONArray responseJsonArray = new JSONArray(response);
            JSONObject objectMatch, objectAwayTeam, objectHomeTeam;
            for (int i = 0; i< responseJsonArray.length(); i++) {
                objectMatch = responseJsonArray.getJSONObject(i);
                objectAwayTeam = objectMatch.getJSONObject(AWAY_TEAM);
                objectHomeTeam = objectMatch.getJSONObject(HOME_TEAM);
                match = new Match();
                match.setmTeams(objectAwayTeam.getString(CODE) + " " + objectAwayTeam.getString(GOALS) + " - " + objectHomeTeam.getString(GOALS) + " " + objectHomeTeam.getString(CODE));
                match.setmLocation(objectMatch.getString(LOCATION));
                matches.add(match);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return matches;
    }
}
