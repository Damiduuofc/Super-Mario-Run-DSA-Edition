# 10-Minute Video Script: DSA in Super Mario DSA

## 0:00-0:40 - Introduction

**Show:** Start the game and display the main menu.

**Say:**

> Hello everyone. This video demonstrates my Super Mario DSA game, developed using Java, libGDX, and Gradle.
>
> The main purpose of this project is to show how Data Structures and Algorithms can be applied to real gameplay instead of being used only in theoretical examples.
>
> The game contains three progressive levels, enemies, coins, power-ups, collision detection, saving and resuming, a leaderboard, and several custom DSA implementations.

**Show files:**

- `README.md`
- `core/src/main/java/com/damidu/supermariodsa/screens/GameScreen.java`

## 0:40-1:20 - Game Architecture

**Show:** Play the first level briefly.

**Say:**

> The central gameplay class is `GameScreen`.
>
> It controls the player, enemies, coins, power-ups, collisions, score, level progression, saving, and DSA structures.
>
> The game uses arrays for fixed level objects such as platforms, coins, enemies, and power-ups.
>
> In `Level.java`, these objects are stored using arrays:
>
> ```java
> private final Platform[] platforms;
> private final Coin[] coins;
> private final Enemy[] enemies;
> private final PowerUp[] powerUps;
> ```
>
> Arrays are appropriate because the level layout is known and fixed. The game can loop through these objects every frame to update and render them.

**Show:** `core/src/main/java/com/damidu/supermariodsa/level/Level.java`

## 1:20-2:20 - Arrays and Collision Detection

**Show:** Run into a platform and jump onto one.

**Say:**

> Arrays are also used during collision detection.
>
> Every frame, the `CollisionManager` receives the player and the platform array:
>
> ```java
> public void update(Player player, Platform[] platforms, float delta)
> ```
>
> The player position is calculated first. Then the program loops through the platforms and checks whether the player's rectangular bounds overlap with a platform.
>
> ```java
> for (Platform platform : platforms) {
>     if (testBounds.overlaps(platform.getBounds())) {
>         // Resolve collision
>     }
> }
> ```
>
> The collision algorithm separates horizontal and vertical movement. This prevents the player from becoming stuck inside corners.
>
> When the player is falling and touches the top of a platform, the player lands on it. When the player jumps and hits the underside, the vertical velocity is stopped.
>
> The collision loop takes `O(p)` time, where `p` is the number of platforms.

**Show:** `core/src/main/java/com/damidu/supermariodsa/physics/CollisionManager.java`

## 2:20-3:30 - Stack and Movement Undo

**Show:** Move Mario, jump, then press `Z` or `U`.

**Say:**

> The first major DSA feature is the custom stack.
>
> The stack implements the LIFO principle: Last In, First Out.
>
> This is ideal for the undo feature because the most recent player state should be restored first.
>
> In `GameScreen`, the stack is created like this:
>
> ```java
> this.undoStack = new CustomStack<>(80);
> ```
>
> Every 0.35 seconds, the game stores a snapshot of the player:
>
> ```java
> if (snapshotTimer >= 0.35f) {
>     snapshotTimer = 0f;
>     undoStack.push(player.createSnapshot());
> }
> ```
>
> A snapshot contains information such as position, velocity, health, score, and coins.
>
> When the player presses the undo key, the latest snapshot is removed using `pop()`.
>
> ```java
> public void push(T item) {
>     top = new Node<>(item, top);
>     size++;
> }
>
> public T pop() {
>     T data = top.data;
>     top = top.next;
>     size--;
>     return data;
> }
> ```
>
> Both `push` and `pop` operate in `O(1)` time. The stack also has a maximum capacity so that old snapshots are removed when the history becomes full.

**Show:**

- `core/src/main/java/com/damidu/supermariodsa/dsa/CustomStack.java`
- `core/src/main/java/com/damidu/supermariodsa/model/PlayerStateSnapshot.java`

