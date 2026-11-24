# PetraRTW E-Commerce: NetBeans Installation Guide
 
Step-by-step setup for running this project (a Java servlet/JSP web app using Apache Derby) in Apache NetBeans.
 
> **Note:** The database steps (Part 3) were written from reading the project files. They have not been run end to end, so check the Troubleshooting section if a script gives errors.
 
---
 
## What this project needs
 
| Item | Requirement | Why |
|---|---|---|
| JDK | **JDK 8** (recommended) | Project compiles at source/target 1.8; JDK 8 also bundles Java DB (Derby) |
| IDE | Apache NetBeans with the **Java Web and EE** features | It is a NetBeans Ant web project (`nbproject/`, `build.xml`) |
| Server | **GlassFish 4.1 / 5** or **Payara 5**, or **Tomcat 8 / 9** | Code uses `javax.servlet.*`. **Tomcat 10+ will not work** (it uses `jakarta.*`) |
| Database | **Apache Derby (Java DB)** on port **1527** | `DBConnection.java` connects to `jdbc:derby://localhost:1527/petra` |
| Driver | `derbyclient.jar` | Not included in the repo (`*.jar` is git-ignored) |
 
---
 
## Part 1: Get the project
 
**Option A: Git (recommended)**
 
1. Install Git, then run:
```
git clone https://github.com/dzariefIman/petrartw_e-commerce.git
```
 
**Option B: ZIP**
 
1. Open the GitHub repo page, click **Code → Download ZIP**.
2. Extract it somewhere with a short path that has no spaces (e.g. `C:\projects\petrartw_e-commerce`).
---
 
## Part 2: Open the project in NetBeans
 
1. Open NetBeans and choose **File → Open Project…**
2. Browse to the project folder. NetBeans shows a project icon (a folder with a small globe) for it. Select it and click **Open Project**.
3. The project name is **PetraRTW**. If a red **"Resolve Reference Problems"** dialog appears, continue to the next steps. It is normal and will be fixed in Part 2.2 and 2.3.
### 2.1 Check the JDK
 
1. In NetBeans go to **Tools → Java Platforms** and make sure a **JDK 8** is listed. Click **Add Platform…** if not.
2. Right-click the project → **Properties → Sources**, and set **Source/Binary Format** to **8**.
### 2.2 Add / choose the application server
 
1. Go to **Tools → Servers → Add Server…** and pick **GlassFish Server**, **Payara Server** or **Apache Tomcat**.
2. Point it to the folder where you installed the server and finish the wizard.
3. Right-click the project → **Properties → Run**, and select that server under **Server**.
   - The project file was saved with a GlassFish 3.1-type setting. NetBeans will ask you to pick a server again, which is expected.
4. Leave **Context Path** as `/PetraRTW` (or whatever NetBeans suggests).
### 2.3 Add the Derby driver (`derbyclient.jar`)
 
The app throws `Derby driver not found. Add derbyclient.jar to WEB-INF/lib.` without this step.
 
1. Find `derbyclient.jar`:
   - **JDK 8:** `<JDK 8 folder>/db/lib/derbyclient.jar`
   - Or download Apache Derby from https://db.apache.org/derby/ and use `lib/derbyclient.jar`.
2. In NetBeans, expand the project, right-click **Libraries → Add JAR/Folder…**
3. Select `derbyclient.jar`, and make sure **Package** is set so the jar is copied into `WEB-INF/lib` when the project is built. Click **Open**.
---
 
## Part 3: Set up the Derby database
 
The app expects:
 
| Setting | Value |
|---|---|
| Host / Port | `localhost` / `1527` |
| Database name | `petra` |
| User / Password | `petra` / `petra` |
 
### 3.1 Start Java DB and create the database
 
1. Open the **Services** tab (**Window → Services**).
2. Expand **Databases**. Right-click **Java DB → Properties** and set:
   - **Java DB Installation:** your JDK 8 `db` folder (or your downloaded Derby folder)
   - **Database Location:** any folder you like
3. Right-click **Java DB → Start Server**.
4. Right-click **Java DB → Create Database…** and enter:
   - **Database Name:** `petra`
   - **User Name:** `petra`
   - **Password:** `petra`
5. Click **OK**. A connection `jdbc:derby://localhost:1527/petra` appears in the list.
### 3.2 Connect
 
1. Right-click the new `jdbc:derby://localhost:1527/petra` connection → **Connect…**
2. **Always run the SQL scripts as user `petra`.** Derby puts tables in a schema named after the user, and the app connects as `petra`.
### 3.3 Create the tables
 
