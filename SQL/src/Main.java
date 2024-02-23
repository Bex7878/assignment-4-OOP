import java.sql.*;
import java.util.Scanner;

interface StudentFactory {
    Student createStudent(String name, String surname, int age, String favoriteSubject);
}

class Student {
    private String name;
    private String surname;
    private int age;
    private String favoriteSubject;

    public Student(String name, String surname, int age, String favoriteSubject) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.favoriteSubject = favoriteSubject;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getFavoriteSubject() {
        return favoriteSubject;
    }
}

class SimpleStudentFactory implements StudentFactory {
    @Override
    public Student createStudent(String name, String surname, int age, String favoriteSubject) {
        return new Student(name, surname, age, favoriteSubject);
    }
}

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = ",trceknfy";
    private static final StudentFactory studentFactory = new SimpleStudentFactory();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("1. Create");
                System.out.println("2. Read");
                System.out.println("3. Update");
                System.out.println("4. Delete");
                System.out.println("0. Exit");
                System.out.println("Choose operation:");

                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createStudent(connection, scanner);
                        break;
                    case 2:
                        readStudents(connection);
                        break;
                    case 3:
                        updateStudent(connection, scanner);
                        break;
                    case 4:
                        deleteStudent(connection, scanner);
                        break;
                }

            } while (choice != 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student name:");
        String name = scanner.nextLine();

        System.out.println("Enter student surname:");
        String surname = scanner.nextLine();

        System.out.println("Enter student age:");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter student favorite subject:");
        String favorite_subject = scanner.nextLine();

        Student student = studentFactory.createStudent(name, surname, age, favorite_subject);

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO students (name, surname, age, favorite_subject) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getSurname());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setString(4, student.getFavoriteSubject());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student created successfully!");
            } else {
                System.out.println("Failed to create student.");
            }
        }
    }

    private static void readStudents(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM students")) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Surname: " + resultSet.getString("surname") +
                        ", Age: " + resultSet.getInt("age") +
                        ", Favorite Subject: " + resultSet.getString("favorite_subject"));
            }
        }
    }

    private static void updateStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student ID to update:");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new student name:");
        String name = scanner.nextLine();

        System.out.println("Enter new student surname:");
        String surname = scanner.nextLine();

        System.out.println("Enter new student age:");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new student favorite subject:");
        String favorite_subject = scanner.nextLine();

        Student updatedStudent = studentFactory.createStudent(name, surname, age, favorite_subject);

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE students SET name=?, surname=?, age=?, favorite_subject=? WHERE id=?")) {
            preparedStatement.setString(1, updatedStudent.getName());
            preparedStatement.setString(2, updatedStudent.getSurname());
            preparedStatement.setInt(3, updatedStudent.getAge());
            preparedStatement.setString(4, updatedStudent.getFavoriteSubject());
            preparedStatement.setInt(5, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Failed to update student. Student not found");
            }
        }
    }

    private static void deleteStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student ID to delete:");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM students WHERE id=?")) {
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Failed to delete student. Student not found.");
            }
        }
    }
}
