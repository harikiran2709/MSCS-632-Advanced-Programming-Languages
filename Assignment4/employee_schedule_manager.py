class EmployeeScheduleManager:
    def __init__(self):
        self.DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        self.SHIFTS = ["Morning", "Afternoon", "Evening"]
        self.MAX_DAYS_PER_EMPLOYEE = 5
        self.MIN_EMPLOYEES_PER_SHIFT = 2
        
        self.employees = {}
        self.schedule = {}
        self._initialize_schedule()
    
    def _initialize_schedule(self):
        """Initialize the schedule data structure"""
        for day in self.DAYS:
            self.schedule[day] = {}
            for shift in self.SHIFTS:
                self.schedule[day][shift] = []
    
    def add_employee(self, name):
        """Add a new employee to the system"""
        self.employees[name] = Employee(name)
    
    def set_employee_preference(self, name, day, shift):
        """Set an employee's shift preference for a specific day"""
        if name in self.employees:
            self.employees[name].set_preference(day, shift)
    
    def generate_schedule(self):
        """Generate the weekly schedule using control structures"""
        # First pass: Fill minimum requirements (priority over preferences)
        self._fill_minimum_requirements()
        
        # Second pass: Assign preferred shifts (if space available)
        self._assign_preferred_shifts()
        
        # Third pass: Resolve conflicts
        self._resolve_conflicts()
    
    def _assign_preferred_shifts(self):
        """Assign employees to their preferred shifts"""
        for day in self.DAYS:
            for employee_name in self.employees:
                employee = self.employees[employee_name]
                
                # Check if employee can work more days (conditional)
                if employee.get_days_worked() >= self.MAX_DAYS_PER_EMPLOYEE:
                    continue
                
                # Check if employee already has a shift this day (conditional)
                if employee.has_shift_on_day(day):
                    continue
                
                preferred_shift = employee.get_preference(day)
                # Conditional check for shift availability
                if (preferred_shift is not None and 
                    len(self.schedule[day][preferred_shift]) < 3):
                    
                    self.schedule[day][preferred_shift].append(employee_name)
                    employee.assign_shift(day, preferred_shift)
    
    def _fill_minimum_requirements(self):
        """Ensure minimum employee requirements are met for each shift"""
        # Calculate total shifts needed: 7 days × 3 shifts × 2 employees = 42 employee-shifts
        # Available: 9 employees × 5 days = 45 employee-shifts
        # Strategy: Round-robin distribution to ensure all days get covered
        
        # Create a list of all shifts that need to be filled
        shifts_to_fill = []
        for day in self.DAYS:
            for shift in self.SHIFTS:
                shifts_to_fill.append((day, shift))
        
        # Round-robin assignment: assign employees to shifts in rotation
        employee_index = 0
        employee_names = list(self.employees.keys())
        
        for day, shift in shifts_to_fill:
            shift_employees = self.schedule[day][shift]
            
            # Assign 2 employees to this shift
            for i in range(self.MIN_EMPLOYEES_PER_SHIFT):
                employee_name = self._find_next_available_employee(day, employee_names, employee_index)
                if employee_name is not None:
                    shift_employees.append(employee_name)
                    self.employees[employee_name].assign_shift(day, shift)
                    print(f"Assigned {employee_name} to {day} {shift} (minimum requirement)")
                    employee_index = (employee_index + 1) % len(employee_names)
                else:
                    print(f"WARNING: Cannot meet minimum requirement for {day} {shift} (need more employees or adjust preferences)")
                    break
    
    def _distribute_remaining_capacity(self):
        """Distribute remaining employee capacity evenly across all days"""
        # Find employees who haven't worked 5 days yet
        available_employees = []
        for employee_name in self.employees:
            employee = self.employees[employee_name]
            if employee.get_days_worked() < self.MAX_DAYS_PER_EMPLOYEE:
                available_employees.append(employee_name)
        
        # Distribute remaining capacity across all days evenly
        for employee_name in available_employees:
            employee = self.employees[employee_name]
            remaining_days = self.MAX_DAYS_PER_EMPLOYEE - employee.get_days_worked()
            
            # Find days where this employee can work and shifts need more people
            for day in self.DAYS:
                if remaining_days <= 0:
                    break
                if employee.has_shift_on_day(day):
                    continue
                
                for shift in self.SHIFTS:
                    if remaining_days <= 0:
                        break
                    shift_employees = self.schedule[day][shift]
                    
                    # Add employee if shift has less than 3 people (to avoid over-staffing)
                    if len(shift_employees) < 3:
                        shift_employees.append(employee_name)
                        employee.assign_shift(day, shift)
                        print(f"Assigned {employee_name} to {day} {shift} (additional capacity)")
                        remaining_days -= 1
                        break  # Move to next day
    
    def _find_available_employee(self, day):
        """Find an employee available for a specific day"""
        for employee_name in self.employees:
            employee = self.employees[employee_name]
            
            # Conditional checks for availability
            if (employee.get_days_worked() < self.MAX_DAYS_PER_EMPLOYEE and 
                not employee.has_shift_on_day(day)):
                return employee_name
        return None
    
    def _find_next_available_employee(self, day, employee_names, start_index):
        """Find an available employee starting from the given index"""
        # Try to find an available employee starting from the given index
        for i in range(len(employee_names)):
            index = (start_index + i) % len(employee_names)
            employee_name = employee_names[index]
            employee = self.employees[employee_name]
            
            if (employee.get_days_worked() < self.MAX_DAYS_PER_EMPLOYEE and 
                not employee.has_shift_on_day(day)):
                return employee_name
        return None
    
    def _find_employee_for_forced_assignment(self, day):
        """Find employee for forced assignment to meet minimum requirements"""
        for employee_name in self.employees:
            employee = self.employees[employee_name]
            
            # Find employee who hasn't worked 5 days yet and doesn't have shift this day
            if (employee.get_days_worked() < self.MAX_DAYS_PER_EMPLOYEE and 
                not employee.has_shift_on_day(day)):
                return employee_name
        return None
    
    def _resolve_conflicts(self):
        """Resolve scheduling conflicts"""
        for employee_name in self.employees:
            employee = self.employees[employee_name]
            
            # Conditional check for over-scheduling
            if employee.get_days_worked() > self.MAX_DAYS_PER_EMPLOYEE:
                # Remove excess shifts
                shifts_to_remove = employee.get_excess_shifts()
                for shift_info in shifts_to_remove:
                    day, shift = shift_info.split(":")
                    self.schedule[day][shift].remove(employee_name)
                    employee.remove_shift(day, shift)
    
    def print_schedule(self):
        """Print the weekly schedule in a readable format"""
        print("\n=== WEEKLY EMPLOYEE SCHEDULE ===")
        print("=" * 50)
        
        # Loop through days
        for day in self.DAYS:
            print(f"\n{day}:")
            print("-" * 20)
            
            # Loop through shifts
            for shift in self.SHIFTS:
                shift_employees = self.schedule[day][shift]
                if not shift_employees:
                    print(f"{shift:<10}: No employees assigned")
                else:
                    print(f"{shift:<10}: {', '.join(shift_employees)}")
        
        print("\n" + "=" * 50)
        self._print_employee_summary()
    
    def _print_employee_summary(self):
        """Print summary of employee work days"""
        print("\nEMPLOYEE SUMMARY:")
        print("-" * 30)
        
        for employee_name in self.employees:
            employee = self.employees[employee_name]
            print(f"{employee_name:<15}: {employee.get_days_worked()} days worked")


