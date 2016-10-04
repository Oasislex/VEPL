package by.scherbakov.vepl;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScorersMaterialFragment extends Fragment {

    private static final String MATCH_INFO_URL_START = "http://www.premierleague.com/ajax/match/";
    private static final String MATCH_INFO_URL_END = "/fixture-header.json";
    private static final String HTML_MATCH_INFO_URL = "http://www.premierleague.com/match/";
    private int matchId;
    private boolean isHomeScorers;
    private String playerId;

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public void setHomeScorers(boolean homeScorers) {
        isHomeScorers = homeScorers;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView teamRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_scorers_material, container, false);

        new HTMLParse(teamRecycler).execute(HTML_MATCH_INFO_URL + matchId);
        //new JSONParse(teamRecycler).execute(MATCH_INFO_URL_START + matchId + MATCH_INFO_URL_END);

        return teamRecycler;
    }

    class HTMLParse extends AsyncTask<String, Void, Document> {
        private String url;
        private final WeakReference<RecyclerView> teamRecyclerReference;

        public HTMLParse(RecyclerView teamRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            teamRecyclerReference = new WeakReference<RecyclerView>(teamRecycler);
        }

        @Override
        protected Document doInBackground(String... args) {
            url = args[0];
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }
        @Override
        protected void onPostExecute(Document document) {

            try {
                Elements htmlGoals;
                if(isHomeScorers){
                    htmlGoals = document.select("div[class=timeLine timeLineContainer]").select("div[class=eventLine timeLineEventsContainer]").select("div[class=event home]");
                }else{
                    htmlGoals = document.select("div[class=timeLine timeLineContainer]").select("div[class=eventLine timeLineEventsContainer]").select("div[class=event away]");
                }
                int valueGoals = 0;
                for(int i=0; i<htmlGoals.size(); i++){
                    if ((htmlGoals.get(i).select("span").attr("class").equals("icn card-red")) || (htmlGoals.get(i).select("span").attr("class").equals("icn card-yellow-red")) || (htmlGoals.get(i).select("span").attr("class").equals("icn card-yellow")) ) {
                        valueGoals++;
                    }else if (htmlGoals.get(i).select("span").attr("class").equals("icn sub-w")) {
                        valueGoals=valueGoals+2;
                    }else{
//                        if (htmlGoals.get(i).select("div[class=assist]").size()>0){
//                            valueGoals=valueGoals+2;
//                        }else{
                            valueGoals=valueGoals+1;
//                        }
                    }
                }

                final String[] scorerIds = new String[valueGoals];
                final String[] scorerNames = new String[valueGoals];
                final String[] icoTypes = new String[valueGoals];
                final String[] hrefPlayers = new String[valueGoals];

                int j=0;
                for(int i=0; i<htmlGoals.size(); i++){
                    if ((htmlGoals.get(i).select("span").attr("class").equals("icn card-red")) || (htmlGoals.get(i).select("span").attr("class").equals("icn card-yellow-red")) || (htmlGoals.get(i).select("span").attr("class").equals("icn card-yellow")) ) {

                        scorerIds[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("img").attr("data-player");
                        scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
                        icoTypes[j] = htmlGoals.get(i).select("span").attr("class");
                        hrefPlayers[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").attr("href");
                        j++;

                    }else if (htmlGoals.get(i).select("span").attr("class").equals("icn sub-w")) {

                        scorerIds[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("img").attr("data-player");
                        scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
                        icoTypes[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").select("div").attr("class");
                        hrefPlayers[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").attr("href");
                        j++;
                        scorerIds[j] = htmlGoals.get(i).select("div[class=eventInfoContent subOn]").select("img").attr("data-player");
                        scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent subOn]").select("a[class=name]").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
                        icoTypes[j] = htmlGoals.get(i).select("div[class=eventInfoContent subOn]").select("a[class=name]").select("div").attr("class");
                        hrefPlayers[j] = htmlGoals.get(i).select("div[class=eventInfoContent subOn]").select("a[class=name]").attr("href");
                        j++;

                    }else{
                        if (htmlGoals.get(i).select("div[class=assist]").size()>0){
                            scorerIds[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("img").attr("data-player");
                            scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
                            icoTypes[j] = htmlGoals.get(i).select("span").attr("class");
                            hrefPlayers[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").attr("href");
                            j++;
//                            String href = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("div[class=assist]").select("a").attr("href");
//                            scorerIds[j] = document.select("section[class=squads mcMainTab]").select("a[href="+ href +"]").select("img").attr("data-player");
//                            scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("div[class=assist]").select("a").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
//                            icoTypes[j] = "icn ball-assist";
//                            j++;
                        }else{
                            scorerIds[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("img").attr("data-player");
                            scorerNames[j] = htmlGoals.get(i).select("div[class=eventInfoContent ]").select("a[class=name]").text() + " " + htmlGoals.get(i).select("time[class=min]").text();
                            icoTypes[j] = htmlGoals.get(i).select("span").attr("class");
                            j++;
                        }
                    }
                }


                final RecyclerView teamRecycler = teamRecyclerReference.get();
                CaptionedImagesScorersAdapter adapter = new CaptionedImagesScorersAdapter(scorerIds, scorerNames, icoTypes);
                teamRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                teamRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesScorersAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra(PlayerActivity.EXTRA_PLAYERNO, hrefPlayers[position]);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        getActivity().startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    class JSONParse extends AsyncTask<String, Void, JSONObject> {
        private String url;
        private final WeakReference<RecyclerView> teamRecyclerReference;

        public JSONParse(RecyclerView teamRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            teamRecyclerReference = new WeakReference<RecyclerView>(teamRecycler);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            url = args[0];
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONObject matchDetails = json.getJSONObject("matchHeaderSection").getJSONObject("matchDetails");
                JSONArray scorers;
                if(isHomeScorers){
                    scorers = matchDetails.getJSONArray("homeTeamScorers");
                }else{
                    scorers = matchDetails.getJSONArray("awayTeamScorers");
                }
                int valueGoals = 0;
                for(int i=0; i<scorers.length(); i++){
                    valueGoals = valueGoals + scorers.getJSONObject(i).getJSONArray("goals").length();
                }

                final String[] scorerIds = new String[valueGoals];
                final String[] scorerNames = new String[valueGoals];
                final String[] scorerTimes = new String[valueGoals];
                final String[] scorerTypes = new String[valueGoals];

                ArrayList<ScorerGoal> ListScorers = new ArrayList<ScorerGoal>();

                for (int i=0; i<scorers.length(); i++) {

                    JSONArray goals = scorers.getJSONObject(i).getJSONArray("goals");
                    for (int j=0; j<goals.length();j++){


                        String scorerId = scorers.getJSONObject(i).getJSONObject("player").getString("id");
                        String scorerName = scorers.getJSONObject(i).getJSONObject("player").getString("fullName");
                        int scorerTime = goals.getJSONObject(j).getInt("time");
                        String scorerTimePlus = goals.getJSONObject(j).getString("time+x");
                        String scorerType;
                        if(goals.getJSONObject(j).getString("type").equals("PENALTY")){
                            scorerType = getString(R.string.penalty);
                        }else if(goals.getJSONObject(j).getString("type").equals("OWN_GOAL")){
                            scorerType = getString(R.string.own_goal);
                        }else{
                            scorerType = "";
                        }

                        ListScorers.add(new ScorerGoal(scorerId,scorerName,scorerTime,scorerTimePlus,scorerType));




                    }

                }

                Collections.sort(ListScorers);
                for (int i=0; i<ListScorers.size(); i++){
                    scorerIds[i] = ListScorers.get(i).getScorerId();
                    scorerNames[i] = ListScorers.get(i).getScorerName();
                    scorerTimes[i] = ListScorers.get(i).getScorerTimePlus();
                    scorerTypes[i] = ListScorers.get(i).getScorerType();
                }


                final RecyclerView teamRecycler = teamRecyclerReference.get();
                CaptionedImagesScorersAdapter adapter = new CaptionedImagesScorersAdapter(scorerIds, scorerNames, scorerTimes, scorerTypes);
                teamRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                teamRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesScorersAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra(PlayerActivity.EXTRA_PLAYERNO, scorerIds[position]);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        getActivity().startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
