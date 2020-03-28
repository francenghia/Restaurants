package com.example.restaurants.ProfileActivities;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.common.Restaurateur;
import com.example.restaurants.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagerProfileFragment extends Fragment {
    private String addr, descr, mail, phone, time;
    private OnFragmentInteractionListener mListener;

    public PagerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager_profile, container, false);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child(RESTAURATEUR_INFO + "/" + ROOT_UID).child("info");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Restaurateur restaurateur = dataSnapshot.getValue(Restaurateur.class);

                    addr = restaurateur.getAddr();
                    descr = restaurateur.getCuisine();
                    mail = restaurateur.getMail();
                    phone = restaurateur.getPhone();
                    time = restaurateur.getOpeningTime();

                    ((TextView)view.findViewById(R.id.address)).setText(addr);
                    ((TextView)view.findViewById(R.id.description)).setText(descr);
                    ((TextView)view.findViewById(R.id.mail)).setText(mail);
                    ((TextView)view.findViewById(R.id.phone2)).setText(phone);
                    ((TextView)view.findViewById(R.id.time_text)).setText(time);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("DAILY OFFER", "Failed to read value.", error.toException());
            }
        });
        
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
