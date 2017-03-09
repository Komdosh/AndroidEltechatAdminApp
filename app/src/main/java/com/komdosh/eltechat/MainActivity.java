package com.komdosh.eltechat;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.komdosh.eltechat.admin.AdminCommandConstants;
import com.komdosh.eltechat.admin.AdminCommands;
import com.komdosh.eltechat.chat.Message;
import com.komdosh.eltechat.chat.MessagesListAdapter;
import com.komdosh.eltechat.constants.JSONConstants;
import com.komdosh.eltechat.constants.ServerResponseConstants;
import com.komdosh.eltechat.utils.Configs;
import com.komdosh.eltechat.utils.Utils;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.inputMsg)
    public EditText inputMsg;

    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    @BindView(R.id.list_view_messages)
    public ListView listViewMessages;
    @BindView(R.id.online)
    public TextView onlineTextView;
    @BindView(R.id.nickName)
    public TextView nicknameTextView;

    private WebSocket webSocketClient;

    private Utils utils = Utils.getInstance();

    private String clientName = null;
    private String clientPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        utils.init(getApplicationContext());

        Intent i = getIntent();
        clientName = i.getStringExtra("name");
        clientPassword = i.getStringExtra("password");
        nicknameTextView.setText(clientName);

        listMessages = new ArrayList<>();

        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        socketInit();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (AdminCommandConstants.KICK.equals(item.getTitle())) {
            sendMessageToServer(AdminCommands.kick(listMessages.get(item.getItemId()).getFromName()));
        } else if(AdminCommandConstants.MUTE.equals(item.getTitle())){
            sendMessageToServer(AdminCommands.mute(listMessages.get(item.getItemId()).getFromName()));
        } else if(AdminCommandConstants.UNMUTE.equals(item.getTitle())){
            sendMessageToServer(AdminCommands.unmute(listMessages.get(item.getItemId()).getFromName()));
        } else if(AdminCommandConstants.DELETE_MESSAGE.equals(item.getTitle())){
            sendMessageToServer(AdminCommands.deleteMessage(listMessages.get(item.getItemId()).getId()));
        }

        return super.onContextItemSelected(item);
    }

    private void socketInit(){
        String hostAddress = Configs.URL+clientName;

        if(!clientPassword.isEmpty()){
            hostAddress+="&password="+utils.getMd5Hash(clientPassword);
        }

        URI uri;
        try {
            uri = new URI(hostAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        Log.i("Websocket", "URI " + uri.toString());
        try {
            webSocketClient = new WebSocketFactory().createSocket(uri, 5000);
        } catch (IOException e){
            Log.e("WebSocketConnect", e.toString());
        }

        webSocketClient.addListener(new WebSocketAdapter(){
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                super.onTextMessage(websocket, text);
                final String message = text;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseMessage(message);
                    }
                });
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                super.onError(websocket, cause);
            }
        });

        new AsyncRequest().execute();
    }

    class AsyncRequest extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String ...args) {
            try{
                webSocketClient.connect();
            }catch (WebSocketException e){
                Log.i("Websocket", e.toString());
            }
            return null;
        }
    }

    @OnClick(R.id.btnSend)
    public void send(){
        String messageText = inputMsg.getText().toString();

        if(!messageText.isEmpty()){
            sendMessageToServer(utils.getJSONForMessage(messageText, clientName));
        }

        inputMsg.setText("");
    }

    private void sendMessageToServer(final String message) {
        webSocketClient.sendText(message);
    }

    private void parseMessage(final String msg) {
        try {
            JSONObject json = new JSONObject(msg);

            // JSON node 'flag'
            String flag = json.getString(JSONConstants.FLAG);

            switch (flag){
                case ServerResponseConstants.MESSAGE:
                    newMessage(json);
                    break;
                case ServerResponseConstants.LOGIN_SUCCESS:
                    loginSuccess(json);
                    break;
                case ServerResponseConstants.LOGIN_FAILURE_NICKNAME:
                    loginFailure(json);
                    finish();
                    break;
                case ServerResponseConstants.LOGIN_FAILURE_SERVER:
                    serverError(json);
                    break;
                case ServerResponseConstants.DELETE_MESSAGE:
                    deleteMessage(json);
                    break;
                case ServerResponseConstants.NEW_USER_CONNECT:
                    newUserConnect(json);
                    break;
                case ServerResponseConstants.USER_DISCONNECT:
                    userDisconnect(json);
                    break;
                case ServerResponseConstants.KICK:
                    kickUser(json);
                    break;
                case ServerResponseConstants.MUTE:
                    muteUser(json);
                    break;
                case ServerResponseConstants.UNMUTE:
                    unMuteUser(json);
                    break;
                default:
                    showToast(getString(R.string.smthStrangeHappensWithServer));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void finish() {
        webSocketClient.disconnect();
        super.finish();
    }

    private void loginSuccess(JSONObject jsonObject) throws JSONException{
    }

    private void loginFailure(JSONObject jsonObject) throws JSONException{
        showToast(getString(R.string.loginFailure));
        finish();
    }

    private void newMessage(JSONObject jsonObject) throws JSONException{
        String fromName = jsonObject.getString("name");
        String message = jsonObject.getString("message");
        Long messageId = jsonObject.getLong("messageId");

        boolean isSelf = false;

        if (clientName.equals(fromName)) {
            isSelf = true;
        }

        Message m = new Message(messageId, fromName, message, isSelf);

        appendMessage(m);
    }

    private void serverError(JSONObject jsonObject) throws JSONException{
        showToast(getString(R.string.smthHappensWithServer));
    }

    private void deleteMessage(JSONObject jsonObject) throws JSONException{
        Long deleteMsgId = jsonObject.getLong("messageId");
        for(Message m : listMessages){
            if(m.getId() != null && m.getId().equals(deleteMsgId)){
                listMessages.remove(m);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void newUserConnect(JSONObject jsonObject) throws JSONException{
        String name = jsonObject.getString("name");

        int online = jsonObject.getInt("online");

        appendMessage(new Message(null, "Server",
                getResources().getQuantityString(R.plurals.newUserConnected, online, name, online),
                false, true));

        onlineTextView.setText(getString(R.string.online, online));
    }

    private void userDisconnect(JSONObject jsonObject) throws JSONException{
        String name = jsonObject.getString("name");

        int online = jsonObject.getInt("online");

        appendMessage(new Message(null, "Server",
                getResources().getQuantityString(R.plurals.userDisconnected, online, name, online),
                false, true));

        onlineTextView.setText(getString(R.string.online, online));
    }

    private void kickUser(JSONObject jsonObject){
        showToast(getString(R.string.kick));
        super.finish();
    }

    private void muteUser(JSONObject jsonObject) throws JSONException{
        if(jsonObject.getString("name").equals(clientName)){
            showToast(getString(R.string.mute));
        }
    }

    private void unMuteUser(JSONObject jsonObject) throws JSONException{
        if(jsonObject.getString("name").equals(clientName)){
            showToast(getString(R.string.unmute));
        }
    }

    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            listMessages.add(m);
            adapter.notifyDataSetChanged();
            playBeep();
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void playBeep() {
        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendMessageToServer(utils.getJSONForLogoff(clientName));
    }
}