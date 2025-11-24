# GhostWriter AI - MVP (Daily Summary Feature)

## Project Overview

GhostWriter AI is an intelligent messaging automation system. This repository contains the **Minimum Viable Product (MVP)** focusing on the **Daily Summary Feature** - a functionality that analyzes conversation messages and generates daily summaries.

### Course Information
- **Course**: CS 5800 - Advanced Software Engineering
- **Institution**: California Polytechnic State University
- **Semester**: Fall 2025
- **Project**: Startup Implementation & Demo (Part 4)

---

## Features

The MVP implements the following core functionality:

- ✅ **User Registration & Authentication** - Secure account creation with BCrypt password hashing
- ✅ **Conversation Upload** - Parse and import WhatsApp conversation exports
- ✅ **Intelligent Analysis** - Extract key topics, action items, and questions using AI strategies
- ✅ **Daily Summary Generation** - Automatically create formatted summaries with statistics
- ✅ **Database Persistence** - Store users, conversations, messages, and summaries

---

## Design Patterns Implemented

This project demonstrates **3 Gang of Four design patterns**:

### 1. **Singleton Pattern** 
- **Class**: `Database.java`
- **Purpose**: Ensures only one database connection exists throughout the application
- **Benefits**: Resource efficiency, consistent state, thread-safe access

### 2. **Strategy Pattern**
- **Classes**: `Analyzer.java` (Context) + 4 Strategy implementations
  - `TopicExtractionStrategy` - Extracts key topics using word frequency
  - `ActionItemStrategy` - Identifies tasks and action items
  - `QuestionDetectionStrategy` - Finds unanswered questions
  - `StatisticsStrategy` - Calculates conversation metrics
- **Purpose**: Allows flexible selection of analysis algorithms at runtime
- **Benefits**: Easy to extend, highly testable, follows Open/Closed Principle

### 3. **Factory Method Pattern**
- **Class**: `MessageParserFactory.java`
- **Purpose**: Creates appropriate parser based on conversation file format
- **Benefits**: Centralized parser creation, easy to add new formats

---

## Team Contributions

### Mihail Chitorog (50%)
**User Management & Analysis Layer**
- `User.java` - User operations and workflow orchestration
- `Account.java` - Account management with secure password hashing
- `Authentication.java` - Token-based authentication system
- `Analyzer.java` - Analysis context implementing Strategy pattern
- `TopicExtractionStrategy.java` - Topic extraction algorithm
- `ActionItemStrategy.java` - Action item detection algorithm
- `QuestionDetectionStrategy.java` - Question detection algorithm
- `StatisticsStrategy.java` - Statistics calculation algorithm
- `AnalysisStrategy.java` - Strategy interface
- `AnalysisResult.java` - Analysis result container
- `Summary.java` - Summary generation and formatting
- **Tests**: `UserTest.java`, `AccountTest.java`, `AnalyzerTest.java` (18 unit tests)

### Trung Nghia Vong (25%)
**Message Handling & Parsing**
- `Message.java` - Message data model
- `Conversation.java` - Conversation container with database persistence
- `MessageParser.java` - Parser interface
- `WhatsAppParser.java` - WhatsApp format parser with regex matching
- `MessageParserFactory.java` - Factory for creating parsers

### Jack River Morris (25%)
**Database & Integration**
- `Database.java` - Singleton database layer with SQLite
- `MVPDemo.java` - Complete integration demo application
- **Integration Testing**: End-to-end workflow demonstration

---

## Project Structure

```
ghostwriter-ai-mvp/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── ghostwriter/
│   │               ├── MVPDemo.java                    # Main demo application
│   │               ├── database/
│   │               │   └── Database.java               # Singleton pattern
│   │               ├── user/
│   │               │   ├── User.java
│   │               │   ├── Account.java
│   │               │   └── Authentication.java
│   │               ├── message/
│   │               │   ├── Message.java
│   │               │   ├── Conversation.java
│   │               │   ├── MessageParser.java
│   │               │   ├── WhatsAppParser.java         # Factory pattern
│   │               │   └── MessageParserFactory.java
│   │               └── analysis/
│   │                   ├── Analyzer.java               # Strategy context
│   │                   ├── AnalysisStrategy.java       # Strategy interface
│   │                   ├── TopicExtractionStrategy.java
│   │                   ├── ActionItemStrategy.java
│   │                   ├── QuestionDetectionStrategy.java
│   │                   ├── StatisticsStrategy.java
│   │                   ├── AnalysisResult.java
│   │                   └── Summary.java
│   └── test/
│       └── java/
│           └── com/
│               └── ghostwriter/
│                   ├── user/
│                   │   ├── UserTest.java
│                   │   └── AccountTest.java
│                   └── analysis/
│                       └── AnalyzerTest.java
├── docs/
│   ├── GhostWriter_Class_Diagram_Refactored.puml
│   ├── Daily_Summary_Sequence_Refactored.puml
│   └── Design_Pattern_Documentation.md
├── pom.xml
└── README.md
```

