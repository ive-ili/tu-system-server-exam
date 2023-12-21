import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class UniversitySystem {

    private final ArrayList<User> users;
    private PrintStream printOutStream;

    UniversitySystem() {
        this.users = new ArrayList<>();
        this.createAdminUser();
    }

    public void loginUser(String username, String password, Socket clientSocket) throws IOException {
        printOutStream = new PrintStream(clientSocket.getOutputStream());
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                printOutStream.println("Logged in successfully!");
                switch (user.getUserType()) {
                    case STUDENT:
                        Student student = (Student) user;
                        studentMenu(student);
                        break;
                    case TEACHER:
                        Teacher teacher = (Teacher) user;
                        teacherMenu(teacher, clientSocket);
                        break;
                    case ADMINISTRATOR:
                        Administrator admin = (Administrator) user;
                        adminMenu(admin, clientSocket);
                        break;
                }
                return;
            }
        }
        printOutStream.println("Invalid username or password!");
        printOutStream.println("Please try again!");
    }


    public void adminMenu(Administrator admin, Socket clientSocket) throws IOException {
        printOutStream.println("Welcome " + admin.getUsername() + "!");
        printOutStream.println("Creating new user...");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        printOutStream.println("Please enter user type (1 - Student, 2 - Teacher)");
        int userType = Integer.parseInt(scanner.nextLine());
        switch (userType) {
            case 1:
                if (createStudentUser(scanner)) {
                    printOutStream.println("Student created successfully!");
                } else {
                    printOutStream.println("Student creation failed! Please try again!");
                    createStudentUser(scanner);
                }
                break;
            case 2:
                if (createTeacherUser(scanner)) {
                    printOutStream.println("Teacher created successfully!");
                } else {
                    printOutStream.println("Teacher creation failed! Please try again!");
                    createTeacherUser(scanner);
                }
                break;
        }
        printOutStream.println("Thank you and goodbye + " + admin.getUsername() + "!");
    }

    private boolean createStudentUser(Scanner scanner) {
        printOutStream.println("Please enter username:");
        String studentUsername = scanner.nextLine();
        printOutStream.println("Please enter password:");
        String studentPassword = scanner.nextLine();
        printOutStream.println("Please enter faculty number:");
        String facultyNumber = scanner.nextLine();
        printOutStream.println("Please enter EGN:");
        String egn = scanner.nextLine();
        if (validateStudentCreation(facultyNumber, egn)) {
            Student student = new Student(studentUsername, studentPassword, facultyNumber, egn);
            users.add(student);
            return true;
        }
        return false;
    }

    private boolean createTeacherUser(Scanner scanner) {
        printOutStream.println("Please enter username:");
        String teacherUsername = scanner.nextLine();
        printOutStream.println("Please enter password:");
        String teacherPassword = scanner.nextLine();
        printOutStream.println("Please enter email:");
        String email = scanner.nextLine();
        if (validateTeacherCreation(teacherPassword, email)) {
            Teacher teacher = new Teacher(teacherUsername, teacherPassword, email);
            users.add(teacher);
            return true;
        }
        return false;
    }

    boolean validateStudentCreation(String facultyNumber, String egn) {
        String facultyNumberRegex = "[1-9]{9}";
        String egnRegex = "[0-9]{10}";
        if (!facultyNumber.matches(facultyNumberRegex)) {
            printOutStream.println("Faculty number must be 9 digits long!");
            return false;
        }
        if (!egn.matches(egnRegex)) {
            printOutStream.println("EGN must be 10 digits long!");
            return false;
        }
        return true;
    }

    boolean validateTeacherCreation(String teacherPassword, String email) {
        String emailRegex = "[a-z]+@tu-sofia\\.bg";
        String passwordRegex = "\\S{5,}";
        if (!email.matches(emailRegex)) {
            printOutStream.println("Invalid email!");
            return false;
        }
        if (!teacherPassword.matches(passwordRegex)) {
            printOutStream.println("Password must be at least 5 characters long!");
            return false;
        }
        return true;
    }

    private void teacherMenu(Teacher teacher, Socket clientSocket) throws IOException {
        printOutStream.println("Welcome " + teacher.getUsername() + "!");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        printOutStream.println("Adding new grade...");
        if (addStudentGrade(teacher, scanner)) {
            printOutStream.println("Grade added successfully!");
        } else {
            printOutStream.println("Grade addition failed! Please try again!");
            addStudentGrade(teacher, scanner);
        }
        printOutStream.println("Thank you and goodbye + " + teacher.getUsername() + "!");
    }

    private boolean addStudentGrade(Teacher teacher, Scanner scanner) {

        printOutStream.println("Please enter faculty number:");
        String facultyNumber = scanner.nextLine();
        printOutStream.println("Please enter grade:");
        double grade = scanner.nextDouble();
        scanner.nextLine();
        printOutStream.println("Please enter course:");
        String course = scanner.nextLine();
        printOutStream.println("Please enter year:");
        int year = scanner.nextInt();
        scanner.nextLine();
        Student student = findStudentByFacultyNumber(facultyNumber);
        if (student == null) {
            printOutStream.println("Student not found!");
            return false;
        } else {
            Grade newGrade = new Grade(course, grade, year);
            teacher.addGrade(student, newGrade);
            return true;
        }
    }

    private Student findStudentByFacultyNumber(String facultyNumber) {
        for (User user : users) {
            if (user.getUserType() == UserType.STUDENT) {
                Student student = (Student) user;
                if (student.getFacultyNumber().equals(facultyNumber)) {
                    return student;
                }
            }
        }
        return null;
    }

    private void studentMenu(Student student) {
        printOutStream.println("Welcome " + student.getUsername() + "!");
        String grades = getStudentGrades(student);
        printOutStream.println(grades);
        printOutStream.println("Thank you and goodbye + " + student.getUsername() + "!");
    }

    private String getStudentGrades(Student student) {
        printOutStream.println("Your grades are:");
        ArrayList<Grade> studentGrades = student.getStudentGrades();
        studentGrades.sort((gradeOne, gradeTwo) -> {
            if (gradeOne.getYear() == gradeTwo.getYear()) {
                return gradeOne.getCourseName().compareTo(gradeTwo.getCourseName());
            } else {
                return gradeOne.getYear() - gradeTwo.getYear();
            }
        });
        StringBuilder grades = new StringBuilder();
        for (Grade grade : studentGrades) {
            grades.append(grade.getCourseName()).append(" ").append(grade.getGrade()).append(" ").append(grade.getYear()).append("\n");
        }
        return grades.toString();
    }

    private void createAdminUser() {
        Administrator admin = new Administrator("admin", "admin");
        users.add(admin);
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
