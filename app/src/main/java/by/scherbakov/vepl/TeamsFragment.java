package by.scherbakov.vepl;


import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TeamsFragment extends Fragment {

    private long teamId;
    private View view;
    private Bitmap bitmap;

    public TeamsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       if (savedInstanceState != null){
           teamId = savedInstanceState.getLong("teamId");
       }


        view = inflater.inflate(R.layout.fragment_teams, container, false);
        if (view != null){
            Log.d("myLogs", Long.toString(teamId));
            try{
                SQLiteOpenHelper veplDatabseHelper = new VEPLDatabaseHelper(view.getContext());
                SQLiteDatabase db = veplDatabseHelper.getReadableDatabase();

                Cursor cursor = db.query("TEAMS", new String[]{"NAME", "IMAGE_RESOURCE_ID"}, "_id = ?", new String[]{Long.toString(teamId)}, null, null, null);
                if (cursor.moveToFirst()){
                    String nameText = cursor.getString(0);
                    String imageText = cursor.getString(1);
                    Log.d("myLogs", nameText);
                    Log.d("myLogs", imageText);


                    ImageView teamImage = (ImageView) view.findViewById(R.id.team_image);
                    new BitmapWorkerTask(teamImage).execute(imageText);
                    teamImage.setContentDescription(nameText);

                    TextView teamName = (TextView) view.findViewById(R.id.team_name);
                    teamName.setText(nameText);

                }
                cursor.close();
                db.close();
            }catch(SQLiteException e){
                Toast toast = Toast.makeText(view.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        //super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String url;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("teamId", teamId);
    }

    public void setTeamId(long id){
        this.teamId = id;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
