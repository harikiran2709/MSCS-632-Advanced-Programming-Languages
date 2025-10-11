from collections import Counter
from typing import List, Union


class DataAnalyzer:
    """A class to compute basic statistical measures (mean, median, mode) on integer datasets."""
    
    def __init__(self, dataset: List[int]):
        """
        Initialize the DataAnalyzer with a list of integers.
        
        Args:
            dataset: List of integers to analyze
        """
        self.dataset = dataset.copy()  # Create a copy to preserve original data
    
    def compute_average(self) -> float:
        """
        Compute the arithmetic mean of the dataset.
        
        Returns:
            float: The average value
        """
        if not self.dataset:
            return 0.0
        return sum(self.dataset) / len(self.dataset)
    
    def find_central_value(self) -> float:
        """
        Find the median (central value) of the dataset.
        
        Returns:
            float: The median value
        """
        if not self.dataset:
            return 0.0
        
        ordered_data = sorted(self.dataset)
        size = len(ordered_data)
        
        if size % 2 == 0:
            # Even number of elements - average of two central values
            first_center = ordered_data[size // 2 - 1]
            second_center = ordered_data[size // 2]
            return (first_center + second_center) / 2.0
        else:
            # Odd number of elements - central value
            return float(ordered_data[size // 2])
    
    def identify_most_common(self) -> List[int]:
        """
        Identify the most frequently occurring value(s) in the dataset.
        
        Returns:
            List[int]: List of most common values (can be multiple if tied)
        """
        if not self.dataset:
            return []
        
        # Use Counter to count occurrences
        frequency_counter = Counter(self.dataset)
        highest_frequency = max(frequency_counter.values())
        
        # Find all values with highest frequency
        most_common_values = [value for value, frequency in frequency_counter.items() if frequency == highest_frequency]
        return sorted(most_common_values)
    
    def get_all_measures(self) -> dict:
        """
        Get all statistical measures in a dictionary format.
        
        Returns:
            dict: Dictionary containing mean, median, and mode
        """
        return {
            'mean': self.compute_average(),
            'median': self.find_central_value(),
            'mode': self.identify_most_common()
        }
    
    def display_results(self) -> None:
        """Display all statistical measures in a formatted way."""
        print(f"Mean: {self.compute_average():.2f}")
        print(f"Median: {self.find_central_value():.2f}")
        modes = self.identify_most_common()
        if modes:
            print(f"Mode: {modes}")
        else:
            print("Mode: No mode")
    
    def __str__(self) -> str:
        """String representation of the analyzer."""
        return f"DataAnalyzer(dataset={self.dataset})"
    
    def __repr__(self) -> str:
        """Detailed string representation of the analyzer."""
        return f"DataAnalyzer(dataset={self.dataset}, mean={self.compute_average():.2f})"


def main():
    """Main function to demonstrate the DataAnalyzer class."""
    print("=== Statistics Calculator (Python - Object-Oriented) ===\n")
    
    # Test data
    data = [1, 2, 3, 4, 5, 2, 3, 2, 1, 3]
    
    print(f"Input data: {data}\n")
    
    # Create DataAnalyzer instance
    analyzer = DataAnalyzer(data)
    
    # Calculate and display statistics
    analyzer.display_results()
    print()


if __name__ == "__main__":
    main()
