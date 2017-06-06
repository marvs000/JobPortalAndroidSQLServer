package com.mobileapplication.mobileapplication.Portal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobileapplication.mobileapplication.R;
import com.mobileapplication.mobileapplication.util.GifImageView;
import com.mobileapplication.mobileapplication.util.ToastUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.jobPortal.Job;
import core.jobPortal.JobPortalService;


public class CvFragment extends Fragment implements View.OnClickListener {

    private Job jobApplied;

    private CvFragmentListener mListener;
    private ApplicationDetails mApplicationDetails = new ApplicationDetails();

    public CvFragment() {
    }

    private void setJobApplied(Job job){
        this.jobApplied = job;
    }

    public static CvFragment newInstance(Job jobApplied) {
        CvFragment fragment = new CvFragment();
        fragment.setJobApplied(jobApplied);
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

        View mView = inflater.inflate(R.layout.fragment_cv, container, false);

        TextView JobTitle = (TextView) mView.findViewById(R.id.cvJobTitle);
        JobTitle.setText(jobApplied.jobTitle);

        TextView JobDescription = (TextView) mView.findViewById(R.id.cvJobDescription);
        JobDescription.setText(jobApplied.jobDescription);

        TextView JobSalaryRange = (TextView) mView.findViewById(R.id.cvSalary);
        JobSalaryRange.setText(Integer.toString(jobApplied.minimumSalary) + " - " + Integer.toString(jobApplied.maximumSalary));

        Button AttachResumeButton = (Button) mView.findViewById(R.id.cvUploadResumeButton);
        AttachResumeButton.setOnClickListener(this);

        Button SubmitApplicationButton = (Button) mView.findViewById(R.id.cvSubmitApplication);
        SubmitApplicationButton.setOnClickListener(this);

        Button ReturnButton = (Button) mView.findViewById(R.id.cvReturnToPortal);
        ReturnButton.setOnClickListener(this);

        TextView JobQualification = (TextView) mView.findViewById(R.id.cvJobQualification);
        JobQualification.setText(jobApplied.qualification.qualification);

        return mView;
    }

    public void onClick(View v){

        switch(v.getId()){

            case(R.id.cvUploadResumeButton):
                /* upload resume */
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("*/*");
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a file to upload"),
                            0);
                }catch (ActivityNotFoundException ex){
                            ToastUtil.createToast(ex.getMessage(), getActivity());
                }

                break;

            case(R.id.cvSubmitApplication):
                if(validateForm()){

                    GifImageView spinner = (GifImageView) getView().findViewById(R.id.cvSpinner);
                    spinner.setGifImageResource(R.drawable.spinner);
                    spinner.setVisibility(View.VISIBLE);
                    View spinnerView = getView().findViewById(R.id.cvFormSpinner);
                    spinnerView.setVisibility(View.VISIBLE);

                    getView().findViewById(R.id.cvApplicationForm).setVisibility(View.GONE);

                    (new Apply()).execute(getApplicationDetails());
                }
                break;

            case(R.id.cvReturnToPortal):
                ((CvFragmentListener)getActivity()).onCvFormRequestReturn();
                break;

