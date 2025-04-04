public class Employee {
    private String employeeId;
    private String name;
    private String role;
    private double performanceScore;

    public Employee(String employeeId, String name, String role, double performanceScore) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
        this.performanceScore = performanceScore;
    }

    // Getter và Setter
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %.2f", employeeId, name, role, performanceScore);
    }
}