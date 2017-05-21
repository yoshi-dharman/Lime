package id.ac.ukdw.ti.lime;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenu extends AppCompatActivity {

    private Button btnRandChat, btnPublicchat, btnLogout;
    private TextView txtUsername;

    FirebaseDatabase db;
    DatabaseReference dbUser;
    DatabaseReference dbFollower;
    private List<Map> ListUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);


        txtUsername = (TextView) findViewById(R.id.txtUsername);
        btnRandChat = (Button)findViewById(R.id.btnRandChat);
        btnPublicchat = (Button)findViewById(R.id.btnPublicchat);
        btnLogout = (Button)findViewById(R.id.btnLogout);

        txtUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        btnRandChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, RandomChat.class);
                startActivity(intent);
            }
        });
        btnPublicchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, PublicChat.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainMenu.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        //menampilkan data dari firebase database

        db = FirebaseDatabase.getInstance();
        dbFollower = db.getReference("follower");
        dbUser = db.getReference("userdata"); //setiap ada ketambahan data baru langsung ke trigger
        ListUser = new ArrayList<Map>();

        //menampilkan data dari firebase database

//        dbUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ListUser = (List<Map>) dataSnapshot.getValue();
//                if (ListUser != null){
//                    String tmpStr = "" ;
//                    for (int i = 0; i < ListUser.size(); i++){
//                        ListUser.get(i).get("user");
//                    }
//
//                } else{
//                    ListUser = new ArrayList<Map>();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        //Map map = new HashMap();
        //map.put("id", ListUser.size()+1);
        //map.put("user", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //ListUser.add(map);
        //dbUser.setValue(ListUser);

    }

}