            default:
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                    if(resultCode == Activity.RESULT_OK) {
                        // Get the Uri of the selected file

                        try {

                            Uri uri = data.getData();
                            String path = getPath(this.getActivity().getApplicationContext(), uri);

                            if(path == null) {
                                throw new FileNotFoundException();
                            }

                            ((TextView) (getView().findViewById(R.id.cvResumeLabel))).setText(path);

                        } catch (FileNotFoundException ex){

                            ToastUtil.createToast("An error occurred while retrieving the resume path.", this.getActivity());
                            System.out.println("Could not find the file specified by the uri: "+  ex.getMessage());

                        } catch (IOException ex){

                            System.out.println("An error occured while trying to create a copy of the selected file: " + ex.getMessage());

                        }

                    }else{
                        ToastUtil.createToast("The activity failed to fetch the file path for the resume", this.getActivity());
                    }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri){
        if ("content".equalsIgnoreCase(uri.getScheme())) {

            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");

                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }

            } catch (Exception e) {
                System.out.println( "An error occurred while retrieving the file: " + e.getMessage());
            }finally {
                if(cursor!=null){
                    cursor.close();
                }
            }

        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {

            System.out.println("CONTENT FOUND: " + uri.getPath());
            return uri.getPath();

        }

        return null;
    }

    private boolean validateForm(){
        ApplicationDetails formValues = getApplicationDetails();
        boolean emptyOrNullValueFound = false;
        boolean isValidEmail = isValidEmail(formValues.email);
        boolean isValidFile = isValidFile(formValues.resumeFilepath);

        try {
            emptyOrNullValueFound = (formValues.firstname == null | formValues.firstname.length() == 0) ? true : emptyOrNullValueFound;
            emptyOrNullValueFound = (formValues.middlename == null | formValues.middlename.length() == 0) ? true : emptyOrNullValueFound;
            emptyOrNullValueFound = (formValues.lastname == null | formValues.firstname.length() == 0) ? true : emptyOrNullValueFound;
            emptyOrNullValueFound = (formValues.contactNumber == null) ? true : emptyOrNullValueFound;
            emptyOrNullValueFound = (formValues.civilStatus == null) ? true : emptyOrNullValueFound;

            String validationErrorMessage = "";

            if(!isValidEmail){
                validationErrorMessage += "Please check if your email address is valid\n";
            }

            if(!isValidFile){
                validationErrorMessage += "Please check if your resume is a valid pdf file\n";
            }

            if(emptyOrNullValueFound){
                validationErrorMessage += "Please check if all fields have been correctly entered.\n";
            }



            boolean isFormValid = isValidEmail&&isValidFile&&(!emptyOrNullValueFound);

            if(!isFormValid){
                ToastUtil.createToast(validationErrorMessage, getActivity());
            }

            return isFormValid;

        }catch (Exception ex){

            return false;
        }
    }

    private boolean isValidEmail(String email){

        if(email != null) {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }else{
            return false;
        }
    }

    private boolean isValidFile(String resumeFilepath){
        if(resumeFilepath != null) {
            return resumeFilepath.endsWith(".pdf");
        }else{
            return false;
        }
    }

    private ApplicationDetails getApplicationDetails(){

        this.mApplicationDetails.firstname = ((TextView) (getView().findViewById(R.id.cvFirstName))).getText().toString();
        this.mApplicationDetails.middlename = ((TextView) (getView().findViewById(R.id.cvMiddleName))).getText().toString();
        this.mApplicationDetails.lastname = ((TextView) (getView().findViewById(R.id.cvLastName))).getText().toString();
        this.mApplicationDetails.email = ((TextView) (getView().findViewById(R.id.cvEmail))).getText().toString();

        try {
            this.mApplicationDetails.contactNumber = Long.parseLong(((TextView) (getView().findViewById(R.id.cvContactNumber))).getText().toString());
        }catch (Exception ex){
            this.mApplicationDetails.contactNumber = null;
        }

        this.mApplicationDetails.vacancyID = this.jobApplied.jobID;
        this.mApplicationDetails.resumeFilepath = ((TextView) (getView().findViewById(R.id.cvResumeLabel))).getText().toString();

        try {
            RadioGroup civilStatusRadios = (RadioGroup) (getView().findViewById(R.id.cvCivilStatusRadioGroup));
            int checkedRadioId = civilStatusRadios.getCheckedRadioButtonId();
            this.mApplicationDetails.civilStatus = ((RadioButton) (getView().findViewById(checkedRadioId))).getText().toString();
        }catch (Exception ex){
            this.mApplicationDetails.civilStatus = null;
        }

        return this.mApplicationDetails;
    }

    private class ApplicationDetails{

        Long vacancyID = null;
        String firstname = null;
        String middlename = null;
        String lastname = null;
        String email = null;
        Long contactNumber = null;
        String resumeFilepath = null;
        String civilStatus = null;

    }

    private class Apply extends AsyncTask<ApplicationDetails, Void, Boolean>{

        @Override
        protected Boolean doInBackground(ApplicationDetails... Params){

            ApplicationDetails application = Params[0];
            try {
                JobPortalService.ApplyForJob(application.vacancyID, application.firstname, application.middlename, application.lastname, application.email, application.contactNumber, application.civilStatus, new File(application.resumeFilepath));
            }catch (Exception ex){
                //ToastUtil.createToast(ex.getMessage(), getActivity());
                System.out.println(ex.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected  void onPostExecute(Boolean applicationSuccessful){
            /* update UI in this function */
            View spinnerView = getView().findViewById(R.id.cvFormSpinner);
            spinnerView.setVisibility(View.GONE);

            TextView message = (TextView) getView().findViewById(R.id.cvFormFinalMessage);

            if(applicationSuccessful){
                message.setText("Thank you for submitting your application!");
            }else{
                message.setText("Oops! An error occured during the submission of your application. Please try again.");
            }

            getView().findViewById(R.id.cvApplicationFormMessage).setVisibility(View.VISIBLE);

            return;
        }

    }

    public interface CvFragmentListener{
        void onCvFormRequestReturn();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CvFragmentListener) {
            mListener = (CvFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CvFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
