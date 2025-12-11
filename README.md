# GhostWriter AI - MVP (Daily Summary Feature)

## Project Overview

GhostWriter AI is an intelligent messaging automation system. This repository contains the **Minimum Viable Product (MVP)** focusing on the **Daily Summary Feature** - a functionality that analyzes conversation messages and generates daily summaries.

### Course Information
- **Course**: CS 5800 - Advanced Software Engineering
- **Institution**: California Polytechnic State University
- **Semester**: Fall 2025
- **Project**: Startup Implementation & Demo (Final)

---

## Repository Structure

| Part | Branch | Tag | Description |
|------|--------|-----|-------------|
| Part 1 | `final-part1` | `part1-design-patterns` | 5 Design Patterns Implementation |
| Part 2 | `final-part2` | `part2-major-features` | Export Feature + Batch Processing |
| Part 3 | `final-part3` | `part3-cleancode` | Clean Code Refactoring |

---

## Features

### Core Functionality
- ✅ **User Registration & Authentication** - Secure account creation with BCrypt password hashing
- ✅ **Conversation Upload** - Parse and import WhatsApp conversation exports
- ✅ **Intelligent Analysis** - Extract key topics, action items, and questions using AI strategies
- ✅ **Daily Summary Generation** - Automatically create formatted summaries with statistics
- ✅ **Database Persistence** - Store users, conversations, messages, and summaries

### Part 2: Major Features
- ✅ **Export Feature** - Export summaries to multiple formats (Plain Text, Markdown, HTML, JSON)
- ✅ **Batch Processing** - Generate Weekly and Monthly reports with trend analysis

### Part 3: Clean Code
- ✅ **Refactored Code** - Following Google Java Style and Clean Code standards
- ✅ **190 Unit Tests** - Comprehensive test coverage with 100% pass rate

---

## Design Patterns Implemented (6 Total)

### Part 1: Core Patterns

#### 1. **Singleton Pattern** 
- **Class**: `Database.java`
- **Purpose**: Ensures only one database connection exists throughout the application
- **Benefits**: Resource efficiency, consistent state, thread-safe access

#### 2. **Strategy Pattern**
- **Classes**: `Analyzer.java` (Context) + 4 Strategy implementations
  - `TopicExtractionStrategy` - Extracts key topics using word frequency
  - `ActionItemStrategy` - Identifies tasks and action items
  - `QuestionDetectionStrategy` - Finds unanswered questions
  - `StatisticsStrategy` - Calculates conversation metrics
- **Purpose**: Allows flexible selection of analysis algorithms at runtime
- **Benefits**: Easy to extend, highly testable, follows Open/Closed Principle

#### 3. **Factory Method Pattern**
- **Class**: `MessageParserFactory.java`
- **Purpose**: Creates appropriate parser based on conversation file format
- **Benefits**: Centralized parser creation, easy to add new formats

#### 4. **Observer Pattern**
- **Classes**: `Summary.java` (Subject), `SummaryObserver.java` (Interface)
  - `ConsoleNotifier` - Prints notifications to console
  - `EmailNotifier` - Sends email notifications (simulated)
  - `LogFileNotifier` - Logs notifications to file
- **Purpose**: Notifies multiple observers when a summary is saved
- **Benefits**: Loose coupling, easy to add new notification channels

#### 5. **Decorator Pattern**
- **Classes**: `SummaryFormatter.java` (Interface), `PlainTextFormatter.java` (Base)
  - `MarkdownDecorator` - Adds Markdown formatting
  - `HTMLDecorator` - Adds HTML formatting
  - `JSONDecorator` - Adds JSON formatting
- **Purpose**: Dynamically add formatting capabilities to summaries
- **Benefits**: Flexible format combinations, follows Open/Closed Principle

### Part 3: Additional Pattern

#### 6. **Builder Pattern**
- **Classes**: Inner Builder classes in:
  - `Summary.Builder` - Builds Summary objects (8 → 3 required params)
  - `Account.Builder` - Builds Account objects (6 → 2 required params)
  - `User.Builder` - Builds User objects (4 → 1 required param)
  - `Message.Builder` - Builds Message objects (4 → 2 required params)
- **Purpose**: Simplify object construction with many parameters
- **Benefits**: Readable code, immutable objects, flexible construction

---

## Team Contributions

### Mihail Chitorog (50%) - Team Lead
**User Management, Analysis Layer & Clean Code Refactoring**

*Part 1 - Core Implementation:*
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
- `Summary.java` - Summary generation with Observer pattern

