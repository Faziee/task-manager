import java.util.ArrayList;
import java.util.Scanner;

public class Main
{

    static ArrayList<Task> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        System.out.println("\n Welcome to your Personal Task Manager");

        while (true)
        {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice)
            {
                case 1 -> addTask();
                case 2 -> listTasks();
                case 3 -> markTaskAsDone();
                case 4 -> exitApp();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void showMenu()
    {
        System.out.println("\n === Personal Task Manager ===");
        System.out.println("1. Add task");
        System.out.println("2. List tasks");
        System.out.println("3. Mark Task as done");
        System.out.println("4. Exit");
        System.out.print("Choose an option (1-4): ");
    }

    static void addTask()
    {
        System.out.print("Enter task: ");
        String title = scanner.nextLine();
        tasks.add(new Task(title));
        System.out.println("Task added.");
    }

    static void listTasks()
    {
        if (tasks.isEmpty())
        {
            System.out.println("No tasks yet.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++)
        {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    static void markTaskAsDone()
    {
        if (tasks.isEmpty())
        {
            System.out.println("No tasks to mark.");
            return;
        }

        listTasks();
        System.out.println("Enter task number to mark as done: ");

        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > tasks.size())
        {
            System.out.println("Invalid task number :(");
            return;
        }

        tasks.get(index - 1).markCompleted();
        System.out.println("Task marked as done!!");
    }

    static void exitApp()
    {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
