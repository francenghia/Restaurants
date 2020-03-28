package com.example.restaurants.ProfileActivities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.restaurants.HomeActivities.HomeFragment;
import com.example.restaurants.R;
import com.example.restaurants.Startup.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.common.Shared.Address;
import static com.example.common.Shared.Description;
import static com.example.common.Shared.Mail;
import static com.example.common.Shared.Name;
import static com.example.common.Shared.Phone;
import static com.example.common.Shared.Photo;
import static com.example.common.Shared.Time;
import static com.example.common.Shared.ROOT_UID;
/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private String name, addr, descr, mail, phone, photoUri, time;
    private OnFragmentInteractionListener mListener;
    private PagerAdapter pagerAdapter;
    private TabLayout tab;
    private ViewPager viewPager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_profile, container, false);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.edit_profile:
                Intent editProfile = new Intent(getContext(), UpdateProfileActivity.class);

                editProfile.putExtra(Name, name);
                editProfile.putExtra(Description, descr);
                editProfile.putExtra(Address, addr);
                editProfile.putExtra(Mail, mail);
                editProfile.putExtra(Phone, phone);
                editProfile.putExtra(Photo, photoUri);
                editProfile.putExtra(Time, time);

                startActivity(editProfile);
                return true;

            case R.id.edit_password:
                Intent editPsw = new Intent(getContext(), UpdatePasswordActivity.class);
                startActivity(editPsw);
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                ROOT_UID = "";

                Intent mainActivity = new Intent(getContext(), MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        if (context instanceof HomeFragment.OnFragmentInteractionListener) {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class PagerAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new PagerProfileFragment();
            case 1:
                return new RatingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}