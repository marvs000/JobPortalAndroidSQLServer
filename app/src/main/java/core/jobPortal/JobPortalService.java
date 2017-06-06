package core.jobPortal;

import android.util.Base64;

import org.apache.commons.codec.net.URLCodec;
import core.database.DBConnection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ernestepistola on 2/4/17.
 */
public class JobPortalService {


    public static List<Job> searchVacancies(){
        return searchJobs(".*");
    }

    public static List<Job> searchJobs(String query){

        //String _query = "/" + query + "/g";
        String _query = query;

        String sql = "SELECT * FROM HR1_JobVacancy WHERE vacancyStatus = 'Ongoing'";
        ArrayList<Job> vacancies= new ArrayList<Job>();

        try {

            DBConnection.openConnection();
            Connection conn = DBConnection.getConnection();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                String vacancyDescription = rs.getString("description");

                long jobId = rs.getLong("jobID");

                System.out.println("Vacancy found: ID = " + Long.toString(jobId) + " - " + vacancyDescription);

                try{
                    /* fetch job from database */
                    Job job = searchJob(jobId);

                    if(vacancyDescription.toLowerCase().contains(_query.toLowerCase()) || job.jobDescription.matches(_query) || job.jobTitle.toLowerCase().contains(_query.toLowerCase()) || job.jobTitle.matches(_query) || job.jobDescription.toLowerCase().contains(_query.toLowerCase())) {
                        System.out.println("Adding job to list of vacancies: " + Long.toString(jobId));
                        System.out.println(job.jobDescription);
                        vacancies.add(job);
                    }

                }catch (Exception ex){
                    System.out.println("Failed to add job " + Long.toString(jobId) +  " to vacancies list: " + ex.getMessage());
                }

            }

            DBConnection.closeConnection();

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        System.out.println("A total of " + vacancies.size() + " vacancies were found");

        return vacancies;
    }

    private static Job searchJob(long job_id) throws SQLException{
        /* connection must be open when this function is called */
        Job result = null;

        String sql = "SELECT * FROM HR_Jobs WHERE jobID = " + Long.toString(job_id);

        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            JobQualification qualification = new JobQualification(job_id, rs.getString("jobQualification"));
            result = new Job(rs.getInt("jobID"), rs.getString("jobTitle"), rs.getString("jobDescription"), rs.getInt("minimumSalary"), rs.getInt("maximumSalary"), qualification);
        }

