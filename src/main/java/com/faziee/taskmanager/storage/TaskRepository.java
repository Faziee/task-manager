package com.faziee.taskmanager.storage;

import com.faziee.taskmanager.core.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository
{

    private final String fileName;
    private final Gson gson;

    public TaskRepository(String fileName)
    {
        this.fileName = fileName;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public List<Task> load() throws IOException
    {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file))
        {
            Type listType = new TypeToken<List<Task>>()
            {
            }.getType();
            List<Task> tasks = gson.fromJson(reader, listType);
            return tasks == null ? new ArrayList<>() : tasks;
        }
    }

    public void save(List<Task> tasks) throws IOException
    {
        try (Writer writer = new FileWriter(fileName))
        {
            gson.toJson(tasks, writer);
        }
    }
}