---

## Technologies Used

- **Language**: Java 11
- **Build Tool**: Maven
- **Database**: SQLite (via `sqlite-jdbc` 3.44.1.0)
- **Password Hashing**: BCrypt (via `jbcrypt` 0.4)
- **Testing**: JUnit 5

---

## Installation & Setup

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/mikeleon001/ghostwriter-ai-mvp.git
cd ghostwriter-ai-mvp
```

2. **Compile the project**
```bash
mvn clean compile
```

3. **Run tests**
```bash
mvn test
```

4. **Run the MVP demo**
```bash
mvn exec:java -Dexec.mainClass="com.ghostwriter.MVPDemo"
```

---

## Demo Output

When you run the MVP demo, you'll see:

```
╔════════════════════════════════════════════════╗
║   GhostWriter AI - Daily Summary MVP Demo     ║
╚════════════════════════════════════════════════╝

STEP 1: User Registration ✅
STEP 2: User Login ✅
STEP 3: Upload Conversation ✅
STEP 4: Generate Daily Summary ✅
STEP 5: View Daily Summary ✅

═══════════════════════════════════════════════════
   📅 DAILY SUMMARY - 2025-11-23
═══════════════════════════════════════════════════

📊 MESSAGE STATISTICS
Total Messages: 10
Participants: Alice (5), Bob (5)

🔑 KEY TOPICS DISCUSSED
1. meeting (mentioned 2 times)
2. project (mentioned 2 times)

⚡ ACTION ITEMS
☐ "We need to finish the final report by Friday" - Bob
☐ "I'll email it to you this afternoon" - Bob

❓ PENDING QUESTIONS
? "Can you send me the latest draft?" - Alice
? "What should we discuss in the meeting?" - Alice

═══════════════════════════════════════════════════
```

---

## Testing

The project includes comprehensive unit tests:

- **UserTest.java**: 4 tests for user registration and login
- **AccountTest.java**: 2 tests for password hashing and verification
- **AnalyzerTest.java**: 12 tests including:
  - Individual strategy tests
  - Strategy composition tests
  - Error handling tests
  - Strategy pattern behavior verification

**Total**: 18 unit tests

Run all tests:
```bash
mvn test
```

Expected output:
```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Design Pattern Benefits Demonstrated

### Singleton Pattern (Database)
- **Before**: Risk of multiple connections, resource waste
- **After**: Single, thread-safe connection; consistent state

### Strategy Pattern (Analyzer)
- **Before**: Monolithic analyzer class; hard to extend or test
- **After**: Each analysis type is independent; easy to add new strategies

### Factory Pattern (MessageParser)
- **Before**: Hard-coded parser selection; difficult to add formats
- **After**: Centralized creation; easy to support SMS, Telegram, etc.

---

## Future Enhancements

While the MVP focuses on daily summaries, the original vision includes:

- 🔮 Auto-response generation using learned communication styles
- 🤖 Real-time integration with messaging platforms
- 📱 Mobile application with push notifications
- 🧠 Advanced AI model training for personalized responses
- 🌐 Multi-platform support (WhatsApp, SMS, Telegram, Discord)

---

## Documentation

Additional documentation can be found in the `docs/` directory:

- **UML Diagrams**: Class, Sequence, and Activity diagrams
- **Design Pattern Analysis**: Detailed explanation of pattern choices
- **Test Documentation**: Test coverage and strategy pattern testing

---


## Contact

- **Mihail Chitorog** - [GitHub](https://github.com/mikeleon001)
- **Repository**: [ghostwriter-ai-mvp](https://github.com/mikeleon001/ghostwriter-ai-mvp)

---

## Acknowledgments

- **Instructor**: Nima Davarpanah
- **Course**: CS 5800 - Advanced Software Engineering
- **Institution**: California Polytechnic State University

---

*Last Updated: November 2025*
