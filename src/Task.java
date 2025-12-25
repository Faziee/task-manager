public class Task
{
    private String title;
    private boolean completed;

    public Task(String title)
    {
        this.title = title;
        this.completed = false;
    }

    public String getTitle()
    {
        return this.title;
    }

    public boolean isCompleted()
    {
        return this.completed;
    }

    public void markCompleted()
    {
        this.completed = true;
    }

    @Override
    public String toString()
    {
        return (this.completed ? "[x] " : "[ ] ") + title;
    }
}
