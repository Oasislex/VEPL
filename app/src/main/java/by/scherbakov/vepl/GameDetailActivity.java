package by.scherbakov.vepl;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameDetailActivity extends Activity {

    private static final String MATCH_INFO_URL_START = "http://www.premierleague.com/ajax/match/";
    private static final String MATCH_INFO_URL_END = "/fixture-header.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";
    public static final String EXTRA_TEAMNO = "teamNo";
    private static final String HTML_MATCH_INFO_URL = "http://www.premierleague.com/match/";
    private final static String HTML_LOGO_URL = "https://platform-static-files.s3.amazonaws.com/premierleague/badges/t";
    private int teamNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        teamNo = (Integer) getIntent().getExtras().get(EXTRA_TEAMNO);

        //new JSONParse().execute(MATCH_INFO_URL_START + teamNo + MATCH_INFO_URL_END);
        new HTMLParse().execute(HTML_MATCH_INFO_URL + teamNo);


    }

    class HTMLParse extends AsyncTask<String, Void, Document> {
        private String url;

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

                ImageView homeTeamImage = (ImageView) findViewById(R.id.home_team_image);
                ImageView awayTeamImage = (ImageView) findViewById(R.id.away_team_image);
                TextView score = (TextView) findViewById(R.id.score_status_text);
                TextView matchState = (TextView) findViewById(R.id.match_status_text);

                String homeTeam = document.select("div[class=teamsContainer]").select("div[class=team home]").select("a").get(0).select("span").get(0).attr("class");
                homeTeam = homeTeam.replace("badge-50 t", "");
                String homeTeamUrl = HTML_LOGO_URL + homeTeam + ".png";

                Picasso.with(getApplicationContext())
                        .load(homeTeamUrl)
                        .into(homeTeamImage);

                String homeTeamName = document.select("div[class=teamsContainer]").select("div[class=team home]").select("a").get(1).select("span[class=long]").text();
                homeTeamImage.setContentDescription(homeTeamName);
                String homeTeamOverview = document.select("div[class=teamsContainer]").select("div[class=team home]").select("a").attr("href");
                String[] sh = homeTeamOverview.split("/");

                final int homeTeamId = Integer.valueOf(sh[2]);


                String awayTeam = document.select("div[class=teamsContainer]").select("div[class=team away]").select("a").get(0).select("span").get(0).attr("class");
                awayTeam = awayTeam.replace("badge-50 t", "");
                String awayTeamUrl = HTML_LOGO_URL + awayTeam + ".png";

                Picasso.with(getApplicationContext())
                        .load(awayTeamUrl)
                        .into(awayTeamImage);

                String awayTeamName = document.select("div[class=teamsContainer]").select("div[class=team away]").select("a").get(1).select("span[class=long]").text();
                awayTeamImage.setContentDescription(awayTeamName);
                String awayTeamOverview = document.select("div[class=teamsContainer]").select("div[class=team away]").select("a").attr("href");
                String[] sa = homeTeamOverview.split("/");

                final int awayTeamId = Integer.valueOf(sa[2]);

                if (document.select("div[class=matchScoreContainer]").select("div[class=centre]").select("div[class=score fullTime]").text() != ""){
                    score.setTextSize(50);
                    score.setText(document.select("div[class=matchScoreContainer]").select("div[class=centre]").select("div[class=score fullTime]").text());
                }else if(document.select("div[class=matchScoreContainer]").select("div[class=countdownContainer]").attr("data-time") != ""){
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    score.setTextSize(20);
                    score.setText(df.format(new Date(document.select("div[class=matchScoreContainer]").select("div[class=countdownContainer]").attr("data-time"))));
                }else{
                    score.setTextSize(50);
                    score.setText("LIVE");
                    //matchState.setText(matchDetails.getString("minutesIntoMatch") + "'");
                }


                ScorersMaterialFragment fragmentHome = new ScorersMaterialFragment();
                ScorersMaterialFragment fragmentAway = new ScorersMaterialFragment();
                fragmentHome.setMatchId(teamNo);
                fragmentHome.setHomeScorers(true);
                fragmentAway.setMatchId(teamNo);
                fragmentAway.setHomeScorers(false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.home_scorers_frame, fragmentHome);
                ft.replace(R.id.away_scorers_frame, fragmentAway);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


                homeTeamImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, homeTeamId);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        startActivity(intent);
                    }
                });

                awayTeamImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, awayTeamId);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        startActivity(intent);
                    }
                });



            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("teamNo", teamNo);
    }

    class JSONParse extends AsyncTask<String, Void, JSONObject> {
        private String url;

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

                final JSONObject matchDetails = json.getJSONObject("matchHeaderSection").getJSONObject("matchDetails");

                ImageView homeTeamImage = (ImageView) findViewById(R.id.home_team_image);
                ImageView awayTeamImage = (ImageView) findViewById(R.id.away_team_image);
                TextView score = (TextView) findViewById(R.id.score_status_text);
                TextView matchState = (TextView) findViewById(R.id.match_status_text);

                String homeTeamImageURL = (String) matchDetails.getJSONObject("homeTeam").getJSONArray("cmsAlias").get(0);
                homeTeamImageURL = FIRST_LOGO_URL + homeTeamImageURL.charAt(0) + "/" + homeTeamImageURL + SECOND_LOGO_HIGH_URL;

                Picasso.with(getApplicationContext())
                        .load(homeTeamImageURL)
                        .into(homeTeamImage);

                homeTeamImage.setContentDescription(matchDetails.getJSONObject("homeTeam").getString("clubFullName"));
                final int homeTeamId = matchDetails.getJSONObject("homeTeam").getInt("id");

                String awayTeamImageURL = (String) matchDetails.getJSONObject("awayTeam").getJSONArray("cmsAlias").get(0);
                awayTeamImageURL = FIRST_LOGO_URL + awayTeamImageURL.charAt(0) + "/" + awayTeamImageURL + SECOND_LOGO_HIGH_URL;

                Picasso.with(getApplicationContext())
                        .load(awayTeamImageURL)
                        .into(awayTeamImage);

                awayTeamImage.setContentDescription(matchDetails.getJSONObject("awayTeam").getString("clubFullName"));
                final int awayTeamId = matchDetails.getJSONObject("awayTeam").getInt("id");

                if (matchDetails.getString("matchState").equals("LIVE")){
                    matchState.setText(matchDetails.getString("minutesIntoMatch") + "'");
                    score.setTextSize(50);
                    score.setText(matchDetails.getJSONObject("finalScore").getInt("home") + "-" + matchDetails.getJSONObject("finalScore").getInt("away"));
                }else if(matchDetails.getString("matchState").equals("PRE_MATCH")){
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    score.setTextSize(20);
                    score.setText(df.format(new Date(matchDetails.getLong("matchDate"))));
                }else{
                    score.setTextSize(50);
                    score.setText(matchDetails.getJSONObject("finalScore").getInt("home") + "-" + matchDetails.getJSONObject("finalScore").getInt("away"));
                }


                ScorersMaterialFragment fragmentHome = new ScorersMaterialFragment();
                ScorersMaterialFragment fragmentAway = new ScorersMaterialFragment();
                fragmentHome.setMatchId(teamNo);
                fragmentHome.setHomeScorers(true);
                fragmentAway.setMatchId(teamNo);
                fragmentAway.setHomeScorers(false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.home_scorers_frame, fragmentHome);
                ft.replace(R.id.away_scorers_frame, fragmentAway);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


                homeTeamImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, homeTeamId);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        startActivity(intent);
                    }
                });

                awayTeamImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, awayTeamId);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        startActivity(intent);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
