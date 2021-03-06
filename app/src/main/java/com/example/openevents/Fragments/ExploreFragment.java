package com.example.openevents.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openevents.API.APIClient;
import com.example.openevents.API.OpenEventsCallback;
import com.example.openevents.Activities.EventActivity;
import com.example.openevents.Activities.OwnEventActivity;
import com.example.openevents.Adapters.EventsAdapter;
import com.example.openevents.R;
import com.example.openevents.Response.EventResponse;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment implements SearchView.OnQueryTextListener {
    private ArrayList<EventResponse> events = new ArrayList<>();
    EventsAdapter eventsAdapter;
    SearchView searchView;
    CheckBox bestButton;
    ExtendedFloatingActionButton educationButton, sportButton, travelButton, concertButton, clearButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        setViews(v);
        searchView.setOnQueryTextListener(this);
        executeApiCall("", false);
        filterByCategory();

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterByCategory() {

        sportButton.setOnClickListener(view -> {
            events.removeIf(event -> !event.getType().equalsIgnoreCase("sports"));
            eventsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Filtered by Sports",Toast.LENGTH_SHORT).show();
        });

        educationButton.setOnClickListener(view -> {
            events.removeIf(event -> !event.getType().equalsIgnoreCase("education"));
            eventsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Filtered by Education",Toast.LENGTH_SHORT).show();
        });

        concertButton.setOnClickListener(view -> {
            events.removeIf(event -> !event.getType().equalsIgnoreCase("concert"));
            eventsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Filtered by Concert",Toast.LENGTH_SHORT).show();
        });

        travelButton.setOnClickListener(view -> {
            events.removeIf(event -> !event.getType().equalsIgnoreCase("travel"));
            eventsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Filtered by Travel",Toast.LENGTH_SHORT).show();
        });


        clearButton.setOnClickListener(view -> {
            executeApiCall("",false);
            eventsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Filters cleared",Toast.LENGTH_SHORT).show();
        });

    }

    private void setViews(View v) {
        eventsAdapter = new EventsAdapter(getContext(), events, event -> {
            Intent intent;
            if (getUserId() == event.getOwner_id()) {
                intent = new Intent(getContext(), OwnEventActivity.class);
            } else {
                intent = new Intent(getContext(), EventActivity.class);
            }
            intent.putExtra("event", event);
            startActivity(intent);
        });

        RecyclerView rvEvents = v.findViewById(R.id.rv_events);
        rvEvents.setAdapter(eventsAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = v.findViewById(R.id.search_users_widget_events);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        bestButton = v.findViewById(R.id.best_star_icon);
        bestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeApiCall("", true);
            }
        });

        bestButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                executeApiCall("", b);
            }
        });

        educationButton = v.findViewById(R.id.category_education_event);
        sportButton = v.findViewById(R.id.category_sports_event);
        travelButton = v.findViewById(R.id.category_travel_event);
        concertButton = v.findViewById(R.id.category_concerts_event);
        clearButton = v.findViewById(R.id.category_clear_events);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        executeApiCall(s, false);
        return false;
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("userId", MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", null);
        return Integer.parseInt(id);
    }

    private void executeApiCall(String event, Boolean best) {
        APIClient apiClient = APIClient.getInstance(getContext());

        if (best) {
            apiClient.getBestEvents(new OpenEventsCallback<List<EventResponse>>() {
                @Override
                public void onResponseOpenEvents(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                    if (response.isSuccessful()) {
                        events.clear();
                        if (response.body() != null) {
                            events.addAll(response.body());
                            eventsAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailureOpenEvents() {

                }
            });
        } else {
            apiClient.searchEventsByString(event, new OpenEventsCallback<List<EventResponse>>() {
                @Override
                public void onResponseOpenEvents(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                    if (response.isSuccessful()) {
                        events.clear();
                        if (response.body() != null) {
                            events.addAll(response.body());
                            eventsAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailureOpenEvents() {

                }
            });
        }
    }

}