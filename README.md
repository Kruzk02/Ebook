# EBook

EBook is a project aimed at share Ebook using Spring Boot in Java.

## Installation

1. Clone the repository: ` https://github.com/Kruzk02/Ebook.git `.
2. Navigate to the project directory: cd ` Ebook `.
3. Start the project using Maven: `mvn clean spring-boot:run`.
4. Access the API at <http://localhost:8080>.
   
## Usage

Once the application is running, you can use tools like Postman or curl to interact with the API endpoints. Here's an example:
1. **Create a new user**:
  Send a POST request to `/api/auth/register` with the following JSON payload:
   ```json
      {
      "username": "myname",
      "password": "password123",
      "email": "myemail@example.com"
      }
   ```
3. **Authenticate the user**:
 Send a POST request to `/api/auth/login` with the following JSON payload to receive a JWT token:
   ```json
      {
      "username": "myname",
      "password": "password123"
      }
   ```
5. Use the generated JWT token to access protected endpoints.

# Configuration

- Redis configuration: Configure redis connection properties in `RedisConfig.java`.
- Async configuration: Configure async properties in `AsyncConfig.java`.
- Security configuration: Customize security settings in `SecurityConfig.java`.

# Contributing 
Contributions are welcome! Please fork the repository and create a pull request with your changes.
