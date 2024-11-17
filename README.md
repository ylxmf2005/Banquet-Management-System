### SQL Connection Part
IDE: intelliJ IDEA
Make sure ojdbc8.jar (Oracle JDBC driver) is added to your project libary.

You can see demo usage in Main.java. You can execute fixed SQL or parameterized SQL (to avoid SQL injection and enhance flexibility) through SQLConnection. Remember to close ResultSet and Statement after using queries (SELECT).

I have also provided another version: SQLConnection_deprecated, which returns List<Map<String, Object>> and automatically closes ResultSet and Statement. You can use it if you need it, but not recommended because it makes thing complex.

Since the Oracle database provided by the PolyU can only be accessed on the campus network, I have set up my own Oracle server. The server login are already provided in the code, you can use directly.

If you need any guidance or modifications, please feel free to contact me / just modify it to your need.