import java.util.List;
import java.util.Scanner;

public class Main
{

    private static final Scanner scanner = new Scanner(System.in);
    private static final TaskManager taskManager = new TaskManager();
    private static final TaskRepository repository = new TaskRepository("tasks.txt");

    public static void main(String[] args)
    {
        System.out.println("Welcome to your Personal Task Manager!");

        try
        {
            taskManager.setTasks(repository.load());
        }
        catch (Exception e)
        {
            System.out.println("Could not load tasks file (starting fresh).");
        }

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

    // ---------- UI ----------

    private static void showMenu()
    {
        System.out.println();
        System.out.println("================================");
        System.out.println(" Personal Task Manager");
        System.out.println(" Tasks: " + taskManager.size() + "\n");
        System.out.println("1) Add task");
        System.out.println("2) List tasks");
        System.out.println("3) Mark task as done");
        System.out.println("4) Exit");
    }

    private static void addTask()
    {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty())
        {
            System.out.println("Task title can't be empty.");
            pause();
            return;
        }

        taskManager.addTask(title);
        System.out.println("Task added!");
        pause();
    }

    private static void listTasks()
    {
        if (taskManager.isEmpty())
        {
            System.out.println("No tasks yet. Add one with option 1.");
            pause();
            return;
        }

        System.out.println("\nYour tasks:");
        List<Task> tasks = taskManager.getTasks();
        for (int i = 0; i < tasks.size(); i++)
        {
            System.out.printf("%2d) %s%n", (i + 1), tasks.get(i));
        }
        pause();
    }

    private static void markTaskAsDone()
    {
        if (taskManager.isEmpty())
        {
            System.out.println("No tasks to mark.");
            pause();
            return;
        }

        System.out.println("\nYour tasks:");
        List<Task> tasks = taskManager.getTasks();
        for (int i = 0; i < tasks.size(); i++)
        {
            System.out.printf("%2d) %s%n", (i + 1), tasks.get(i));
        }

        int index = readInt("Enter task number to mark as done: ");

        if (!taskManager.markTaskDone(index))
        {
            System.out.println("Invalid task number.");
        }
        else
        {
            System.out.println("Task marked as done!");
        }
        pause();
    }

    private static void exitApp()
    {
        System.out.print("Are you sure you want to exit? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("y") || input.equals("yes"))
        {
            try
            {
                repository.save(taskManager.getTasks());
            }
            catch (Exception e)
            {
                System.out.println("Could not save tasks file.");
            }
            System.out.println("Goodbye!");
            System.exit(0);
        }
        else
        {
            System.out.println("Continuing...");
            pause();
        }
    }

    // ---------- Helpers ----------

    private static void pause()
    {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static int readInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try
            {
                return Integer.parseInt(line);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a number.");
            }
        }
    }
}