## 3:30-4:25 - Queue and Game Notifications

**Show:** Collect coins or defeat an enemy several times quickly.

**Say:**

> The next structure is a custom queue.
>
> A queue follows FIFO: First In, First Out.
>
> Gameplay events such as collecting a coin, taking damage, defeating an enemy, or collecting a power-up are added to the event queue.
>
> For example:
>
> ```java
> eventQueue.enqueue(new GameEvent(
>     GameEvent.EventType.COIN_COLLECTED,
>     "Coin collected! +200 pts",
>     200
> ));
> ```
>
> The HUD displays these events one at a time by removing the event at the front:
>
> ```java
> GameEvent event = eventQueue.dequeue();
> ```
>
> This means notifications appear in the same order that the events happened during gameplay.
>
> The queue stores both a `front` and a `rear` pointer, so enqueue and dequeue are both `O(1)` operations.

**Show:**

- `core/src/main/java/com/damidu/supermariodsa/dsa/CustomQueue.java`
- `core/src/main/java/com/damidu/supermariodsa/ui/HUD.java`

## 4:25-5:10 - Doubly Linked List

**Show:** Open `CustomLinkedList.java`.

**Say:**

> The game also includes a custom doubly linked list.
>
> Each node contains the stored data, a pointer to the next node, and a pointer to the previous node:
>
> ```java
> public static class Node<T> {
>     public T data;
>     public Node<T> next;
>     public Node<T> prev;
> }
> ```
>
> The game uses this structure to record dynamic gameplay event history.
>
> When an enemy is defeated, the event message is added to the end of the list:
>
> ```java
> eventHistoryList.addLast(msg);
> ```
>
> Because the list has both a head and a tail pointer, adding or removing from either end takes `O(1)` time.

**Show:** `core/src/main/java/com/damidu/supermariodsa/dsa/CustomLinkedList.java`

## 5:10-6:10 - Bubble Sort and Leaderboard

**Show:** Open the leaderboard screen and press `B`.

**Say:**

> The leaderboard uses a manually implemented Bubble Sort algorithm.
>
> Each score is ranked from highest to lowest.
>
> The leaderboard manager calls:
>
> ```java
> BubbleSort.sort(entries, count);
> ```
>
> The algorithm compares neighboring scores:
>
> ```java
> if (entries[j].getScore() < entries[j + 1].getScore()) {
>     LeaderboardEntry temp = entries[j];
>     entries[j] = entries[j + 1];
>     entries[j + 1] = temp;
> }
> ```
>
> Because this is descending order, a lower score on the left is swapped with a higher score on the right.
>
> The implementation uses an early-exit flag. If a complete pass makes no swaps, the array is already sorted and the algorithm stops.
>
> The worst-case complexity is `O(n^2)`, while the best case with early exit is `O(n)`.

**Show:**

- `core/src/main/java/com/damidu/supermariodsa/dsa/BubbleSort.java`
- `core/src/main/java/com/damidu/supermariodsa/persistence/LeaderboardManager.java`

## 6:10-7:10 - Linear Search

**Show:** On the leaderboard screen, press `L`.

**Say:**

> The game uses Linear Search to find a player by name.
>
> Linear Search is appropriate because the leaderboard is sorted by score, not by player name. Therefore, the names are not guaranteed to be alphabetically sorted.
>
> The method checks each valid entry in sequence:
>
> ```java
> for (int i = 0; i < count; i++) {
>     LeaderboardEntry current = entries[i];
>
>     if (current.getPlayerName().equalsIgnoreCase(target)) {
>         return new SearchResult(true, current, i, comparisons, trace);
>     }
> }
> ```
>
> The game creates a trace showing every index checked.
>
> The best case is `O(1)` when the player is found first. The worst case is `O(n)` when every entry must be inspected.

**Show:** `core/src/main/java/com/damidu/supermariodsa/dsa/LinearSearch.java`

## 7:10-8:00 - Recursive Binary Search

**Show:** On the leaderboard screen, press `S`.

