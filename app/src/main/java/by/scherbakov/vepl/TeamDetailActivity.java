package by.scherbakov.vepl;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class TeamDetailActivity extends Activity {

    private final static String LIST_CLUBS_URL = "http://www.premierleague.com/pa-services/api/football/desktop/competition/fandr/api/seasons.json";
    private final static String FIRST_LOGO_URL = "http://www.premierleague.com/content/dam/premierleague/shared-images/clubs/";
    private final static String SECOND_LOGO_HIGH_URL = "/logo.png/_jcr_content/renditions/cq5dam.thumbnail.200.200.png";

    private ShareActionProvider shareActionProvider;
    public static final String EXTRA_TEAMNO = "teamNo";
    public String teamNo;


    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView teamText = (TextView) findViewById(R.id.team_text);
        ImageView teamImage = (ImageView) findViewById(R.id.team_image);

        if (savedInstanceState != null){
            teamNo = savedInstanceState.getString("teamNo");
        }else{
            teamNo = (String) getIntent().getExtras().get(EXTRA_TEAMNO);
            teamNo = teamNo.replace("overview", "squad");
        }
        new HTMLParse(teamText, teamImage).execute(teamNo);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("teamNo", teamNo);
    }
    class HTMLParse extends AsyncTask<String, Void, Document> {
        private String url;
        private final WeakReference<TextView> teamNameReference;
        private final WeakReference<ImageView> teamImageReference;

        public HTMLParse(TextView teamName, ImageView teamImage) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            teamNameReference = new WeakReference<TextView>(teamName);
            teamImageReference = new WeakReference<ImageView>(teamImage);
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

                String teamFullName = document.getElementById("mainContent").select("h1[class=team]").text();
                String labelHigh = "http:" + document.getElementById("mainContent").select("img[class=clubBadgeFallback]").attr("src");
                final TextView textView = teamNameReference.get();
                textView.setText(teamFullName);

                final ImageView imageView = teamImageReference.get();

                Picasso.with(getApplicationContext())
                        .load(labelHigh)
                        .into(imageView);

                imageView.setContentDescription(teamFullName);

                SquadFragment fragmentSquadGK = new SquadFragment();
                fragmentSquadGK.setTeamId(url);
                fragmentSquadGK.setPositionCode("Goalkeeper");

                SquadFragment fragmentSquadDF = new SquadFragment();
                fragmentSquadDF.setTeamId(url);
                fragmentSquadDF.setPositionCode("Defender");

                SquadFragment fragmentSquadMD = new SquadFragment();
                fragmentSquadMD.setTeamId(url);
                fragmentSquadMD.setPositionCode("Midfielder");

                SquadFragment fragmentSquadFW = new SquadFragment();
                fragmentSquadFW.setTeamId(url);
                fragmentSquadFW.setPositionCode("Forward");


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.squad_frame_gk, fragmentSquadGK);
                ft.replace(R.id.squad_frame_df, fragmentSquadDF);
                ft.replace(R.id.squad_frame_md, fragmentSquadMD);
                ft.replace(R.id.squad_frame_fw, fragmentSquadFW);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }




}
