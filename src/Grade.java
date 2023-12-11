public class Grade {

    private String courseName;
    private double grade;
    private int year;

    public Grade(String courseName, double grade, int year) {
        this.courseName = courseName;
        this.grade = grade;
        this.year = year;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getGrade() {
        return grade;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "courseName='" + courseName + '\'' +
                ", grade=" + grade +
                ", year=" + year +
                '}';
    }
}
