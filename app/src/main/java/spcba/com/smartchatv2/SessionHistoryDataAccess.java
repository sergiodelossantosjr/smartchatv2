package spcba.com.smartchatv2;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SessionHistoryDataAccess {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static ProgressDialog pDialog;
    private static ArrayList<SessionHistoryDTO> itemname = new ArrayList<SessionHistoryDTO>();
    private static ListView list;
    private static Integer[] imgid = {
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
    };

    private static MessageAdapter messageAdapter;
    private static ListView messagesView;
    private static MemberData data;

    public static void GetHistory(final SessionHistoryFragment fragment, final View _view, final String username){
// Progress dialog
        pDialog = new ProgressDialog(fragment.getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");
        showDialog();
        try {

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("createdby", username);
            StringEntity entity = new StringEntity(jsonParams.toString());

            client.post(fragment.getActivity(), "http://teamagilevm.eastasia.cloudapp.azure.com:8080/smartchat/api/v1/gethistory.php", entity, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, final JSONArray response) {

                            hideDialog();
                            itemname.clear();
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject obj = response.getJSONObject(i);

                                        SessionHistoryDTO sessionHistoryDTO = new SessionHistoryDTO();
                                        sessionHistoryDTO.sessionno = obj.getString("sessionno");
                                        sessionHistoryDTO.createdon = obj.getString("createdon");
                                        sessionHistoryDTO.messages = obj.getJSONArray("messages");

                                        itemname.add(sessionHistoryDTO);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //then display
                                SessionHistoryAdapter adapter = new SessionHistoryAdapter(fragment.getActivity(), itemname, imgid);
                                list = (ListView) _view.findViewById(R.id.list);
                                list.setAdapter(adapter);

                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {
                                        // TODO Auto-generated method stub
                                        String Selecteditem = itemname.get(+position).sessionno;
                                        Toast.makeText(fragment.getActivity().getApplicationContext(), Selecteditem, Toast.LENGTH_LONG).show();

                                        JSONArray _messageList = itemname.get(+position).messages;

                                        messageAdapter = new MessageAdapter(fragment.getActivity());
                                        messagesView = (ListView) _view.findViewById(R.id.messages_view2);
                                        messagesView.setAdapter(messageAdapter);

                                        for(int x = 0; x < _messageList.length(); x++){
                                            try {
                                                JSONObject obj = _messageList.getJSONObject(x);
                                                Message _message = null;

                                                SessionMessageDTO sessionMessageDTO = new SessionMessageDTO();
                                                sessionMessageDTO.text = obj.getString("text");
                                                sessionMessageDTO.createdon = obj.getString("createdon");
                                                sessionMessageDTO.createdby = obj.getString("createdby");

                                                if(sessionMessageDTO.createdby.equalsIgnoreCase(username)){
                                                    //user
                                                    data = new MemberData(username, "","https://www.stuff.tv/sites/stuff.tv/files/styles/author-profile/public/avatar.png");
                                                    _message = new Message(sessionMessageDTO.text, data, true);
                                                    messageAdapter.add(_message);
                                                    messagesView.setSelection(messagesView.getCount() - 1);
                                                }else{
                                                    //agent
                                                    data = new MemberData(sessionMessageDTO.createdby, "","https://www.stuff.tv/sites/stuff.tv/files/styles/author-profile/public/avatar.png");
                                                    _message = new Message(sessionMessageDTO.text, data, false);
                                                    messageAdapter.add(_message);
                                                    messagesView.setSelection(messagesView.getCount() - 1);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }


                                        list.setVisibility(View.GONE);
                                        messagesView.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                Toast.makeText(fragment.getActivity().getApplicationContext(), "No session yet.", Toast.LENGTH_LONG).show();
                            }
                        }



                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    hideDialog();
                    Toast.makeText(fragment.getActivity().getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception ex) {
            hideDialog();
            Toast.makeText(fragment.getActivity().getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Private Methods
    private static void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private static void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