        return result;
    }

    private static JobQualification searchJobQualification(long job_id) throws SQLException{
        /* connection must be open when this function is called */
        JobQualification result = null;

        String sql = "SELECT * FROM HR1_Qualification WHERE jobID = " + Long.toString(job_id);

        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            result = new JobQualification(rs.getLong("jobID"), rs.getString("qualification"));
        }

        return result;
    }

    public static void ApplyForJob(long vacancyID, String firstname, String middlename, String lastname, String email, long contactNumber, String civilStatus, File resume) throws JobPortalApplicationFailedException{

        Boolean isVacancyValid = false;

        try {

            DBConnection.openConnection();

            /* Check if vacancy exists in database */
            System.out.println("Checking availability of vacancy " + vacancyID);
            String vacancyQuery = "SELECT * FROM HR1_JobVacancy WHERE jobID=?";
            PreparedStatement vacancyQueryStmt = DBConnection.getConnection().prepareStatement(vacancyQuery);
            vacancyQueryStmt.setLong(1, vacancyID);

            ResultSet _rs = vacancyQueryStmt.executeQuery();
            Long _vacancyID = null;

            while(_rs.next()){
                _vacancyID = _rs.getLong("vacancyID");
                isVacancyValid = true;
            }

            //DBConnection.closeConnection();

            if(isVacancyValid && _vacancyID != null) {

                //DBConnection.openConnection();

                /* update HR1_OnlineApplicant */
                String sql = "INSERT INTO HR1_OnlineApplicant (vacancyID, firstname, middlename, lastname, email, contactNumber, applicationStatus, civilStatus) VALUES (?,?,?,?,?,?,?,?)";

                PreparedStatement createOnlineApplicant = DBConnection.getConnection().prepareStatement(sql);

                createOnlineApplicant.setLong(1, _vacancyID);
                createOnlineApplicant.setString(2, firstname);
                createOnlineApplicant.setString(3, middlename);
                createOnlineApplicant.setString(4, lastname);
                createOnlineApplicant.setString(5, email);
                createOnlineApplicant.setLong(6, contactNumber);
                createOnlineApplicant.setString(7, "Pending");
                createOnlineApplicant.setString(8, civilStatus);
                createOnlineApplicant.executeUpdate();

                /* Fetch newly created onlineApplicantID */
                sql = "SELECT onlineApplicantID FROM HR1_OnlineApplicant WHERE vacancyID=? AND firstname=? AND middlename=? AND lastname=? AND email=? AND contactNumber=?";

                PreparedStatement queryApplicantID = DBConnection.getConnection().prepareStatement(sql);

                queryApplicantID.setLong(1, _vacancyID);
                queryApplicantID.setString(2, firstname);
                queryApplicantID.setString(3, middlename);
                queryApplicantID.setString(4, lastname);
                queryApplicantID.setString(5, email);
                queryApplicantID.setLong(6, contactNumber);
                ResultSet rs = queryApplicantID.executeQuery();

                Long onlineApplicantID = null;

                while (rs.next()) {
                    onlineApplicantID = rs.getLong("onlineApplicantID");
                }

                try{
                    sql="UPDATE HR1_OnlineApplicant SET files=?, resume=?, dateAdded=? WHERE onlineApplicantID=?";
                    PreparedStatement updateResume = DBConnection.getConnection().prepareStatement(sql);
                    FileInputStream fis = new FileInputStream(resume);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    fis.close();
                    updateResume.setBytes(1, buffer);
                    updateResume.setString(2, resume.getName());
                    updateResume.setDate(3, new Date(System.currentTimeMillis()));
                    updateResume.setLong(4, onlineApplicantID);
                    updateResume.executeUpdate();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                DBConnection.closeConnection();

            }else{
                /* Invalid vacancy ID */
                throw new JobPortalApplicationFailedException("There are no job vacancies that match the vacancy ID provided.");
            }

        } catch (SQLException ex) {
            throw new JobPortalApplicationFailedException("Failed to apply for the job vacancy due to an SQL Exception: " + ex.getMessage());
        }

    }

    public static List<String> FetchApplicantData(){
        ArrayList<String> result = new ArrayList<String>();

        DBConnection.openConnection();

        try{

                String _sql = "SELECT * FROM HR1_OnlineApplicant";
                PreparedStatement _stmt = DBConnection.getConnection().prepareStatement(_sql);
                ResultSet applicantInfo = _stmt.executeQuery();

                while(applicantInfo.next()){
                    String entry = "\n";

                    String applicantName = "NAME: " + applicantInfo.getString("firstname") + " " + applicantInfo.getString("middlename") + " " + applicantInfo.getString("lastname") + "\n";
                    String email =  "EMAIL: " + applicantInfo.getString("email") + "\n";
                    String contactNumber =  "CONTACT NUMBER: " + Long.toString(applicantInfo.getLong("contactNumber")) + "\n";
                    String civilStatus =  "CIVIL STATUS: " + applicantInfo.getString("civilStatus") + "\n";
                    String applicationStatus = "APPLICATION STATUS: " + applicantInfo.getString("applicationStatus") + "\n";
                    entry +=applicantName + email + contactNumber + civilStatus + applicationStatus;

                    long vacancyID = applicantInfo.getLong("vacancyID");

                    String __sql = "SELECT * FROM HR1_JobVacancy WHERE vacancyID=?";
                    PreparedStatement __stmt = DBConnection.getConnection().prepareStatement(__sql);
                    __stmt.setLong(1, vacancyID);
                    ResultSet vacancyInfo = __stmt.executeQuery();

                    while(vacancyInfo.next()){
                        String space = "*";
                        String vacancyIDstr = space + "VACANCY ID: " + Long.toString(vacancyInfo.getLong("vacancyID")) + "\n";
                        entry += vacancyIDstr;

                        long jobID = vacancyInfo.getLong("jobID");
                        Job jobInfo = searchJob(jobID);

                        entry += space + "JOB TITLE: " + jobInfo.jobTitle + "\n";
                        entry += space + "JOB DESCRIPTION: " + jobInfo.jobDescription + "\n";

                    }

                    result.add(entry + "\n");
                }

        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }

        for(String entry : result){
            System.out.println(entry);
        }

        DBConnection.closeConnection();

        return result;
    }

    public static void main(String[] args){
        /* used for testing database connectivity */
        //searchVacancies();
        //searchJobs("fourth");
        //FetchApplicantData();
    }


}
