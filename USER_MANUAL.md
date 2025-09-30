# Travel Plan Assistant - User Manual

## Table of Contents
1. [System Overview](#system-overview)
2. [Getting Started](#getting-started)
3. [User Plan Status Scenarios](#user-plan-status-scenarios)
4. [Complete User Workflow](#complete-user-workflow)
5. [Feature Details](#feature-details)
6. [Troubleshooting](#troubleshooting)

## System Overview

The Travel Plan Assistant is a comprehensive web application that helps users create, discover, and manage group travel plans. The system supports real-time communication, collaborative decision-making through polls, and shared expense management.

### Key Features
- **User Management**: Registration, authentication, and profile management
- **Travel Plan Creation**: Create detailed travel plans with multiple criteria
- **Plan Discovery**: Search and filter travel plans based on preferences
- **Member Management**: Invite users, accept applications, and manage group members
- **Real-time Communication**: Chat room functionality with WebSocket support
- **Polling System**: Create and participate in polls for group decisions
- **Expense Sharing**: Track and split shared expenses among group members

## Getting Started

### 1. User Registration

**Test Users for Demonstration:**
- **Tester AAA**: Birthday 1990, English speaker
- **Tester BBB**: Birthday 2000, Japanese speaker

**Registration Process:**
1. Navigate to the registration page
2. Fill in required information:
   - Email address
   - Password
   - First name and last name
   - Date of birth
   - Gender
   - Preferred language
   - Location (city, country)
3. Submit registration
4. System automatically creates user profile

### 2. User Login/Logout

**Login Process:**
1. Enter email and password
2. System validates credentials
3. JWT token is generated for session management
4. User is redirected to the main dashboard

**Logout Process:**
1. Click logout button
2. JWT token is invalidated
3. User is redirected to login page

## User Plan Status Scenarios

Understanding user plan statuses is crucial for navigating the system effectively. Here are the different scenarios and their meanings:

### User Plan Status Values

| Status | Description | When It Occurs |
|--------|-------------|----------------|
| **OWNED** | User created/owns the travel plan | When user creates a new travel plan |
| **APPLIED** | User applied to join a travel plan | When user applies to a public plan |
| **APPLIED_CANCELLED** | User cancelled their application | When applicant cancels their own application |
| **APPLIED_ACCEPTED** | Application was accepted by owner | When plan owner accepts the application |
| **APPLIED_REFUSED** | Application was refused by owner | When plan owner refuses the application |
| **INVITED** | User was invited to join a plan | When plan owner sends invitation |
| **INVITED_ACCEPTED** | Invitation was accepted by invitee | When invitee accepts the invitation |
| **INVITED_REFUSED** | Invitation was refused by invitee | When invitee refuses the invitation |

### Current Plan Scenarios

**When a user HAS a current plan:**
- User has status `OWNED`, `APPLIED_ACCEPTED`, or `INVITED_ACCEPTED` for a plan with status `NEW` or `IN_PROGRESS`
- User can access plan details, chat room, polls, and shared expenses
- User CANNOT apply to other plans or be invited to other plans
- User appears in "Current" tab of My Plans

**When a user DOES NOT have a current plan:**
- User has no active plan relationships
- User can search and apply to plans in Discovery
- User can be invited to plans
- User sees "No current plans" in My Plans Current tab

### Discovery Restrictions

Users can only see and apply to plans that meet ALL these criteria:
- Plan status is `NEW`
- Plan type is `PUBLIC`
- Plan is not full (current members < max members)
- User is not already a member of the plan
- User meets age, language, and gender restrictions (if specified)

## Complete User Workflow

### Step 1: Create Travel Plan (Tester AAA)

**Using Tester AAA account:**

1. **Navigate to My Plans → Current tab**
2. **Click "Create Plan" button**
3. **Fill in plan details:**
   - Title: "Weekend Hiking Trip"
   - Category: TRIP
   - Plan Type: PUBLIC or PRIVATE
   - Destination: "Banff National Park"
   - Start Date: Future date
   - End Date: After start date
   - Max Members: 4
   - Description: Detailed plan description
   - Age restrictions: 18-50
   - Language: English
   - Gender preference: ANY

4. **Submit plan creation**
5. **System automatically:**
   - Creates plan with status `NEW`
   - Sets user status to `OWNED`
   - Shows plan in Current tab

### Step 2: Invite Members (Tester AAA)

**Invite liangli5100@gmail.com:**

1. **Open plan details**
2. **Click "Invite Member" button**
3. **Enter email: liangli5100@gmail.com**
4. **Send invitation**
5. **System automatically:**
   - Creates `INVITED` status for invitee
   - Sends email notification
   - Shows invitation in plan members list

### Step 3: Handle Invitations (liangli5100@gmail.com)

**Login with liangli5100@gmail.com:**

1. **Check email for invitation notification**
2. **Navigate to plan details**
3. **Two options:**
   - **Accept**: Status changes to `INVITED_ACCEPTED`
   - **Refuse**: Status changes to `INVITED_REFUSED`

**Different warning scenarios:**
- **APPLIED_REFUSED**: "User has already been refused to join the plan"
- **APPLIED_CANCELLED**: "User has already cancelled the application"
- **INVITED_REFUSED**: "User has already refused to join the plan"

### Step 4: Discovery and Application (Tester BBB)

**Using Tester BBB account:**

1. **Navigate to Discovery page**
2. **Search for available plans:**
   - Only shows `NEW`, `PUBLIC`, not full plans
   - Filters by age, language, gender restrictions
   - Excludes plans user is already part of

3. **Click on a plan to view details**
4. **Click "Apply" button**
5. **System automatically:**
   - Creates `APPLIED` status
   - Shows application to plan owner
   - Prevents user from applying to other plans

### Step 5: Handle Applications (Tester AAA)

**As plan owner:**

1. **Open plan details**
2. **View pending applications in members list**
3. **For each application:**
   - **Accept**: Status changes to `APPLIED_ACCEPTED`
   - **Refuse**: Status changes to `APPLIED_REFUSED`

4. **Automatic rejection when plan is full:**
   - When max members reached, remaining applications are automatically refused
   - System shows green for decided members, blue for pending

### Step 6: CRON Job - Plan Start

**Automatic plan start (scheduled job):**

1. **CRON job runs twice daily (01:00 and 13:00)**
2. **For plans starting today:**
   - Automatically refuses all pending applications and invitations
   - Changes plan status from `NEW` to `IN_PROGRESS`
   - Sends start reminder emails to all members
   - Enables chat room, polls, and shared expenses

### Step 7: Active Plan Features

**During IN_PROGRESS status:**

**Chat Room:**
- Real-time messaging via WebSocket
- Only active members can participate
- Message history is preserved

**Polls:**
- Create polls for group decisions
- Multiple choice or single choice options
- Time-based expiration
- Anonymous or named voting

**Shared Expenses:**
- Record expenses paid by any member
- Automatically calculate individual shares
- Upload receipt images
- Track payment status

### Step 8: CRON Job - Plan Completion

**Automatic plan completion:**

1. **CRON job runs twice daily (01:10 and 13:10)**
2. **For plans ending today:**
   - Changes plan status from `IN_PROGRESS` to `COMPLETED`
   - Sends completion reminder emails
   - Moves plan to History tab

### Step 9: History Tab

**View completed plans:**

1. **Navigate to My Plans → History tab**
2. **View all completed and cancelled plans**
3. **Click on plan to view details**
4. **Access chat history, polls, and expense records**

### Step 10: User Profile Management

**Profile features:**
- Edit personal information
- Upload profile picture
- Update preferences
- View plan statistics

## Feature Details

### Plan Types

**PUBLIC Plans:**
- Visible in Discovery page
- Anyone can apply (if they meet criteria)
- Searchable and filterable

**PRIVATE Plans:**
- Not visible in Discovery
- Only accessible via direct invitation
- Owner must invite specific users

### Member Management

**Active Members (counted toward max):**
- `OWNED` - Plan creator
- `APPLIED_ACCEPTED` - Accepted applicants
- `INVITED_ACCEPTED` - Accepted invitees

**Pending Members (not counted):**
- `APPLIED` - Pending applications
- `INVITED` - Pending invitations

**Inactive Members:**
- `APPLIED_REFUSED` - Refused applications
- `APPLIED_CANCELLED` - Cancelled applications
- `INVITED_REFUSED` - Refused invitations

### Email Notifications

**Automatic notifications sent for:**
- Invitation received
- Application status updates
- Plan start reminders
- Plan completion reminders
- Custom notifications from plan owner

### Real-time Updates

**Polling mechanisms:**
- Discovery page: 15-second intervals
- My Plans: 12-second intervals
- Plan Details: 10-second intervals
- Chat room: Real-time via WebSocket

## Troubleshooting

### Common Issues

**"User already has a current travel plan"**
- User must complete or leave current plan before joining another
- Check My Plans → Current tab for active plans

**"Plan is not available for application"**
- Plan might be private, full, or not in NEW status
- Check plan details for current status

**"User already has a relationship with this plan"**
- User has previously applied, been invited, or is already a member
- Check plan members list for user's current status

**"Travel plan has reached maximum number of members"**
- Plan is full and cannot accept more members
- Wait for someone to leave or find another plan

### Status Color Coding

**In plan member lists:**
- **Green**: Decided members (accepted/owned)
- **Blue**: Pending members (applied/invited)
- **Red**: Refused members (refused/cancelled)

### Plan Status Transitions

**NEW → IN_PROGRESS**: Automatic via CRON job on start date
**IN_PROGRESS → COMPLETED**: Automatic via CRON job on end date
**NEW/IN_PROGRESS → CANCELLED**: Manual by plan owner

### Best Practices

1. **Plan Creation**: Set realistic member limits and clear descriptions
2. **Invitations**: Send invitations to users who are likely to participate
3. **Applications**: Review applications promptly to maintain user engagement
4. **Communication**: Use chat room for real-time coordination
5. **Decision Making**: Use polls for group decisions
6. **Expense Tracking**: Record expenses promptly for accurate sharing

---

This manual provides comprehensive guidance for using the Travel Plan Assistant system. For technical support or additional questions, please refer to the system documentation or contact the development team.
