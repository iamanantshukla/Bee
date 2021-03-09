package com.devanant.bee.UI.Chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devanant.bee.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
     private static final int  TYPE_MESSAGE_SENT=0;
    private static final int TYPE_MESSAGE_RECEIVED=1;
    private static final int TYPE_IMAGE_SENT=2;
    private static final int TYPE_IMAGE_RECEIVED=3;

    private LayoutInflater inflater;
    private List<JSONObject> messages=new ArrayList<>();

    public  MessageAdapter(LayoutInflater inflater)
    {
        this.inflater=inflater;
    }

    public class SendMessageHolder extends RecyclerView.ViewHolder{
      TextView messageText;
        public SendMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageText=itemView.findViewById(R.id.sendmessagetext);
        }
    }

    public class SendImageHolder extends RecyclerView.ViewHolder{
     ImageView imageView;
        public SendImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imagechat);
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder
    {
        TextView nameTxt,messageReceived;
        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.nametext);
            messageReceived=itemView.findViewById(R.id.receivedText);
        }
    }

    public class ReceivedImageHolder extends RecyclerView.ViewHolder{
          TextView nameTxt;
          ImageView imageView;
        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imagechat);
            nameTxt=itemView.findViewById(R.id.nametext);
        }
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message=messages.get(position);
        try {
            if(message.getBoolean("isSent"))
            {
                if(message.has("message"))
                    return TYPE_MESSAGE_SENT;
               else
                   return TYPE_IMAGE_SENT;

            }
            else
            {
                if(message.has("message"))
                {
                    return TYPE_MESSAGE_RECEIVED;
                }
                else
                    return TYPE_IMAGE_RECEIVED;
                
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view;
        switch (viewType){
            case TYPE_IMAGE_SENT:
                view=inflater.inflate(R.layout.sentchatimage,parent,false);
                return  new SendImageHolder(view);

            case TYPE_MESSAGE_SENT:
                view=inflater.inflate(R.layout.sentchatmessage,parent,false);
                return  new SendMessageHolder(view);

            case TYPE_MESSAGE_RECEIVED:
                view=inflater.inflate(R.layout.receivechatmessage,parent,false);
                return  new ReceivedMessageHolder(view);

            case TYPE_IMAGE_RECEIVED:
                view=inflater.inflate(R.layout.receivechatimage,parent,false);
                return  new ReceivedImageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       JSONObject message=messages.get(position);

        try {
            if(message.getBoolean("isSent"))
            {
                if(message.has("message")){
                    SendMessageHolder messageHolder= (SendMessageHolder) holder;
                    messageHolder.messageText.setText(message.getString("message"));

                }
                else
                {
                    SendImageHolder imageHolder= (SendImageHolder) holder;
                    Bitmap bitmap=getBitmapFormString(message.getString("image"));

                    imageHolder.imageView.setImageBitmap(bitmap);
                }
            }
            else {

                if(message.has("message")){
                    ReceivedMessageHolder messageHolder= (ReceivedMessageHolder) holder;
                    messageHolder.nameTxt.setText(message.getString("name"));
                    messageHolder.messageReceived.setText(message.getString("message"));

                }
                else
                {
                    ReceivedImageHolder imageHolder= (ReceivedImageHolder) holder;
                    imageHolder.nameTxt.setText(message.getString("name"));
                    Bitmap bitmap=getBitmapFormString(message.getString("image"));
                    imageHolder.imageView.setImageBitmap(bitmap);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFormString(String image) {

        byte[] bytes= Base64.decode(image,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void additem(JSONObject jsonObject)
    {
        messages.add(jsonObject);
        notifyDataSetChanged();
    }
}
