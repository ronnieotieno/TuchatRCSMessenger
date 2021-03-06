package com.example.tuchatrcsmessenger.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuchatrcsmessenger.ChatsActivity;
import com.example.tuchatrcsmessenger.Classes.ConversationsClass;
import com.example.tuchatrcsmessenger.MainActivity;
import com.example.tuchatrcsmessenger.R;
import com.example.tuchatrcsmessenger.data.db.AppDatabase;
import com.example.tuchatrcsmessenger.data.entity.LastMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    //Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase path variables
    String chatRoomsCollection = "chatrooms";
    String messagesCollection = "messages";
    //
    Date strDate;
    String strDateDay;
    String strDateYear;
    private List<ConversationsClass> listItems;
    private Context context;
    private Date currentDate;
    private SimpleDateFormat formatterFullDate = new SimpleDateFormat("d MMM yyyy");
    private SimpleDateFormat formatterHalfDate = new SimpleDateFormat("d MMM");
    private SimpleDateFormat formatterDay = new SimpleDateFormat("d");
    private SimpleDateFormat formatterYear = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
    private String currentDateString;
    private String currentDateStringDay;
    private String currentDateStringYear;


    public ConversationsAdapter(List<ConversationsClass> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        this.currentDate = new Date();
        this.currentDate.getTime();
        this.currentDateString = formatterFullDate.format(currentDate);
        this.currentDateStringDay = formatterDay.format(currentDate);
        this.currentDateStringYear = formatterYear.format(currentDate);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationsClass listItem = listItems.get(position);


        strDateDay = formatterDay.format(listItem.getSentTime());
        strDateYear = formatterYear.format(listItem.getSentTime());

        getLastMessage(listItem.getChatRoomId(), holder.messageBody, holder.sentTime, holder.readStatusButton);

        holder.sender.setText(listItem.getSenderName());

        //Show unread messages
        holder.readStatusButton.setBackgroundResource(R.drawable.notification_dot_read);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    //Get last message
    private void getLastMessage(final String chatRoomID, final TextView lastMessageText, final TextView sentTimeText, final Button readStatusButton) {
        final AppDatabase db = AppDatabase.getInstance(context);

        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                db.getLastMessageDao().getLastMessageLiveData(chatRoomID).observe(((MainActivity) context), new Observer<LastMessage>() {
                    @Override
                    public void onChanged(LastMessage lastMessage) {
                        if (lastMessage != null) {

                            try {

                                if (lastMessage.getUnreadCount()>0){
                                    readStatusButton.setBackgroundResource(R.drawable.notification_dot);
                                    readStatusButton.setText(Integer.toString((lastMessage.getUnreadCount())));
                                }

                                strDate = formatterFullDate.parse(lastMessage.getSentTime());
                                if (formatterHalfDate.format(strDate).equals(currentDateString) && strDateYear.equals(currentDateStringYear)) {

                                    sentTimeText.setText(formatterTime.format(strDate));

                                } else if (Integer.parseInt(strDateDay) == Integer.parseInt(currentDateStringDay) - 1 && strDateYear.equals(currentDateStringYear)) {
                                    sentTimeText.setText("Yesterday");
                                } else if (strDateYear.equals(currentDateStringYear)) {

                                    sentTimeText.setText(formatterHalfDate.format(strDate));

                                } else {
                                    sentTimeText.setText(formatterHalfDate.format(strDate));
                                }
                                // Remove all tab spaces and enters and replace them with spaces
                                String messageBody = lastMessage.getMessage().replaceAll("\\s", " ");

                                // Trim the string to the first 30 characters. Add  ellipses if message length exceeds 30 chars
                                String trimmedString;
                                int preferredMessageLength = 30;
                                if (Math.min(messageBody.length(), preferredMessageLength) == messageBody.length()) {
                                    trimmedString = messageBody;
                                } else {
                                    trimmedString = messageBody.substring(0, preferredMessageLength) + "...";
                                }
                                lastMessageText.setText(trimmedString);
                            } catch (ParseException e) {
                               
                                e.printStackTrace();
                            }


                        }


                    }
                });
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView sender;
        public TextView sentTime;
        public TextView messageBody;
        public Button readStatusButton;
        public String readStatus;
        public String chatID;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            sender = itemView.findViewById(R.id.sender_name);
            sentTime = itemView.findViewById(R.id.sent_time);
            messageBody = itemView.findViewById(R.id.message_body);
            readStatusButton = itemView.findViewById(R.id.button);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();

            Intent openChatMessages = new Intent(context, ChatsActivity.class);
            openChatMessages.putExtra("Sender Name", this.sender.getText().toString());
            openChatMessages.putExtra("Chat ID", listItems.get(pos).chatRoomId);
            context.startActivity(openChatMessages);

            //Animate transition into called activity
            ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }
}