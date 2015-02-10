package com.example.micaelacavallo.worldcupapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
public class WorldCupFragment extends Fragment {

    TextView mTextViewCountryCode;
    ListView mListViewMatches;
    final static String LOG_TAG = WorldCupFragment.class.getSimpleName();

    public WorldCupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTextViewCountryCode = (TextView) rootView.findViewById(R.id.edit_text_country_code);
        mListViewMatches = (ListView) rootView.findViewById(R.id.list_view_result);
        Button buttonGetTeams = (Button) rootView.findViewById(R.id.button_get_teams);
        buttonGetTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = mTextViewCountryCode.getText().toString();
                new FetchTeamsTask().execute(group);
            }
        });
        return rootView;
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

    private String readFullResponse (InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            stringBuilder.append(line).append("\n");
        }
        if (stringBuilder.length()>0) {
            response = stringBuilder.toString();
        }
        return response;
    }

    private List<String> parseResponse (String response) {
        final String HOME_TEAM= "home_team";
        final String AWAY_TEAM= "away_team";
        final String COUNTRY = "country";
        final String GOALS = "goals";
        List<String> teams = new ArrayList<>();
        try {
            JSONArray responseJsonArray = new JSONArray(response);
            JSONObject objectMatch;
            JSONObject objectAwayTeam;
            JSONObject objectHomeTeam;
            for (int i = 0; i< responseJsonArray.length(); i++) {
                objectMatch = responseJsonArray.getJSONObject(i);
                objectAwayTeam = objectMatch.getJSONObject(AWAY_TEAM);
                objectHomeTeam = objectMatch.getJSONObject(HOME_TEAM);
                String result = objectAwayTeam.getString(COUNTRY) + " " + objectAwayTeam.getString(GOALS) + " - " + objectHomeTeam.getString(GOALS) + " " + objectHomeTeam.getString(COUNTRY);
                teams.add(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return teams;

    }

    class FetchTeamsTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String country;
            String response = "";
            List<String> listOfTeams = null;
            if (params.length > 0) {
                country = params[0];
            } else {
                country = "USA";
            }
            try {
                URL url = constructURLQuery(country);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                try {
                    response = readFullResponse(httpURLConnection.getInputStream());
                    listOfTeams = parseResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listOfTeams;
        }

        @Override
        protected void onPostExecute(List<String> response) {
            super.onPostExecute(response);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, response);
            mListViewMatches.setAdapter(adapter);
        }
    }


}
