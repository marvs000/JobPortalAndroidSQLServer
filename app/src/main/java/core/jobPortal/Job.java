package core.jobPortal;

/**
 * Created by ernestepistola on 3/17/17.
 */
public class Job {

    public long jobID;
    public String jobTitle;
    public String jobDescription;
    public int minimumSalary;
    public int maximumSalary;
    int requiredQuantity;
    int nextJob;
    public JobQualification qualification;

    public Job(int jobID, String jobTitle, String jobDescription, int minimumSalary, int maximumSalary, JobQualification qualification) {
        this.jobID = jobID;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.minimumSalary = minimumSalary;
        this.maximumSalary = maximumSalary;
        this.qualification = qualification;
    }
}
