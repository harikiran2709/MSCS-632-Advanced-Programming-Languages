# Testing Guide for Employee Schedule Manager

## How to Test Using Interactive Input

### Test Scenario 1: Sufficient Employees (9 employees)
**File:** `test_data_sufficient.txt`

1. Run Java: `java EmployeeScheduleManager`
2. Run Python: `python3 employee_schedule_manager.py`
3. Follow the prompts and enter the data from `test_data_sufficient.txt`
4. **Expected Result:** All shifts filled with 2+ employees, no warnings

### Test Scenario 2: Insufficient Employees (3 employees)
**File:** `test_data_insufficient.txt`

1. Run Java: `java EmployeeScheduleManager`
2. Run Python: `python3 employee_schedule_manager.py`
3. Follow the prompts and enter the data from `test_data_insufficient.txt`
4. **Expected Result:** Many warnings about not meeting minimum requirements

### Test Scenario 3: Edge Case (6 employees)
**File:** `test_data_edge.txt`

1. Run Java: `java EmployeeScheduleManager`
2. Run Python: `python3 employee_schedule_manager.py`
3. Follow the prompts and enter the data from `test_data_edge.txt`
4. **Expected Result:** Some shifts filled, some warnings for weekend shifts

## What Each Test Demonstrates

### Requirements Coverage:
✅ **Input and Storage:** Employee names and preferences stored in data structures
✅ **Scheduling Logic:** 
   - No employee works more than one shift per day
   - Maximum 5 days per week per employee
   - At least 2 employees per shift per day (with warnings when not possible)
✅ **Conflict Resolution:** Handles conflicts and reassigns employees
✅ **Output:** Readable schedule format
✅ **Control Structures:** Conditionals, loops, and branching throughout

### Language-Specific Features:
- **Java:** Classes, OOP principles, control structures
- **Python:** Classes, control structures, data structures (dict, list)

## Quick Test Commands

```bash
# Java
javac EmployeeScheduleManager.java
java EmployeeScheduleManager

# Python
python3 employee_schedule_manager.py
```

Then copy-paste the test data from the corresponding `.txt` files when prompted.
