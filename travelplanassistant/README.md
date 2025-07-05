# Group Travel Assistant

A comprehensive web-based application for creating, managing, and collaborating on travel plans. Built with Spring Boot and React.js, this application enables users to organize group travel activities, communicate in real-time, conduct polls, and manage shared expenses.

## Features

### Core Functionality
- **User Management**: Registration, authentication, and profile management
- **Travel Plan Creation**: Create detailed travel plans with multiple criteria
- **Plan Discovery**: Search and filter travel plans based on preferences
- **Member Management**: Invite users, accept applications, and manage group members
- **Real-time Communication**: Chat room functionality with WebSocket support
- **Polling System**: Create and participate in polls for group decisions
- **Expense Sharing**: Track and split shared expenses among group members

### Travel Plan Features
- **Plan Types**: Support for trips, concerts, sports games, weddings, and business events
- **Filtering System**: Filter plans by destination, transport type, gender preference, age range, language, and budget
- **Status Management**: Automatic status transitions (Created → In Progress → Completed/Cancelled)
- **Visibility Control**: Public and private plan options
- **Member Limits**: Configurable minimum and maximum member counts

### Collaboration Tools
- **Real-time Chat**: WebSocket-based chat rooms for each travel plan
- **Polling**: Create polls with multiple options and time-based expiration
- **Expense Tracking**: Record shared expenses and automatically calculate individual shares
- **Email Notifications**: System notifications for important events

## Technology Stack

### Backend
- **Java 17**: Core programming language
- **Spring Boot 3.3.4**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence
- **Spring WebSocket**: Real-time communication
- **MySQL 8.0**: Database
- **JWT**: Token-based authentication
- **Maven**: Build tool

### Frontend (Planned)
- **React.js 18**: User interface framework
- **Tailwind CSS 3.4**: Styling framework
- **WebSocket**: Real-time communication client

## Project Structure

```
travelplanassistant/
├── src/
│   ├── main/
│   │   ├── java/com/comp8047/majorproject/travelplanassistant/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── security/         # Security configuration
│   │   │   ├── service/          # Business logic
│   │   │   └── TravelplanassistantApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Test classes
├── pom.xml                       # Maven dependencies
└── README.md
```

## Database Schema

### Core Entities
- **User**: User profiles and authentication
- **TravelPlan**: Travel plan details and metadata
- **UserPlanStatus**: Relationship between users and travel plans
- **ChatMessage**: Real-time chat messages
- **Poll**: Polling questions and options
- **SharedExpense**: Expense tracking and allocation

### Key Relationships
- Users can own, apply to, or be invited to travel plans
- Travel plans have multiple members with different statuses
- Chat messages, polls, and expenses are associated with travel plans

## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Database Setup
1. Create a MySQL database:
```sql
CREATE DATABASE travel_assistant;
```

2. Update `application.properties` with your database credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Application Setup
1. Clone the repository:
```bash
git clone <repository-url>
cd travelplanassistant
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Configuration
Update the following properties in `application.properties`:
- Database connection details
- JWT secret key
- Email configuration (for notifications)
- Application-specific settings

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user profile

### Travel Plan Endpoints
- `GET /api/travel-plans` - List travel plans
- `POST /api/travel-plans` - Create new travel plan
- `GET /api/travel-plans/{id}` - Get travel plan details
- `PUT /api/travel-plans/{id}` - Update travel plan
- `DELETE /api/travel-plans/{id}` - Delete travel plan

### User Management Endpoints
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users/search` - Search users

### Chat Endpoints
- `GET /api/chat/{travelPlanId}/messages` - Get chat messages
- `POST /api/chat/{travelPlanId}/messages` - Send message
- WebSocket endpoint: `/ws/chat/{travelPlanId}`

### Polling Endpoints
- `GET /api/polls/{travelPlanId}` - Get polls for travel plan
- `POST /api/polls` - Create new poll
- `POST /api/polls/{pollId}/vote` - Vote on poll

### Expense Endpoints
- `GET /api/expenses/{travelPlanId}` - Get expenses for travel plan
- `POST /api/expenses` - Create new expense
- `PUT /api/expenses/{expenseId}/settle` - Mark expense as settled

## Testing

### Unit Tests
Run unit tests:
```bash
mvn test
```

### Integration Tests
The application includes integration tests for:
- User registration and authentication
- Travel plan creation and management
- Chat functionality
- Polling system
- Expense tracking

## Deployment

### Production Setup
1. Update `application.properties` for production environment
2. Set up MySQL database with proper credentials
3. Configure email service for notifications
4. Set secure JWT secret key
5. Configure CORS settings for your domain

### Docker Deployment (Future)
```bash
# Build Docker image
docker build -t travel-assistant .

# Run container
docker run -p 8080:8080 travel-assistant
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

## Roadmap

### Phase 1 (Current)
- [x] Basic user authentication
- [x] Travel plan creation and management
- [x] User-plan relationship management
- [ ] Chat functionality
- [ ] Polling system
- [ ] Expense tracking

### Phase 2 (Future)
- [ ] React.js frontend
- [ ] Real-time notifications
- [ ] File upload for receipts
- [ ] Advanced search and filtering
- [ ] Mobile responsiveness

### Phase 3 (Future)
- [ ] Mobile application
- [ ] Payment integration
- [ ] Social features
- [ ] Advanced analytics 