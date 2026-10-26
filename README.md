# Super Mario Run — DSA Edition
**University Data Structures & Algorithms (IT22013) Assignment**  
**Student Project** | **Language:** Java 21 | **Framework:** libGDX 1.13.1 | **Backend:** LWJGL3 | **Build System:** Gradle

---

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [How to Build and Run](#2-how-to-build-and-run)
3. [Gameplay Guide & Controls](#3-gameplay-guide--controls)
   - [Keyboard Controls](#keyboard-controls)
   - [Gameplay Mechanics & Rules](#gameplay-mechanics--rules)
   - [Power-Ups & Items](#power-ups--items)
   - [The 3 Progressive Levels](#the-3-progressive-levels)
4. [Data Structures & Algorithms (DSA) Architecture](#4-data-structures--algorithms-dsa-architecture)
   - [A. Arrays](#a-arrays-fixed-world-layouts--resizable-leaderboard)
   - [B. Doubly Linked List](#b-doubly-linked-list-customlinkedlistt)
   - [C. Stack (LIFO) & Movement Undo](#c-stack-lifo--movement-undo-customstackt)
   - [D. Queue (FIFO) & Event Notifications](#d-queue-fifo--event-notifications-customqueuet)
   - [E. Sorting: Manual Bubble Sort](#e-sorting-manual-bubble-sort-bubblesortjava)
   - [F. Searching: Linear & Recursive Binary Search](#f-searching-linear-search--recursive-binary-search)
   - [G. Recursion: Countdown & Score Bonus](#g-recursion-countdown--score-bonus-recurrence)
5. [Summary of Algorithmic Complexities (Big-O)](#5-summary-of-algorithmic-complexities-big-o)
6. [Interactive DSA Demonstrations in the Game](#6-interactive-dsa-demonstrations-in-the-game)
7. [Automated Testing Suite (JUnit 5)](#7-automated-testing-suite-junit-5)
8. [Viva Defense Questions & Answers](#8-viva-defense-questions--answers)

---

## 1. Project Overview

**Super Mario Run — DSA Edition** is a complete, original 2D side-scrolling platformer built with Java 21 and the libGDX framework. The game features real-time kinematics, camera tracking, Axis-Aligned Bounding Box (AABB) collision detection, enemies, collectibles, and progressive levels ending in a final boss encounter.

Rather than relying on built-in collections from `java.util.*`, the core data structures and algorithms required by the university syllabus were **implemented from scratch in pure Java** (`com.damidu.supermariodsa.dsa`). Each structure serves an essential gameplay role rather than merely satisfying a checklist.

```mermaid
flowchart TD
    A["MainMenuScreen"] --> B["GameScreen (World 1-1)"]
    B --> C["World 1-2 (Underground)"]
    C --> D["World 1-3 (Bowser's Castle)"]
    D --> E["VictoryScreen"]
    B -.-> F["GameOverScreen"]
    A --> G["LeaderboardScreen (DSA Inspector)"]
    A --> H["HowToPlayScreen (DSA Guide)"]
    B <--> I["PauseScreen"]
```

---

## 2. How to Build and Run

### System Requirements
- **Operating System:** macOS (Apple Silicon or Intel), Windows 10/11, or Linux
- **Java Development Kit (JDK):** Java 21 LTS installed and set on your path
- **Build Tool:** Gradle wrapper included in the project repository (`./gradlew`)

### Running the Game

1. Open your terminal and navigate to the project directory:
   ```bash
   cd "/Users/damiduuofc/Downloads/DSA game"
   ```

2. **Launch the desktop game:**
   ```bash
   ./gradlew lwjgl3:run
   ```

3. **Run all 30 automated unit tests:**
   ```bash
   ./gradlew test
   ```

4. **Package a standalone executable JAR:**
   ```bash
   ./gradlew lwjgl3:jar
   java -jar lwjgl3/build/libs/SuperMarioDSA-1.0.0.jar
   ```

> [!NOTE]
> On macOS, LWJGL3 requires GLFW to execute on the first thread. This is handled automatically through `jvmArgs("-XstartOnFirstThread")` in `lwjgl3/build.gradle` and the included `StartupHelper.java`.

---

## 3. Gameplay Guide & Controls

### Keyboard Controls

| Key | In Gameplay | In Menus & Leaderboard |
| :--- | :--- | :--- |
| <kbd>A</kbd> / <kbd>D</kbd> or <kbd>←</kbd> / <kbd>→</kbd> | Run Left / Right (with acceleration and friction) | — |
| <kbd>W</kbd> / <kbd>↑</kbd> / <kbd>Space</kbd> | Jump (allowed only when on solid ground) | Navigate Up / Select |
| <kbd>S</kbd> / <kbd>↓</kbd> | Crouch / Fast descent | Navigate Down |
| <kbd>Z</kbd> or <kbd>U</kbd> | **UNDO MOVEMENT** (Pops previous snapshot from Stack) | — |
| <kbd>P</kbd> or <kbd>Esc</kbd> | Pause / Resume game | Close dialog / Back |
| <kbd>M</kbd> | Return to Main Menu | Back to Main Menu |
| <kbd>R</kbd> | Quick Respawn at last checkpoint | — |
| <kbd>B</kbd> | — | Run manual **Bubble Sort** demo |
| <kbd>L</kbd> | — | Run **Linear Search** with step trace |
| <kbd>S</kbd> | — | Run **Recursive Binary Search** with depth trace |

---

### Gameplay Mechanics & Rules

1. **Kinematics & Gravity:**
   - Mario experiences constant gravitational acceleration of $-1200\text{ px/s}^2$ with a terminal velocity of $-800\text{ px/s}$.
   - Horizontal movement features responsive acceleration ($1200\text{ px/s}^2$) and surface friction ($1000\text{ px/s}^2$) so Mario does not instantly halt.
2. **Jumping & Stomping Enemies:**
   - Mario jumps with an upward impulse of $520\text{ px/s}$ (producing an arc of approximately 120 pixels).
   - Jumping onto an enemy from above defeats them, plays a stomp sound, awards bonus score, and bounces Mario back into the air.
3. **Health & Lives System:**
   - Mario starts with **3 Lives** and **3 Hearts** (Health Points) per life.
   - Touching an enemy horizontally damages Mario by 1 heart and grants **1.8 seconds of invincibility** (indicated by character flickering).
   - Falling into a bottomless pit or losing all 3 hearts deducts 1 life and resets Mario to the level start or checkpoint.
4. **Camera Scrolling:**
   - An orthographic camera smoothly follows Mario horizontally, clamped between $x = 400$ and $x = \text{LevelWidth} - 400$, ensuring the player never loses sight of the action.

---

### Power-Ups & Items

- **Golden Coins (+200 pts):** Spinning coins scattered across platforms. Collecting 100 coins awards an extra life ($+1\text{ Life}$).
- **Super Mushroom (+1000 pts):** Enlarges Mario by 25%, restores 1 heart of lost health, and boosts jump height.
- **Invincibility Star (+1000 pts):** Grants 10 seconds of complete invulnerability accompanied by a rainbow aura, increased movement speed (+35%), and the ability to defeat any enemy on contact.

---

### The 3 Progressive Levels

```mermaid
graph LR
    L1["World 1-1: Sunny Grassland<br/>(Width: 2200px | 4 Goombas)"] --> L2["World 1-2: Underground Cavern<br/>(Width: 2560px | Koopas & Toxic Pits)"]
    L2 --> L3["World 1-3: Bowser's Fortress<br/>(Width: 2750px | Lava Pits & Boss Bowser)"]
    L3 --> VIC["VICTORY SCREEN<br/>(Total Score + Recursive Coin Bonus)"]
```

1. **World 1-1: Sunny Grassland (Tutorial & Fundamentals):**
   - Introduces basic platforming, 80px pit gaps, mystery blocks, stepping brick stairs, and patrol Goombas.
   - Ends at the classic green flagpole at $x = 2080$.
2. **World 1-2: Underground Cavern (Precision Platforming):**
   - Dark cavern aesthetics with narrow floating platforms above toxic bottom pits.
   - Features agile Koopas, a mid-level checkpoint at $x = 1200$, and an Invincibility Star.
3. **World 1-3: Bowser's Fortress (Final Boss Challenge):**
   - Castle ramparts with boiling lava pits.
   - Guarded by **Boss Bowser** ($x = 2150$) who possesses 5 Hit Points, periodic ground stomps, and jump attacks.
   - Defeating Bowser or reaching the Golden Victory Flagpole wins the game.

---

## 4. Data Structures & Algorithms (DSA) Architecture

The project implements seven core DSA concepts in real Java source code:

```
com.damidu.supermariodsa.dsa
├── CustomLinkedList.java   <-- Doubly Linked List with head and tail pointers
├── CustomStack.java        <-- Generic LIFO Stack with bound-trimming
├── CustomQueue.java        <-- Generic FIFO Queue for event notifications
├── BubbleSort.java         <-- Manual Bubble Sort with O(n) early-exit
├── LinearSearch.java       <-- Manual Linear Search with step-by-step trace
├── BinarySearch.java       <-- Recursive Binary Search on descending data
└── RecursionHelper.java    <-- Recursive countdown & coin bonus formula
```

---

### A. Arrays: Fixed World Layouts & Resizable Leaderboard
- **Classes:** `Platform[]`, `Coin[]`, `Enemy[]` in [`Level.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/level/Level.java); `LeaderboardEntry[]` in [`LeaderboardManager.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/persistence/LeaderboardManager.java).
- **Justification:** Level layouts have a fixed size per stage. Plain arrays provide $O(1)$ constant-time random access, ensuring collision checks execute each frame with zero overhead.
- **Dynamic Resizing Implementation:** In `LeaderboardManager`, entries are stored in a raw array `LeaderboardEntry[] entries`. When `count >= entries.length`, the array dynamically doubles in size:
  ```java
  private void resizeArray() {
      LeaderboardEntry[] newArray = new LeaderboardEntry[entries.length * 2];
      System.arraycopy(entries, 0, newArray, 0, count);
      this.entries = newArray;
  }
  ```

---

### B. Doubly Linked List: `CustomLinkedList<T>`
- **Class:** [`CustomLinkedList.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/dsa/CustomLinkedList.java)
- **Role in Game:** Manages the dynamic game event history log and inventory records.
- **Justification:** Adding new events at the end and removing old events from the beginning occurs frequently. A doubly linked list with both `head` and `tail` pointers enables $O(1)$ insertions and removals at both ends without shifting elements (which would cost $O(n)$ in an array).
- **Core Operations:**
  - `addLast(T data)`: Inserts at tail in $O(1)$.
  - `addFirst(T data)`: Inserts at head in $O(1)$.
  - `removeFirst()` / `removeLast()`: Unlinks head or tail in $O(1)$ via `prev`/`next` references.
  - Implements `Iterable<T>` to allow standard `for (T item : list)` traversal.

```mermaid
graph LR
    H["Head Pointer"] --> N1["Node: Event 1"]
    N1 <--> N2["Node: Event 2"]
    N2 <--> N3["Node: Event 3"]
    T["Tail Pointer"] --> N3
```

---

### C. Stack (LIFO) & Movement Undo: `CustomStack<T>`
- **Class:** [`CustomStack.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/dsa/CustomStack.java)
- **Role in Game:** Powers the player's **Undo Movement** feature (<kbd>Z</kbd> or <kbd>U</kbd> keys).
- **Justification:** Player movements occur in chronological order. To undo the most recent mistake (e.g., misjudged jump or enemy collision), the system must restore the most recently captured state first (**Last-In, First-Out**).
- **Implementation & Capacity Management:**
  - Every 0.35 seconds, a `PlayerStateSnapshot` $(x, y, v_x, v_y, \text{health}, \text{score}, \text{coins})$ is pushed to the stack.
  - When the player presses <kbd>Z</kbd> or <kbd>U</kbd>, `undoStack.pop()` restores the state.
  - Memory leak protection: The stack enforces a maximum capacity of 80 snapshots. If full, `trimBottom()` unlinks the oldest node at the bottom of the stack in $O(n)$, keeping recent history bounded in $O(1)$ space.

```mermaid
sequenceDiagram
    participant Player
    participant Stack as CustomStack (LIFO)
    Player->>Stack: push(Snapshot t=1.0s)
    Player->>Stack: push(Snapshot t=1.35s)
    Player->>Stack: push(Snapshot t=1.70s) [TOP]
    Note over Player: Player makes misstep
    Player->>Stack: pop() -> Snapshot t=1.70s
    Stack-->>Player: Restores position, velocity, and health
```

---

### D. Queue (FIFO) & Event Notifications: `CustomQueue<T>`
- **Class:** [`CustomQueue.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/dsa/CustomQueue.java)
- **Role in Game:** Manages the game notification toast pipeline.
- **Justification:** Gameplay events (coins collected, enemies defeated, power-ups acquired, damage taken) occur asynchronously. If displayed all at once, notifications would overlap. A **First-In, First-Out (FIFO)** queue buffers these events, allowing the HUD to dequeue and display them sequentially in chronological order.
- **Core Operations:**
  - `enqueue(GameEvent event)`: Inserts at `rear` in $O(1)$.
  - `dequeue()`: Extracts from `front` in $O(1)$.
  - Handles empty and full states safely without throwing unhandled exceptions.

```mermaid
graph LR
    IN["Gameplay Event<br/>(Coin / Stomp / Hurt)"] --> REAR["rear -> Node N"]
    REAR --> MID["..."]
    MID --> FRONT["front -> Node 1"]
    FRONT --> OUT["HUD Toast Notification<br/>(Displayed for 2.2s)"]
```

---

### E. Sorting: Manual Bubble Sort: `BubbleSort.java`
- **Class:** [`BubbleSort.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/dsa/BubbleSort.java)
- **Role in Game:** Ranks player scores on the leaderboard in descending order (highest score first).
- **Requirements Satisfied:**
  - Implemented manually with nested loops without using `Arrays.sort()`, `Collections.sort()`, or Java streams.
  - Includes an **early-exit optimization flag** (`boolean swapped`). If an entire pass completes with zero swaps, the array is already sorted, terminating execution immediately.
- **Time Complexity:**
  - **Best Case:** $O(n)$ comparisons and $0$ swaps when the leaderboard is already sorted.
  - **Worst / Average Case:** $O(n^2)$ comparisons and swaps.
  - **Space Complexity:** $O(1)$ auxiliary space (in-place swap).

```java
public static SortMetrics sort(LeaderboardEntry[] entries, int count) {
    SortMetrics metrics = new SortMetrics();
    int n = Math.min(count, entries.length);

    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        metrics.passes++;
        for (int j = 0; j < n - i - 1; j++) {
            metrics.comparisons++;
            // Descending order: swap if left entry has lower score than right
            if (entries[j].getScore() < entries[j + 1].getScore()) {
                LeaderboardEntry temp = entries[j];
                entries[j] = entries[j + 1];
                entries[j + 1] = temp;
                swapped = true;
                metrics.swaps++;
            }
        }
        if (!swapped) break; // Early exit!
    }
    return metrics;
}
```

---

### F. Searching: Linear Search & Recursive Binary Search

#### 1. Linear Search (`LinearSearch.java`)
- **Use Case:** Searching for a player record by name in the leaderboard.
- **Justification:** The leaderboard array is ordered by **score**, not alphabetically by name. Because the names are unsorted, Binary Search cannot be applied. Linear Search is the correct choice, inspecting each element sequentially.
- **Viva Feature:** Generates a step-by-step trace showing every index checked, the comparisons made, and whether a match was found.
- **Time Complexity:** Best: $O(1)$, Worst/Average: $O(n)$. Space: $O(1)$.

#### 2. Recursive Binary Search (`BinarySearch.java`)
- **Use Case:** Searching for a specific score target in the leaderboard.
- **Justification:** Because the leaderboard is pre-sorted in descending order by score via Bubble Sort, we can apply **Divide-and-Conquer** binary search to locate scores in $O(\log n)$ time.
- **Descending Array Adaptation:**
  - If $\text{targetScore} > \text{midScore}$, recurse into the **left half** $[low .. mid - 1]$.
  - If $\text{targetScore} < \text{midScore}$, recurse into the **right half** $[mid + 1 .. high]$.
- **Time Complexity:** $O(\log n)$. Space Complexity: $O(\log n)$ call stack frames.

---

### G. Recursion: Countdown & Score Bonus Recurrence
- **Class:** [`RecursionHelper.java`](file:///Users/damiduuofc/Downloads/DSA%20game/core/src/main/java/com/damidu/supermariodsa/dsa/RecursionHelper.java)

#### 1. Recursive 3-2-1-GO Countdown
- **Algorithm:** At the beginning of each stage, the game executes a recursive countdown:
  - $\text{recurseCountdown}(3) \rightarrow \text{recurseCountdown}(2) \rightarrow \text{recurseCountdown}(1) \rightarrow \text{Base Case (0): emit 'GO!'}$.
- **Base Case:** When $n \le 0$, adds `"GO!"` to the sequence and terminates.
- **Safety:** Guaranteed termination after $n + 1$ frames with stack depth $O(n)$, avoiding stack overflow.

#### 2. Mathematical Score Recurrence for Collected Coins
- At level completion, bonus score is computed via a recursive recurrence:
  $$\text{Bonus}(n) = (100 + 10n) + \text{Bonus}(n - 1), \quad \text{with base case: } \text{Bonus}(0) = 0$$
- Unrolling for 3 coins:
  $$\text{Bonus}(3) = 130 + \text{Bonus}(2) = 130 + 120 + \text{Bonus}(1) = 130 + 120 + 110 + 0 = 360\text{ bonus pts}$$

---

## 5. Summary of Algorithmic Complexities (Big-O)

| Operation / Feature | Underlying Structure | Time (Best) | Time (Worst) | Space (Auxiliary) |
| :--- | :--- | :---: | :---: | :---: |
| Platform Collision Lookup | Fixed 1D `Platform[]` Array | $O(1)$ | $O(1)$ | $O(1)$ |
| Dynamic Leaderboard Growth | Array Resizing (`copy`) | $O(1)$ amortized | $O(n)$ | $O(n)$ |
| Event Log Insertion | `CustomLinkedList` (Tail) | $O(1)$ | $O(1)$ | $O(1)$ |
| Event Log Deletion | `CustomLinkedList` (Head) | $O(1)$ | $O(1)$ | $O(1)$ |
| Snapshot Push (Undo) | `CustomStack` (LIFO) | $O(1)$ | $O(1)$ | $O(1)$ |
| Snapshot Pop (Undo) | `CustomStack` (LIFO) | $O(1)$ | $O(1)$ | $O(1)$ |
| Notification Enqueue | `CustomQueue` (FIFO) | $O(1)$ | $O(1)$ | $O(1)$ |
| Notification Dequeue | `CustomQueue` (FIFO) | $O(1)$ | $O(1)$ | $O(1)$ |
| Leaderboard Ranking | Manual `BubbleSort` | $O(n)$ | $O(n^2)$ | $O(1)$ |
| Search by Player Name | Manual `LinearSearch` | $O(1)$ | $O(n)$ | $O(1)$ |
| Search by Score Target | Recursive `BinarySearch` | $O(1)$ | $O(\log n)$ | $O(\log n)$ call stack |
| Round Start Countdown | Recursive Call Stack | $O(k)$ | $O(k)$ | $O(k)$ call stack |
| Coin Bonus Calculation | Recursive Recurrence | $O(c)$ | $O(c)$ | $O(c)$ call stack |

---

## 6. Interactive DSA Demonstrations in the Game

During your university viva or presentation, you can demonstrate the algorithms live inside the application:

1. **Demonstrating Movement Undo (Stack):**
   - Play any level, jump across obstacles, and press <kbd>Z</kbd> or <kbd>U</kbd>.
   - Mario jumps backwards in time through recent snapshots while the HUD reflects the updating stack size: `[Z/U] Undo: 42`.
2. **Demonstrating the Event Queue (FIFO):**
   - Collect multiple coins or defeat Goombas in rapid succession.
   - Observe the notification toasts at the top of the screen appearing one by one in chronological order.
3. **Demonstrating Bubble Sort Metrics:**
   - From the Main Menu, open **Leaderboard & DSA Search**.
   - Press <kbd>B</kbd>. The screen executes `BubbleSort.sort()` and displays real-time metrics:
     `Bubble Sort Complete: 28 comparisons, 0 swaps, 1 passes (O(n) early-exit verified)!`
4. **Demonstrating Linear Search with Step Trace:**
   - On the Leaderboard screen, press <kbd>L</kbd>.
   - A modal window pops up tracing the step-by-step search for player `"Damidu"` through every index until the match is located.
5. **Demonstrating Recursive Binary Search:**
   - On the Leaderboard screen, press <kbd>S</kbd>.
   - A modal window opens displaying the binary search divide-and-conquer steps, showing `[Depth 1]`, `[Depth 2]`, middle index inspections, and search interval halving on descending data.

---

## 7. Automated Testing Suite (JUnit 5)

The project includes an automated test suite comprising **30 test cases** covering every data structure, sorting method, searching algorithm, and collision boundary condition.

To run the full suite:
```bash
./gradlew test
```

### Test Coverage Summary

```
CustomLinkedListTest    .....................  6 passed (100%)
CustomStackTest         .....................  4 passed (100%)
CustomQueueTest         .....................  4 passed (100%)
BubbleSortTest          .....................  3 passed (100%)
LinearSearchTest        .....................  2 passed (100%)
BinarySearchTest        .....................  3 passed (100%)
RecursionHelperTest     .....................  3 passed (100%)
CollisionManagerTest    .....................  5 passed (100%)
-------------------------------------------------------------
TOTAL: 30 tests, 0 failures, 100% pass rate
```

---

## 8. Viva Defense Questions & Answers

### Q1: Why did you write your own data structures instead of using `java.util`?
> *"The assignment requires demonstrating a deep understanding of memory management, pointer manipulation, and asymptotic complexity. By building `CustomLinkedList`, `CustomStack`, and `CustomQueue` from scratch, we manage head, tail, prev, and next pointers directly and enforce constraints like capacity-bound trimming."*

### Q2: Why is a Doubly Linked List preferred over a Singly Linked List for the event log?
> *"In a Singly Linked List, removing from the tail requires traversing the entire list ($O(n)$) to find the penultimate node. In a Doubly Linked List, each node maintains a `prev` pointer, allowing `removeLast()` to update the tail in $O(1)$ constant time."*

### Q3: Why does your Bubble Sort have an $O(n)$ best-case time complexity?
> *"We implemented an early-exit flag (`boolean swapped = false`). If a pass completes with zero swaps, the array is confirmed to be in sorted order. The algorithm terminates immediately after $n - 1$ comparisons, avoiding the remaining $n - 1$ passes."*

### Q4: Why can't you use Binary Search to look up player names on your leaderboard?
> *"Binary Search requires the array to be sorted by the search key. Because our leaderboard is sorted by **score** (numerical order) rather than alphabetically by player name, binary search on names would yield incorrect results. Therefore, **Linear Search** is appropriate for name queries, while **Binary Search** is used for score queries."*

### Q5: How do you prevent Stack Overflow in your recursive methods?
> *"Each recursive method includes a well-defined base case that is reached monotonically. In the countdown, $n$ decrements towards $0$; in the coin bonus, remaining coins decrement to $0$. The maximum recursion depth is bounded ($n \le 100$), which requires less than a few kilobytes of stack space—well within the JVM's default 1024 KB thread stack limit."*

---

### Project Package Structure Reference

```
com.damidu.supermariodsa
├── SuperMarioGame.java          (Game entry & shared resource lifecycle)
├── audio/
│   └── SoundManager.java        (Procedural 8-bit sound waveform generator)
├── dsa/
│   ├── BinarySearch.java        (Recursive binary search with depth trace)
│   ├── BubbleSort.java          (Manual bubble sort with early-exit flag)
│   ├── CustomLinkedList.java    (Doubly linked list with iterator)
│   ├── CustomQueue.java         (FIFO queue for event notifications)
│   ├── CustomStack.java         (LIFO stack for movement undo)
│   ├── LinearSearch.java        (Linear search with step-by-step trace)
│   └── RecursionHelper.java     (Recursive countdown & coin bonus)
├── level/
│   ├── Level.java               (Level container using plain arrays)
│   └── LevelManager.java        (World 1-1, 1-2, and 1-3 stage builder)
├── model/
│   ├── Coin.java                (Spinning collectible coin)
│   ├── Enemy.java               (Goombas, Koopas, and Boss Bowser)
│   ├── GameEvent.java           (Queue event container)
│   ├── GoalFlag.java            (Level exit flagpole)
│   ├── Platform.java            (Terrain, bricks, pipes, mystery blocks)
│   ├── Player.java              (Mario kinematics, health, and rendering)
│   ├── PlayerStateSnapshot.java (Stack undo snapshot record)
│   └── PowerUp.java             (Super Mushroom and Star items)
├── persistence/
│   ├── LeaderboardEntry.java    (Leaderboard score record)
│   ├── LeaderboardManager.java  (Plain array leaderboard file I/O)
│   └── SaveManager.java         (Progress file persistence for continue)
├── physics/
│   └── CollisionManager.java    (Two-axis separated AABB collision engine)
├── screens/
│   ├── GameOverScreen.java      (Game over display and restart)
│   ├── GameScreen.java          (Main camera, rendering, and game loop)
│   ├── HowToPlayScreen.java     (In-game DSA guide and controls)
│   ├── LeaderboardScreen.java   (Interactive DSA algorithm demonstrator)
│   ├── MainMenuScreen.java      (Main menu navigation)
│   └── VictoryScreen.java       (Game completion and bonus breakdown)
└── ui/
    └── HUD.java                 (Health hearts, lives, score, and queue toasts)
```
