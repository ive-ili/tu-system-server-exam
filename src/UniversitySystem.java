import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class UniversitySystem {

    private final ArrayList<User> users;

    UniversitySystem() {
        this.users = new ArrayList<>();
        this.createAdminUser();
    }

    public void loginUser(String username, String password, Socket clientSocket) throws IOException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("Logged in successfully!");
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
        System.out.println("Invalid username or password!");
        System.out.println("Please try again!");
    }


    public void adminMenu(Administrator admin, Socket clientSocket) throws IOException {
        System.out.println("Welcome " + admin.getUsername() + "!");
        System.out.println("Creating new user...");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        System.out.println("Please enter user type (1 - Student, 2 - Teacher)");
        int userType = Integer.parseInt(scanner.nextLine());
        switch (userType) {
            case 1:
                if (createStudentUser(scanner)) {
                    System.out.println("Student created successfully!");
                } else {
                    System.out.println("Student creation failed! Please try again!");
                    createStudentUser(scanner);
                }
                break;
            case 2:
                if (createTeacherUser(scanner)) {
                    System.out.println("Teacher created successfully!");
                } else {
                    System.out.println("Teacher creation failed! Please try again!");
                    createTeacherUser(scanner);
                }
                break;
        }
        System.out.println("Thank you and goodbye + " + admin.getUsername() + "!");
    }

    private boolean createStudentUser(Scanner scanner) {
        System.out.println("Please enter username:");
        String studentUsername = scanner.nextLine();
        System.out.println("Please enter password:");
        String studentPassword = scanner.nextLine();
        System.out.println("Please enter faculty number:");
        String facultyNumber = scanner.nextLine();
        System.out.println("Please enter EGN:");
        String egn = scanner.nextLine();
        if (validateStudentCreation(facultyNumber, egn)) {
            Student student = new Student(studentUsername, studentPassword, facultyNumber, egn);
            users.add(student);
            return true;
        }
        return false;
    }

    private boolean createTeacherUser(Scanner scanner) {
        System.out.println("Please enter username:");
        String teacherUsername = scanner.nextLine();
        System.out.println("Please enter password:");
        String teacherPassword = scanner.nextLine();
        System.out.println("Please enter email:");
        String email = scanner.nextLine();
        if (validateTeacherCreation(teacherPassword, email)) {
            Teacher teacher = new Teacher(teacherUsername, teacherPassword, email);
            users.add(teacher);
            return true;
        }
        return false;
    }

    boolean validateStudentCreation(String facultyNumber, String egn) {
        if (facultyNumber.length() != 9) {
            System.out.println("Faculty number must be 9 digits long!");
            return false;
        }
        if (egn.length() != 10) {
            System.out.println("EGN must be 10 digits long!");
            return false;
        }
        return true;
    }

    boolean validateTeacherCreation(String teacherPassword, String email) {
        String emailRegex = "[a-z]+@tu-sofia\\.bg";
        if (!email.matches(emailRegex)) {
            System.out.println("Invalid email!");
            return false;
        }
        if (teacherPassword.length() < 5) {
            System.out.println("Password must be at least 5 characters long!");
            return false;
        }
        return true;
    }

    private void teacherMenu(Teacher teacher, Socket clientSocket) throws IOException {
        System.out.println("Welcome " + teacher.getUsername() + "!");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        System.out.println("Adding new grade...");
        if (addStudentGrade(teacher, scanner)) {
            System.out.println("Grade added successfully!");
        } else {
            System.out.println("Grade addition failed! Please try again!");
            addStudentGrade(teacher, scanner);
        }
        System.out.println("Thank you and goodbye + " + teacher.getUsername() + "!");
    }

    private boolean addStudentGrade(Teacher teacher, Scanner scanner) {

        System.out.println("Please enter faculty number:");
        String facultyNumber = scanner.nextLine();
        System.out.println("Please enter grade:");
        double grade = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Please enter course:");
        String course = scanner.nextLine();
        System.out.println("Please enter year:");
        int year = scanner.nextInt();
        scanner.nextLine();
        Student student = findStudentByFacultyNumber(facultyNumber);
        if (student == null) {
            System.out.println("Student not found!");
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
        System.out.println("Welcome " + student.getUsername() + "!");
        String grades = getStudentGrades(student);
        System.out.println(grades);
        System.out.println("Thank you and goodbye + " + student.getUsername() + "!");
    }

    private String getStudentGrades(Student student) {
        System.out.println("Your grades are:");
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
