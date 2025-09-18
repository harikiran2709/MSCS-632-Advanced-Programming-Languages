import java.util.*;

public class test_java {
    public static void main(String[] args) {
        EmployeeScheduleManager manager = new EmployeeScheduleManager();
        
        // Add employees
        manager.addEmployee("Alice");
        manager.addEmployee("Bob");
        manager.addEmployee("Charlie");
        manager.addEmployee("David");
        manager.addEmployee("Eve");
        manager.addEmployee("Frank");
        manager.addEmployee("Grace");
        manager.addEmployee("Henry");
        manager.addEmployee("Ivy");
        
        // Set some preferences
        manager.setEmployeePreference("Alice", "Monday", "Morning");
        manager.setEmployeePreference("Bob", "Monday", "Afternoon");
        manager.setEmployeePreference("Charlie", "Monday", "Evening");
        manager.setEmployeePreference("David", "Tuesday", "Morning");
        manager.setEmployeePreference("Eve", "Tuesday", "Afternoon");
        manager.setEmployeePreference("Frank", "Tuesday", "Evening");
        manager.setEmployeePreference("Grace", "Wednesday", "Morning");
        manager.setEmployeePreference("Henry", "Wednesday", "Afternoon");
        manager.setEmployeePreference("Ivy", "Wednesday", "Evening");
        
        // Generate and print schedule
        manager.generateSchedule();
        manager.printSchedule();
    }
}

