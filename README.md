## Table of Contents



## Introduction



## Demo

[Website](https://bms-frontend-oohkp5osq-ylxmf2005s-projects.vercel.app/login)

- Admin account: `bmsadmin@polyu.hk`
- User account: `test@polyu.hk`, `student1@polyu.hk`, `guest1@polyu.hk` (Or you can register one)
- Password: `2411project`



## Getting Started

This is a full-stack application with separated frontend and backend, which can be deployed independently.

### Backend

Make sure you have Gradle installed, recommended version: 8.11

The backend server will run on port 2411 by default. To change the port:

- Navigate to `src/main/java/hk/polyu/comp/project2411/bms/service/RestAPIServer.java`
- Modify the port number in line: `Server server = new Server(2411);`

Run in development mode:

```bash
gradle run
```

Or build and run the production JAR:
```bash
gradle build
java -jar build/libs/BMS.jar
```

### Frontend

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

Start development server:

```bash
npm run dev
```

Now you can access your frontend at http://localhost:3000.

Or you can build the production files and deploy it to a hosting service like Vercel:

```
npm run build
```



## Admin Console

### Banquet Management

<img src="https://s2.loli.net/2024/11/25/fQJKTOVevNFBnGd.png" width="80%">

#### 1. Create New Banquet

<img src="https://s2.loli.net/2024/11/25/gR6FrSEHomuIiCk.png" width="80%" />

Restrictions:

- All the fileds shown in picture are required.
- Quota must be an non-negative integer.
- Dish Name must be unique within the banquet.

Common errors:

- Input format error

  <img src="https://s2.loli.net/2024/11/25/wSDsolBb54aPtNj.png" width="50%" />

- Input length is too long.

  ![](https://s2.loli.net/2024/11/25/7erLFX8qOuyVzYQ.png)

- There are two identical dish names.

  ![](https://s2.loli.net/2024/11/25/w9HEBMJ4rq2LtWy.png)

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

It's similar to [create new banquet](#create-new-banquet).

#### 4. DELETE

<img src="https://s2.loli.net/2024/11/25/E2KHOtQnhiD9Mec.png" width="80%" />

Before deletion, confirmation is required. 

Note that registrations related to the banquet will also be deleted.

#### 5. Previous/Next Page

When the number of banquets exceeds the maximum display limit per page, you can navigate through them using page turning.



### Attendee Management

