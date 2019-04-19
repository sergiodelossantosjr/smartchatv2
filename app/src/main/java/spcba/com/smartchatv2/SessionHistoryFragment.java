package spcba.com.smartchatv2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SessionHistoryFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        view = inflater.inflate(R.layout.session_historyfragment, container, false);
        return view;
    }

    public void setName(String name)
    {
        TextView txtName = (TextView) view.findViewById(R.id.textView);
        txtName.setText("Hi " + name);
    }

}
