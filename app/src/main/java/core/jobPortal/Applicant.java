package core.jobPortal;

import java.io.File;
import java.sql.Blob;
import java.util.Objects;
import java.util.List;
/**
 * Created by ernestepistola on 3/17/17.
 */

@Deprecated
public class Applicant {

    public int applciant_id;
    public int job_id;

    public String username;
    public String password;

    public String firstname;
    public String lastname;
    public File resume;

    /* Based on class diagram in documentation */

    public Applicant(String username, String password, String firstname, String lastname, File resume) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.resume = resume;
    }

}
