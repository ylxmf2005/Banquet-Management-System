Warning: 
This project is in the early stages of development. The functionality is incomplete and the source is subject to change at any time. The README is currently for developers' reference only and may not reflect the latest state of the repository.

## How to Setup

### Backend
Make sure you have Gradle installed.
`gradle clean build`
`gradle run`

### FrontEnd
`cd bms_frontend`
`npm install`
`npm run dev`

## To-Do List
- Update README
- Encrypted password storage

## Developer manual

### SQLConnection
Execute fixed SQL or parameterized SQL (to avoid SQL injection and enhance flexibility) through SQLConnection.
Another version: SQLConnectionDeprecated, returns QureyResult for query, now abandoned.

Since the Oracle database provided by the PolyU can only be accessed on the campus network, I have set up my own Oracle server. The server login are already provided in the code, which can be used directly.
