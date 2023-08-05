package com.example.corpoints.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.corpoints.R;
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.restful.models.Group;
import com.example.restful.models.RequestInGroup;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class RequestsFragment extends Fragment {
    private FrameLayout main_layout;
    private ArrayAdapter<String> AdapterRequests;

    private View.OnClickListener AcceptRequestListener;
    private View.OnClickListener RejectRequestListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout) inflater.inflate(R.layout.fragment_requests_group, container, false);
        List<RequestInGroup> requests = DataCash.getRequestsFromGroup(((GroupActivity)getActivity()).getGroup());

        AcceptRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = ChooseAndRemoveItem(v, R.id.AcceptItem);

                MainAPI.SendResultRequest(requests.get(number), true);
            }
        };
        RejectRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = ChooseAndRemoveItem(v, R.id.RejectItem);

                MainAPI.SendResultRequest(requests.get(number), false);
            }
        };

        View.OnClickListener ExitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupActivity)getActivity()).ExitPFragment();
            }
        };

        initializeAdapterRequests(requests);
        main_layout.findViewById(R.id.back_req).setOnClickListener(ExitListener);


        ((ListView)main_layout.findViewById(R.id.ListRequests)).setAdapter(AdapterRequests);

        return main_layout;
    }

    private void initializeAdapterRequests(List<RequestInGroup> requests) {
        AdapterRequests = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        AdapterRequests.clear();
        for (int j = 0; j < requests.size(); j++)
            AdapterRequests.add(requests.get(j).getOwner().getUsername());

        LinearLayout layout = main_layout.findViewById(R.id.ListBC);
        layout.removeAllViews(); //clear all views

        for (int j = 0; j < requests.size(); j++) {
            getLayoutInflater().inflate(R.layout.item_request_togroup, layout);
            layout.getChildAt(layout.getChildCount()-1).findViewById(R.id.AcceptItem).setOnClickListener(AcceptRequestListener);
            layout.getChildAt(layout.getChildCount()-1).findViewById(R.id.RejectItem).setOnClickListener(RejectRequestListener);
        }
    }

    private int ChooseAndRemoveItem(View v, int id) {
        int number = -1;
        LinearLayout layout = main_layout.findViewById(R.id.ListBC);
        for (int j = 0; j < layout.getChildCount(); j++)
            if (layout.getChildAt(j).findViewById(id) == v)
            {
                layout.removeView(layout.getChildAt(j));
                number = j;
                break;
            }
        ListView listView = main_layout.findViewById(R.id.ListRequests);
        AdapterRequests.remove(listView.getItemAtPosition(number).toString()); //delete request

        return number;
    }
}