class Employee:
    def __init__(self, name):
        self.name = name
        self.preferences = {}  # day -> preferred shift
        self.assigned_shifts = {}  # day -> assigned shift
        self.days_worked = 0
    
    def set_preference(self, day, shift):
        """Set shift preference for a specific day"""
        self.preferences[day] = shift
    
    def get_preference(self, day):
        """Get shift preference for a specific day"""
        return self.preferences.get(day)
    
    def assign_shift(self, day, shift):
        """Assign a shift to the employee"""
        if day not in self.assigned_shifts:
            self.assigned_shifts[day] = shift
            self.days_worked += 1
    
    def remove_shift(self, day, shift):
        """Remove a shift assignment"""
        if day in self.assigned_shifts and self.assigned_shifts[day] == shift:
            del self.assigned_shifts[day]
            self.days_worked -= 1
    
    def has_shift_on_day(self, day):
        """Check if employee has a shift on a specific day"""
        return day in self.assigned_shifts
    
    def get_days_worked(self):
        """Get total number of days worked"""
        return self.days_worked
    
    def get_excess_shifts(self):
        """Get list of excess shifts to remove"""
        excess = []
        if self.days_worked > 5:  # MAX_DAYS_PER_EMPLOYEE
            to_remove = self.days_worked - 5
            all_shifts = list(self.assigned_shifts.keys())
            
            # Loop to collect excess shifts
            for i in range(min(to_remove, len(all_shifts))):
                day = all_shifts[i]
                shift = self.assigned_shifts[day]
                excess.append(f"{day}:{shift}")
        
        return excess


def main():
    """Main function to run the employee schedule manager"""
    manager = EmployeeScheduleManager()
    
    print("=== EMPLOYEE SCHEDULE MANAGER ===")
    print("Enter employee information and preferences:")
    
    # Add employees
    num_employees = int(input("Enter number of employees: "))
    
    for i in range(num_employees):
        name = input("Enter employee name: ")
        manager.add_employee(name)
    
    # Set preferences
    print("\nEnter shift preferences for each employee:")
    for employee_name in manager.employees:
        print(f"\nPreferences for {employee_name}:")
        
        for day in manager.DAYS:
            print(f"{day} (1=Morning, 2=Afternoon, 3=Evening, 0=None): ", end="")
            choice = int(input())
            
            if 1 <= choice <= 3:
                shift = manager.SHIFTS[choice - 1]
                manager.set_employee_preference(employee_name, day, shift)
    
    # Generate and display schedule
    print("\nGenerating schedule...")
    manager.generate_schedule()
    manager.print_schedule()


if __name__ == "__main__":
    main()
