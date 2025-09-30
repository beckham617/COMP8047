# SharedExpense API Documentation

## Overview
The SharedExpenseController provides REST endpoints for managing shared expenses within travel plans. Users can create, view, update, and delete shared expenses, as well as track payment status for expense allocations.

## Base URL
```
/api/shared-expenses
```

## Authentication
All endpoints require authentication. The current user is automatically determined from the authentication context.

---

## Endpoints

### 1. Create Shared Expense
**POST** `/api/shared-expenses`

Creates a new shared expense for a travel plan.

**Request Body:**
```json
{
  "description": "Dinner at Italian Restaurant",
  "purpose": "Group meal",
  "travelPlanId": 1,
  "totalAmount": 120.50,
  "currency": "CAD",
  "expenseDate": "2025-09-15T19:30:00",
  "receipt": "receipt_001.jpg",
  "participants": [
    {
      "userId": 2,
      "amountOwed": 40.17,
      "isPaid": false
    },
    {
      "userId": 3,
      "amountOwed": 40.17,
      "isPaid": false
    },
    {
      "userId": 4,
      "amountOwed": 40.16,
      "isPaid": false
    }
  ]
}
```

**Response:** `201 Created`
```json
{
  "id": 15,
  "description": "Dinner at Italian Restaurant",
  "purpose": "Group meal",
  "travelPlanId": 1,
  "travelPlanTitle": "European Adventure",
  "paidBy": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "profilePicture": "profile_001.jpg"
  },
  "totalAmount": 120.50,
  "currency": "CAD",
  "expenseDate": "2025-09-15T19:30:00",
  "createdAt": "2025-09-15T20:00:00",
  "participants": [...],
  "isSettled": false,
  "totalPaid": 0.00,
  "totalOwed": 120.50
}
```

---

### 2. Get Expenses for Travel Plan
**GET** `/api/shared-expenses/travel-plan/{travelPlanId}`

Retrieves all shared expenses for a specific travel plan with pagination.

**Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: expenseDate)
- `sortDir` (optional): Sort direction (default: desc)

**Example:**
```
GET /api/shared-expenses/travel-plan/1?page=0&size=5&sortBy=expenseDate&sortDir=desc
```

**Response:** `200 OK`
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 25,
  "totalPages": 5,
  "first": true,
  "last": false,
  "numberOfElements": 5
}
```

---

### 3. Get Specific Expense
**GET** `/api/shared-expenses/{expenseId}`

Retrieves details of a specific shared expense.

**Response:** `200 OK`
```json
{
  "id": 15,
  "description": "Dinner at Italian Restaurant",
  "purpose": "Group meal",
  // ... full expense details
}
```

---

### 4. Update Shared Expense
**PUT** `/api/shared-expenses/{expenseId}`

Updates an existing shared expense. Only the person who paid can update.

**Request Body:** Same as create expense

**Response:** `200 OK` - Updated expense object

---

### 5. Delete Shared Expense
**DELETE** `/api/shared-expenses/{expenseId}`

Deletes a shared expense. Only the person who paid can delete.

**Response:** `200 OK`
```json
{
  "message": "Shared expense deleted successfully"
}
```

---

### 6. Mark Participant as Paid
**PATCH** `/api/shared-expenses/{expenseId}/participants/{participantId}/mark-paid`

Marks a participant's payment as received. Only the person who paid can mark payments.

**Response:** `200 OK` - Updated expense object

---

### 7. Get Expense Summary
**GET** `/api/shared-expenses/travel-plan/{travelPlanId}/summary`

Gets expense summary for the current user in a travel plan.

**Response:** `200 OK`
```json
{
  "totalPaid": 450.75,
  "totalOwed": 320.50,
  "totalOwedToPay": 120.25,
  "balance": 130.25
}
```

---

### 8. Get My Debts (Coming Soon)
**GET** `/api/shared-expenses/my-debts`

Retrieves expenses where the current user owes money.

---

### 9. Get My Credits (Coming Soon)
**GET** `/api/shared-expenses/my-credits`

Retrieves expenses where the current user is owed money.

---

### 10. Health Check
**GET** `/api/shared-expenses/health`

Health check endpoint for the service.

**Response:** `200 OK`
```json
{
  "status": "UP",
  "service": "SharedExpenseController",
  "timestamp": "2025-09-15T20:00:00"
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "You are not a member of this travel plan"
}
```

### 404 Not Found
```json
{
  "error": "Shared expense not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "An unexpected error occurred: [error details]"
}
```

---

## Business Rules

1. **Access Control**: Only members of a travel plan can view and create expenses for that plan
2. **Ownership**: Only the person who paid can update or delete an expense
3. **Payment Tracking**: Only the payer can mark participant payments as received
4. **Allocation Management**: Expenses are allocated to participants with specific amounts
5. **Currency Support**: Expenses support different currencies (default: CAD)
6. **Status Tracking**: Expenses can be PENDING or SETTLED
7. **Receipt Management**: Optional receipt images can be attached to expenses

---

## Data Models

### SharedExpenseRequest
- `description` (required): Expense description (max 500 chars)
- `purpose` (required): Expense purpose (max 200 chars)
- `travelPlanId` (required): ID of the travel plan
- `totalAmount` (required): Total expense amount (positive)
- `currency`: Currency code (default: CAD)
- `expenseDate`: Date of expense (default: current time)
- `receipt`: Receipt image filename
- `participants`: List of expense participants

### ExpenseParticipantRequest
- `userId` (required): ID of the participant
- `amountOwed` (required): Amount owed by participant (positive)
- `isPaid`: Whether the participant has paid (default: false)
