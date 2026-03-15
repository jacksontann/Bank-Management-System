# Bank Management System

A desktop banking app built in Java. Supports user registration, login, account creation, deposits, withdrawals, transfers, and contact storage. Uses a layered setup with UI, services, models, and data access separated.

---

## What it does

**Accounts**
- Create and manage several bank accounts per user
- Deposit, withdraw, and transfer money
- Freeze and unfreeze accounts
- Close accounts when balance is zero

**Users**
- Sign up with email and password
- Sign in with stored credentials
- Passwords hashed with SHA-256 and salt

**Transactions**
- Transaction history per account
- Records deposits, withdrawals, and transfers with notes
- Transactions stored for audit

**Contacts**
- Save names and account numbers for frequent transfers
- Search by name or account number
- Stored in a JSON file

**Security**
- Password hashing with random salt
- Foreign keys in the database
- Checks before operations (e.g. balance, frozen status)

---

## Project structure

| Layer | Classes |
|-------|---------|
| UI | LoginWindow, RegistrationWindow, MainWindow, ContactPanel |
| Service | LoginService, RegistrationService, MainService, ContactService |
| Model | User, Account, Transaction, Contact |
| Data | DatabaseManager, UserManager, AccountManager, TransactionManager, ContactManager |

DatabaseManager uses a singleton pattern for connections. Data is stored in SQLite; contacts use a JSON file.

---

## How to run

**Requirements:** Java 22+ and Maven (or the Maven wrapper in the project)

**Build:**
```bash
mvn clean compile
```

**Run:**
```bash
mvn exec:java
```

**Windows:**
```powershell
.\run.ps1
```

The SQLite database is created at `config/Banking.db` on first run. Tables and constraints are created automatically.

---

## Usage

1. Start the app and open the login screen.
2. Use Register to create an account, then sign in.
3. Open a new bank account from the main screen.
4. Use Deposit, Withdraw, and Transfer with a chosen account.
5. Optionally add contacts for quick transfers.

You can freeze accounts to block actions, unfreeze to allow them, and close accounts when the balance is zero.

---

## Tech used

| Part | Tool |
|------|------|
| Language | Java 25 |
| Database | SQLite |
| UI | JavaFX |
| JSON | Gson |
| Tests | JUnit Jupiter |
| Build | Maven |

---

## Main classes

- **DatabaseManager** – singleton for DB connections
- **UserManager** – user creation, login, password hashing
- **AccountManager** – account CRUD and money operations
- **TransactionManager** – transaction storage and history
- **MainService** – banking workflows (deposit, withdraw, transfer)

---

## Tests

```bash
mvn test
```

Or run a single test class:
```bash
mvn test -Dtest=UserManagerTest
mvn test -Dtest=AccountManagerTest
mvn test -Dtest=TransactionManagerTest
```

Tests cover user registration and login, account handling, money operations, and transaction history.
