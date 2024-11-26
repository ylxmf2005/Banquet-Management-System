# Table of Contents

1. [Introduction](#1-introduction)
2. [Demo](#2-demo)
3. [Getting Started](#3-getting-started)
   - 3.1. [User Notice](#31-user-notice)
   - 3.2. [Backend](#32-backend)
     - 3.2.1. [Port Configuration](#321-port-configuration)
     - 3.2.2. [Database Configuration](#322-database-configuration)
     - 3.2.3. [Start Server](#323-start-server)
     - 3.2.4. [Database Initialization/Reset](#324-database-initializationreset)
   - 3.3. [Frontend](#33-frontend)
     - 3.3.1. [API Endpoint Configuration](#331-api-endpoint-configuration)
     - 3.3.2. [Start Server](#332-start-server)
4. [Registration](#4-registration)
5. [Login](#5-login)
6. [Admin Page](#6-admin-page)
   - 6.1. [Banquet Management](#61-banquet-management)
     - 6.1.1. [Create New Banquet](#611-create-new-banquet)
     - 6.1.2. [Menu](#612-menu)
     - 6.1.3. [Edit](#613-edit)
     - 6.1.4. [Delete](#614-delete)
     - 6.1.5. [Previous/Next Page](#615-previousnext-page)
   - 6.2. [Attendee Management](#62-attendee-management)
     - 6.2.1. [Search Attendee by Email](#621-search-attendee-by-email)
     - 6.2.2. [Update Attendee](#622-update-attendee)
     - 6.2.3. [Delete Attendee](#623-delete-attendee)
     - 6.2.4. [Update Attendee's Registration](#624-update-attendees-registration)
     - 6.2.5. [Delete Attendee's Registration](#625-delete-attendees-registration)
   - 6.3. [Registration Management](#63-registration-management)
     - 6.3.1. [Search Registrations by BIN](#631-search-registrations-by-bin)
     - 6.3.2. [Edit Registration](#632-edit-registration)
     - 6.3.3. [Delete Registration](#633-delete-registration)
   - 6.4. [Report Generation](#64-report-generation)
7. [User Page](#7-user-page)
   - 7.1. [Profile Tab](#71-profile-tab)
   - 7.2. [Banquet Registration Tab](#72-banquet-registration-tab)
     - 7.2.1. [Search Available Banquets](#721-search-available-banquets)
     - 7.2.2. [Clear Search](#722-clear-search)
     - 7.2.3. [Register Available Banquets](#723-register-available-banquets)
   - 7.3. [My Registrations Tab](#73-my-registrations-tab)
     - 7.3.1. [Search Your Registration](#731-search-your-registration)
     - 7.3.2. [Clear Search](#732-clear-search)
     - 7.3.3. [Update Your Registration](#733-update-your-registration)
     - 7.3.4. [Unregister Your Registration](#734-unregister-your-registration)

# 1. Introduction

Welcome to the user guide for the Banquet Management System (BMS). This comprehensive guide provides detailed instructions on how to navigate and utilize the various features of the BMS, both for administrators and attendees. Whether you're managing banquets or registering for events, this guide aims to help you make the most of the system efficiently and effectively.

# 2. Demo

Access the demo of the BMS here: [Website](http://bms.ethanelift.com)

- Admin account: `bmsadmin@polyu.hk`
- Admin password: `2411project`

# 3. Getting Started

## 3.1 User Notice

- The BMS is a full-stack application with separate frontend and backend components that can be deployed independently. 
- The system implements data validation at both the frontend and database levels to ensure data integrity. 
- To reflect the raw database errors, the exact `SQLException` error messages from the backend are displayed directly to the user.

## 3.2 Backend

Make sure you have Gradle installed, recommended version: 8.11

###  3.2.1 Port Configuration

The backend server will run on port 2411 by default. To change the port:

- Navigate to `src/main/java/hk/polyu/comp/project2411/bms/service/RestAPIServer.java`
- Modify the port number in line: `Server server = new Server(2411);`

### 3.2.2 Database Configuration

The `SQLConnection` class at `src/main/java/hk/polyu/comp/project2411/bms/connection/SQLConnection.java` provides a ready-to-use Oracle database connection. By default, it connects to a pre-configured Oracle database server.

To use your own database server, modify these fields in the `SQLConnection` class:

```java:src/main/java/hk/polyu/comp/project2411/bms/connection/SQLConnection.java
private String url = "jdbc:oracle:thin:@your_host:your_port:your_sid";
private String username = "your_username";
private String password = "your_password";
```

- For Oracle: `jdbc:oracle:thin:@hostname:port:SID`
- For MySQL: `jdbc:mysql://hostname:port/database`
- For PostgreSQL: `jdbc:postgresql://hostname:port/database`

### 3.2.3 Start the Server

Run in development mode:

```bash
gradle run
```

Or build and run the production JAR:
```bash
gradle build
java -jar build/libs/BMS.jar
```

### 3.2.4 Database Initialization

By default:

- The system initializes the database with `clearIfExists=true`, meaning existing database tables will be cleared and recreated.
- Additionally, a parameter `createSampleData=true` is also set, which initializes the database with sample data during setup (may need a while).

To preserve existing database tables:

- Navigate to `src/main/java/hk/polyu/comp/project2411/bms/service/BMSMain.java`.

- Modify the initialization parameters to `initDatabase(false, false);`.
- Restart the server.

## 3.3 Frontend

### 3.3.1 API Endpoint Configuration

Make sure you have Node.js installed, recommended version: v23.3.0

Navigate to the frontend directory:

```bash
cd bms_frontend
```

Install dependencies:

```bash
npm install
```

The frontend needs to know where to find the backend API. There are two ways to configure this:

1. Environment Variables (Recommended):

   - Development: Create `.env.local`:

     ```env
     NEXT_PUBLIC_API_URL=http://localhost:2411
     ```

   - Production: Set in your hosting platform's dashboard.

2. Direct Configuration:

   - Edit `src/service/api.ts`:

     ```typescript
     baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:2411'
     ```

### 3.3.2 Start the Server

Start development server:

```bash
npm run dev
```

Now you can access your frontend at http://localhost:3000.

Or build for production:

```
npm run build
```

# 4. Registration

<img src="https://s2.loli.net/2024/11/26/u3Ug5WJMj7bZIoK.png" width="30%;" />

Restrictions:

- All field is required to register an account.
- First name and last name can only contain characters.
- Email address must be valid and have not been registered before, otherwise an error will occur:

  ![](https://s2.loli.net/2024/11/26/ZOxlYBLGmA89c3d.png)

- Password must be at least 6 characters and contain only letters, numbers, and common special characters.
- The Mobile Number must be eight digits.

# 5. Login

<img src="https://s2.loli.net/2024/11/26/drguwvtjS7ODWhy.png" width="80%;" />

The forgot password function is temporarily not available.

# 6. Admin Page

## 6.1 Banquet Management

<img src="https://s2.loli.net/2024/11/25/fQJKTOVevNFBnGd.png" width="80%;" />

### 6.1.1 Create New Banquet

<img src="https://s2.loli.net/2024/11/25/gR6FrSEHomuIiCk.png" width="80%;" />

Restrictions:

- All the fields shown in the picture are required.
- Quota must be a non-negative integer.
- Dish Name must be unique within the banquet.

Common errors:

- Input format error:

  <img src="https://s2.loli.net/2024/11/25/wSDsolBb54aPtNj.png" width="40%;" />

- Input length is too long:

  <img src="https://s2.loli.net/2024/11/25/7erLFX8qOuyVzYQ.png" width="100%;" />

- There are two identical dish names:

  <img src="https://s2.loli.net/2024/11/25/w9HEBMJ4rq2LtWy.png" width="90%;" />

If an error occurs during the creation of a meal, the already created banquet and its associated meals will also be rolled back to maintain consistency.

### 6.1.2 Menu

<img src="https://s2.loli.net/2024/11/25/EAnBagWpPj2XFcY.png" width="30%;" />

<img src="https://s2.loli.net/2024/11/25/sGEd7yamQN43CfM.png" width="50%;" />

### 6.1.3 Edit

<img src="https://s2.loli.net/2024/11/25/4yujtnpNc7ob8x5.png" width="80%;" />

It's similar to [Create New Banquet](#611-create-new-banquet).

Additional Note:

- BIN cannot be modified.
- Meal cannot be deleted.
- If you change a banquet's status from available to unavailable, any existing registrations for that banquet will remain unaffected. Attendees already registered will not be automatically removed.
- You can still modify the banquet information even if there are existing registrations, but:
  - It is not recommended to modify a dish name that attendees have already selected in their registrations (the participant's originally selected dish will be overwritten by the new one). If you need to make such changes, you'd better notify the attendees to reselect their meal or update the registrations manually via the Registration Management Tab.
  - It is not recommended to adjust the quota to a number smaller than the current number of registrations. The database does not restrict this action, as administrators may manually delete registrations under unavoidable circumstances.

### 6.1.4 Delete

<img src="https://s2.loli.net/2024/11/25/E2KHOtQnhiD9Mec.png" width="80%;" />

Before deletion, confirmation is required (All delete operations have this confirmation).

Note: Meals and registrations related to the banquet will also be deleted.

### 6.1.5 Previous/Next Page

When the number of banquets exceeds the maximum display limit per page, you can navigate through them using page turning.

## 6.2 Attendee Management

<img src="https://s2.loli.net/2024/11/25/y8QK34fasPmDwtM.png" width="80%;" />

### 6.2.1 Search Attendee By Email

When an attendee cannot be found, the system will display the following message:

<img src="https://s2.loli.net/2024/11/25/C1FLVwHQA2gWqvO.png" width="30%;" />

### 6.2.2 Update Attendee

- Updating attendee information follows the same constraints as when registering a new attendee.
- The password cannot be viewed (because the database encrypts storage passwords) but can be reset to a new one.
- If the attendee's email address is updated, the email address of their registration records will also be updated accordingly.
- Ensure that the updated email address does not conflict with any other attendee's email address; otherwise, an error will occur:

  <img src="https://s2.loli.net/2024/11/26/SQnFJoTYBx3iwsD.png" width="100%;" />

### 6.2.3 Delete Attendee

- Deleting an attendee will also delete all banquet registrations associated with them.
- The seat assigned to the attendee will be automatically released for reuse.

### 6.2.4 Update Attendee's Registration

<img src="https://s2.loli.net/2024/11/25/DKfasCnQHzqrO63.png" width="80%;" />

- The registration time cannot be modified.
- Only one of the four pre-configured meal options for the banquet can be selected.
- When updating an attendee's seat number:
  - Ensure the new seat number does not conflict with any already assigned seat numbers for the same banquet.
  - If a conflict occurs, the update will fail and an error message will be displayed:

    <img src="https://s2.loli.net/2024/11/25/2R4F7odBUbqH5W3.png" width="80%;" />

### 6.2.5 Delete Attendee's Registration

When deleting an attendee's registration, the seat assigned to the attendee for that registration will be automatically released for reuse.

## 6.3 Registration Management

<img src="https://s2.loli.net/2024/11/26/MKWpv7fkFeQxHc3.png" />

The system generates visual statistics for drink choice and meal choice. These bar charts provide an overview of attendee preferences.

### 6.3.1 Search Registrations By BIN

Use this feature to manage registrations for a specific banquet.

### 6.3.2 Edit Registration

It's similar to [6.2.4 Update Attendee's Registration](#624-update-attendees-registration).

### 6.3.3 Delete Registration

It's similar to [6.2.5 Delete Attendee's Registration](#625-delete-attendees-registration).

## 6.4 Report Generation

<img src="https://s2.loli.net/2024/11/26/aHnfNJL1he9Vu8z.png" width="80%;" />

Press the `DOWNLOAD REPORT` button, and the report PDF will be generated and downloaded automatically.

# 7. User Page

## 7.1 Profile Tab

<img src="https://s2.loli.net/2024/11/26/EL3psezQW6gNxXt.png" width="80%;" />

It's similar to [6.2.4 Update Attendee's Registration](#624-update-attendees-registration).

## 7.2 Banquet Registration Tab

<img src="https://s2.loli.net/2024/11/26/AHmT8pBF91IGlz2.png" width="80%;" />

Only banquets with a status of available can be viewed and registered by attendees.

### 7.2.1 Search Available Banquets

<img src="https://s2.loli.net/2024/11/26/TUsxNyV3qZv4cde.png" width="80%;" />

Choose a date and time range in the Start Date & Time and End Date & Time fields or search by any part of a banquet name (contain).

If any of these fields are left blank, it will consider the condition as "any," meaning no restriction is applied. (All blank by default)

### 7.2.2 Clear Search

Click the Clear Search button to reset all search filters and show all available banquets.

### 7.2.3 Register Available Banquets

<img src="https://s2.loli.net/2024/11/26/2mEYt1fjSsoTL78.png" width="80%;" />

Click the Register button for your desired banquet.

- Enter your preferred drink in the provided text field (required).
- Use the dropdown menu to select your preferred meal option (required).
- Use the remark field (optional) to add any additional comments or special requests.

If your registration is successful (quota is enough), the system will notify you and assign you a seat number (no conflicted seat number is guaranteed). You can view your assigned seat in the My Registrations tab.

<img src="https://s2.loli.net/2024/11/26/KELDfiH8NIdWc6Q.png" width="40%;" />

If your registration fails, the system will notify you:

<img src="https://s2.loli.net/2024/11/26/cqrDPvlx5nbEfTe.png" width="50%;" />

## 7.3 My Registrations Tab

<img src="https://s2.loli.net/2024/11/26/KFSMVLZEzPqyu49.png" width="80%;" />

### 7.3.1 Search Your Registration

It's similar to [7.2.1 Search Available Banquets](#721-search-available-banquets).

### 7.3.2 Clear Search

It's similar to [7.2.2 Clear Search](#722-clear-search).

### 7.3.3 Update Your Registration

<img src="https://s2.loli.net/2024/11/26/LS25PO4C1cNb8kQ.png" width="80%;" />

It's similar to [6.2.4 Update Attendee's Registration](#624-update-attendees-registration).

### 7.3.4 Unregister Your Registration

It's similar to [6.2.5 Delete Attendee's Registration](#625-delete-attendees-registration).

