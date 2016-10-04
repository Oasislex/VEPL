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
 * Created by User on 25.04.2016.
 */
public class CaptionedImagesTableAdapter extends RecyclerView.Adapter<CaptionedImagesTableAdapter.ViewHolder> {

    private int[] placeIds;
    private String[] teamImages;
    private String[] teamNames;
    private int[] matchesPlayed;
    private int[] matchesWon;
    private int[] matchesDrawn;
    private int[] matchesLost;
    private int[] goalsFor;
    private int[] goalsAgainst;
    private int[] goalsDifference;
    private int[] points;

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {
        public void onClick(int position);
    }

    public CaptionedImagesTableAdapter(int[] points, int[] placeIds, String[] teamImages, String[] teamNames, int[] matchesPlayed, int[] matchesWon, int[] matchesDrawn, int[] matchesLost, int[] goalsFor, int[] goalsAgainst, int[] goalsDifference) {
        this.points = points;
        this.placeIds = placeIds;
        this.teamImages = teamImages;
        this.teamNames = teamNames;
        this.matchesPlayed = matchesPlayed;
        this.matchesWon = matchesWon;
        this.matchesDrawn = matchesDrawn;
        this.matchesLost = matchesLost;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalsDifference = goalsDifference;
    }

    @Override
    public CaptionedImagesTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image_table, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CaptionedImagesTableAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;



        TextView placeText = (TextView) cardView.findViewById(R.id.place_number_text);
        placeText.setText(String.valueOf(placeIds[position]));

        ImageView imageView = (ImageView) cardView.findViewById(R.id.team_image);
        Picasso.with(cardView.getContext())
                .load(teamImages[position])
                .into(imageView);

        imageView.setContentDescription(teamNames[position]);

        TextView teamNameText = (TextView) cardView.findViewById(R.id.team_name_text);
        teamNameText.setText(teamNames[position]);

        TextView matchesPlayedText = (TextView) cardView.findViewById(R.id.matches_played_text);
        matchesPlayedText.setText(String.valueOf(matchesPlayed[position]));


        TextView matchesWonText = (TextView) cardView.findViewById(R.id.matches_won_text);
        matchesWonText.setText(String.valueOf(matchesWon[position]));

        TextView matchesDrawnText = (TextView) cardView.findViewById(R.id.matches_drawn_text);
        matchesDrawnText.setText(String.valueOf(matchesDrawn[position]));

        TextView matchesLostText = (TextView) cardView.findViewById(R.id.matches_lost_text);
        matchesLostText.setText(String.valueOf(matchesLost[position]));

        TextView goalsForText = (TextView) cardView.findViewById(R.id.goals_for_text);
        goalsForText.setText(String.valueOf(goalsFor[position]) + "-" + String.valueOf(goalsAgainst[position]));

//        TextView goalsAgainstText = (TextView) cardView.findViewById(R.id.goals_against_text);
//        goalsAgainstText.setText(String.valueOf(goalsAgainst[position]));
//
//        TextView goalsDifferenceText = (TextView) cardView.findViewById(R.id.goals_difference_text);
//        goalsDifferenceText.setText(String.valueOf(goalsDifference[position]));

        TextView pointsText = (TextView) cardView.findViewById(R.id.points_text);
        pointsText.setText(String.valueOf(points[position]));
        pointsText.setTextColor(R.color.mediumBlue);
        pointsText.setTextSize(20);

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
        return placeIds.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }
}
