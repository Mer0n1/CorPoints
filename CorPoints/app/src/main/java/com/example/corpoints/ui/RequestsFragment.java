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
import com.example.corpoints.cserver.Server;

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

        AdapterRequests   = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        AcceptRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = -1;
                LinearLayout layout = main_layout.findViewById(R.id.ListBC);
                for (int j = 0; j < layout.getChildCount(); j++)
                    if (layout.getChildAt(j).findViewById(R.id.AcceptItem) == v)
                    {
                        layout.removeView(layout.getChildAt(j));
                        number = j;
                        break;
                    }
                ListView listView = main_layout.findViewById(R.id.ListRequests);
                AdapterRequests.remove(listView.getItemAtPosition(number).toString()); //delete request
                Server.ProtocolAcceptRequest(listView.getItemAtPosition(number).toString());
            }
        };
        RejectRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = -1;
                LinearLayout layout = main_layout.findViewById(R.id.ListBC);
                for (int j = 0; j < layout.getChildCount(); j++)
                    if (layout.getChildAt(j).findViewById(R.id.RejectItem) == v)
                    {
                        layout.removeView(layout.getChildAt(j));
                        number = j;
                        break;
                    }
                ListView listView = main_layout.findViewById(R.id.ListRequests);
                AdapterRequests.remove(listView.getItemAtPosition(number).toString()); //delete request
                Server.ProtocolRejectRequest(listView.getItemAtPosition(number).toString());
            }
        };

        View.OnClickListener ExitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupActivity)getActivity()).ExitPFragment();
            }
        };
        main_layout.findViewById(R.id.back_req).setOnClickListener(ExitListener);

        ((ListView)main_layout.findViewById(R.id.ListRequests)).setAdapter(AdapterRequests);

        return main_layout;
    }

    public void setRequests(String[] array) {
        AdapterRequests.clear();
        for (int j = 0; j < array.length; j++)
            AdapterRequests.add(array[j]);

        LinearLayout layout = main_layout.findViewById(R.id.ListBC);
        layout.removeAllViews(); //clear all views

        for (int j = 0; j < array.length; j++) {
            getLayoutInflater().inflate(R.layout.item_request_togroup, layout);
            layout.getChildAt(layout.getChildCount()-1).findViewById(R.id.AcceptItem).setOnClickListener(AcceptRequestListener);
            layout.getChildAt(layout.getChildCount()-1).findViewById(R.id.RejectItem).setOnClickListener(RejectRequestListener);
        }
    }
}
