package com.mobileapplication.mobileapplication.Portal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapplication.mobileapplication.R;
import com.mobileapplication.mobileapplication.util.GifImageView;

import java.util.List;

import core.jobPortal.JobPortalService;


public class ApplicationsPreviewFragment extends Fragment {

    public ApplicationsPreviewFragment() {
        // Required empty public constructor
    }

    public static ApplicationsPreviewFragment newInstance() {
        ApplicationsPreviewFragment fragment = new ApplicationsPreviewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_applications_preview, container, false);

        GifImageView spinner = (GifImageView) mView.findViewById(R.id.viewApplicantsSpinner);
        spinner.setGifImageResource(R.drawable.spinner);
        spinner.setVisibility(View.VISIBLE);

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        new Fetch().execute();
    }

    private class Fetch extends AsyncTask<Void, Void, List<String>>{

        @Override
        protected List<String> doInBackground(Void... params){
            return JobPortalService.FetchApplicantData();
        }

        @Override
        protected void onPostExecute(List<String> result){

            try{

                getView().findViewById(R.id.viewApplicantsSpinner).setVisibility(View.GONE);

                TextView content = (TextView) getView().findViewById(R.id.content);
                content.setText("");

                for(String entry : result){
                    content.setText(content.getText() + entry);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
