package by.scherbakov.vepl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends Activity {

    private static final String PLAYERS_LIST_URL = "http://jokecamp.github.io/epl-fantasy-geek/js/data.json";
    private static final String PLAYER_INFO_URL = "http://fantasy.premierleague.com/web/api/elements/";
    private final static String PLAYER__PHOTO_URL = "http://cdn.ismfg.net/static/plfpl/img/shirts/photos/";
    public static final String EXTRA_PLAYERNO = "playerId";
    String playerId;
    int elementId;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        playerId = (String) getIntent().getExtras().get(EXTRA_PLAYERNO);
        new HTMLParse().execute("http://www.premierleague.com" + playerId);

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



//                TextView playerName = (TextView) findViewById(R.id.player_name_text);
//                playerName.setText(firstName + " " + secondName);
//
//                ImageView playerPhoto = (ImageView) findViewById(R.id.player_image);
//
//                Picasso.with(getApplicationContext())
//                        .load(photoPlayer)
//                        .into(playerPhoto);
//
//                playerPhoto.setContentDescription(firstName + " " + secondName);
//
//                TextView playerPosition = (TextView) findViewById(R.id.position_text);
//                playerPosition.setText(positionName);
//
//                TextView teamView = (TextView) findViewById(R.id.team_text);
//                teamView.setText(teamName);
//
//                TextView newsView = (TextView) findViewById(R.id.news_text);
//                newsView.setText(news);

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class JSONParseFirst extends AsyncTask<String, Void, JSONObject> {
        private String url;

        @Override
        protected JSONObject doInBackground(String... args) {
            url = args[0];
            JSONParser jParser = new JSONParser();

            JSONObject json = jParser.getJSONFromUrl(url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                JSONArray players = json.getJSONArray("elInfo");
                for(int i=1; i<players.length(); i++){

                    JSONArray player = (JSONArray) players.get(i);
                    if (((int)player.get(2)) == Integer.valueOf(playerId)) {

                        elementId = (int) player.get(0);
                        break;
                    }
                }
                new JSONParseSecond().execute(PLAYER_INFO_URL+elementId);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class JSONParseSecond extends AsyncTask<String, Void, JSONObject> {
        private String url;

        @Override
        protected JSONObject doInBackground(String... args) {
            url = args[0];
            JSONParser jParser = new JSONParser();

            JSONObject json = jParser.getJSONFromUrl(url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                String firstName = json.getString("first_name");
                String secondName = json.getString("second_name");
                String photoPlayer = PLAYER__PHOTO_URL + json.getString("photo");
                String positionName = json.getString("type_name");
                String minutesPlayed = json.getString("minutes");
                String goalsScored = json.getString("goals_scored");
                String assists = json.getString("assists");
                String cleanSheets = json.getString("clean_sheets");
                String ownGoals = json.getString("own_goals");
                String teamName = json.getString("team_name");
                String news = json.getString("news");

                JSONArray historyMatches = json.getJSONObject("fixture_history").getJSONArray("all");

                TextView playerName = (TextView) findViewById(R.id.player_name_text);
                playerName.setText(firstName + " " + secondName);

                ImageView playerPhoto = (ImageView) findViewById(R.id.player_image);

                Picasso.with(getApplicationContext())
                        .load(photoPlayer)
                        .into(playerPhoto);

                playerPhoto.setContentDescription(firstName + " " + secondName);

                TextView playerPosition = (TextView) findViewById(R.id.position_text);
                playerPosition.setText(positionName);

                TextView teamView = (TextView) findViewById(R.id.team_text);
                teamView.setText(teamName);

                TextView newsView = (TextView) findViewById(R.id.news_text);
                newsView.setText(news);


                final ArrayList<PlayerStats> mList = new ArrayList<PlayerStats>();
                mList.add(new PlayerStats("Opponent", "MP", "GS", "A", "CS", "OG"));

                for (int i = 0; i < historyMatches.length(); i++) {
                    JSONArray match = (JSONArray) historyMatches.get(i);
                    mList.add(new PlayerStats(((String)match.get(2)),String.valueOf((int)match.get(3)),String.valueOf((int)match.get(4)),String.valueOf((int)match.get(5)),String.valueOf((int)match.get(6)),String.valueOf((int)match.get(8)) ));

//                    mList.add((String)match.get(0));//String Date = (String)match.get(0);
//                    mList.add((String)match.get(2));//String Opponent = (String) match.get(2);
//                    mList.add(String.valueOf((int)match.get(3)));//String MP = (String)match.get(3);
//                    mList.add(String.valueOf((int)match.get(4)));//String GS = (String)match.get(4);
//                    mList.add(String.valueOf((int)match.get(5)));//String A = (String)match.get(5);
//                    mList.add(String.valueOf((int)match.get(6)));//String CS = (String)match.get(6);
//                    mList.add(String.valueOf((int)match.get(8)));//String OG = (String)match.get(8);
                }
                mList.add(new PlayerStats("Total:", minutesPlayed, goalsScored, assists, cleanSheets, ownGoals));

                playerPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_fixture_history);
                        dialog.setTitle("Fixture history");

                        GridView gridView= (GridView)dialog.findViewById(R.id.grid);
                        PlayerStatsGridViewAdapter gridAdapter = new PlayerStatsGridViewAdapter(context, R.layout.row_grid, mList);
                        gridView.setAdapter(gridAdapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // do something here
                            }
                        });

                        dialog.show();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