1. In the **Files** tab, open `schema.sql` from the project root (or use **File → Open File…**).
2. In the editor toolbar choose the `petra` connection.
3. Click **Run SQL** (or press **Ctrl+Shift+E**).
4. Expand **petra → Schemas → PETRA → Tables**. You should see: `SALES_ADMIN`, `CUSTOMERS`, `PRODUCTS`, `ADVERTISEMENTS`, `ORDERS`, `PAYMENTS`, `ISSUES`, `ISSUE_CHAT`, `FEEDBACK`, `CART`.
### 3.4 (Optional) Load the sample data
 
1. Open `seedData.sql` the same way and run it on the `petra` connection.
2. If it shows errors, see **Troubleshooting → Seed data errors** below. You can still use the app without it (see Part 5).
> `resetDB.sql` drops every table and recreates them. Use it only when you want to wipe the database.
 
---
 
## Part 4: Build and run
 
1. Make sure the Java DB server is **running** (Services → Databases → Java DB).
2. Right-click the project → **Clean and Build**. Wait for `BUILD SUCCESSFUL` in the Output window.
3. Right-click the project → **Run** (or press **F6**).
4. NetBeans starts the server, deploys `PetraRTW`, and opens your browser at the start page.
5. If it does not open automatically, go to:
```
http://localhost:8080/PetraRTW/
```
   (the port may differ depending on your server, e.g. `8080` for Tomcat/GlassFish)
6. Click **LOGIN**, or go directly to `http://localhost:8080/PetraRTW/login`.
 
---
 
## Part 5: Logging in
 
The login page has two roles: **customer** and **staff** (Sales Admin).
 
- **Customers:** use the register form on the login page to create a new account, then log in.
- **Staff / Sales Admin:** the sample data includes an admin (`dzariefAdmin`), but its password is stored as a hash, so you need to know the original password. If you do not, create your own staff account:
  1. In the NetBeans Projects tab, open `src/java/petra/util/PasswordUtil.java`.
  2. Right-click the file → **Run File** (Shift+F6). The Output window prints `Hash: <salt>:<hash>` for the password `admin123`.
  3. Copy that hash and run this on the `petra` connection (**Services → right-click connection → Execute Command…**):
```sql
INSERT INTO sales_admin (staff_name, username, password, staff_email)
VALUES ('Test Admin', 'admin', '<paste hash here>', 'admin@example.com');
```
  4. Log in with role **staff**, username `admin`, password `admin123`.
> The five demo customers in `seedData.sql` have the placeholder password `hashed_password123`, which does not match the app's salted SHA-256 format, so they cannot log in. Register a new customer instead.
 
---
 
## Troubleshooting
 
**`Derby driver not found. Add derbyclient.jar to WEB-INF/lib.`**
Part 2.3 was skipped or the jar was not packaged. Re-add it under **Libraries**, then **Clean and Build**.
 
**`Cannot connect to Derby at jdbc:derby://localhost:1527/petra`**
- The Java DB server is not running: **Services → Databases → Java DB → Start Server**.
- The database was not created as `petra` / `petra` (Part 3.1).
- Port 1527 is used by something else.
**`Table/View 'CUSTOMERS' does not exist`**
The tables were created under a different user or schema. Drop and recreate them after connecting as `petra` (or run `resetDB.sql` on the `petra` connection).
 
**Server shows `HTTP Status 404`**
Check the URL context path matches the project (`/PetraRTW`), and look at the Output window for deployment errors.
 
**Tomcat 10+ errors such as `ClassNotFoundException: javax.servlet...`**
Use Tomcat 8 or 9, or GlassFish 4.1 / 5, or Payara 5. This project uses the older `javax.*` packages.
 
**Project shows red errors on `javax.servlet` imports**
No server is selected. Do Part 2.2, then right-click the project → **Clean and Build**.
 
**Seed data errors**
Two things in `seedData.sql` can cause errors in Derby:
- The section headers (`===== CUSTOMERS =====`, etc.) are not SQL comments (they have no `--`), so the statement following each header may fail.
- The inserts set explicit ID values, while `schema.sql` defines those columns as `GENERATED ALWAYS AS IDENTITY`, which Derby does not allow.
If this happens, skip the seed data and register your own accounts (Part 5), or ask for a fixed seed script.
 
**Product images are broken**
The sample products point to files under `uploads/products/`, and `web/uploads/` is git-ignored, so those pictures are not in the repo. Log in as staff and re-upload product images from the Products page. Uploaded files are created in `uploads/` automatically.
 
---
