import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

enum Category {
    FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, OTHER;
}

class Expense {
    private Category category;
    private double amount;
    private LocalDate date;
    private String description;

    public Expense(Category category, double amount, LocalDate date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("Category: %s | Amount: %.2f | Date: %s | Description: %s",
                category, amount, date, description);
    }
}

class ExpenseManager {
    private List<Expense> expenses;
    private double dailyLimit;

    public ExpenseManager() {
        expenses = new ArrayList<>();
        dailyLimit = 0.0;
    }

    public void setDailyLimit(double limit) {
        this.dailyLimit = limit;
        System.out.printf("Daily limit set to %.2f\n", limit);
    }

    private double getTotalForDate(LocalDate date) {
        return expenses.stream()
                .filter(expense -> expense.getDate().isEqual(date))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public void addExpense(Expense expense) {
        double totalForDay = getTotalForDate(expense.getDate());
        if ((totalForDay + expense.getAmount()) > dailyLimit && dailyLimit > 0) {
            System.out.printf("Warning: Adding this expense will exceed your daily limit of %.2f!\n", dailyLimit);
        } else {
            expenses.add(expense);
            System.out.println("Expense added!");
        }
    }

    public void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("Expenses:");
        expenses.forEach(expense -> System.out.println(expense));
    }

    public void removeExpense(int index) {
        try {
            Expense removedExpense = expenses.remove(index);
            System.out.println("Expense removed: " + removedExpense);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid index.");
        }
    }

    public void calculateDailyExpense(LocalDate date) {
        double total = getTotalForDate(date);
        System.out.println("Expenses for date: " + date);
        List<Expense> dailyExpenses = expenses.stream()
                .filter(expense -> expense.getDate().isEqual(date))
                .collect(Collectors.toList());
        
        if (dailyExpenses.isEmpty()) {
            System.out.println("No expenses recorded for this date.");
        } else {
            dailyExpenses.forEach(System.out::println);
            System.out.printf("Total expenses for %s: %.2f\n", date, total);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();
        Scanner scanner = new Scanner(System.in);
        String choice;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        do {
            System.out.println("\n1. Set Daily Limit");
            System.out.println("2. Add Expense");
            System.out.println("3. View Expenses");
            System.out.println("4. Remove Expense");
            System.out.println("5. View Expenses for a Specific Date");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter the daily expense limit: ");
                    double limit = Double.parseDouble(scanner.nextLine());
                    manager.setDailyLimit(limit);
                    break;
                case "2":
                    try {
                        System.out.print("Enter category (FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, OTHER): ");
                        Category category = Category.valueOf(scanner.nextLine().toUpperCase());
                        System.out.print("Enter amount: ");
                        double amount = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormatter);
                        System.out.print("Enter description: ");
                        String description = scanner.nextLine();
                        manager.addExpense(new Expense(category, amount, date, description));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid category or date format. Please try again.");
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    break;
                case "3":
                    manager.viewExpenses();
                    break;
                case "4":
                    System.out.print("Enter expense index to remove: ");
                    int index = Integer.parseInt(scanner.nextLine()) - 1;
                    manager.removeExpense(index);
                    break;
                case "5":
                    System.out.print("Enter the date to view expenses (YYYY-MM-DD): ");
                    try {
                        LocalDate expenseDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
                        manager.calculateDailyExpense(expenseDate);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    break;
                case "6":
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (!choice.equals("6"));
        
        scanner.close();
    }
}
