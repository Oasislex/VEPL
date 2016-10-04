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
 * Created by SBT-Sherbakov-AI on 03.05.2016.
 */
public class CaptionedImagesSquadAdapter extends RecyclerView.Adapter<CaptionedImagesSquadAdapter.ViewHolder> {

private String[] playerIds;
private String[] playerNames;
private Listener listener;
private final static String PLAYER__PHOTO_URL = "http://cdn.ismfg.net/static/plfpl/img/shirts/photos/";
private final static String HTML_PLAYER_PHOTO_URL = "http://platform-static-files.s3.amazonaws.com/premierleague/photos/players/110x140/";

public void setListener(Listener listener) {
        this.listener = listener;
        }

public static interface Listener {
    public void onClick(int position);
}

    public CaptionedImagesSquadAdapter(String[] playerIds, String[] playerNames) {
        this.playerIds = playerIds;
        this.playerNames = playerNames;

    }

    @Override
    public CaptionedImagesSquadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_image_squad, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CaptionedImagesSquadAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.player_image);
        Picasso.with(cardView.getContext())
                .load(HTML_PLAYER_PHOTO_URL + playerIds[position] + ".png")
                .into(imageView);

        imageView.setContentDescription(playerNames[position]);
        TextView playerName = (TextView)cardView.findViewById(R.id.player_name);
        playerName.setText(playerNames[position]);



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
        return playerIds.length;
    }

public static class ViewHolder extends RecyclerView.ViewHolder {

    private CardView cardView;
    public ViewHolder(CardView v){
        super(v);
        cardView = v;
    }
}
}
