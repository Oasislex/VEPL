package by.scherbakov.vepl;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeamMaterialFragment extends Fragment {

    private final static String LIST_CLUBS_URL = "http://www.premierleague.com/pa-services/api/football/desktop/competition/fandr/api/seasons.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";
    private final static String HTML_LIST_CLUBS_URL = "http://www.premierleague.com/clubs";
    private final static String HTML_LOGO_URL = "https://platform-static-files.s3.amazonaws.com/premierleague/badges/t";
    public Team[] teams = new Team[20];

    public TeamMaterialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        RecyclerView teamRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_team_material, container, false);
        //new JSONParse(teamRecycler).execute(LIST_CLUBS_URL);
        new HTMLParse(teamRecycler).execute(HTML_LIST_CLUBS_URL);

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
                final String[] teamNames = new String[teams.length];
                final String[] teamImages = new String[teams.length];
                final int[] teamIds = new int[teams.length];
                final String[] teamDetailIds = new String[teams.length];

                Elements htmlTeams = document.getElementById("mainContent").select("div").select("div").select("div").select("div").select("li");
                //Log.d("logs html", htmlTeams.ownText());
                for (int i = 0; i < htmlTeams.size(); i++) {

                    String htmlTeamId = htmlTeams.get(i).select("a").select("div[class=indexBadge]").select("span").attr("class");
                    htmlTeamId = htmlTeamId.replace("badge-50 t", "");
                    teamIds[i] = Integer.valueOf(htmlTeamId);
                    teamNames[i] = htmlTeams.get(i).select("a").select("div[class=indexInfo clubColourBg]").select("div[class=clubName]").text();
                    teamImages[i] = HTML_LOGO_URL + htmlTeamId + ".png";
                    teamDetailIds[i] = "http://www.premierleague.com" + htmlTeams.get(i).select("a").attr("href");

                }







                final RecyclerView teamRecycler = teamRecyclerReference.get();
                CaptionedImagesAdapter adapter = new CaptionedImagesAdapter(teamNames, teamImages);
                teamRecycler.setAdapter(adapter);

                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                teamRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, teamDetailIds[position]);
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
                //получение массива seasons
                JSONArray seasons = json.getJSONObject("abstractCacheWebRootElement").getJSONArray("seasons");
                //получение массива команд в текущем сезоне
                JSONArray currentSeasonTeams = seasons.getJSONObject(0).getJSONArray("seasonTeams");

                final String[] teamNames = new String[teams.length];
                final String[] teamImages = new String[teams.length];
                final int[] teamIds = new int[teams.length];
                for (int i=0; i<currentSeasonTeams.length(); i++) {

                    teamIds[i] = currentSeasonTeams.getJSONObject(i).getInt("teamId");
                    String teamFullName = currentSeasonTeams.getJSONObject(i).getString("teamDisplayFullName");
                    String clubURLName = currentSeasonTeams.getJSONObject(i).getString("clubURLName");
                    String labelHigh = FIRST_LOGO_URL + clubURLName.charAt(0) + "/" + clubURLName + SECOND_LOGO_HIGH_URL;


                    teamNames[i] = teamFullName;
                    teamImages[i] = labelHigh;


                }

                final RecyclerView teamRecycler = teamRecyclerReference.get();
                CaptionedImagesAdapter adapter = new CaptionedImagesAdapter(teamNames, teamImages);
                teamRecycler.setAdapter(adapter);

                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                teamRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                        intent.putExtra(TeamDetailActivity.EXTRA_TEAMNO, teamIds[position]);
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
