package spcba.com.smartchatv2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ListView;


public class SessionHistoryFragment extends Fragment {

    private View view;
    private static final String TAG = SessionHistoryFragment.class.getSimpleName();
    private ProgressDialog pDialog;

    private String username;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.content_main3, container, false);
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        view.setVisibility(View.VISIBLE);
        if(this.getArguments() != null){
            username  = this.getArguments().getString("username");
            GetHistory(username);
        }

        return view;
    }

    private void GetHistory(String username) {
        SessionHistoryDataAccess.GetHistory(SessionHistoryFragment.this, view, username);
    }
}
