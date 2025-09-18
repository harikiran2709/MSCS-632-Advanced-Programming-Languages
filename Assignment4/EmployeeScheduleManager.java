import java.util.*;

public class EmployeeScheduleManager {
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String[] SHIFTS = {"Morning", "Afternoon", "Evening"};
    private static final int MAX_DAYS_PER_EMPLOYEE = 5;
    private static final int MIN_EMPLOYEES_PER_SHIFT = 2;
    
    private Map<String, Employee> employees;
    private Map<String, Map<String, List<String>>> schedule; // day -> shift -> employees
    
    public EmployeeScheduleManager() {
        this.employees = new HashMap<>();
        this.schedule = new HashMap<>();
        initializeSchedule();
    }
    
    private void initializeSchedule() {
        for (String day : DAYS) {
            schedule.put(day, new HashMap<>());
            for (String shift : SHIFTS) {
                schedule.get(day).put(shift, new ArrayList<>());
            }
        }
    }
    
    public void addEmployee(String name) {
        employees.put(name, new Employee(name));
    }
    
    public void setEmployeePreference(String name, String day, String shift) {
        if (employees.containsKey(name)) {
            employees.get(name).setPreference(day, shift);
        }
    }
    
    public void generateSchedule() {
        // First pass: Fill minimum requirements (priority over preferences)
        fillMinimumRequirements();
        
        // Second pass: Assign preferred shifts (if space available)
        assignPreferredShifts();
        
        // Third pass: Resolve conflicts
        resolveConflicts();
    }
    
    private void assignPreferredShifts() {
        for (String day : DAYS) {
            for (String employeeName : employees.keySet()) {
                Employee employee = employees.get(employeeName);
                
                // Check if employee can work more days
                if (employee.getDaysWorked() >= MAX_DAYS_PER_EMPLOYEE) {
                    continue;
                }
                
                // Check if employee already has a shift this day
                if (employee.hasShiftOnDay(day)) {
                    continue;
                }
                
                String preferredShift = employee.getPreference(day);
                if (preferredShift != null && schedule.get(day).get(preferredShift).size() < 3) {
                    schedule.get(day).get(preferredShift).add(employeeName);
                    employee.assignShift(day, preferredShift);
                }
            }
        }
    }
    
    private void fillMinimumRequirements() {
        // Calculate total shifts needed: 7 days × 3 shifts × 2 employees = 42 employee-shifts
        // Available: 9 employees × 5 days = 45 employee-shifts
        // Strategy: Round-robin distribution to ensure all days get covered
        
        // Create a list of all shifts that need to be filled
        List<String[]> shiftsToFill = new ArrayList<>();
        for (String day : DAYS) {
            for (String shift : SHIFTS) {
                shiftsToFill.add(new String[]{day, shift});
            }
        }
        
        // Round-robin assignment: assign employees to shifts in rotation
        int employeeIndex = 0;
        List<String> employeeNames = new ArrayList<>(employees.keySet());
        
        for (String[] shiftInfo : shiftsToFill) {
            String day = shiftInfo[0];
            String shift = shiftInfo[1];
            List<String> shiftEmployees = schedule.get(day).get(shift);
            
            // Assign 2 employees to this shift
            for (int i = 0; i < MIN_EMPLOYEES_PER_SHIFT; i++) {
                String employeeName = findNextAvailableEmployee(day, employeeNames, employeeIndex);
                if (employeeName != null) {
                    shiftEmployees.add(employeeName);
                    employees.get(employeeName).assignShift(day, shift);
                    System.out.println("Assigned " + employeeName + " to " + day + " " + shift + " (minimum requirement)");
                    employeeIndex = (employeeIndex + 1) % employeeNames.size();
                } else {
                    System.out.println("WARNING: Cannot meet minimum requirement for " + day + " " + shift + 
                                     " (need more employees or adjust preferences)");
                    break;
                }
            }
        }
    }
    
    private String findNextAvailableEmployee(String day, List<String> employeeNames, int startIndex) {
        // Try to find an available employee starting from the given index
        for (int i = 0; i < employeeNames.size(); i++) {
            int index = (startIndex + i) % employeeNames.size();
            String employeeName = employeeNames.get(index);
            Employee employee = employees.get(employeeName);
            
            if (employee.getDaysWorked() < MAX_DAYS_PER_EMPLOYEE && !employee.hasShiftOnDay(day)) {
                return employeeName;
            }
        }
        return null;
    }
    
    private void distributeRemainingCapacity() {
        // Find employees who haven't worked 5 days yet
        List<String> availableEmployees = new ArrayList<>();
        for (String employeeName : employees.keySet()) {
            Employee employee = employees.get(employeeName);
            if (employee.getDaysWorked() < MAX_DAYS_PER_EMPLOYEE) {
                availableEmployees.add(employeeName);
            }
        }
        
        // Distribute remaining capacity across all days evenly
        for (String employeeName : availableEmployees) {
            Employee employee = employees.get(employeeName);
            int remainingDays = MAX_DAYS_PER_EMPLOYEE - employee.getDaysWorked();
            
            // Find days where this employee can work and shifts need more people
            for (String day : DAYS) {
                if (remainingDays <= 0) break;
                if (employee.hasShiftOnDay(day)) continue;
                
                for (String shift : SHIFTS) {
                    if (remainingDays <= 0) break;
                    List<String> shiftEmployees = schedule.get(day).get(shift);
                    
                    // Add employee if shift has less than 3 people (to avoid over-staffing)
                    if (shiftEmployees.size() < 3) {
                        shiftEmployees.add(employeeName);
                        employee.assignShift(day, shift);
                        System.out.println("Assigned " + employeeName + " to " + day + " " + shift + " (additional capacity)");
                        remainingDays--;
                        break; // Move to next day
                    }
                }
            }
        }
    }
    
