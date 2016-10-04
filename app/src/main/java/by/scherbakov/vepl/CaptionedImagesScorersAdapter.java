package by.scherbakov.vepl;

import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by User on 23.04.2016.
 */
public class CaptionedImagesScorersAdapter extends RecyclerView.Adapter<CaptionedImagesScorersAdapter.ViewHolder> {

    private String[] scorerIds;
    private String[] scorerNames;
    private String[] icoTypes;
    private String[] scorerTimes;
    private String[] scorerTypes;
    private Listener listener;
    private CardView cardView;
    private ImageView imageView;
    private ImageView imageIcoView;
    private final static String PLAYER__PHOTO_URL = "http://cdn.ismfg.net/static/plfpl/img/shirts/photos/";
    private final static String HTML_PLAYER_URL = "http://www.premierleague.com";
    private final static String HTML_PLAYER_PHOTO_URL = "http://platform-static-files.s3.amazonaws.com/premierleague/photos/players/110x140/";

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {
        public void onClick(int position);
    }

    public CaptionedImagesScorersAdapter(String[] scorerIds, String[] scorerNames, String[] icoTypes) {
        this.scorerIds = scorerIds;
        this.scorerNames = scorerNames;
        this.icoTypes = icoTypes;
    }

    public CaptionedImagesScorersAdapter(String[] scorerIds, String[] scorerNames, String[] scorerTimes, String[] scorerTypes) {
        this.scorerIds = scorerIds;
        this.scorerNames = scorerNames;
        this.scorerTimes = scorerTimes;
        this.scorerTypes = scorerTypes;
    }

    @Override
    public CaptionedImagesScorersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image_scorers, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CaptionedImagesScorersAdapter.ViewHolder holder, final int position) {
        cardView = holder.cardView;
        imageView = (ImageView) cardView.findViewById(R.id.scorer_image);
        imageIcoView = (ImageView) cardView.findViewById(R.id.ico_type);
//        Picasso.with(cardView.getContext())
//                .load(PLAYER__PHOTO_URL + scorerIds[position] + ".jpg")
//                .into(imageView);
//
//        imageView.setContentDescription(scorerNames[position]);
//        TextView scorerName = (TextView)cardView.findViewById(R.id.scorer_name);
//        scorerName.setText(scorerNames[position]);
//
//        TextView scorerTime = (TextView) cardView.findViewById(R.id.score_time);
//        scorerTime.setText(scorerTimes[position] + scorerTypes[position]);

        switch (icoTypes[position]){
        case "icn sub-off":
            imageIcoView.setImageResource(R.drawable.out);
            break;
        case "icn sub-on":
            imageIcoView.setImageResource(R.drawable.in);
            break;
        case "icn card-yellow":
            imageIcoView.setImageResource(R.drawable.yellowcard);
            break;
        case "icn card-red":
            imageIcoView.setImageResource(R.drawable.redcard);
            break;
        case "icn card-yellow-red":
            imageIcoView.setImageResource(R.drawable.doubleyellowcard);
            break;
        case "icn ball-sm-w":
            imageIcoView.setImageResource(R.drawable.goal);
            break;
        default : break;
        }

        Picasso.with(cardView.getContext())
                .load(HTML_PLAYER_PHOTO_URL + scorerIds[position] + ".png")
                .into(imageView);
        TextView scorerName = (TextView)cardView.findViewById(R.id.scorer_name);
        scorerName.setText(scorerNames[position]);
        imageView.setContentDescription(scorerNames[position]);

        cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(position);
                }
            }
        });
        //new HTMLParse().execute(HTML_PLAYER_URL + scorerIds[position]);



    }

    class HTMLParse extends AsyncTask<String, Void, Document> {
        private String url;

        @Override
        protected Document doInBackground(String... args) {
            url = args[0];
            int positionHtml = Integer.valueOf(args[1]);
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





            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return scorerIds.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }
}
