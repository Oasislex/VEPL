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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SquadFragment extends Fragment {

    private String teamId;
    private String positionCode;
    private static final String PLAYERS_LIST_URL = "http://jokecamp.github.io/epl-fantasy-geek/js/data.json";

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public SquadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView squadRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_squad, container, false);
        new HTMLParse(squadRecycler).execute(teamId);

        return squadRecycler;

    }
    class HTMLParse extends AsyncTask<String, Void, Document> {
        private String url;
        private final WeakReference<RecyclerView> squadRecyclerReference;

        public HTMLParse(RecyclerView squadRecycler) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            squadRecyclerReference = new WeakReference<RecyclerView>(squadRecycler);
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

                Elements htmlPlayers = document.getElementById("mainContent").select("a[class=playerOverviewCard active]");
                int playersOnPositionsSize = 0;
                for(int i=0; i<htmlPlayers.size(); i++){
                    if (htmlPlayers.get(i).select("span[class=position]").text().equals(positionCode)){
                        playersOnPositionsSize++;
                    }
                }


                String[] playerIds = new String[playersOnPositionsSize];
                final String[] playerUrls = new String[playersOnPositionsSize];
                String[] playerNames = new String[playersOnPositionsSize];

                int j=0;
                for (int i=0; i<htmlPlayers.size(); i++){
                    if (htmlPlayers.get(i).select("span[class=position]").text().equals(positionCode)){
                        playerIds[j] = htmlPlayers.get(i).select("img[class=img statCardImg]").attr("data-player");
                        playerUrls[j] = htmlPlayers.get(i).attr("href");
                        playerNames[j] = htmlPlayers.get(i).select("h4[class=name]").text();
                        j++;
                    }

                }


                final RecyclerView squadRecycler = squadRecyclerReference.get();
                CaptionedImagesSquadAdapter adapter = new CaptionedImagesSquadAdapter(playerIds, playerNames);
                squadRecycler.setAdapter(adapter);

                //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false);
                squadRecycler.setLayoutManager(layoutManager);

                adapter.setListener(new CaptionedImagesSquadAdapter.Listener(){

                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra(PlayerActivity.EXTRA_PLAYERNO, playerUrls[position]);
                        //intent.putExtra(TeamDetailActivity.EXTRA_TEAMIMAGE, teamImages[position]);
                        getActivity().startActivity(intent);
                    }
                });

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
