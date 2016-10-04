package by.scherbakov.vepl;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class TableMaterialFragment extends Fragment {

    private final static String LIST_CLUBS_URL = "http://www.premierleague.com/pa-services/api/football/desktop/competition/fandr/api/seasons.json";
    private final static String LIST_RESULTS_URL = "http://www.premierleague.com/ajax/site-header/ajax-all-results.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";
    private final static String HTML_TABLE_URL = "http://www.premierleague.com/tables";
    private final static String HTML_TEAM_PHOTO_URL = "http://platform-static-files.s3.amazonaws.com/premierleague/badges/";

    private HashMap<Integer, Team> teams = new HashMap<>();
    private ArrayList<Team> arrayTeams = new ArrayList<>();

    public TableMaterialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView tableRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_table_material, container, false);
        //new JSONParse(tableRecycler).execute(LIST_CLUBS_URL, LIST_RESULTS_URL);
        new HTMLParse(tableRecycler).execute(HTML_TABLE_URL);
        return tableRecycler;
    }

    class HTMLParse extends AsyncTask<String, Void, Document>{

        private String url;
        private final WeakReference<RecyclerView> tableRecyclerReference;

        public HTMLParse(RecyclerView tableRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            tableRecyclerReference = new WeakReference<RecyclerView>(tableRecycler);
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

                Elements clubsTable = document.getElementById("mainContent").select("div[data-ui-tab=First Team]").select("div[class=tableContainer").select("table").select("tbody[class=tableBodyContainer]").select("tr[data-filtered-entry-size=20");

                final int[] placeIds = new int[20];
                final String[] teamImages = new String[20];
                final String[] teamNames = new String[20];
                final int[] matchesPlayed = new int[20];
                final int[] matchesWon = new int[20];
                final int[] matchesDrawn = new int[20];
                final int[] matchesLost = new int[20];
                final int[] goalsFor = new int[20];
                final int[] goalsAgainst = new int[20];
                final int[] goalsDifference = new int[20];
                final int[] points = new int[20];

                for (int i=0; i<clubsTable.size(); i++){

                    placeIds[i] = Integer.valueOf(clubsTable.get(i).select("td[class=pos button-tooltip]").select("span[class=value]").text());
                    teamImages[i] = HTML_TEAM_PHOTO_URL + clubsTable.get(i).attr("data-filtered-table-row-opta") + ".png";
                    teamNames[i] = clubsTable.get(i).select("td[class=team]").select("span[class=long]").text();
                    matchesPlayed[i] = Integer.valueOf(clubsTable.get(i).select("td").get(3).text());
                    matchesWon[i] = Integer.valueOf(clubsTable.get(i).select("td").get(4).text());
                    matchesDrawn[i] = Integer.valueOf(clubsTable.get(i).select("td").get(5).text());
                    matchesLost[i] = Integer.valueOf(clubsTable.get(i).select("td").get(6).text());
                    goalsFor[i] = Integer.valueOf(clubsTable.get(i).select("td").get(7).text());
                    goalsAgainst[i] = Integer.valueOf(clubsTable.get(i).select("td").get(8).text());
                    goalsDifference[i] = goalsFor[i] - goalsAgainst[i];
                    points[i] = Integer.valueOf(clubsTable.get(i).select("td[class=points").text());


                }

                final RecyclerView tableRecycler = tableRecyclerReference.get();
                CaptionedImagesTableAdapter adapter = new CaptionedImagesTableAdapter(points, placeIds, teamImages, teamNames, matchesPlayed, matchesWon, matchesDrawn, matchesLost, goalsFor, goalsAgainst, goalsDifference);
                tableRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                tableRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesTableAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, arrayTeams.get(position).getTeamId());
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
        private final WeakReference<RecyclerView> tableRecyclerReference;

        public JSONParse(RecyclerView tableRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            tableRecyclerReference = new WeakReference<RecyclerView>(tableRecycler);
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
                    team.setTeamName(teamFullName);
                    team.setTeamImageResource(labelHigh);
                    team.setMatchesPlayed(0);
                    team.setMatchesWon(0);
                    team.setMatchesDrawn(0);
                    team.setMatchesLost(0);
                    team.setGoalsFor(0);
                    team.setGoalsAgainst(0);
                    team.setGoalsDifference(0);
                    team.setPoints(0);
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
                //получение массива seasons
                JSONArray matches = json.getJSONObject("siteHeaderSection").getJSONArray("matches");

                for (int i=0; i<matches.length(); i++) {

                    int homeTeamId = matches.getJSONObject(i).getInt("homeTeamId");
                    Team homeTeam = teams.get(homeTeamId);

                    int awayTeamId = matches.getJSONObject(i).getInt("awayTeamId");
                    Team awayTeam = teams.get(awayTeamId);

                    int homeScore = matches.getJSONObject(i).getJSONObject("score").getInt("home");
                    int awayScore = matches.getJSONObject(i).getJSONObject("score").getInt("away");

                    if (homeScore != awayScore){

                        if(homeScore > awayScore){

                            homeTeam.setMatchesPlayed(homeTeam.getMatchesPlayed() + 1);
                            homeTeam.setMatchesWon(homeTeam.getMatchesWon() + 1);
                            homeTeam.setGoalsFor(homeTeam.getGoalsFor() + homeScore);
                            homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + awayScore);
                            homeTeam.setGoalsDifference(homeTeam.getGoalsDifference() + homeScore - awayScore);
                            homeTeam.setPoints(homeTeam.getPoints() + 3);

                            awayTeam.setMatchesPlayed(awayTeam.getMatchesPlayed() + 1);
                            awayTeam.setMatchesLost(awayTeam.getMatchesLost() + 1);
                            awayTeam.setGoalsFor(awayTeam.getGoalsFor() + awayScore);
                            awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + homeScore);
                            awayTeam.setGoalsDifference(awayTeam.getGoalsDifference() + awayScore - homeScore);



                        }else{

                            homeTeam.setMatchesPlayed(homeTeam.getMatchesPlayed() + 1);
                            homeTeam.setMatchesLost(homeTeam.getMatchesLost() + 1);
                            homeTeam.setGoalsFor(homeTeam.getGoalsFor() + homeScore);
                            homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + awayScore);
                            homeTeam.setGoalsDifference(homeTeam.getGoalsDifference() + homeScore - awayScore);

                            awayTeam.setMatchesPlayed(awayTeam.getMatchesPlayed() + 1);
                            awayTeam.setMatchesWon(awayTeam.getMatchesWon() + 1);
                            awayTeam.setGoalsFor(awayTeam.getGoalsFor() + awayScore);
                            awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + homeScore);
                            awayTeam.setGoalsDifference(awayTeam.getGoalsDifference() + awayScore - homeScore);
                            awayTeam.setPoints(awayTeam.getPoints() + 3);



                        }

                    }else{

                        homeTeam.setMatchesPlayed(homeTeam.getMatchesPlayed() + 1);
                        homeTeam.setMatchesDrawn(homeTeam.getMatchesDrawn() + 1);
                        homeTeam.setGoalsFor(homeTeam.getGoalsFor() + homeScore);
                        homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + awayScore);
                        homeTeam.setPoints(homeTeam.getPoints() + 1);

                        awayTeam.setMatchesPlayed(awayTeam.getMatchesPlayed() + 1);
                        awayTeam.setMatchesDrawn(awayTeam.getMatchesDrawn() + 1);
                        awayTeam.setGoalsFor(awayTeam.getGoalsFor() + awayScore);
                        awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + homeScore);
                        awayTeam.setPoints(awayTeam.getPoints() + 1);

                    }

                    teams.put(homeTeamId, homeTeam);
                    teams.put(awayTeamId, awayTeam);

                }

                Set set = teams.entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Map.Entry me = (Map.Entry)iterator.next();
                    arrayTeams.add((Team)me.getValue());
                }
                Collections.sort(arrayTeams);

                final int[] placeIds = new int[20];
                final String[] teamImages = new String[20];
                final String[] teamNames = new String[20];
                final int[] matchesPlayed = new int[20];
                final int[] matchesWon = new int[20];
                final int[] matchesDrawn = new int[20];
                final int[] matchesLost = new int[20];
                final int[] goalsFor = new int[20];
                final int[] goalsAgainst = new int[20];
                final int[] goalsDifference = new int[20];
                final int[] points = new int[20];

                int j = 0;
                for (Team team : arrayTeams){

                    placeIds[j] = j + 1;
                    teamImages[j] = team.getTeamImageResource();
                    teamNames[j] = team.getTeamName();
                    matchesPlayed[j] = team.getMatchesPlayed();
                    matchesWon[j] = team.getMatchesWon();
                    matchesDrawn[j] = team.getMatchesDrawn();
                    matchesLost[j] = team.getMatchesLost();
                    goalsFor[j] = team.getGoalsFor();
                    goalsAgainst[j] = team.getGoalsAgainst();
                    goalsDifference[j] = team.getGoalsDifference();
                    points[j] = team.getPoints();
                    j++;


                }

                final RecyclerView tableRecycler = tableRecyclerReference.get();
                CaptionedImagesTableAdapter adapter = new CaptionedImagesTableAdapter(points, placeIds, teamImages, teamNames, matchesPlayed, matchesWon, matchesDrawn, matchesLost, goalsFor, goalsAgainst, goalsDifference);
                tableRecycler.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                tableRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesTableAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, arrayTeams.get(position).getTeamId());
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
