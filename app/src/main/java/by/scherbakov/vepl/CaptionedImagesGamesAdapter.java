package by.scherbakov.vepl;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class CaptionedImagesGamesAdapter extends RecyclerView.Adapter<CaptionedImagesGamesAdapter.ViewHolder> {

    private String[] captionsHome;
    private String[] imageIdsHome;
    private String[] scores;
    private String[] captionsAway;
    private String[] imageIdsAway;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {
        public void onClick(int position);
    }

    public CaptionedImagesGamesAdapter(String[] captionsHome, String[] imageIdsHome, String[] scores, String[] captionsAway, String[] imageIdsAway) {
        this.captionsHome = captionsHome;
        this.imageIdsHome = imageIdsHome;
        this.scores = scores;
        this.captionsAway = captionsAway;
        this.imageIdsAway = imageIdsAway;
    }

    @Override
    public CaptionedImagesGamesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image_games, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CaptionedImagesGamesAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView imageViewHome = (ImageView) cardView.findViewById(R.id.home_team_image);


        Picasso.with(cardView.getContext())
                .load(imageIdsHome[position])
                .into(imageViewHome);

        imageViewHome.setContentDescription(captionsHome[position]);
        TextView textView = (TextView)cardView.findViewById(R.id.score_status_text);
        if (scores[position].length()>3){
            textView.setTextSize(20);
        }else{
            textView.setTextSize(50);
        }

        textView.setText(scores[position]);
        ImageView imageViewAway = (ImageView) cardView.findViewById(R.id.away_team_image);


        Picasso.with(cardView.getContext())
                .load(imageIdsAway[position])
                .into(imageViewAway);

        imageViewAway.setContentDescription(captionsAway[position]);

        cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return captionsHome.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }


}
