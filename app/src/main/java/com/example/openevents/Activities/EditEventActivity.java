package com.example.openevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.openevents.API.APIClient;
import com.example.openevents.API.OpenEventsCallback;
import com.example.openevents.R;
import com.example.openevents.Request.CreateEventRequest;
import com.example.openevents.Response.EventResponse;

import retrofit2.Call;
import retrofit2.Response;

public class EditEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] categories = {"Cat1", "Cat2", "Cat3", "Cat4"};
    Spinner spinnerCategories;
    EditText etName, etImage, etLocation, etDescription, etEventStart_date, etEventEnd_date, etN_participants;
    EventResponse event;

    private APIClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        apiClient = APIClient.getInstance(getApplicationContext());


        setViews();
        setData();
    }

    private void setViews() {
        etName = findViewById(R.id.et_edit_event_name);
        etImage = findViewById(R.id.et_edit_event_profile_picture);
        etLocation = findViewById(R.id.et_edit_event_location);
        etDescription = findViewById(R.id.et_edit_event_description);
        etEventStart_date = findViewById(R.id.et_edit_event_start_date);
        etEventEnd_date = findViewById(R.id.et_edit_event_end_date);
        etN_participants = findViewById(R.id.et_edit_participants);
        spinnerCategories = findViewById(R.id.sp_edit_event_categories);

        configureSpinner();

        Button editEvent = findViewById(R.id.bt_edit_event);
        editEvent.setOnClickListener(view -> {

            String name = etName.getText().toString();
            String image = etImage.getText().toString();
            String location = etLocation.getText().toString();
            String description = etDescription.getText().toString();
            String startDate = etEventStart_date.getText().toString();
            String endDate = etEventEnd_date.getText().toString();
            int participants = Integer.parseInt(String.valueOf(etN_participants.getText()));
            String type = spinnerCategories.getSelectedItem().toString();

            CreateEventRequest createEventRequest = new CreateEventRequest(name, image, location, description, startDate, endDate, participants, type);
            editEventApiCall(createEventRequest);
        });

    }

    private void setData() {
        Intent i = getIntent();
        event = (EventResponse) i.getSerializableExtra("event");

        etName.setText(event.getName());
        etImage.setText(event.getImage());
        etLocation.setText(event.getLocation());
        etDescription.setText(event.getDescription());
        etEventStart_date.setText(event.getEventStart_date());
        etEventEnd_date.setText(event.getEventEnd_date());
        etN_participants.setText(String.valueOf(event.getN_participators()));
    }

    private void configureSpinner() {
        spinnerCategories = findViewById(R.id.sp_edit_event_categories);
        spinnerCategories.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);
    }

    private void editEventApiCall(CreateEventRequest createEventRequest) {
        int id = event.getId();
        apiClient.editEvent(id, createEventRequest, new OpenEventsCallback() {
            @Override
            public void onResponseOpenEvents(Call call, Response response) {

                Toast.makeText(getApplicationContext(), "Event edited", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailureOpenEvents() {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}