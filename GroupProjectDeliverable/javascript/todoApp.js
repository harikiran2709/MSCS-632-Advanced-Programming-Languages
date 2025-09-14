// Collaborative To-Do List Application - JavaScript Implementation
// Demonstrates: async/await for concurrency and JSON for data storage

const readline = require('readline');

class User {
    constructor(name) {
        this.id = Math.random().toString(36).substr(2, 9);
        this.name = name;
    }
}

class Task {
    constructor(title, category, assignedUserId) {
        this.id = Math.random().toString(36).substr(2, 9);
        this.title = title;
        this.category = category;
        this.status = 'pending';
        this.assignedUserId = assignedUserId;
    }
}

class TodoService {
    constructor() {
        this.users = new Map();
        this.tasks = new Map();
    }
    
    async createUser(name) {
        const user = new User(name);
        this.users.set(user.id, user);
        return user;
    }
    
    async createTask(title, category, userId) {
        const task = new Task(title, category, userId);
        this.tasks.set(task.id, task);
        return task;
    }
    
    async updateTaskStatus(taskId, status) {
        const task = this.tasks.get(taskId);
        if (task) {
            task.status = status;
        }
    }
    
    async deleteTask(taskId) {
        return this.tasks.delete(taskId);
    }
    
    getTasksByUser(userId) {
        return Array.from(this.tasks.values())
                   .filter(task => task.assignedUserId === userId);
    }
    
    getTasksByCategory(category) {
        return Array.from(this.tasks.values())
                   .filter(task => task.category === category);
    }
    
    getAllTasks() {
        return Array.from(this.tasks.values());
    }
    
    // JSON storage simulation
    async saveToJSON() {
        const data = {
            users: Array.from(this.users.values()),
            tasks: Array.from(this.tasks.values())
        };
        console.log('Saving to JSON:', JSON.stringify(data, null, 2));
    }
}

