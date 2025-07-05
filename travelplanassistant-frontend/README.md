# Travel Plan Assistant

A comprehensive React-based travel planning application that allows users to discover, create, and manage travel plans with friends.

## Features

### Authentication
- User registration with comprehensive form validation
- User login with email/password
- Profile management with avatar upload
- Secure authentication context

### Plan Management
- **Discovery**: Browse and search for public travel plans
- **My Plans**: View current and historical plans
- **Plan Creation**: Create new plans with images and details
- **Plan Details**: View plan information and manage members

### User Roles & Statuses
- **Plan Status**: new, in_progress, completed, cancelled
- **User Plan Status**: owned, applied, applied_accepted, applied_refused, invited, invited_accepted, invited_refused

### In-Progress Features (for active plans)
- **Chat Room**: Real-time messaging between plan members
- **Polls**: Create and vote on polls for group decisions
- **Shared Expenses**: Track and split expenses among members

### Member Management
- Invite users by email
- Accept/refuse applications
- Manage member permissions
- View user profiles

## Technology Stack

- **React 19** with Vite
- **Material-UI** for UI components
- **React Router** for navigation
- **Formik & Yup** for form validation
- **LocalStorage** for data persistence (demo purposes)

## Getting Started

### Prerequisites
- Node.js (version 16 or higher)
- npm or yarn

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd travelplanassistant-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

4. Open your browser and navigate to `http://localhost:5173`

## Usage

### First Time Setup
1. Click "Register" on the welcome page
2. Fill in your profile information and upload an avatar
3. Complete registration to be redirected to login
4. Log in with your credentials

### Creating a Plan
1. Navigate to "My Plans" from the bottom navigation
2. Click "NEW" button if no current plans exist
3. Fill in plan details including title, description, dates, and location
4. Upload images (optional)
5. Set visibility and maximum members
6. Click "Create Plan"

### Discovering Plans
1. Navigate to "Discovery" from the bottom navigation
2. Browse available public plans
3. Use the search bar to find specific plans
4. Click on a plan card to view details
5. Click "Apply" to join a plan

### Managing Plans
- **As Owner**: Accept/refuse applications, invite new members
- **As Member**: Participate in polls, chat, and expense tracking
- **As Applicant**: Cancel application if not yet accepted

## Project Structure

```
src/
├── components/          # Reusable UI components
│   └── MainLayout.jsx  # Main layout with bottom navigation
├── contexts/           # React contexts
│   └── AuthContext.jsx # Authentication state management
├── pages/              # Application pages
│   ├── Welcome.js      # Landing page
│   ├── Login.js        # Login form
│   ├── Register.js     # Registration form
│   ├── Discovery.jsx   # Plan discovery
│   ├── MyPlans.jsx     # User's plans
│   ├── Profile.jsx     # User profile
│   ├── EditProfile.jsx # Profile editing
│   ├── PlanDetails.jsx # Plan details and management
│   ├── UserProfile.jsx # Other user profiles
│   ├── CreatePlan.jsx  # Plan creation
│   ├── ChatRoom.jsx    # In-plan chat
│   ├── Poll.jsx        # Poll listing
│   ├── CreatePoll.jsx  # Poll creation
│   ├── SharedExpense.jsx # Expense listing
│   └── CreateSharedExpense.jsx # Expense creation
├── App.jsx             # Main application component
├── main.jsx           # Application entry point
└── index.css          # Global styles
```

## Data Storage

This application uses localStorage for data persistence (for demo purposes). In a production environment, this would be replaced with a proper backend API.

### LocalStorage Keys
- `users`: Registered user data
- `plans`: Travel plan data
- `chat_${planId}`: Chat messages for each plan
- `polls_${planId}`: Polls for each plan
- `expenses_${planId}`: Expenses for each plan

## Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Code Style

The project uses ESLint for code linting and follows React best practices with functional components and hooks.

## Future Enhancements

- Backend API integration
- Real-time WebSocket connections
- Image upload to cloud storage
- Push notifications
- Mobile app development
- Advanced search and filtering
- Plan templates
- Integration with travel APIs

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