**Say:**

> The game also demonstrates Recursive Binary Search, but this time it searches for a score.
>
> Binary Search is possible because leaderboard entries are already sorted by score.
>
> The algorithm checks the middle entry and eliminates half of the search range after every comparison.
>
> ```java
> int mid = low + (high - low) / 2;
> ```
>
> Because the leaderboard is sorted in descending order, the search direction is adjusted:
>
> ```java
> if (targetScore > midScore) {
>     return recursiveSearch(entries, low, mid - 1, targetScore, depth + 1, trace);
> } else {
>     return recursiveSearch(entries, mid + 1, high, targetScore, depth + 1, trace);
> }
> ```
>
> The base cases are when the target is found or when the search range becomes empty.
>
> Binary Search has a time complexity of `O(log n)` and uses `O(log n)` recursive stack space.

**Show:** `core/src/main/java/com/damidu/supermariodsa/dsa/BinarySearch.java`

## 8:00-8:50 - Recursion in Gameplay

**Show:** Start a level and show the countdown. Then complete a level or show the victory screen.

**Say:**

> Recursion is used in two visible game features.
>
> First, the level begins with a recursive countdown:
>
> ```java
> buildCountdownTrace(3)
> ```
>
> This produces:
>
> ```text
> 3, 2, 1, GO!
> ```
>
> The recursive method has a base case when the number reaches zero:
>
> ```java
> if (n <= 0) {
>     sequence.add("GO!");
>     return;
> }
> ```
>
> Recursion is also used to calculate the coin bonus when completing a level:
>
> ```java
> public static int calculateCoinBonus(int coinsRemaining) {
>     if (coinsRemaining <= 0) {
>         return 0;
>     }
>
>     int currentCoinValue = 100 + coinsRemaining * 10;
>     return currentCoinValue
>         + calculateCoinBonus(coinsRemaining - 1);
> }
> ```
>
> The base case is zero coins. Each recursive call decreases the remaining coin count, guaranteeing termination.
>
> This algorithm takes `O(c)` time and `O(c)` recursive stack space, where `c` is the number of coins.

**Show:**

- `core/src/main/java/com/damidu/supermariodsa/dsa/RecursionHelper.java`
- `core/src/main/java/com/damidu/supermariodsa/screens/VictoryScreen.java`

## 8:50-9:30 - Saving and Resuming Progress

**Show:** Press `F5`, exit to the menu, and choose Continue.

**Say:**

> The game also includes a save and resume system.
>
> When the player presses `F5` or `K`, the game saves the player name, level, lives, health, score, coins, and checkpoint coordinates.
>
> The data is written to a local file in a structured format:
>
> ```text
> PlayerName;Level;Lives;Health;Score;Coins;CheckpointX;CheckpointY
> ```
>
> The `SaveManager` loads these values when the player selects Continue.
>
> This allows the game to restore the player at the correct level and checkpoint instead of starting from the beginning.

**Show:** `core/src/main/java/com/damidu/supermariodsa/persistence/SaveManager.java`

## 9:30-10:00 - Testing and Conclusion

**Show:** Open a terminal and run:

```bash
./gradlew test
```

**Say:**

> Finally, the project includes automated JUnit tests for the custom linked list, stack, queue, Bubble Sort, Linear Search, Binary Search, recursion, saving, and collision detection.
>
> These tests verify that the structures work correctly independently of the game.
>
> To conclude, this project demonstrates that DSA concepts can directly control gameplay:
>
> - Arrays store level objects.
> - A stack provides movement undo.
> - A queue controls event notifications.
> - A doubly linked list records dynamic history.
> - Bubble Sort ranks the leaderboard.
> - Linear Search finds players by name.
> - Binary Search finds scores efficiently.
> - Recursion controls countdowns and coin bonuses.
>
> This shows how data structures and algorithms can be connected to real user interactions and game mechanics.

**End by showing:** The running game, leaderboard, and DSA source-code package.