*Part 2 - Features:*
- `BatchProcessor.java` - Batch processing orchestration
- `BatchResult.java` - Batch result container
- `WeeklyReport.java` - Weekly report generation
- `MonthlyReport.java` - Monthly report with trend analysis
- `PlainTextFormatter.java` - Base formatter implementation
- `SummaryFormatterDecorator.java` - Decorator base class
- `ConsoleNotifier.java` - Console notification observer
- `EmailNotifier.java` - Email notification observer

*Part 3 - Clean Code Refactoring:*
- `StatisticsKeys.java` - Constants for statistics keys
- `DatabaseColumns.java` - Constants for database columns
- `ReportUtils.java` - Shared utility methods (DRY principle)
- Builder pattern implementation in Summary, Account, User, Message
- Method extraction in Summary, WeeklyReport, MonthlyReport, Database
- Test refactoring and documentation

*Tests:*
- `UserTest.java`, `AccountTest.java`, `AnalyzerTest.java`
- `BatchProcessorTest.java`, `BatchResultTest.java`
- `WeeklyReportTest.java`, `MonthlyReportTest.java`

### Trung Nghia Vong (25%)
**Message Handling, Parsing & Decorator Implementation**

*Part 1:*
- `Message.java` - Message data model
- `Conversation.java` - Conversation container with database persistence
- `MessageParser.java` - Parser interface
- `WhatsAppParser.java` - WhatsApp format parser with regex matching
- `MessageParserFactory.java` - Factory for creating parsers

*Part 2:*
- `HTMLDecorator.java` - HTML formatting decorator
- `MarkdownDecorator.java` - Markdown formatting decorator
- `LogFileNotifier.java` - Log file notification observer

*Tests:*
- `ObserverPatternTest.java`

### Jack River Morris (25%)
**Database, Integration & Export Implementation**

*Part 1:*
- `Database.java` - Singleton database layer with SQLite
- `MVPDemo.java` - Complete integration demo application

*Part 2:*
- `JSONDecorator.java` - JSON formatting decorator
- `SummaryExporter.java` - Export orchestration

*Tests:*
- `MessageParserTest.java` - 25 tests for WhatsApp parsing
- `ConversationTest.java` - 17 tests for conversation persistence
- `AuthenticationTest.java` - 5 tests for authentication
- `DecoratorPatternTest.java` - Decorator pattern tests

---

## Project Structure

