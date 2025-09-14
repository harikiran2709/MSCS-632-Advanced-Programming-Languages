import java.util.*;
import java.util.concurrent.*;
import java.util.Scanner;

// Collaborative To-Do List Application - Java Implementation
// Demonstrates: Classes, OOP principles, and threads for concurrency

class User {
    private String id;
    private String name;
    
    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
}

class Task {
    private String id;
    private String title;
    private String category;
    private String status;
    private String assignedUserId;
    
    public Task(String title, String category, String assignedUserId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.category = category;
        this.status = "pending";
        this.assignedUserId = assignedUserId;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getAssignedUserId() { return assignedUserId; }
    
    public void setStatus(String status) { this.status = status; }
}

class TodoService {
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, Task> tasks = new ConcurrentHashMap<>();
    
    public synchronized User createUser(String name) {
        User user = new User(name);
        users.put(user.getId(), user);
        return user;
    }
    
    public synchronized Task createTask(String title, String category, String userId) {
        Task task = new Task(title, category, userId);
        tasks.put(task.getId(), task);
        return task;
    }
    
    public synchronized void updateTaskStatus(String taskId, String status) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(status);
        }
    }
    
    public synchronized boolean deleteTask(String taskId) {
        Task removed = tasks.remove(taskId);
        return removed != null;
    }
    
    public List<Task> getTasksByUser(String userId) {
        return tasks.values().stream()
                   .filter(task -> task.getAssignedUserId().equals(userId))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public List<Task> getTasksByCategory(String category) {
        return tasks.values().stream()
                   .filter(task -> task.getCategory().equals(category))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
}

public class TodoApp {
    private static TodoService service = new TodoService();
    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, User> currentUsers = new HashMap<>();
    
    public static void main(String[] args) {
        System.out.println("=== Collaborative To-Do List Application (Java) ===");
        
        // Create some default users
        currentUsers.put("alice", service.createUser("Alice"));
        currentUsers.put("bob", service.createUser("Bob"));
        
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    deleteTask();
                    break;
                case 3:
                    markTaskComplete();
                    break;
                case 4:
                    viewTasks();
                    break;
                case 5:
                    runConcurrencyDemo();
                    break;
                case 6:
                    runLanguageSpecificDemo();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Thank you for using the Collaborative To-Do List!");
    }
    
    private static void showMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Create Task");
        System.out.println("2. Delete Task");
        System.out.println("3. Mark Task Complete");
        System.out.println("4. View Tasks");
        System.out.println("5. Run Concurrency Demo");
        System.out.println("6. Run Language-Specific Demo (Threads + OOP)");
        System.out.println("7. Exit");
    }
    
    private static void createTask() {
        System.out.println("\n=== Create Task ===");
        System.out.println("Available users: alice, bob");
        String username = getStringInput("Enter username: ");
        
        if (!currentUsers.containsKey(username)) {
            System.out.println("User not found!");
            return;
        }
        
        String title = getStringInput("Enter task title: ");
        String category = getStringInput("Enter category (Work/Personal): ");
        
        Task task = service.createTask(title, category, currentUsers.get(username).getId());
        System.out.println("Task created successfully: " + task.getTitle());
    }
    
    private static void deleteTask() {
        System.out.println("\n=== Delete Task ===");
        List<Task> allTasks = service.getAllTasks();
        
        if (allTasks.isEmpty()) {
            System.out.println("No tasks available to delete.");
            return;
        }
        
        System.out.println("Available tasks:");
        for (int i = 0; i < allTasks.size(); i++) {
            Task task = allTasks.get(i);
            System.out.println((i + 1) + ". " + task.getTitle() + " (" + task.getCategory() + ")");
        }
        
        int taskIndex = getIntInput("Enter task number to delete: ") - 1;
        if (taskIndex >= 0 && taskIndex < allTasks.size()) {
            Task task = allTasks.get(taskIndex);
            boolean deleted = service.deleteTask(task.getId());
            if (deleted) {
                System.out.println("Task deleted successfully: " + task.getTitle());
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }
    
    private static void markTaskComplete() {
        System.out.println("\n=== Mark Task Complete ===");
        List<Task> allTasks = service.getAllTasks();
        
        if (allTasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        
        System.out.println("Available tasks:");
        for (int i = 0; i < allTasks.size(); i++) {
            Task task = allTasks.get(i);
            System.out.println((i + 1) + ". " + task.getTitle() + " - " + task.getStatus());
        }
        
        int taskIndex = getIntInput("Enter task number to mark complete: ") - 1;
        if (taskIndex >= 0 && taskIndex < allTasks.size()) {
            Task task = allTasks.get(taskIndex);
            service.updateTaskStatus(task.getId(), "completed");
            System.out.println("Task marked as completed: " + task.getTitle());
        } else {
            System.out.println("Invalid task number.");
        }
    }
    
    private static void viewTasks() {
        System.out.println("\n=== View Tasks ===");
        System.out.println("1. View all tasks");
        System.out.println("2. View tasks by user");
        System.out.println("3. View tasks by category");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                List<Task> allTasks = service.getAllTasks();
                System.out.println("\nAll tasks:");
                allTasks.forEach(task -> 
                    System.out.println("- " + task.getTitle() + " (" + task.getCategory() + ") - " + task.getStatus()));
                break;
            case 2:
                String username = getStringInput("Enter username (alice/bob): ");
                if (currentUsers.containsKey(username)) {
                    List<Task> userTasks = service.getTasksByUser(currentUsers.get(username).getId());
                    System.out.println("\nTasks for " + username + ":");
                    userTasks.forEach(task -> 
                        System.out.println("- " + task.getTitle() + " - " + task.getStatus()));
                } else {
                    System.out.println("User not found!");
                }
                break;
            case 3:
                String category = getStringInput("Enter category (Work/Personal): ");
                List<Task> categoryTasks = service.getTasksByCategory(category);
                System.out.println("\nTasks in " + category + " category:");
                categoryTasks.forEach(task -> 
                    System.out.println("- " + task.getTitle() + " - " + task.getStatus()));
                break;
        }
    }
    
    private static void runConcurrencyDemo() {
        System.out.println("\n=== Running Concurrency Demo ===");
        
        // Create some tasks for demo
        service.createTask("Demo Task 1", "Work", currentUsers.get("alice").getId());
        service.createTask("Demo Task 2", "Personal", currentUsers.get("bob").getId());
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        executor.submit(() -> {
            List<Task> aliceTasks = service.getTasksByUser(currentUsers.get("alice").getId());
            for (Task task : aliceTasks) {
                service.updateTaskStatus(task.getId(), "completed");
                System.out.println("Alice completed: " + task.getTitle());
            }
        });
        
        executor.submit(() -> {
            List<Task> bobTasks = service.getTasksByUser(currentUsers.get("bob").getId());
            for (Task task : bobTasks) {
                service.updateTaskStatus(task.getId(), "in-progress");
                System.out.println("Bob started: " + task.getTitle());
            }
        });
        
        executor.shutdown();
        System.out.println("Concurrency demo completed!");
    }
    
    private static void runLanguageSpecificDemo() {
        System.out.println("\n=== Java Language-Specific Demo ===");
        System.out.println("Demonstrating: Threads + OOP principles");
        
        // Create users and tasks
        User alice = service.createUser("Alice");
        User bob = service.createUser("Bob");
        
        Task task1 = service.createTask("Write report", "Work", alice.getId());
        Task task2 = service.createTask("Buy groceries", "Personal", bob.getId());
        Task task3 = service.createTask("Fix bug", "Work", alice.getId());
        
        // Demonstrate task removal
        System.out.println("Before deletion - Total tasks: " + service.getTasksByCategory("Work").size());
        service.deleteTask(task1.getId());
        System.out.println("After deletion - Total tasks: " + service.getTasksByCategory("Work").size());
        
        // Demonstrate thread-based concurrency
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Thread 1: Alice updates tasks
        executor.submit(() -> {
            List<Task> aliceTasks = service.getTasksByUser(alice.getId());
            for (Task task : aliceTasks) {
                service.updateTaskStatus(task.getId(), "completed");
                System.out.println("Alice completed: " + task.getTitle());
            }
        });
        
        // Thread 2: Bob updates tasks
        executor.submit(() -> {
            List<Task> bobTasks = service.getTasksByUser(bob.getId());
            for (Task task : bobTasks) {
                service.updateTaskStatus(task.getId(), "in-progress");
                System.out.println("Bob started: " + task.getTitle());
            }
        });
        
        executor.shutdown();
        
        // Show results
        System.out.println("\nAll tasks:");
        service.getTasksByCategory("Work").forEach(task -> 
            System.out.println(task.getTitle() + " - " + task.getStatus()));
    }
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}