// Interactive application
class TodoApp {
    constructor() {
        this.service = new TodoService();
        this.currentUsers = new Map();
        this.rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout
        });
    }
    
    async start() {
        console.log('=== Collaborative To-Do List Application (JavaScript) ===');
        
        // Create default users
        const alice = await this.service.createUser('Alice');
        const bob = await this.service.createUser('Bob');
        this.currentUsers.set('alice', alice);
        this.currentUsers.set('bob', bob);
        
        await this.showMenu();
    }
    
    async showMenu() {
        console.log('\n=== Main Menu ===');
        console.log('1. Create Task');
        console.log('2. Delete Task');
        console.log('3. Mark Task Complete');
        console.log('4. View Tasks');
        console.log('5. Run Concurrency Demo');
        console.log('6. Run Language-Specific Demo (async/await + JSON)');
        console.log('7. Exit');
        
        const choice = await this.getInput('Enter your choice: ');
        
        switch (choice) {
            case '1':
                await this.createTask();
                break;
            case '2':
                await this.deleteTask();
                break;
            case '3':
                await this.markTaskComplete();
                break;
            case '4':
                await this.viewTasks();
                break;
            case '5':
                await this.runConcurrencyDemo();
                break;
            case '6':
                await this.runLanguageSpecificDemo();
                break;
            case '7':
                console.log('Thank you for using the Collaborative To-Do List!');
                this.rl.close();
                return;
            default:
                console.log('Invalid choice. Please try again.');
        }
        
        await this.showMenu();
    }
    
    async createTask() {
        console.log('\n=== Create Task ===');
        console.log('Available users: alice, bob');
        const username = await this.getInput('Enter username: ');
        
        if (!this.currentUsers.has(username)) {
            console.log('User not found!');
            return;
        }
        
        const title = await this.getInput('Enter task title: ');
        const category = await this.getInput('Enter category (Work/Personal): ');
        
        const task = await this.service.createTask(title, category, this.currentUsers.get(username).id);
        console.log('Task created successfully:', task.title);
    }
    
    async deleteTask() {
        console.log('\n=== Delete Task ===');
        const allTasks = this.service.getAllTasks();
        
        if (allTasks.length === 0) {
            console.log('No tasks available to delete.');
            return;
        }
        
        console.log('Available tasks:');
        allTasks.forEach((task, index) => {
            console.log(`${index + 1}. ${task.title} (${task.category})`);
        });
        
        const taskIndex = parseInt(await this.getInput('Enter task number to delete: ')) - 1;
        if (taskIndex >= 0 && taskIndex < allTasks.length) {
            const task = allTasks[taskIndex];
            const deleted = await this.service.deleteTask(task.id);
            if (deleted) {
                console.log('Task deleted successfully:', task.title);
            }
        } else {
            console.log('Invalid task number.');
        }
    }
    
    async markTaskComplete() {
        console.log('\n=== Mark Task Complete ===');
        const allTasks = this.service.getAllTasks();
        
        if (allTasks.length === 0) {
            console.log('No tasks available.');
            return;
        }
        
        console.log('Available tasks:');
        allTasks.forEach((task, index) => {
            console.log(`${index + 1}. ${task.title} - ${task.status}`);
        });
        
        const taskIndex = parseInt(await this.getInput('Enter task number to mark complete: ')) - 1;
        if (taskIndex >= 0 && taskIndex < allTasks.length) {
            const task = allTasks[taskIndex];
            await this.service.updateTaskStatus(task.id, 'completed');
            console.log('Task marked as completed:', task.title);
        } else {
            console.log('Invalid task number.');
        }
    }
    
    async viewTasks() {
        console.log('\n=== View Tasks ===');
        console.log('1. View all tasks');
        console.log('2. View tasks by user');
        console.log('3. View tasks by category');
        
        const choice = await this.getInput('Enter choice: ');
        
        switch (choice) {
            case '1':
                const allTasks = this.service.getAllTasks();
                console.log('\nAll tasks:');
                allTasks.forEach(task => 
                    console.log(`- ${task.title} (${task.category}) - ${task.status}`));
                break;
            case '2':
                const username = await this.getInput('Enter username (alice/bob): ');
                if (this.currentUsers.has(username)) {
                    const userTasks = this.service.getTasksByUser(this.currentUsers.get(username).id);
                    console.log(`\nTasks for ${username}:`);
                    userTasks.forEach(task => 
                        console.log(`- ${task.title} - ${task.status}`));
                } else {
                    console.log('User not found!');
                }
                break;
            case '3':
                const category = await this.getInput('Enter category (Work/Personal): ');
                const categoryTasks = this.service.getTasksByCategory(category);
                console.log(`\nTasks in ${category} category:`);
                categoryTasks.forEach(task => 
                    console.log(`- ${task.title} - ${task.status}`));
                break;
        }
    }
    
    async runConcurrencyDemo() {
        console.log('\n=== Running Concurrency Demo ===');
        
        // Create demo tasks
        await this.service.createTask('Demo Task 1', 'Work', this.currentUsers.get('alice').id);
        await this.service.createTask('Demo Task 2', 'Personal', this.currentUsers.get('bob').id);
        
        const promises = [];
        
        // Alice updates tasks
        promises.push((async () => {
            const aliceTasks = this.service.getTasksByUser(this.currentUsers.get('alice').id);
            for (const task of aliceTasks) {
                await this.service.updateTaskStatus(task.id, 'completed');
                console.log(`Alice completed: ${task.title}`);
            }
        })());
        
        // Bob updates tasks
        promises.push((async () => {
            const bobTasks = this.service.getTasksByUser(this.currentUsers.get('bob').id);
            for (const task of bobTasks) {
                await this.service.updateTaskStatus(task.id, 'in-progress');
                console.log(`Bob started: ${task.title}`);
            }
        })());
        
        await Promise.all(promises);
        console.log('Concurrency demo completed!');
    }
    
    async runLanguageSpecificDemo() {
        console.log('\n=== JavaScript Language-Specific Demo ===');
        console.log('Demonstrating: async/await + JSON storage');
        
        // Create users and tasks
        const alice = await this.service.createUser('Alice');
        const bob = await this.service.createUser('Bob');
        
        const task1 = await this.service.createTask('Write report', 'Work', alice.id);
        const task2 = await this.service.createTask('Buy groceries', 'Personal', bob.id);
        const task3 = await this.service.createTask('Fix bug', 'Work', alice.id);
        
        // Demonstrate task removal
        console.log('Before deletion - Total tasks:', this.service.getTasksByCategory('Work').length);
        await this.service.deleteTask(task1.id);
        console.log('After deletion - Total tasks:', this.service.getTasksByCategory('Work').length);
        
        // Demonstrate async/await concurrency
        const promises = [];
        
        // Alice updates tasks
        promises.push((async () => {
            const aliceTasks = this.service.getTasksByUser(alice.id);
            for (const task of aliceTasks) {
                await this.service.updateTaskStatus(task.id, 'completed');
                console.log(`Alice completed: ${task.title}`);
            }
        })());
        
        // Bob updates tasks
        promises.push((async () => {
            const bobTasks = this.service.getTasksByUser(bob.id);
            for (const task of bobTasks) {
                await this.service.updateTaskStatus(task.id, 'in-progress');
                console.log(`Bob started: ${task.title}`);
            }
        })());
        
        // Wait for all concurrent operations
        await Promise.all(promises);
        
        // Show results
        console.log('\nAll tasks:');
        this.service.getTasksByCategory('Work').forEach(task => 
            console.log(`${task.title} - ${task.status}`));
        
        // Demonstrate JSON storage
        await this.service.saveToJSON();
    }
    
    getInput(prompt) {
        return new Promise((resolve) => {
            this.rl.question(prompt, resolve);
        });
    }
}

// Run the application
const app = new TodoApp();
app.start().catch(console.error);