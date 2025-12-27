package com.faziee.taskmanager.app;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;
import com.faziee.taskmanager.ui.TaskManagerApp;
import com.formdev.flatlaf.FlatLightLaf;


import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            System.setProperty("flatlaf.uiScale", "1.0");
            System.setProperty("flatlaf.useWindowDecorations", "true");
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            System.setProperty("flatlaf.roundedCorners", "true");
            UIManager.put("defaultFont", UIManager.getFont("Label.font").deriveFont(15f));

            UIManager.setLookAndFeel(new FlatLightLaf());
        }
        catch (Exception e)
        {
            System.err.println("Failed to initialize FlatLaf");
        }

        TaskManager taskManager = new TaskManager();
        TaskRepository repository = new TaskRepository("tasks.json");

        try
        {
            taskManager.setTasks(repository.load());
        }
        catch (Exception e)
        {
            System.out.println("Could not load tasks file (starting fresh).");
        }

        SwingUtilities.invokeLater(() ->
        {
            TaskManagerApp app = new TaskManagerApp(taskManager, repository);
            app.setVisible(true);
        });
    }
}
