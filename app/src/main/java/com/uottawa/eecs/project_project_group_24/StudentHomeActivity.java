package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Timestamp;

public class StudentHomeActivity extends AppCompatActivity {
    Button btnTestSess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OTA_STUDENTHOME","Creating layout...");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("OTA_STUDENTHOME","Created Layout.");
        btnTestSess = findViewById(R.id.TestSess);
        Log.d("OTA_STUDENTHOME",String.valueOf(btnTestSess!=null));
        Log.d("OTA_STUDENTHOME",String.valueOf(btnTestSess));
        btnTestSess.setOnClickListener(v ->{
            Log.d("OTA_STUDENTHOME","Creating Database obj...");
            FirebaseSessionsRepository db = new FirebaseSessionsRepository();
//            db.add("lnoceda@gmailcom");
            Log.d("OTA_STUDENTHOME","Created Database obj.");
            Timestamp time = new Timestamp(Long.parseLong("1762863900000"));
            Log.d("OTA_STUDENTHOME","Timestamp is "+time.toString());
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setManualApproval("true");
            slot.setId( "-OdpT5BBi4fB6wN0iiWd");
            slot.setCourseCode("ITI1121");
            slot.setDurationMin(30);
            slot.setTutorId("vidu@gmail.com");
            slot.setStartMillis(Long.parseLong("1762863900000"));

            if(Session.isAvailable(slot, slot.getStartMillis())){
                Session session = new Session("-OdpT5BBi4fB6wN0iiWd", "teststudent", "jane doe", "CSI2110",time, "APPROVED", slot) ;


                db.add("vidu@gmailcom", session, new FirebaseSessionsRepository.OpCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("OTA_STUDENTHOME","SUCCESS");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("OTA_STUDENTHOME",message);
                    }
                });
            }
        });
    }
}