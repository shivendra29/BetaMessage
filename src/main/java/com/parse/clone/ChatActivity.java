package com.parse.clone;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ChatActivity extends AppCompatActivity {

    Handler mHandler;
    String active_user = "";

//    Intent intent = getIntent();
//    String active_user = intent.getStringExtra("username");

    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    public void SendChat(View view)
    {
        final EditText chat_edit = (EditText) findViewById(R.id.chat_edit);
        final ListView chatList = (ListView) findViewById(R.id.chat_listview);


        ParseObject message = new ParseObject("Message");
        Log.d("Error","here");

        final String messageContent = chat_edit.getText().toString();

        if(messageContent.equals("")){
            Toast.makeText(ChatActivity.this,"Enter a message",Toast.LENGTH_SHORT).show();
        }else {
            message.put("sender",ParseUser.getCurrentUser().getUsername());
            message.put("recipient",active_user);
            message.put("message",chat_edit.getText().toString());

            chat_edit.setText("");

            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        messages.add(messageContent);
                        arrayAdapter.notifyDataSetChanged();

                        Toast.makeText(ChatActivity.this,"Message Sent",Toast.LENGTH_SHORT).show();
                        chatList.setSelection(arrayAdapter.getCount()-1);
                    }
                }
            });
        }
    }

    ParseQuery<ParseObject> query;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_chat);

        this.mHandler = new Handler();

        Intent intent = getIntent();
        active_user = intent.getStringExtra("username");

        Log.d("Info",active_user);

        setTitle(active_user.substring(0,1).toUpperCase()+active_user.substring(1));

        final ListView chatList = (ListView) findViewById(R.id.chat_listview);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,messages);

        chatList.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

        query1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient",active_user);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

        query2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender",active_user);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(query1);
        queries.add(query2);

        //ParseQuery<ParseObject>
        query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            final public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){

                        messages.clear();

                        for (ParseObject message : objects){

                            String messageContent = message.getString("message");

                            if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()));{

                                messageContent = "> " + messageContent;
                            }

                            messages.add(messageContent);
                        }
                        arrayAdapter.notifyDataSetChanged();
                        chatList.setSelection(arrayAdapter.getCount()-1);
                    }
                }

            }


        });


        this.mHandler.postDelayed(m_Runnable,5000);

    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            final ListView chatList = (ListView) findViewById(R.id.chat_listview);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                final public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if(objects.size() > 0){

                            messages.clear();

                            for (ParseObject message : objects){

                                String messageContent = message.getString("message");

                                if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()));{

                                    messageContent = "> " + messageContent;
                                }

                                messages.add(messageContent);
                                //Log.d("ERROE",messageContent);
                                //chatList.smoothScrollToPosition(arrayAdapter.getCount()-1);
                            }
                            arrayAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });


//            Toast.makeText(ChatActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            //arrayAdapter.notifyDataSetChanged();
            ChatActivity.this.mHandler.postDelayed(m_Runnable, 5000);
        }

    };
}
