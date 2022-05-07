package com.example.openevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.openevents.API.APIClient;
import com.example.openevents.API.OpenEventsCallback;
import com.example.openevents.Fragments.EventsFragment;
import com.example.openevents.Fragments.ExploreFragment;
import com.example.openevents.Fragments.ProfileFragment;
import com.example.openevents.Fragments.SearchUsersFragment;
import com.example.openevents.Response.UserResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragmentManagerActivity extends AppCompatActivity implements EventsFragment.EventsFragmentOutput {

    private APIClient apiClient;
    BottomNavigationView navigationView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_fragment_manager);

        Intent i = getIntent();
        String email = i.getStringExtra("email");
        getUserId(email);

        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnItemSelectedListener(selectedListener);

        ExploreFragment fragment = new ExploreFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment, "");
        fragmentTransaction.commit();
    }

    @SuppressLint("NonConstantResourceId")
    private NavigationBarView.OnItemSelectedListener selectedListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.explore_events:
                ExploreFragment exploreFragmentfragment = new ExploreFragment();
                FragmentTransaction exploreFragmentTransaction = getSupportFragmentManager().beginTransaction();
                exploreFragmentTransaction.replace(R.id.flFragment, exploreFragmentfragment, "");
                exploreFragmentTransaction.commit();
                return true;

            case R.id.events:
                EventsFragment eventsFragment = new EventsFragment();
                FragmentTransaction eventsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                eventsFragmentTransaction.replace(R.id.flFragment, eventsFragment, "");
                eventsFragmentTransaction.commit();
                return true;

            case R.id.search_users:
                SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
                FragmentTransaction searchusersFragmentTransaction = getSupportFragmentManager().beginTransaction();
                searchusersFragmentTransaction.replace(R.id.flFragment, searchUsersFragment, "");
                searchusersFragmentTransaction.commit();
                return true;

            case R.id.profile:
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                profileFragmentTransaction.replace(R.id.flFragment, profileFragment, "");
                profileFragmentTransaction.commit();
                return true;

        }
        return false;
    };


    @Override
    public void NavigateToCreate() {
        Intent i = new Intent(this, CreateEventActivity.class );
        startActivity(i);
    }

    private void getUserId(String email) {
        apiClient = APIClient.getInstance(getApplicationContext());
        apiClient.searchUsersByString(email, new OpenEventsCallback<List<UserResponse>>() {
                @Override
                public void onResponseOpenEvents(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                    if (response.isSuccessful()) {
                        List<UserResponse> users = response.body();
                        System.out.println(users.get(0).getName() + " " + users.get(0).getId());
                        if (users.size() > 0) {
                            userId = users.get(0).getId();
                        }
                    }
                }
                @Override
                public void onFailureOpenEvents() {
                    System.out.println("failure");
                }
        }
        );
    }
}