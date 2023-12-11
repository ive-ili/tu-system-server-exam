import java.util.ArrayList;

public class Student extends User {

    private final ArrayList<Grade> studentGrades;
    private String facultyNumber;
    private String egn;

    public Student(String username, String password, String facultyNumber, String egn) {
        super(username, password);
        studentGrades = new ArrayList<>();
        this.facultyNumber = facultyNumber;
        this.egn = egn;
    }

    @Override
    public UserType getUserType() {
        return UserType.STUDENT;
    }

    public String getFacultyNumber() {
        return facultyNumber;
    }

    public String getEgn() {
        return egn;
    }

    public ArrayList<Grade> getStudentGrades() {
        synchronized (studentGrades) {
            return studentGrades;
        }

    }

    @Override
    public String toString() {
        return "Student{" + username + " studentGrades=" + studentGrades + ", facultyNumber=" + facultyNumber + ", egn=" + egn + '}';
    }
}
