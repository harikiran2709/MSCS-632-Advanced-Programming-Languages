#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Compute arithmetic average
double compute_average(int numbers[], int size) {
    double total = 0.0;
    for (int i = 0; i < size; i++) {
        total += numbers[i];
    }
    return total / size;
}

// Arrange elements in ascending order
void arrange_ascending(int numbers[], int size) {
    for (int i = 0; i < size - 1; i++) {
        for (int j = 0; j < size - i - 1; j++) {
            if (numbers[j] > numbers[j + 1]) {
                int swap = numbers[j];
                numbers[j] = numbers[j + 1];
                numbers[j + 1] = swap;
            }
        }
    }
}

// Find middle value
double find_middle_value(int numbers[], int size) {
    int *temp_array = malloc(size * sizeof(int));
    memcpy(temp_array, numbers, size * sizeof(int));
    
    arrange_ascending(temp_array, size);
    
    double result;
    if (size % 2 == 0) {
        result = (temp_array[size/2 - 1] + temp_array[size/2]) / 2.0;
    } else {
        result = temp_array[size/2];
    }
    
    free(temp_array);
    return result;
}

// Determine most frequent values
void find_most_frequent(int numbers[], int size, int frequent_values[], int *count) {
    int highest_frequency = 0;
    *count = 0;
    
    for (int i = 0; i < size; i++) {
        int frequency = 0;
        for (int j = 0; j < size; j++) {
            if (numbers[j] == numbers[i]) {
                frequency++;
            }
        }
        
        if (frequency > highest_frequency) {
            highest_frequency = frequency;
            *count = 0;
            frequent_values[*count] = numbers[i];
            (*count)++;
        } else if (frequency == highest_frequency) {
            int already_exists = 0;
            for (int k = 0; k < *count; k++) {
                if (frequent_values[k] == numbers[i]) {
                    already_exists = 1;
                    break;
                }
            }
            if (!already_exists) {
                frequent_values[*count] = numbers[i];
                (*count)++;
            }
        }
    }
}

// Function to print array
void print_array(int arr[], int n) {
    printf("[");
    for (int i = 0; i < n; i++) {
        printf("%d", arr[i]);
        if (i < n - 1) printf(", ");
    }
    printf("]");
}

int main() {
    printf("=== Statistics Calculator (C - Procedural) ===\n\n");
    
    // Test data
    int data[] = {1, 2, 3, 4, 5, 2, 3, 2, 1, 3};
    int n = sizeof(data) / sizeof(data[0]);
    
    printf("Input data: ");
    print_array(data, n);
    printf("\n\n");
    
    // Calculate and display mean
    double mean = compute_average(data, n);
    printf("Mean: %.2f\n", mean);
    
    // Calculate and display median
    double median = find_middle_value(data, n);
    printf("Median: %.2f\n", median);
    
    // Calculate and display mode
    int modes[100]; // Assuming max 100 different modes
    int mode_count;
    find_most_frequent(data, n, modes, &mode_count);
    
    printf("Mode: ");
    if (mode_count == 0) {
        printf("No mode");
    } else {
        print_array(modes, mode_count);
    }
    printf("\n");
    
    return 0;
}
