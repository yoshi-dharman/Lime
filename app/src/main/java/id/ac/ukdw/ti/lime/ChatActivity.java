package id.ac.ukdw.ti.lime;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private EditText txtPost;
    private FloatingActionButton btnPost;
    private TextView txtHasil;
    private String getname;

    FirebaseDatabase db;
    DatabaseReference dbChat;
    DatabaseReference dbUser;
    private List<Map> ListChat;
    private List<Map> ListUser;
    private String room;
    public int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatactivity);
        txtPost = (EditText)findViewById(R.id.txtPost);
        btnPost = (FloatingActionButton)findViewById(R.id.btnPost);
        txtHasil = (TextView)findViewById(R.id.txtHasil);


        Intent mIntent = getIntent();
        room = mIntent.getExtras().getString("room");

        //Toast.makeText(getApplicationContext(), room, Toast.LENGTH_LONG).show();


        //setting firebase di database

        db = FirebaseDatabase.getInstance();
        dbChat = db.getReference("room").child(room); //setiap ada ketambahan data baru langsung ke trigger
        ListChat = new ArrayList<Map>();

        dbUser = db.getReference("userdata").child(room); //setiap ada ketambahan data baru langsung ke trigger
        ListUser = new ArrayList<Map>();

        //menampilkan data dari firebase database

        dbChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListChat = (List<Map>) dataSnapshot.getValue();
                if (ListChat != null) {
                    String tmpStr = "";
                    for (int i = 0; i < ListChat.size(); i++) {
                        tmpStr += ListChat.get(i).get("user")+"("+ListChat.get(i).get("tanggal")+","+ListChat.get(i).get("waktu")+")\n "+ListChat.get(i).get("message")+"\n\n";
                    }
                    txtHasil.setText(tmpStr);
                } else {
                    ListChat = new ArrayList<Map>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //menambahkan pesan baru

        btnPost.setOnClickListener(new View.OnClickListener() {
            private String getTanggal() {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                return dateFormat.format(date);
            }

            private String getWaktu() {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                return dateFormat.format(date);
            }
            @Override
            public void onClick(View view) {
                if(txtPost.getText().length()!=0){
                    Map map = new HashMap();
                    map.put("user", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    map.put("tanggal", getTanggal());
                    map.put("waktu", getWaktu());
                    map.put("message", txtPost.getText().toString());
                    ListChat.add(map);
                    dbChat.setValue(ListChat);
                    txtPost.setText("");
                }
                else{
                    Toast.makeText(ChatActivity.this, "Empty Message",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}