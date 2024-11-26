## Table of Contents



## Introduction



## Demo

[Website](https://bms-frontend-oohkp5osq-ylxmf2005s-projects.vercel.app/login)

- Admin account: `bmsadmin@polyu.hk`
- Admin password: `2411project`



## Getting Started

This is a full-stack application with separated frontend and backend, which can be deployed independently.

### Backend

Make sure you have Gradle installed, recommended version: 8.11

#### Port Configuration

The backend server will run on port 2411 by default. To change the port:

- Navigate to `src/main/java/hk/polyu/comp/project2411/bms/service/RestAPIServer.java`
- Modify the port number in line: `Server server = new Server(2411);`

#### Database Configuration

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

#### Start Server

**Start server**

Run in development mode:

```bash
gradle run
```

Or build and run the production JAR:
```bash
gradle build
java -jar build/libs/BMS.jar
```

#### Database Initialization/Reset

By default:

- The system automatically creates necessary database tables if they don't exist
- Existing database tables are preserved

To reset the database:

- Navigate to `src/main/java/hk/polyu/comp/project2411/bms/service/BMSMain.java`

- Set clearIfExists to true: `initDatabase(true);`
- Restart the server.

Note: Use `clearIfExists=true` with caution as it will delete all existing data.



### Frontend

#### API Endpoint Configuration

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

   - Production: Set in Vercel dashboard or your hosting platform

2. Direct Configuration:

   - Edit `src/service/api.ts`:

     ```typescript
     baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:2411'
     ```

#### Start Server

Start development server:

```bash
npm run dev
```

Now you can access your frontend at http://localhost:3000.

Or you can build the production files and deploy it to a hosting service like Vercel:

```
npm run build
```





## Login & Registration

<img src="https://s2.loli.net/2024/11/26/u3Ug5WJMj7bZIoK.png" width="30%;" />

Restrictions:

- All field is required to register an account.

- First name and last name can only contain characters.

- Email address must be valid and have not been registered before, otherwise an error will occur:
- Password must be at least 6 characters and contain only letters, numbers, and common special characters.

- The Mobile Number must be eight digits.



## Admin Page

### Banquet Management

<img src="https://s2.loli.net/2024/11/25/fQJKTOVevNFBnGd.png" width="80%">

#### 1. Create New Banquet

<img src="https://s2.loli.net/2024/11/25/gR6FrSEHomuIiCk.png" width="80%" />

Restrictions:

- All the fileds shown in picture are required.
- Quota must be an non-negative integer.
- Dish Name must be unique within the banquet.

Common errors:

- Input format error:

  <img src="https://s2.loli.net/2024/11/25/wSDsolBb54aPtNj.png" width="30%" />

- Input length is too long:

  ![](https://s2.loli.net/2024/11/25/7erLFX8qOuyVzYQ.png)

- There are two identical dish names:

  <img src="https://s2.loli.net/2024/11/25/w9HEBMJ4rq2LtWy.png" width="90%" />

If an error occurs during the creation of a meal, the already created banquet and its associated meals will also be rolled back to maintain consistency.

#### 2. Menu

This button **is applicable to all column headers**. Simply hover your mouse over the desired column header to activate the button.

This button allows you to: 

- Sort in ascending or descending order.

- Set conditions and input values to filter data.

- Customize column visibility.

<img src="https://s2.loli.net/2024/11/25/EAnBagWpPj2XFcY.png" width="30%" />

<img src="https://s2.loli.net/2024/11/25/sGEd7yamQN43CfM.png" width="50%" />

#### 3. Edit

<img src="https://s2.loli.net/2024/11/25/4yujtnpNc7ob8x5.png" width="80%" />

It's similar to [Create New Banquet](#1-create-new-banquet).

Additional Note:

- If you change a banquet's status from available to unavailable, any existing registrations for that banquet will remain unaffected. Attendees already registered will not be automatically removed.
- It is not recommended to modify a dish name that attendees have already selected in their registrations. If you need to make such changes, ensure you notify the attendees to reselect their meal or update the registrations manually via the Registration Management Tab.

#### 4. Delete

<img src="https://s2.loli.net/2024/11/25/E2KHOtQnhiD9Mec.png" width="80%" />

Before deletion, confirmation is required (All delete operations have this confirmation）. 

Note: meals and registrations related to the banquet will also be deleted.

#### 5. Previous/Next Page

When the number of banquets exceeds the maximum display limit per page, you can navigate through them using page turning.



### Attendee Management

<img src="https://s2.loli.net/2024/11/25/y8QK34fasPmDwtM.png" width="80%"/>

#### 1. Search Attendee By Email

When an attendee cannot be found, the system will display the following message:

<img src="https://s2.loli.net/2024/11/25/C1FLVwHQA2gWqvO.png" width = "30%" />

#### 2. Update Attendee

- Updating attendee information follows the same constraints as when registering a new attendee.

- The password cannot be viewed (Because the database encrypted storage password) but can be reset to a new one.

#### 3. Delete Attendee

- Deleting an attendee will also delete all banquet registrations associated with them.

- The seat assigned to the attendee will be automatically released for reuse.

#### 4. Update Attendee's Registration

<img src="https://s2.loli.net/2024/11/25/DKfasCnQHzqrO63.png" width="80%"/>

- The registration time cannot be modified.

- Only one of the four pre-configured meal options for the banquet can be selected.

- If the attendee's email address is updated, the email address of his registration records will also be updated accordingly.

- Ensure that the updated email address does not conflict with any other attendee's email address; otherwise, an error will occur:![image-20241126173426310](https://s2.loli.net/2024/11/26/SQnFJoTYBx3iwsD.png)

- When updating an attendee's seat number:

  - Ensure the new seat number does not conflict with any already assigned seat numbers for the same banquet.

  - If a conflict occurs, the update will fail and an error meessage will be displayed

    ![image-20241125234823316](https://s2.loli.net/2024/11/25/2R4F7odBUbqH5W3.png)

#### 5. Delete Attendee's Registration

When deleting an attendee's registration, the seat assigned to the attendee for that registration will be automatically released for reuse.



### Registration Management

![](https://s2.loli.net/2024/11/26/obJ9iXmSBtk1NuT.png)

The system generates visual statistics for drink choice and meal choice. These bar charts provide an overview of attendee preferences.

#### 1. Search Registrations By BIN

Use this feature to manage registrations for a specific banquet.

#### 2. Edit Registration

It's similar to [Update Attendee's Registration](#4-update-attendees-registration)

#### 3. Delete Registration

It's similar to [Delete Attendee's Registration](#5-delete-attendees-registration).



### Report Generation

![](https://s2.loli.net/2024/11/26/aHnfNJL1he9Vu8z.png)

Press the `DOWNLOAD REPORT` button, and the report PDF will be generated and downloaded automatically.



## User Page

### Profile Tab

<img src="https://s2.loli.net/2024/11/26/EL3psezQW6gNxXt.png" alt="image-20241126173154414" width = "80%"/>

It's similar to [Update Attendee's Registration](#4-update-attendees-registration)
