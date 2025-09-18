# Assignment 4: Employee Schedule Management

## Overview
Employee Schedule Management application implemented in Java and Python. Manages schedules for a company operating 7 days a week with morning, afternoon, and evening shifts.

## Requirements
- ✅ Collect employee names and shift preferences
- ✅ Ensure no employee works more than one shift per day
- ✅ Maximum 5 days per week per employee
- ✅ At least 2 employees per shift per day
- ✅ Resolve conflicts when preferred shifts are full
- ✅ Output readable weekly schedule

## Files
- `EmployeeScheduleManager.java` - Java implementation
- `employee_schedule_manager.py` - Python implementation
- `test_data_sufficient.txt` - Test data (9 employees)

## How to Run

### Java
```bash
javac EmployeeScheduleManager.java
java EmployeeScheduleManager
```

### Python
```bash
python3 employee_schedule_manager.py
```

## Input Format
1. Enter number of employees
2. Enter employee names
3. For each employee, enter preferences for each day:
   - 1 = Morning
   - 2 = Afternoon  
   - 3 = Evening
   - 0 = No preference

## Test Data
Use `test_data_sufficient.txt` for testing with 9 employees (meets all requirements).

## Expected Output
- Weekly schedule showing employee assignments
- Employee summary with days worked
- Warnings if requirements cannot be met

## Language Features
- **Java**: Classes, OOP, control structures, HashMap
- **Python**: Classes, control structures, dictionaries