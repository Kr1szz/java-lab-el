# 💬 Java Multithreaded Chat Application

A real-time chat application built with Java, featuring multithreading for concurrent client handling and a modern Swing GUI.

## ✨ Features

- **Multithreaded Server** - Handles multiple clients simultaneously
- **Real-time Messaging** - Instant message broadcast to all connected users
- **Modern Swing GUI** - Dark-themed interface with custom styling
- **Connection Status** - Live connection indicators
- **Timestamps** - Message timestamps for easy tracking
- **Console Client** - Alternative command-line client available

## 🚀 Getting Started

### Prerequisites

- Java JDK 8 or higher

### Compilation

**Linux/macOS:**
```bash
./compile.sh
```

**Windows:**
```cmd
compile.bat
```

**Or manually:**
```bash
javac *.java
```

### Running the Application

1. **Start the Server:**
   ```bash
   java ChatServer
   ```

2. **Start the GUI Client** (in a new terminal):
   ```bash
   java ChatClientGUI
   ```

3. Enter your name and click **Connect** to start chatting!

> **Tip:** You can open multiple client instances to simulate a group chat.

## 📁 Project Structure

| File | Description |
|------|-------------|
| `ChatServer.java` | Main server handling client connections |
| `ClientHandler.java` | Handles individual client threads |
| `ChatClientGUI.java` | Swing-based graphical client |
| `ChatClient.java` | Console-based client (alternative) |

## 🎨 GUI Preview

The client features a modern dark theme with:
- Connection panel with name input
- Auto-scrolling message area
- Status indicators (🟢 Connected / ⚫ Disconnected)
- Styled buttons with hover effects

## 🛠️ Technologies Used

- **Java SE** - Core language
- **Java Swing** - GUI framework
- **Java Sockets** - Network communication
- **Multithreading** - Concurrent client handling

## 📜 License

This project is open source and available for educational purposes.

---

Made with ☕ Java
