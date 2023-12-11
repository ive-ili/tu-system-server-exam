import java.util.ArrayList;

public class Teacher extends User {

    private String email;

    public Teacher(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }

    @Override
    public UserType getUserType() {
        return UserType.TEACHER;
    }

    public void addGrade(Student student, Grade newGrade) {
        ArrayList<Grade> studentGrades = student.getStudentGrades();
        synchronized (studentGrades) {
            studentGrades.add(newGrade);
        }
    }

    @Override
    public String toString() {
        return "Teacher{" + username + "email=" + email + '}';

    }
}
