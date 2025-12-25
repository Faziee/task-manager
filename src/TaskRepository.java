import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository
{
    private final String fileName;

    public TaskRepository(String fileName)
    {
        this.fileName = fileName;
    }

    public List<Task> load() throws IOException
    {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();

        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue;

                Task task = new Task(parts[0]);
                if (Boolean.parseBoolean(parts[1])) task.markCompleted();
                tasks.add(task);
            }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            for (Task task : tasks)
            {
                writer.write(task.getTitle() + "|" + task.isCompleted());
                writer.newLine();
            }
        }
    }
}
