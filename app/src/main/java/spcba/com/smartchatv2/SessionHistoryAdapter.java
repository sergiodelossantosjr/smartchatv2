package spcba.com.smartchatv2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SessionHistoryAdapter extends ArrayAdapter<SessionHistoryDTO> {
    private final Activity context;
    //private final String[] itemname;
    private final ArrayList<SessionHistoryDTO> itemname;
    private final Integer[] imgid;

    public SessionHistoryAdapter(Activity context, ArrayList<SessionHistoryDTO> itemname, Integer[] imgid) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname.get(position).sessionno);
        imageView.setImageResource(imgid[0]);
        extratxt.setText(itemname.get(position).createdon);
        return rowView;

    };
}
