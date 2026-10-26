# Super Mario Run — DSA Edition
**University Data Structures & Algorithms (IT22013) Assignment**
**Technology Stack:** Java 21 | libGDX 1.13.1 | LWJGL3 | Gradle

---

## 1. Project Overview
**Super Mario Run — DSA Edition** is a complete, original 2D side-scrolling platformer developed using Java 21 and the libGDX game framework. The game incorporates classic platformer kinematics, horizontal camera tracking, AABB collision resolution, three increasingly difficult levels, patrol enemies, power-ups, and a final boss fight with Bowser.

Crucially, every core data structure and algorithm required by the university curriculum is implemented **manually in pure Java** (`com.damidu.supermariodsa.dsa`) and seamlessly tied into active gameplay mechanics.

---

## 2. Mandatory DSA Implementation Summary

| DSA Concept | Role in Game | Custom Class | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :---: | :---: |
| **A. Arrays** | Fixed level layouts (`Platform[]`, `Coin[]`, `Enemy[]`) & dynamic doubling `LeaderboardEntry[]`. | `Level`, `LeaderboardManager` | $O(1)$ access | $O(N)$ contiguous |
| **B. Linked Lists** | Doubly linked list managing dynamic gameplay event history and inventory. | `CustomLinkedList<T>` | $O(1)$ add/remove ends, $O(N)$ search | $O(N)$ node pointers |
| **C. Stacks (LIFO)** | Movement & state snapshot history buffer for the **Undo** mechanic (<kbd>Z</kbd> / <kbd>U</kbd>). | `CustomStack<T>` | $O(1)$ push / pop / peek | $O(C)$ capacity limit |
| **D. Queues (FIFO)** | Event buffering system delivering non-blocking toast notifications in chronological order. | `CustomQueue<T>` | $O(1)$ enqueue / dequeue | $O(C)$ capacity limit |
| **E. Sorting** | Manual Bubble Sort ranking leaderboard entries in descending score order with early exit. | `BubbleSort` | Worst: $O(N^2)$, Best: $O(N)$ | $O(1)$ in-place |
| **F. Searching** | Linear Search by player name & Recursive Binary Search by score with step-by-step trace. | `LinearSearch`, `BinarySearch` | Linear: $O(N)$, Binary: $O(\log N)$ | Linear: $O(1)$, Binary: $O(\log N)$ stack |
| **G. Recursion** | 3-2-1-GO call stack countdown & recursive coin score bonus formula. | `RecursionHelper` | $O(K)$ depth | $O(K)$ call stack |

---

## 3. Game Controls

| Key(s) | Action |
| :--- | :--- |
| <kbd>A</kbd> / <kbd>D</kbd> or <kbd>←</kbd> / <kbd>→</kbd> | Run left / right with acceleration and friction |
| <kbd>W</kbd> / <kbd>↑</kbd> / <kbd>Space</kbd> | Jump (only when on solid ground; stomp enemies from above) |
| <kbd>Z</kbd> or <kbd>U</kbd> | **Undo Movement**: Pops and restores player snapshot from `CustomStack` |
| <kbd>P</kbd> or <kbd>Esc</kbd> | Pause / Resume gameplay |
| <kbd>M</kbd> | Return to Main Menu |
| <kbd>B</kbd> *(in Leaderboard)* | Run manual Bubble Sort demonstration with metrics |
| <kbd>L</kbd> *(in Leaderboard)* | Run Linear Search for "Damidu" with on-screen step trace |
| <kbd>S</kbd> *(in Leaderboard)* | Run Recursive Binary Search for score with depth trace |

---

## 4. How to Build & Run

### A. Run via Gradle (Development Mode)
```bash
./gradlew lwjgl3:run
```

### B. Run Unit Tests (30 Automated JUnit 5 Tests)
```bash
./gradlew test
```

### C. Build Standalone Executable Desktop Jar
```bash
./gradlew lwjgl3:jar
java -jar lwjgl3/build/libs/SuperMarioDSA-1.0.0.jar
```

---

## 5. Viva Defense Notes & Algorithmic Rationale

### Why Doubly Linked List for Event Records?
A doubly linked list (`CustomLinkedList<T>`) retains both `head` and `tail` pointers alongside forward (`next`) and backward (`prev`) links. Adding or removing elements from both ends occurs in strict $O(1)$ constant time with zero array-shifting overhead.

### Why Stacks for Movement Undo?
Player movement and positioning are naturally chronological. The most recent action is always the first one to be undone, aligning with **Last-In-First-Out (LIFO)**. A maximum capacity threshold (80 snapshots) prevents memory leaks by discarding the oldest bottom nodes when full.

### Why Queues for Game Event Notifications?
Game events (coins picked up, enemies stomped, damage taken) arrive asynchronously. To ensure notifications are displayed in the exact order they happened without visual overlapping or gameplay stutter, a **First-In-First-Out (FIFO)** queue buffers the events.

### Why Bubble Sort for Leaderboard?
The leaderboard contains scores that are largely already sorted or only need slight re-ranking when a player finishes a run. Bubble Sort with an early-exit flag achieves an optimal **$O(N)$ best-case** runtime when few swaps are needed, operates **in-place ($O(1)$ space)**, and is intuitive to demonstrate with step metrics during viva defense.
