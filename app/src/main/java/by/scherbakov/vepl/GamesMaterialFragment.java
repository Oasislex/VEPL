package by.scherbakov.vepl;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class GamesMaterialFragment extends Fragment {

    private static final String GAMES_HEADER_URL = "http://www.premierleague.com/ajax/site-header.json";
    private final static String LIST_CLUBS_URL = "http://www.premierleague.com/pa-services/api/football/desktop/competition/fandr/api/seasons.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";
    private final static String HTML_GAMES_URL = "http://www.premierleague.com/";
    private final static String HTML_LOGO_URL = "http://platform-static-files.s3.amazonaws.com/premierleague/badges/t";

    private JSONObject jsonTeams;
    private int startPosition;

    public GamesMaterialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView gameRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_games_material, container, false);
        //new JSONParse(gameRecycler).execute(GAMES_HEADER_URL, LIST_CLUBS_URL);
        new HTMLParse(gameRecycler).execute(HTML_GAMES_URL);
        return gameRecycler;
    }

    class HTMLParse extends AsyncTask<String, Void, Document>{

        private String url;
        private final WeakReference<RecyclerView> gameRecyclerReference;

        public HTMLParse(RecyclerView gameRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            gameRecyclerReference = new WeakReference<RecyclerView>(gameRecycler);
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

                Elements htmlDays = document.getElementById("mainContent").select("div[class=embeddableMatchSummary clubSidebarWidget fixturesAbridgedContainer]").select("div[class=day]");

                //расчет количетсва матчей
                int sizeMatches = 0;
                for (int i = 0; i < htmlDays.size(); i++) {
                    sizeMatches = sizeMatches + htmlDays.get(i).select("li").size();
                }

                final String[] captionsHome = new String[sizeMatches];
                final String[] imageIdsHome = new String[sizeMatches];
                final String[] scores = new String[sizeMatches];
                final String[] captionsAway = new String[sizeMatches];
                final String[] imageIdsAway = new String[sizeMatches];
                final int[] matchesId = new int[sizeMatches];

                int k = 0;
                String idHome, idAway, matchIdUrl, htmlDate;
                for (int i = 0; i < htmlDays.size(); i++) {
                    htmlDate =  htmlDays.get(i).select("time").select("strong").text();
                    Elements htmlGames = htmlDays.get(i).select("li");
                    for (int j=0; j < htmlGames.size(); j++){

                        if (htmlGames.get(j).select("span").select("a").select("span[class=score]").text() != "") {
                            scores[k] = htmlGames.get(j).select("span").select("a").select("span[class=score]").text();
                            captionsHome[k] = htmlGames.get(j).select("span").select("a").select("span").get(0).text();
                            captionsAway[k] = htmlGames.get(j).select("span").select("a").select("span").get(5).text();
                            idHome = htmlGames.get(j).select("span").select("a").select("span").get(1).attr("class");
                            idHome = idHome.replace("badge-25 t", "");
                            idHome = idHome.trim();
                            imageIdsHome[k] = HTML_LOGO_URL + idHome + ".png";
                            idAway = htmlGames.get(j).select("span").select("a").select("span").get(4).attr("class");
                            idAway = idAway.replace("badge-25 t", "");
                            idAway = idAway.trim();
                            imageIdsAway[k] = HTML_LOGO_URL + idAway + ".png";
                        }else {
                            scores[k] = htmlDate + " " + htmlGames.get(j).select("span").select("a").select("time").text();
                            captionsHome[k] = htmlGames.get(j).select("span").select("a").select("span").get(0).text();
                            captionsAway[k] = htmlGames.get(j).select("span").select("a").select("span").get(3).text();
                            idHome = htmlGames.get(j).select("span").select("a").select("span").get(1).attr("class");
                            idHome = idHome.replace("badge-20 t", "");
                            idHome = idHome.trim();
                            imageIdsHome[k] = HTML_LOGO_URL + idHome + ".png";
                            idAway = htmlGames.get(j).select("span").select("a").select("span").get(2).attr("class");
                            idAway = idAway.replace("badge-20 t", "");
                            idAway = idAway.trim();
                            imageIdsAway[k] = HTML_LOGO_URL + idAway + ".png";
                        }




                        matchIdUrl = htmlGames.get(j).select("span").select("a").attr("href");
                        matchIdUrl = matchIdUrl.replace("/match/", "");
                        matchesId[k] = Integer.valueOf(matchIdUrl);

                        k++;
                    }

                }



                final RecyclerView gameRecycler = gameRecyclerReference.get();
                CaptionedImagesGamesAdapter adapter = new CaptionedImagesGamesAdapter(captionsHome, imageIdsHome, scores, captionsAway, imageIdsAway);
                gameRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                layoutManager.scrollToPosition(startPosition);
                gameRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesGamesAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                        intent.putExtra(GameDetailActivity.EXTRA_TEAMNO, matchesId[position]);
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
        private String url1;
        private String url2;
        private final WeakReference<RecyclerView> gameRecyclerReference;

        public JSONParse(RecyclerView gameRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            gameRecyclerReference = new WeakReference<RecyclerView>(gameRecycler);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            url1 = args[0];
            url2 = args[1];
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url1);
            jsonTeams = jParser.getJSONFromUrl(url2);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                //получение массива seasons
                JSONArray matches = json.getJSONObject("siteHeaderSection").getJSONArray("matches");


                final String[] captionsHome = new String[matches.length()];
                final String[] imageIdsHome = new String[matches.length()];
                final String[] scores = new String[matches.length()];
                final String[] captionsAway = new String[matches.length()];
                final String[] imageIdsAway = new String[matches.length()];
                final int[] matchesId = new int[matches.length()];
                for (int i=0; i<matches.length(); i++) {

                    matchesId[i] = matches.getJSONObject(i).getInt("matchId");

                    int homeTeamId = matches.getJSONObject(i).getInt("homeTeamId");
                    int awayTeamId = matches.getJSONObject(i).getInt("awayTeamId");

                    //получение массива seasons
                    JSONArray seasons = jsonTeams.getJSONObject("abstractCacheWebRootElement").getJSONArray("seasons");
                    //получение массива команд в текущем сезоне
                    JSONArray currentSeasonTeams = seasons.getJSONObject(0).getJSONArray("seasonTeams");

                    for (int j=0; j<currentSeasonTeams.length(); j++) {

                        int teamId = currentSeasonTeams.getJSONObject(j).getInt("teamId");
                        if (teamId == homeTeamId){
                            captionsHome[i] = currentSeasonTeams.getJSONObject(j).getString("teamDisplayFullName");
                            String clubURLName = currentSeasonTeams.getJSONObject(j).getString("clubURLName");
                            imageIdsHome[i] = FIRST_LOGO_URL + clubURLName.charAt(0) + "/" + clubURLName + SECOND_LOGO_HIGH_URL;
                        }else if(teamId == awayTeamId){
                            captionsAway[i] = currentSeasonTeams.getJSONObject(j).getString("teamDisplayFullName");
                            String clubURLName = currentSeasonTeams.getJSONObject(j).getString("clubURLName");
                            imageIdsAway[i] = FIRST_LOGO_URL + clubURLName.charAt(0) + "/" + clubURLName + SECOND_LOGO_HIGH_URL;
                        }


                    }

                    String status = matches.getJSONObject(i).getString("matchState");


                    if (status.equals("POST_MATCH")) {
                        int homeScore = matches.getJSONObject(i).getJSONObject("score").getInt("home");
                        int awayScore = matches.getJSONObject(i).getJSONObject("score").getInt("away");
                        scores[i] = String.valueOf(homeScore) + "-" + String.valueOf(awayScore);
                        startPosition = i+1;
                    }else if (status.equals("LIVE")) {
                        int homeScore = matches.getJSONObject(i).getJSONObject("score").getInt("home");
                        int awayScore = matches.getJSONObject(i).getJSONObject("score").getInt("away");
                        scores[i] = "LIVE " + String.valueOf(homeScore) + "-" + String.valueOf(awayScore);
                    }else {
                        long timestamp = matches.getJSONObject(i).getLong("timestamp");
                        Date d = new Date(timestamp);
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String reportDate = df.format(d);
                        scores[i] = reportDate;
                    }



                }

                final RecyclerView gameRecycler = gameRecyclerReference.get();
                CaptionedImagesGamesAdapter adapter = new CaptionedImagesGamesAdapter(captionsHome, imageIdsHome, scores, captionsAway, imageIdsAway);
                gameRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                layoutManager.scrollToPosition(startPosition);
                gameRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesGamesAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                        intent.putExtra(GameDetailActivity.EXTRA_TEAMNO, matchesId[position]);
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
