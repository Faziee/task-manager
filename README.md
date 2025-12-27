# Task Manager (Java Swing)

A clean and modern **desktop task manager** built with **Java Swing**, focused on usability, visual clarity, and persistent task storage.

This project demonstrates practical Java skills, clean UI design, and separation of concerns across application layers.

---

## Features

- Create, edit, and delete tasks  
- Assign priorities (**LOW / MEDIUM / HIGH**)  
- Optional due dates with **automatic overdue highlighting**  
- Mark tasks as completed and undo completion  
- Persistent storage using **JSON** (via Gson)  
- Modern, polished Swing UI using **FlatLaf**  
- Clear visual states for:
  - Selected tasks  
  - Completed tasks  
  - Overdue tasks  

---

## User Interface

- Clean two-panel layout (task list + detailed view)
- Visual accent indicators for task state
- Spacious layout with proper spacing and clear typography
- Non-intrusive color palette focused on readability

The UI was intentionally designed to feel minimal, modern, and distraction-free.

---

## Architecture

The project follows a simple and clear structure:

```
src/main/java/com/faziee/taskmanager
├── app        → Application entry point
├── core       → Task domain logic
├── storage    → JSON persistence (Gson)
└── ui         → Swing UI components and dialogs
```

- **core**: business logic and data models  
- **storage**: file persistence using Gson  
- **ui**: rendering, dialogs, and interaction logic  
- **app**: application bootstrap  

This separation keeps the codebase maintainable and easy to extend.

---

## Running the Application

### Requirements
- Java 11+ (Java 17 recommended)
- Gradle (or use the included wrapper)

### Run with Gradle

```bash
./gradlew run
```

On Windows:
```bash
gradlew.bat run
```

### Notes
- Tasks are stored locally in `tasks.json`
- Data is automatically saved on exit

---

## Tech Stack

- **Java**
- **Swing**
- **FlatLaf**
- **Gson**
- **Gradle**

---

## Motivation

This project was built to practice:
- Java desktop application development
- UI/UX design with Swing
- Clean code structure and state handling
- Persistent data storage without a database

It also serves as a portfolio project demonstrating real-world Java usage beyond basic console programs.

---

## License

This project is for educational and personal portfolio use.
