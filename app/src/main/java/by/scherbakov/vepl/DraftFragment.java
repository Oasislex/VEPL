package by.scherbakov.vepl;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DraftFragment extends Fragment implements View.OnClickListener{


    public DraftFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_draft, container, false);
        Button pickButton = (Button) layout.findViewById(R.id.pick_button);
        pickButton.setOnClickListener(this);
        return layout;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), NotificationMessageService.class);
        intent.putExtra(NotificationMessageService.EXTRA_MESSAGE, getResources().getString(R.string.draft_notification));
        getActivity().startService(intent);
    }


}
