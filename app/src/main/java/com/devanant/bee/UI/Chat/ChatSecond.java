package com.devanant.bee.UI.Chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devanant.bee.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatSecond extends AppCompatActivity implements TextWatcher {

    private String name;
    private WebSocket webSocket;
    private String SERVER_PATH = "https://bee-server-chat.herokuapp.com";
    private EditText messageEdit;
    private View sendBtn, pickImageBtn;
    private RecyclerView recyclerView;
    private int IMAGE_REQUEST_ID = 1;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_second);

        name = getIntent().getStringExtra("name");
        initiateSoketConnection();
    }

    private void initiateSoketConnection() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String string = editable.toString().trim();
        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            sendBtn.setVisibility(View.VISIBLE);
            pickImageBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void resetMessageEdit() {

        messageEdit.removeTextChangedListener(this);
        messageEdit.setText("");
        sendBtn.setVisibility(View.INVISIBLE);
        pickImageBtn.setVisibility(View.VISIBLE);

        messageEdit.addTextChangedListener(this);
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            runOnUiThread(()->{
                try {
                    JSONObject jsonObject=new JSONObject(text);
                    jsonObject.put("isSent",false);
                    messageAdapter.additem(jsonObject);

                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(() -> {
                Toast.makeText(ChatSecond.this, "Socket Connection Successful", Toast.LENGTH_SHORT).show();
                initalizeview();
            });
        }
    }

    private void initalizeview() {

        messageEdit = findViewById(R.id.MessageEdit);
        sendBtn = findViewById(R.id.sendbutton);
        pickImageBtn = findViewById(R.id.imagechat);
        recyclerView = findViewById(R.id.ChatRecycler);

        messageAdapter=new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageEdit.addTextChangedListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("message", messageEdit.getText().toString());

                    webSocket.send(jsonObject.toString());
                    jsonObject.put("isSent",true);
                    messageAdapter.additem(jsonObject);

                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                    resetMessageEdit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        pickImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), IMAGE_REQUEST_ID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && requestCode == RESULT_OK) {

            try {
                InputStream is=getContentResolver().openInputStream(data.getData());
                Bitmap image= BitmapFactory.decodeStream(is);
                 sendImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
        String base64String= android.util.Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("name",name);
            jsonObject.put("image",base64String);
            webSocket.send(jsonObject.toString());

            jsonObject.put("isSent",true);

            messageAdapter.additem(jsonObject);
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}