    private String findAvailableEmployee(String day) {
        for (String employeeName : employees.keySet()) {
            Employee employee = employees.get(employeeName);
            
            // Check if employee can work more days and doesn't have shift this day
            if (employee.getDaysWorked() < MAX_DAYS_PER_EMPLOYEE && !employee.hasShiftOnDay(day)) {
                return employeeName;
            }
        }
        return null;
    }
    
    private String findEmployeeForForcedAssignment(String day) {
        // Find employee who hasn't worked 5 days yet and doesn't have shift this day
        for (String employeeName : employees.keySet()) {
            Employee employee = employees.get(employeeName);
            
            if (employee.getDaysWorked() < MAX_DAYS_PER_EMPLOYEE && !employee.hasShiftOnDay(day)) {
                return employeeName;
            }
        }
        return null;
    }
    
    private void resolveConflicts() {
        // Check for employees working more than 5 days
        for (String employeeName : employees.keySet()) {
            Employee employee = employees.get(employeeName);
            
            if (employee.getDaysWorked() > MAX_DAYS_PER_EMPLOYEE) {
                // Remove excess shifts
                List<String> shiftsToRemove = employee.getExcessShifts();
                for (String shiftInfo : shiftsToRemove) {
                    String[] parts = shiftInfo.split(":");
                    String day = parts[0];
                    String shift = parts[1];
                    
                    schedule.get(day).get(shift).remove(employeeName);
                    employee.removeShift(day, shift);
                }
            }
        }
    }
    
    public void printSchedule() {
        System.out.println("\n=== WEEKLY EMPLOYEE SCHEDULE ===");
        System.out.println("=" + "=".repeat(50));
        
        for (String day : DAYS) {
            System.out.println("\n" + day + ":");
            System.out.println("-".repeat(20));
            
            for (String shift : SHIFTS) {
                List<String> shiftEmployees = schedule.get(day).get(shift);
                System.out.printf("%-10s: %s%n", shift, 
                    shiftEmployees.isEmpty() ? "No employees assigned" : 
                    String.join(", ", shiftEmployees));
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        printEmployeeSummary();
    }
    
    private void printEmployeeSummary() {
        System.out.println("\nEMPLOYEE SUMMARY:");
        System.out.println("-".repeat(30));
        
        for (String employeeName : employees.keySet()) {
            Employee employee = employees.get(employeeName);
            System.out.printf("%-15s: %d days worked%n", employeeName, employee.getDaysWorked());
        }
    }
    
    // Employee class
    private static class Employee {
        private String name;
        private Map<String, String> preferences; // day -> preferred shift
        private Map<String, String> assignedShifts; // day -> assigned shift
        private int daysWorked;
        
        public Employee(String name) {
            this.name = name;
            this.preferences = new HashMap<>();
            this.assignedShifts = new HashMap<>();
            this.daysWorked = 0;
        }
        
        public void setPreference(String day, String shift) {
            preferences.put(day, shift);
        }
        
        public String getPreference(String day) {
            return preferences.get(day);
        }
        
        public void assignShift(String day, String shift) {
            if (!assignedShifts.containsKey(day)) {
                assignedShifts.put(day, shift);
                daysWorked++;
            }
        }
        
        public void removeShift(String day, String shift) {
            if (assignedShifts.containsKey(day) && assignedShifts.get(day).equals(shift)) {
                assignedShifts.remove(day);
                daysWorked--;
            }
        }
        
        public boolean hasShiftOnDay(String day) {
            return assignedShifts.containsKey(day);
        }
        
        public int getDaysWorked() {
            return daysWorked;
        }
        
        public List<String> getExcessShifts() {
            List<String> excess = new ArrayList<>();
            if (daysWorked > MAX_DAYS_PER_EMPLOYEE) {
                int toRemove = daysWorked - MAX_DAYS_PER_EMPLOYEE;
                List<String> allShifts = new ArrayList<>(assignedShifts.keySet());
                
                for (int i = 0; i < toRemove && i < allShifts.size(); i++) {
                    String day = allShifts.get(i);
                    String shift = assignedShifts.get(day);
                    excess.add(day + ":" + shift);
                }
            }
            return excess;
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmployeeScheduleManager manager = new EmployeeScheduleManager();
        
        System.out.println("=== EMPLOYEE SCHEDULE MANAGER ===");
        System.out.println("Enter employee information and preferences:");
        System.out.println("NOTE: To meet minimum requirements (2 employees per shift per day),");
        System.out.println("you need at least 9 employees (7 days × 3 shifts × 2 employees ÷ 5 days max per employee)");
        
        // Add employees
        System.out.print("Enter number of employees (minimum 9 recommended): ");
        int numEmployees = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        for (int i = 0; i < numEmployees; i++) {
            System.out.print("Enter employee name: ");
            String name = scanner.nextLine();
            manager.addEmployee(name);
        }
        
        // Set preferences
        System.out.println("\nEnter shift preferences for each employee:");
        for (String employeeName : manager.employees.keySet()) {
            System.out.println("\nPreferences for " + employeeName + ":");
            
            for (String day : DAYS) {
                System.out.print(day + " (1=Morning, 2=Afternoon, 3=Evening, 0=None): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (choice >= 1 && choice <= 3) {
                    String shift = SHIFTS[choice - 1];
                    manager.setEmployeePreference(employeeName, day, shift);
                }
            }
        }
        
        // Generate and display schedule
        System.out.println("\nGenerating schedule...");
        manager.generateSchedule();
        manager.printSchedule();
        
        scanner.close();
    }
}
