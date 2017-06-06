package com.mobileapplication.mobileapplication.Portal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileapplication.mobileapplication.MainActivity;
import com.mobileapplication.mobileapplication.R;
import com.mobileapplication.mobileapplication.util.GifImageView;
import com.mobileapplication.mobileapplication.util.ToastUtil;

import java.util.ArrayList;

import core.jobPortal.Job;
import core.jobPortal.JobPortalService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobSearchFragment.JobSearchFragmentListener} interface
 * to handle interaction events.
 * Use the {@link JobSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSearchFragment extends Fragment implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private boolean searchJustFinished = true;//flag used for clearing the search field when performing a new search

    private JobSearchFragmentListener mListener;

    public JobSearchFragment() {
        // Required empty public constructor
    }

    private class searchListViewAdapter extends BaseAdapter{

        Context mContext;
        ArrayList<Job> jobs;

        public searchListViewAdapter(Context context, ArrayList<Job> Jobs) {
            mContext = context;
            jobs = Jobs;
        }

        @Override
        public int getCount() {
            return jobs.size();
        }

        @Override
        public Object getItem(int position) {
            return jobs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.job_search_item, null);
            }
            else {
                view = convertView;
            }

            TextView jobTitle = (TextView) view.findViewById(R.id.jobTitle);
            TextView jobDescription = (TextView) view.findViewById(R.id.jobDescription);
            TextView salary = (TextView) view.findViewById(R.id.salary);
            TextView jobQualification = (TextView) view.findViewById(R.id.jobQualification);

            jobTitle.setText(jobs.get(position).jobTitle);
            jobDescription.setText(jobs.get(position).jobDescription);
            jobQualification.setText(jobs.get(position).qualification.qualification);

            String minSal = Integer.toString(jobs.get(position).minimumSalary).toString();
            String maxSal = Integer.toString(jobs.get(position).maximumSalary).toString();
            salary.setText(minSal + " - " + maxSal);

            Button applyButton = (Button) view.findViewById(R.id.applyButton);
            applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Job applyForThisJob = (Job) getItem(position);

                    MainActivity _activity = (MainActivity) getActivity();

                    /*
                    if (mListener != null) {
                        mListener.onPortalInteraction(applyForThisJob);
                    }else{
                        //ToastUtil.createToast("No Context", getActivity());
                    }
                    */

                    try{
                        _activity.onPortalInteraction(applyForThisJob);
                    }catch (Exception e){
                        ToastUtil.createToast(e.getMessage(), getActivity());
                    }


                }
            });

            return view;
        }

    }

    public void onClick(View v){

        if(v.getId() == R.id.searchJobsButton){
            search();

            try {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception ex){
                System.out.println("Unable to close input: " + ex.getMessage());
            }
        }else if(v.getId() == R.id.searchField && searchJustFinished){
            searchJustFinished = false;
            ((TextView) v).setText("");
        }

    }

    public void search(){
        String searchQuery = ((TextView)(getView().findViewById(R.id.searchField))).getText().toString();

        if(searchQuery.length() == 0){
            searchQuery = ".*";
            ToastUtil.createToast("Searching for all job vacancies", getActivity());
        }else{
            ToastUtil.createToast("Search for job vacancies related to \"" + searchQuery + "\"", getActivity());
        }

        View searchParamters = getView().findViewById(R.id.searchParameters);
        searchParamters.setVisibility(View.GONE);

        ListView searchResults = (ListView) getView().findViewById(R.id.searchResults);
        searchResults.setVisibility(View.GONE);

        GifImageView spinner = (GifImageView) getView().findViewById(R.id.spinner);
        spinner.setGifImageResource(R.drawable.spinner);
        spinner.setVisibility(View.VISIBLE);

        View searchLogo = getView().findViewById(R.id.searchLogo);
        searchLogo.setVisibility(View.GONE);

        new Search().execute(searchQuery);

    }

    private void updateJobSearchList(ArrayList<Job> jobs){
        ListView searchResults = (ListView) getView().findViewById(R.id.searchResults);

        searchListViewAdapter adapter = new searchListViewAdapter(getActivity().getApplicationContext(), jobs);
        searchResults.setAdapter(adapter);

        View searchParamters = getView().findViewById(R.id.searchParameters);
        searchParamters.setVisibility(View.VISIBLE);

        searchResults = (ListView) getView().findViewById(R.id.searchResults);
        searchResults.setVisibility(View.VISIBLE);

        GifImageView spinner = (GifImageView) getView().findViewById(R.id.spinner);
        spinner.setGifImageResource(R.drawable.spinner);
        spinner.setVisibility(View.GONE);

        if(jobs.size() > 0) {
            ToastUtil.createToast("A total of " + Integer.toString(jobs.size()) + " vacancies were found", getActivity());
        }else{
            ToastUtil.createToast("Sorry, no job vacancies were found.", getActivity());

            searchResults = (ListView) getView().findViewById(R.id.searchResults);
            searchResults.setVisibility(View.GONE);

            View searchLogo = getView().findViewById(R.id.searchLogo);
            searchLogo.setVisibility(View.VISIBLE);
        }
        searchJustFinished = true;

    }

    private class Search extends AsyncTask<String, Integer, ArrayList<Job>>{

        @Override
        protected ArrayList<Job> doInBackground(String... params) {
            return (ArrayList) JobPortalService.searchJobs(params[0]);
        }

        @Override
        /* Update UI inside this function */
        protected void onPostExecute(ArrayList<Job> vacancies){
            updateJobSearchList(vacancies);
        }

    }

    public static JobSearchFragment newInstance() {
        JobSearchFragment fragment = new JobSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView =  inflater.inflate(R.layout.fragment_portal, container, false);

        Button searchButton = (Button) mView.findViewById(R.id.searchJobsButton);
        searchButton.setOnClickListener(this);

        TextView searchField = (TextView) mView.findViewById(R.id.searchField);
        searchField.setOnClickListener(this);

        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof JobSearchFragmentListener) {
            mListener = (JobSearchFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PortalFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onGlobalLayout(){
        /* execute search function on load and remove self as listener */
        search();
        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public interface JobSearchFragmentListener {
        void onPortalInteraction(Job jobClicked);
    }


}
