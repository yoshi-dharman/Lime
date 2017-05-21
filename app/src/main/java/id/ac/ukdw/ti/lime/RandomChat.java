package id.ac.ukdw.ti.lime;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import android.os.Handler;
import android.widget.Toast;

public class RandomChat extends AppCompatActivity {

    private Button btnStartRand;
    private int n;
    FirebaseDatabase db;
    DatabaseReference dbUser;
    DatabaseReference dbRand;
    DatabaseReference dbRoom;
    private List<Map> ListUser;
    private List<Map> CekUser;
    private List<Map> ListRoom;
    private Thread thread;
    private String session;
    private String roomS;
    private Boolean gotRoom = false;
    Handler handler = new Handler();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_chat);


        btnStartRand = (Button)findViewById(R.id.btnStartRand);
        db = FirebaseDatabase.getInstance();
        dbUser = db.getReference("userdata"); //setiap ada ketambahan data baru langsung ke trigger
        dbRand = db.getReference("random");
        dbRoom = db.getReference("room");
        ListUser = new ArrayList<Map>();
        CekUser = new ArrayList<Map>();
        ListRoom = new ArrayList<Map>();

        //menampilkan data dari firebase database
       btnStartRand.setOnClickListener(new View.OnClickListener() {

           @Override
            public void onClick(View v) {

               BackgroundTask task = new BackgroundTask(RandomChat.this);
               task.execute();

            }
        });

    }


    private class BackgroundTask extends AsyncTask <Void, Void, Void> {
        private ProgressDialog dialog;
        private volatile boolean running = true;


        public BackgroundTask(RandomChat activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Looking for random person...");
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbRand.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dbRand.child(session).removeValue();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    done();
                 }
            });
            dialog.show();
        }

        public void done(){
            cancel(true);
            running = false;
            handler.removeCallbacksAndMessages(null);
            dialog.dismiss();
            onPostExecute(null);
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(Void result) {
//            if(running == false){
//                Toast.makeText(getApplicationContext(), "selesai", Toast.LENGTH_LONG).show();
//            }

        }

        @Override
        protected Void doInBackground(Void... params) {
                while (running) {


                    dbRand.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CekUser = (List<Map>) dataSnapshot.getValue();


                            if (CekUser.size() > 1) {
                                String user = Integer.toString(CekUser.size());

                                for (int i = 0; i < CekUser.size(); i++) {

                                    if (CekUser.get(i).get("user2").equals("")) {
                                        gotRoom = true;
                                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                        //dbRand.child(Integer.toString(i)).child("user2").removeValue();

                                        Map user2updt = new HashMap();
                                        user2updt.put("user2", email);

                                        dbRand.child(Integer.toString(i)).updateChildren(user2updt);

                                        final int randomid = i;
                                        //buat room


                                        dbRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                ListRoom = (List<Map>) dataSnapshot.getValue();


                                                int room = ListRoom.size();
                                                roomS = String.valueOf(room);

                                                dbRoom.child(roomS).child("0").child("message").setValue("Say Hello!");
                                                dbRoom.child(roomS).child("0").child("tanggal").setValue(getTanggal());
                                                dbRoom.child(roomS).child("0").child("waktu").setValue(getWaktu());
                                                dbRoom.child(roomS).child("0").child("user").setValue("Random Chat");

                                                Map roomupdt = new HashMap();
                                                roomupdt.put("room", room);
                                                dbRand.child(Integer.toString(randomid)).updateChildren(roomupdt);

                                                String tmp = "Say Hello! ";

                                                tmp += CekUser.get(randomid).get("user1")+" dan "+FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                                dbRoom.child(roomS).child("0").child("message").setValue(tmp);


                                                //Chat activity
                                                // myDialog.dismiss();

                                                Intent intent = new Intent(RandomChat.this, ChatActivity.class);
                                                intent.putExtra("room", roomS);
                                                done();
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                }

                            }

                            if(gotRoom==false) {
                                String user = Integer.toString(CekUser.size());
                                session = user;
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                dbRand.child(user).child("user1").setValue(email);
                                dbRand.child(user).child("user2").setValue("");
                                dbRand.child(user).child("room").setValue("");

                                check();
                            }
                        }

                        public void check() {
                            dbRand.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CekUser = (List<Map>) dataSnapshot.getValue();
                                    if (CekUser.get(Integer.parseInt(session)).get("room").equals("")) {
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                check();
                                                //Toast.makeText(getApplicationContext(), "gak dapet", Toast.LENGTH_SHORT).show();
                                            }
                                        }, 5000);

                                    } else {

                                        roomS = ""+CekUser.get(Integer.parseInt(session)).get("room");

                                        //lempar ke room yang dha di buat

                                        //myDialog.dismiss();

                                        Toast.makeText(getApplicationContext(), roomS, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RandomChat.this, ChatActivity.class);
                                        intent.putExtra("room", roomS);
                                        startActivity(intent);
                                        finish();
                                        done();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    break;
                }
            return null;
        }

    }


}
