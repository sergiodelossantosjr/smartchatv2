package spcba.com.smartchatv2;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ImageButton imgSent;

    private MemberData data;
    private Socket socket;
    private Thread t;
    private String to, from, sessionNo,AGENT_ID, choosenLanguage, agentColor, customerName;
    private CharSequence languages[] = new CharSequence[]{"English", "Spanish", "Japanese", "German", "Chinese", "Korean"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //custom
        customerName = getIntent().getStringExtra("username");
        editText = (EditText) findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        imgSent = (ImageButton) findViewById(R.id.sent);
        messagesView.setAdapter(messageAdapter);

        imgSent.setEnabled(false);
        agentColor = getRandomColor();

        t = new Thread(new Runnable(){
            public void run(){
                try {
                    socket = IO.socket("http://teamagilevm.eastasia.cloudapp.azure.com:3000");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //socket.emit("foo", "hi");
                        //socket.disconnect();
                    }

                }).on(customerName, new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Message _message = null;
                                Gson gson = new Gson();
                                Payload obj = gson.fromJson(args[0].toString(), Payload.class);

                                if(obj.transaction.equalsIgnoreCase("reply")){
                                    Reply reply = gson.fromJson(args[0].toString(), Reply.class);

                                    AGENT_ID = reply.agent;
                                    to = reply.from;

                                    data = new MemberData(AGENT_ID, "","https://www.stuff.tv/sites/stuff.tv/files/styles/author-profile/public/avatar.png");
                                    _message = new Message(reply.message, data, false);
                                    //enable the button of send
                                    imgSent.setEnabled(true);
                                    sessionNo = obj.sessionno;
                                    playSound();
                                }else if(obj.transaction.equalsIgnoreCase("chat")){
                                    MessageDTO messageDTO = gson.fromJson(args[0].toString(), MessageDTO.class);

                                    data = new MemberData(AGENT_ID, "","https://www.stuff.tv/sites/stuff.tv/files/styles/author-profile/public/avatar.png");
                                    _message = new Message(messageDTO.message, data, false);
                                    sessionNo = obj.sessionno;
                                    playSound();
                                }

                                messageAdapter.add(_message);
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });

                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                    }

                });
                socket.connect();
            }
        });

        t.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off(customerName);

        //call session delete
        deleteSession();
    }

    private void playSound(){
        MediaPlayer mPlayer;
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tri_tone);
        mPlayer.start();
    }

    private void deleteSession(){
        // Tag used to cancel the request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("channel", "AGENTCHANNEL");
            jsonParams.put("agentusername", AGENT_ID);
            jsonParams.put("transaction", "disconnect");

            // prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://teamagilevm.eastasia.cloudapp.azure.com:8080/smartchat/api/v1/deleteagentsession.php",
                    jsonParams,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            // display response
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            );

            // add it to the RequestQueue
            requestQueue.add(getRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(){
        // Tag used to cancel the request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("channel", "AGENTCHANNEL");
            jsonParams.put("customerid", customerName);
            jsonParams.put("language", choosenLanguage);
            jsonParams.put("message", "request");
            jsonParams.put("transaction", "request");
            jsonParams.put("timestamp", "00:00:00");

            // prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://teamagilevm.eastasia.cloudapp.azure.com:8080/smartchat/api/v1/sendrequest.php",
                    jsonParams,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            // display response
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            );

            // add it to the RequestQueue
            requestQueue.add(getRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view) {
        final String message = editText.getText().toString();
        if (message.length() > 0) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JSONObject jsonParams = new JSONObject();
            boolean isTranslate = false;
            if (!choosenLanguage.equalsIgnoreCase("English")){
                isTranslate = true;
            }

            try {
                jsonParams.put("sessionno", sessionNo);
                jsonParams.put("channel", AGENT_ID);
                jsonParams.put("message", message);
                jsonParams.put("translate", isTranslate);
                jsonParams.put("transaction", "chat");
                jsonParams.put("from", choosenLanguage);
                jsonParams.put("to", "en");

                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,
                        "http://teamagilevm.eastasia.cloudapp.azure.com:8080/smartchat/api/v1/sendmessage.php",
                        jsonParams,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Message _message = null;

                                data = new MemberData(AGENT_ID, "","https://www.stuff.tv/sites/stuff.tv/files/styles/author-profile/public/avatar.png");
                                _message = new Message(message, data, true);
                                messageAdapter.add(_message);
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.getMessage());
                            }
                        }
                );

                // add it to the RequestQueue
                requestQueue.add(getRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editText.getText().clear();
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            showLanguageDialog();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {
             //logout
             super.onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLanguageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setTitle("Choose Language");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (languages[which].toString()){
                    case "English" :
                        choosenLanguage = "en";
                        break;
                    case "Spanish" :
                        choosenLanguage = "es";
                        break;
                    case "Japanese" :
                        choosenLanguage = "ja";
                        break;
                    case "German" :
                        choosenLanguage = "de";
                        break;
                    case "Chinese" :
                        choosenLanguage = "zh";
                        break;
                    case "Korean":
                        choosenLanguage = "ko";
                        break;
                    default:
                }
                sendRequest();
                Toast.makeText(getApplicationContext(), "You choose  " + languages[which].toString() + " as language.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
}
