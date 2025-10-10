package main

import (
	"fmt"
	"sync"
	"time"
)

type Task struct {
	ID      int
	Payload string
}

func worker(id int, tasks <-chan Task, results chan<- string, wg *sync.WaitGroup) {
	defer wg.Done()
	fmt.Printf("%s worker %d started\n", time.Now().Format(time.RFC3339), id)
	for t := range tasks {
		// simulate processing delay
		time.Sleep(time.Duration(100+(t.ID%5)*50) * time.Millisecond)
		out := fmt.Sprintf("worker=%d, task=%d, payload='%s'", id, t.ID, t.Payload)
		results <- out
		fmt.Printf("%s worker %d completed task %d\n", time.Now().Format(time.RFC3339), id, t.ID)
	}
	fmt.Printf("%s worker %d finished\n", time.Now().Format(time.RFC3339), id)
}

func main() {
	numWorkers := 4
	numTasks := 20

	tasks := make(chan Task, numTasks)
	results := make(chan string, numTasks)
	var wg sync.WaitGroup

	// start workers
	for i := 1; i <= numWorkers; i++ {
		wg.Add(1)
		go worker(i, tasks, results, &wg)
	}

	// enqueue tasks safely
	for i := 1; i <= numTasks; i++ {
		tasks <- Task{ID: i, Payload: fmt.Sprintf("data-%d", i)}
	}
	close(tasks)

	// collect results concurrently
	var allResults []string
	go func() {
		wg.Wait()
		close(results)
	}()

	// collect all results first
	for r := range results {
		allResults = append(allResults, r)
	}

	// Display results in terminal after all workers finish
	fmt.Println("\n=== PROCESSED TASKS RESULTS ===")
	for _, r := range allResults {
		fmt.Println(r)
	}
	fmt.Println("=== END OF RESULTS ===\n")

	fmt.Printf("%s done. processed=%d tasks\n", time.Now().Format(time.RFC3339), len(allResults))
}


