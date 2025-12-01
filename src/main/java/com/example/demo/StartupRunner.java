package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebClient webClient = WebClient.create();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("APP STARTED — Step 2 running...");

        // Replace these with your webhook URL and access token from Step 5 output
        String webhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJyZWdObyI6IjIyQkFJMTE1OCIsIm5hbWUiOiJTdW5lZXRoIFMiLCJlbWFpbCI6InN1bmVldGguczIwMjJAdml0c3R1ZGVudC5hYy5pbiIsInN1YiI6IndlYmhvb2stdXNlciIsImlhdCI6MTc2NDU4NjMyOSwiZXhwIjoxNzY0NTg3MjI5fQ.0p7NUadvQJxmB-yzygNwloIPC4jLXC9plQn1JzLY0Ms";

        // Final SQL query string (all on one line, quotes escaped)
        String finalSqlQuery = "WITH HighEarners AS (SELECT DISTINCT e.emp_id, e.first_name, e.last_name, e.dob, e.department FROM employee e JOIN payments p ON e.emp_id = p.emp_id WHERE p.amount > 70000), AgeCalc AS (SELECT department, EXTRACT(YEAR FROM AGE(CURRENT_DATE, dob)) AS age, first_name || ' ' || last_name AS full_name FROM HighEarners), Agg AS (SELECT d.department_name, AVG(age) AS average_age, STRING_AGG(full_name, ', ' ORDER BY full_name) AS employee_list FROM AgeCalc a JOIN department d ON a.department = d.department_id GROUP BY d.department_name, d.department_id) SELECT department_name, average_age, CASE WHEN array_length(string_to_array(employee_list, ', '), 1) > 10 THEN array_to_string(array_slice(string_to_array(employee_list, ', '), 1, 10), ', ') ELSE employee_list END AS employee_list FROM Agg ORDER BY department_id DESC;";

        // Create JSON body with escaped quotes
        String jsonBody = "{ \"finalQuery\": \"" + finalSqlQuery.replace("\"", "\\\"") + "\" }";

        // Send POST request to webhook with JWT token authorization
        webClient.post()
                .uri(webhookUrl)
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    System.out.println("Response from webhook: " + response);
                })
                .block();
    }
}
