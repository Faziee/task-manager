import java.util.ArrayList;
import java.util.Scanner;

public class Main
{

    static ArrayList<Task> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        System.out.println("Welcome to your Personal Task Manager!");

        while (true)
        {
            showMenu();
            int choice = readInt("Choose (1-4): ");

            switch (choice)
            {
                case 1 -> addTask();
                case 2 -> listTasks();
                case 3 -> markTaskAsDone();
                case 4 -> exitApp();
                default ->
                {
                    System.out.println("Invalid option. Please choose 1-4.");
                    pause();
                }
            }
        }
    }

    static void showMenu()
    {
        System.out.println();
        System.out.println("============================");
        System.out.println(" Personal Task Manager");
        System.out.println(" Tasks: " + tasks.size() + "\n");
        System.out.println("1) Add task");
        System.out.println("2) List tasks");
        System.out.println("3) Mark task as done");
        System.out.println("4) Exit");
    }

    static void addTask()
    {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty())
        {
            System.out.println("Task title can't be empty.");
            pause();
            return;
        }

        tasks.add(new Task(title));
        System.out.println("Task added!");
        pause();
    }

    static void listTasks()
    {
        System.out.println();

        if (tasks.isEmpty())
        {
            System.out.println("No tasks yet. Add one with option 1.");
            pause();
            return;
        }

        System.out.println("Your tasks:");
        for (int i = 0; i < tasks.size(); i++)
        {
            System.out.printf("%2d) %s%n", (i + 1), tasks.get(i));
        }
        pause();
    }

    static void markTaskAsDone()
    {
        if (tasks.isEmpty())
        {
            System.out.println("No tasks to mark.");
            pause();
            return;
        }

        System.out.println();
        System.out.println("Your tasks:");
        for (int i = 0; i < tasks.size(); i++)
        {
            System.out.printf("%2d) %s%n", (i + 1), tasks.get(i));
        }

        int index = readInt("Enter task number to mark as done: ");

        if (index < 1 || index > tasks.size())
        {
            System.out.println("Invalid task number.");
            pause();
            return;
        }

        Task task = tasks.get(index - 1);
        if (task.isCompleted())
        {
            System.out.println("That task is already completed.");
        } else
        {
            task.markCompleted();
            System.out.println("Task marked as done!!");
        }
        pause();
    }

    static void exitApp()
    {
        System.out.print("Are you sure you want to exit? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("y") || input.equals("yes"))
        {
            System.out.println("Goodbye!");
            System.exit(0);
        } else
        {
            System.out.println("Continuing...");
            pause();
        }
    }

    // --- helpers ---

    static void pause()
    {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    static int readInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try
            {
                return Integer.parseInt(line);
            } catch (NumberFormatException e)
            {
                System.out.println("Please enter a number.");
            }
        }
    }
}
