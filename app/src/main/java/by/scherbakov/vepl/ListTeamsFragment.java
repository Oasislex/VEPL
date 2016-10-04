package by.scherbakov.vepl;


import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListTeamsFragment extends ListFragment {
    private SQLiteDatabase db;
    private Cursor cursor;

    public ListTeamsFragment() {
        // Required empty public constructor
    }

    static interface ListTeamsListener{
        void itemClicked(long id);
    }

    private ListTeamsListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ListTeamsListener)activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (listener != null){
            listener.itemClicked(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ListView listTeams = getListView();
        try{
            SQLiteOpenHelper veplDatabaseHelper = new VEPLDatabaseHelper(inflater.getContext());
            db = veplDatabaseHelper.getReadableDatabase();

            cursor = db.query("TEAMS", new String[]{"_id", "NAME"}, null, null, null, null, null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(), android.R.layout.simple_list_item_1, cursor, new String[]{"NAME"}, new int[]{android.R.id.text1}, 0);
            //listTeams.setAdapter(listAdapter);
            setListAdapter(listAdapter);
        }catch(SQLiteException e){
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailble", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cursor.close();
        db.close();
    }

}