```
ghostwriter-ai-mvp/
├── src/
│   ├── main/java/com/ghostwriter/
│   │   ├── MVPDemo.java
│   │   ├── analysis/
│   │   │   ├── Analyzer.java
│   │   │   ├── AnalysisStrategy.java
│   │   │   ├── AnalysisResult.java
│   │   │   ├── TopicExtractionStrategy.java
│   │   │   ├── ActionItemStrategy.java
│   │   │   ├── QuestionDetectionStrategy.java
│   │   │   ├── StatisticsStrategy.java
│   │   │   ├── StatisticsKeys.java          # Part 3
│   │   │   └── Summary.java
│   │   ├── batch/                            # Part 2
│   │   │   ├── BatchProcessor.java
│   │   │   ├── BatchResult.java
│   │   │   ├── WeeklyReport.java
│   │   │   ├── MonthlyReport.java
│   │   │   └── ReportUtils.java             # Part 3
│   │   ├── database/
│   │   │   ├── Database.java
│   │   │   └── DatabaseColumns.java         # Part 3
│   │   ├── export/                           # Part 2
│   │   │   ├── SummaryFormatter.java
│   │   │   ├── SummaryFormatterDecorator.java
│   │   │   ├── PlainTextFormatter.java
│   │   │   ├── MarkdownDecorator.java
│   │   │   ├── HTMLDecorator.java
│   │   │   ├── JSONDecorator.java
│   │   │   └── SummaryExporter.java
│   │   ├── message/
│   │   │   ├── Message.java
│   │   │   ├── Conversation.java
│   │   │   ├── MessageParser.java
│   │   │   ├── WhatsAppParser.java
│   │   │   └── MessageParserFactory.java
│   │   ├── notification/
│   │   │   ├── SummaryObserver.java
│   │   │   ├── ConsoleNotifier.java
│   │   │   ├── EmailNotifier.java
│   │   │   └── LogFileNotifier.java
│   │   └── user/
│   │       ├── User.java
│   │       ├── Account.java
│   │       └── Authentication.java
│   └── test/java/com/ghostwriter/
│       ├── analysis/
│       │   └── AnalyzerTest.java
│       ├── batch/
│       │   ├── BatchProcessorTest.java
│       │   ├── BatchResultTest.java
│       │   ├── WeeklyReportTest.java
│       │   └── MonthlyReportTest.java
│       ├── export/
│       │   └── DecoratorPatternTest.java
│       ├── message/
│       │   ├── MessageParserTest.java
│       │   └── ConversationTest.java
│       ├── notification/
│       │   └── ObserverPatternTest.java
│       └── user/
│           ├── UserTest.java
│           ├── AccountTest.java
│           └── AuthenticationTest.java
├── docs/
│   ├── GhostWriter_Class_Diagram_Part1_Compact.puml
│   ├── GhostWriter_Sequence_Diagram_Part1_Compact.puml
│   ├── GhostWriter_Activity_Diagram_Part1_Compact.puml
│   ├── Export_Feature_Class_Diagram_Part2.puml
│   ├── Export_Feature_Sequence_Diagram_Part2.puml
│   ├── Batch_Processing_Class_Diagram_Part2.puml
│   ├── Batch_Processing_Sequence_Diagram_Part2.puml
│   └── GhostWriter_Class_Diagram_Part3_CleanCode.puml
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

2. **Checkout the final version**
```bash
git checkout final-part3
```

3. **Compile the project**
```bash
mvn clean compile
```

4. **Run tests**
```bash
mvn test
```

5. **Run the MVP demo**
```bash
mvn exec:java -Dexec.mainClass="com.ghostwriter.MVPDemo"
```

---

## Testing

### Test Suite Overview (190 Tests Total)

| Test Class | Tests | Coverage |
|------------|-------|----------|
| `AnalyzerTest.java` | 12 | Strategy pattern, analysis algorithms |
| `MessageParserTest.java` | 25 | WhatsApp parsing, edge cases |
| `ConversationTest.java` | 17 | Conversation persistence |
| `AccountTest.java` | 2 | Password hashing |
| `AuthenticationTest.java` | 5 | Token generation |
| `UserTest.java` | 2 | User registration |
| `DecoratorPatternTest.java` | 20 | Export formatters |
| `ObserverPatternTest.java` | 10 | Notification observers |
| `BatchProcessorTest.java` | 30 | Batch processing |
| `BatchResultTest.java` | 25 | Result tracking |
| `WeeklyReportTest.java` | 20 | Weekly reports |
| `MonthlyReportTest.java` | 22 | Monthly reports |

### Running Tests

```bash
mvn test
```

Expected output:
```
Tests run: 190, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Clean Code Standards (Part 3)

The codebase follows **Google Java Style** and **Clean Code** principles:

- ✅ **Small Functions**: All methods under 20 lines
- ✅ **Limited Parameters**: Max 3 required parameters (Builder pattern for more)
- ✅ **No Magic Strings**: Constants in `StatisticsKeys` and `DatabaseColumns`
- ✅ **DRY Principle**: Shared code in `ReportUtils`
- ✅ **Consistent Naming**: Boolean methods use `is/has/can` prefix
- ✅ **Documentation**: All classes have `@author` tags

---

## Demo Output

When you run the MVP demo, you'll see:

```
╔════════════════════════════════════════════════╗
║   GhostWriter AI - Daily Summary MVP Demo      ║
╚════════════════════════════════════════════════╝

🔧 Initializing database...
✅ Database ready

STEP 1: User Registration ✅
STEP 2: User Login ✅
STEP 3: Upload Conversation ✅
STEP 4: Generate Daily Summary ✅
STEP 5: View Daily Summary ✅

═══════════════════════════════════════════════════
   📅 DAILY SUMMARY - 2025-12-11
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

╔════════════════════════════════════════════════╗
║         MVP Demo Completed Successfully!       ║
╚════════════════════════════════════════════════╝
```

---

## Future Enhancements

- 🔮 Auto-response generation using learned communication styles
- 🤖 Real-time integration with messaging platforms (WhatsApp API)
- 📱 Mobile application with push notifications
- 🧠 Advanced AI model training for personalized responses
- 🌐 Multi-platform support (WhatsApp, SMS, Telegram, Discord)

---

## Contact

- **Mihail Chitorog** - [GitHub](https://github.com/mikeleon001)
- **Repository**: [ghostwriter-ai-mvp](https://github.com/mikeleon001/ghostwriter-ai-mvp)

---

## Acknowledgments

- **Instructor**: Nima Davarpanah

---

*Last Updated: December 2025*
