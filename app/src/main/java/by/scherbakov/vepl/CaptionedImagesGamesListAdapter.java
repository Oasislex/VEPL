package by.scherbakov.vepl;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by SBT-Sherbakov-AI on 02.05.2016.
 */
public class CaptionedImagesGamesListAdapter extends RecyclerView.Adapter<CaptionedImagesGamesListAdapter.ViewHolder> {

    private String[] timestamps;
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

    public CaptionedImagesGamesListAdapter(String[]timestamps, String[] captionsHome, String[] imageIdsHome, String[] scores, String[] captionsAway, String[] imageIdsAway) {
        this.timestamps = timestamps;
        this.captionsHome = captionsHome;
        this.imageIdsHome = imageIdsHome;
        this.scores = scores;
        this.captionsAway = captionsAway;
        this.imageIdsAway = imageIdsAway;
    }

    @Override
    public CaptionedImagesGamesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image_games_list, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CaptionedImagesGamesListAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView timestampView = (TextView) cardView.findViewById(R.id.timestamp_text);
        timestampView.setText(timestamps[position]);

        ImageView imageViewHome = (ImageView) cardView.findViewById(R.id.team_image_h);
        Picasso.with(cardView.getContext())
                .load(imageIdsHome[position])
                .into(imageViewHome);

        imageViewHome.setContentDescription(captionsHome[position]);
        TextView homeTeam = (TextView)cardView.findViewById(R.id.team_text_h);
        homeTeam.setText(captionsHome[position]);

        TextView resultView = (TextView)cardView.findViewById(R.id.result_text);
        resultView.setText(scores[position]);

        ImageView imageViewAway = (ImageView) cardView.findViewById(R.id.team_image_a);
        Picasso.with(cardView.getContext())
                .load(imageIdsAway[position])
                .into(imageViewAway);

        imageViewAway.setContentDescription(captionsAway[position]);
        TextView awayTeam = (TextView)cardView.findViewById(R.id.team_text_a);
        awayTeam.setText(captionsAway[position]);
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
