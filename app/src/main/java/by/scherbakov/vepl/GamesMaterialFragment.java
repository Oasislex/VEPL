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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


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
    private final static String FIXTURES_JSON_URL = "https://fantasy.premierleague.com/drf/fixtures/";
    private final static String TEAMS_JSON_URL = "https://fantasy.premierleague.com/drf/teams/";

    private JSONArray jsonTeams;
    private int startPosition;
    private static long WEEK = 604800000l;
    private static int START_MATCH_ID = 14039;

    public GamesMaterialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView gameRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_games_material, container, false);
        new JSONParse(gameRecycler).execute(FIXTURES_JSON_URL, TEAMS_JSON_URL);
        //new HTMLParse(gameRecycler).execute(HTML_GAMES_URL);
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

    class JSONParse extends AsyncTask<String, Void, JSONArray> {
        private String url1;
        private String url2;
        private final WeakReference<RecyclerView> gameRecyclerReference;

        public JSONParse(RecyclerView gameRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            gameRecyclerReference = new WeakReference<RecyclerView>(gameRecycler);
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            url1 = args[0];
            url2 = args[1];
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONArray json = jParser.getJSONArrayFromUrl(url1);
            jsonTeams = jParser.getJSONArrayFromUrl(url2);
            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {

            try {

                HashMap<Integer, Team> teamsMap = new HashMap<Integer, Team>();

                for(int i=0; i<jsonTeams.length(); i++){
                    Team teamMap = new Team(((JSONObject)jsonTeams.get(i)).getInt("code"));
                    teamMap.setTeamName(((JSONObject)jsonTeams.get(i)).getString("name"));
                    teamMap.setTeamImageResource(HTML_LOGO_URL + ((JSONObject)jsonTeams.get(i)).getInt("code") + ".png" );
                    teamsMap.put(((JSONObject)jsonTeams.get(i)).getInt("id"), teamMap);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Calendar now = Calendar.getInstance();
                long nowMillis = now.getTimeInMillis();
                int amountGames = 0;

                for(int i=0; i<json.length(); i++){
                    try {
                        Date parsedDate = dateFormat.parse(((JSONObject)json.get(i)).getString("kickoff_time").substring(0, ((JSONObject)json.get(i)).getString("kickoff_time").length()-1));
                        Calendar mydate = new GregorianCalendar();
                        mydate.setTime(parsedDate);
                        long mydateMillis = mydate.getTimeInMillis();

                        if((mydateMillis > nowMillis - WEEK) && (mydateMillis < nowMillis + WEEK)){
                            amountGames++;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                final String[] captionsHome = new String[amountGames];
                final String[] imageIdsHome = new String[amountGames];
                final String[] scores = new String[amountGames];
                final String[] captionsAway = new String[amountGames];
                final String[] imageIdsAway = new String[amountGames];
                final int[] matchesId = new int[amountGames];

                int j = 0;

                for(int i=0; i<json.length(); i++){
                    try {
                        Date parsedDate = dateFormat.parse(((JSONObject)json.get(i)).getString("kickoff_time"));
                        Calendar mydate = new GregorianCalendar();
                        mydate.setTime(parsedDate);
                        long mydateMillis = mydate.getTimeInMillis();

                        if((mydateMillis > nowMillis - WEEK) && (mydateMillis < nowMillis + WEEK)){
                            matchesId[j] = START_MATCH_ID + ((JSONObject)json.get(i)).getInt("id");
                            captionsHome[j] = teamsMap.get(((JSONObject)json.get(i)).getInt("team_h")).getTeamName();
                            imageIdsHome[j] = teamsMap.get(((JSONObject)json.get(i)).getInt("team_h")).getTeamImageResource();
                            captionsAway[j] = teamsMap.get(((JSONObject)json.get(i)).getInt("team_a")).getTeamName();
                            imageIdsAway[j] = teamsMap.get(((JSONObject)json.get(i)).getInt("team_a")).getTeamImageResource();
                            if (((JSONObject)json.get(i)).getBoolean("finished")) {
                                startPosition++;
                                scores[j] = ((JSONObject)json.get(i)).getInt("team_h_score") + "-" + ((JSONObject)json.get(i)).getInt("team_a_score");
                            }else{
                                Date d = new Date(mydateMillis);
                                DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                String reportDate = df.format(d);
                                scores[j] = reportDate;

                            }
                            j++;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
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
