import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static ArrayList<String> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addTask();
                case 2 -> listTasks();
                case 3 -> exitApp();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void showMenu() {
        System.out.println("\n Task Manager");
        System.out.println("1. Add task");
        System.out.println("2. List tasks");
        System.out.println("3. Exit");
        System.out.print("Choose: ");
    }

    static void addTask() {
        System.out.print("Enter task: ");
        String task = scanner.nextLine();
        tasks.add(task);
        System.out.println("Task added.");
    }

    static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks yet.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    static void exitApp() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
