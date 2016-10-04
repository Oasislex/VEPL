package by.scherbakov.vepl;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GamesFragment extends Fragment {

    private final static String LIST_GAMES_URL = "http://www.premierleague.com/ajax/site-header/ajax-all-matches.json";
    private final static String LIST_CLUBS_URL = "http://www.premierleague.com/pa-services/api/football/desktop/competition/fandr/api/seasons.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";
    private static final String HTML_MATCH_INFO_URL = "http://www.premierleague.com/match/";
    private static final String HTML_RESULTS_URL = "http://www.premierleague.com";
    private static final String HTML_MATCHWEEK_URL = "https://www.premierleague.com/matchweek/";
    private final static String HTML_LOGO_URL = "https://platform-static-files.s3.amazonaws.com/premierleague/badges/t";
    private HashMap<Integer, Team> teams = new HashMap<>();

    public GamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView gamesListRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_games, container, false);
        new HTMLParse(gamesListRecycler).execute(HTML_RESULTS_URL);
        return gamesListRecycler;
    }

    class HTMLParse extends AsyncTask<String, Void, ArrayList<Match>>{

        private String url;
        private final WeakReference<RecyclerView> gamesListRecyclerReference;

        public HTMLParse(RecyclerView gamesListRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            gamesListRecyclerReference = new WeakReference<RecyclerView>(gamesListRecycler);
        }

        @Override
        protected ArrayList<Match> doInBackground(String... args) {
            url = args[0];

            Document document = null;
            Document documentMatchweek = null;
            ArrayList<Match> matchResults = new ArrayList<Match>();
            try {
                document = Jsoup.connect(url).get();
                int matchweek = Integer.parseInt(document.select("div[data-widget=gameweek-matches]").attr("data-gameweek"));


                //String[] htmlGamesList = document.select("div[data-ui-tab=First Team]").attr("data-fixturesids").split(",");
                int countMatchweek = 1269;
                while(countMatchweek < matchweek){
                    documentMatchweek = Jsoup.connect(HTML_MATCHWEEK_URL + countMatchweek + "/blog").get();
                    Elements gamesMatchweek = documentMatchweek.select("a[class= matchAbridged  fullTime]");
                    for (int i=0; i<gamesMatchweek.size(); i++){
                        String timestamp = String.valueOf(countMatchweek - 1268);
                        String idHome = gamesMatchweek.get(i).select("span").get(1).attr("class");
                        idHome = idHome.replace("badge-20 t", "");
                        idHome = idHome.trim();
                        String homeTeamUrl = HTML_LOGO_URL + idHome + ".png";
                        String homeTeamName = gamesMatchweek.get(i).select("span").get(0).text();

                        String idAway = gamesMatchweek.get(i).select("span").get(5).attr("class");
                        idAway = idAway.replace("badge-20 t", "");
                        idAway = idAway.trim();
                        String awayTeamUrl = HTML_LOGO_URL + idAway + ".png";
                        String awayTeamName = gamesMatchweek.get(i).select("span").get(6).text();

                        String scores = gamesMatchweek.get(i).select("span[class=score]").text();
                        int matchId = Integer.valueOf(gamesMatchweek.get(i).attr("data-id"));
                        Match match = new Match(timestamp, homeTeamName, homeTeamUrl, scores, awayTeamName, awayTeamUrl, matchId);
                        matchResults.add(match);
                    }
                    countMatchweek++;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return matchResults;
        }
        @Override
        protected void onPostExecute(ArrayList<Match> matchResults) {

            try {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                final String[] timestamps = new String[matchResults.size()];
                final String[] captionsHome = new String[matchResults.size()];
                final String[] imageIdsHome = new String[matchResults.size()];
                final String[] scores = new String[matchResults.size()];
                final String[] captionsAway = new String[matchResults.size()];
                final String[] imageIdsAway = new String[matchResults.size()];
                final int[] matchesId = new int[matchResults.size()];

                for(int i=0; i<matchResults.size(); i++){
                    timestamps[i] = matchResults.get(i).getTimestamps();
                    captionsHome[i] = matchResults.get(i).getCaptionsHome();
                    imageIdsHome[i] = matchResults.get(i).getImageIdsHome();
                    scores[i] = matchResults.get(i).getScores();
                    captionsAway[i] = matchResults.get(i).getCaptionsAway();
                    imageIdsAway[i] = matchResults.get(i).getImageIdsAway();
                    matchesId[i] = matchResults.get(i).getMatchesId();
                }

                final RecyclerView gamesListRecycler = gamesListRecyclerReference.get();
                CaptionedImagesGamesListAdapter adapter = new CaptionedImagesGamesListAdapter(timestamps, captionsHome, imageIdsHome, scores, captionsAway, imageIdsAway);
                gamesListRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                gamesListRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesGamesListAdapter.Listener(){

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
        private final WeakReference<RecyclerView> gamesListRecyclerReference;

        public JSONParse(RecyclerView gamesListRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            gamesListRecyclerReference = new WeakReference<RecyclerView>(gamesListRecycler);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            url1 = args[0];
            url2 = args[1];
            JSONParser jParser = new JSONParser();
            JSONObject jsonClubs = jParser.getJSONFromUrl(url1);
            try {
                //получение массива seasons
                JSONArray seasons = jsonClubs.getJSONObject("abstractCacheWebRootElement").getJSONArray("seasons");
                //получение массива команд в текущем сезоне
                JSONArray currentSeasonTeams = seasons.getJSONObject(0).getJSONArray("seasonTeams");


                for (int i=0; i<currentSeasonTeams.length(); i++) {

                    int teamId = currentSeasonTeams.getJSONObject(i).getInt("teamId");
                    String teamFullName = currentSeasonTeams.getJSONObject(i).getString("teamDisplayFullName");
                    String clubURLName = currentSeasonTeams.getJSONObject(i).getString("clubURLName");
                    String labelHigh = FIRST_LOGO_URL + clubURLName.charAt(0) + "/" + clubURLName + SECOND_LOGO_HIGH_URL;

                    Team team = new Team(teamId);
                    team.setTeamImageResource(labelHigh);
                    team.setTeamNo(i);
                    teams.put(teamId, team);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url2);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray matches = json.getJSONObject("siteHeaderSection").getJSONArray("matches");
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                final String[] timestamps = new String[matches.length()];
                final String[] captionsHome = new String[matches.length()];
                final String[] imageIdsHome = new String[matches.length()];
                final String[] scores = new String[matches.length()];
                final String[] captionsAway = new String[matches.length()];
                final String[] imageIdsAway = new String[matches.length()];
                final int[] matchesId = new int[matches.length()];

                for (int i=0; i<matches.length(); i++) {

                    matchesId[i] = matches.getJSONObject(i).getInt("matchId");
                    captionsHome[i] = matches.getJSONObject(i).getString("homeTeamCode");
                    captionsAway[i] = matches.getJSONObject(i).getString("awayTeamCode");
                    scores[i] = matches.getJSONObject(i).getJSONObject("score").getString("home") + "-" + matches.getJSONObject(i).getJSONObject("score").getString("away");

                    long timestamp = matches.getJSONObject(i).getLong("timestamp");
                    Date d = new Date(timestamp);
                    timestamps[i] = df.format(d);

                    int homeTeamId = matches.getJSONObject(i).getInt("homeTeamId");
                    Team homeTeam = teams.get(homeTeamId);
                    imageIdsHome[i] = homeTeam.getTeamImageResource();

                    int awayTeamId = matches.getJSONObject(i).getInt("awayTeamId");
                    Team awayTeam = teams.get(awayTeamId);
                    imageIdsAway[i] = awayTeam.getTeamImageResource();


                }


                final RecyclerView gamesListRecycler = gamesListRecyclerReference.get();
                CaptionedImagesGamesListAdapter adapter = new CaptionedImagesGamesListAdapter(timestamps, captionsHome, imageIdsHome, scores, captionsAway, imageIdsAway);
                gamesListRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                gamesListRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesGamesListAdapter.Listener(){

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
