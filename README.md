# FIT2099 Assignment (Semester 1, 2026)

```
 _______  _______  ______    _______  _______  _______  _______                          
|       ||   _   ||    _ |  |  _    ||   _   ||       ||       |                         
|    ___||  |_|  ||   | ||  | |_|   ||  |_|  ||    ___||    ___|                         
|   | __ |       ||   |_||_ |       ||       ||   | __ |   |___                          
|   ||  ||       ||    __  ||  _   | |       ||   ||  ||    ___|                         
|   |_| ||   _   ||   |  | || |_|   ||   _   ||   |_| ||   |___                          
|_______||__| |__||___|  |_||_______||__| |__||_______||_______|                         
 _______  _______  ___      ___      _______  _______  _______  ___   _______  __    _   
|       ||       ||   |    |   |    |       ||       ||       ||   | |       ||  |  | |  
|       ||   _   ||   |    |   |    |    ___||       ||_     _||   | |   _   ||   |_| |  
|       ||  | |  ||   |    |   |    |   |___ |       |  |   |  |   | |  | |  ||       |  
|      _||  |_|  ||   |___ |   |___ |    ___||      _|  |   |  |   | |  |_|  ||  _    |  
|     |_ |       ||       ||       ||   |___ |     |_   |   |  |   | |       || | |   |  
|_______||_______||_______||_______||_______||_______|  |___|  |___| |_______||_|  |__|  
 ___   __    _  _______                                                                  
|   | |  |  | ||       |                                                                 
|   | |   |_| ||       |                                                                 
|   | |       ||       |                                                                 
|   | |  _    ||      _| ___                                                             
|   | | | |   ||     |_ |   |                                                            
|___| |_|  |__||_______||___|                                                                                                                                                                                                                         
```
## Contribution log
[Contribution log link](https://docs.google.com/spreadsheets/d/1EAoeuuQ9YHIjVSB8gqZoziGPfBNeuz8M6MIWxhEWIpc/edit?usp=sharing)
## About the Mannequin

The Mannequin is a sophisticated, adaptive AI actor that cycles through four distinct behavioral states based on environmental conditions, inventory status, and worker proximity. It employs a **State Machine Pattern** to manage complex behavior transitions and execution.

### How the Mannequin Works

The Mannequin's behavior is controlled by a **state machine** with four states: **Idle**, **Active**, **Berserk**, and **Mimic**.

---

## State Behaviors

### **1. Idle State**
**Display Character:** 'M'

**How It Works:**
- The Mannequin stands perfectly still, indistinguishable from furniture
- It counts the number of turns it remains idle
- It constantly monitors nearby workers within a 3-tile radius

**Transitions:**
- **→ Berserk** — Instantly, if it detects exactly **1 worker** nearby (solo worker = easy prey)
- **→ Active** — If it has been idle for **10+ turns** AND **0 workers** are in range (safe to move)
- **Stays Idle** — If multiple workers (2+) are nearby (too dangerous) or during the first 10 quiet turns

**Entry Effect:**
- Drops all items from inventory onto the ground
- Resets the idle turn counter to 0

---

### **2. Active State**
**Display Character:** 'M'  
**Active Behaviors:** Seek and pick up items from the ground

**How It Works:**
- The Mannequin roams the map searching for loose items
- Immediately upon entry, it **vacuums** items from its current tile and adjacent tiles (up to its 3-item capacity)
- Each turn, it continues collecting items
  - Checks current location for items
  - Checks adjacent tiles for accessible items
  - Scans the full map to find the nearest uncollected item and moves toward it

**Transitions:**
- **→ Berserk** — If it detects exactly **1 worker** nearby (interrupts collection; hunt mode)
- **→ Mimic** — If its inventory is **full (3 items)** OR if **2+ workers** are nearby AND it **has items** (disguise and heal)
- **→ Idle** — If **2+ workers** are nearby AND it has **no items** (nothing to hide, better to stay motionless)
- **Stays Active** — If conditions favour continuing to hunt for items

**Entry Effect:**
- Displays as 'M'
- Resets the idle turn counter
- Vacuums all available items from current and adjacent locations

---

### **3. Berserk State**
**Display Character:** 'M'  
**Active Behaviors:**
1. Attack adjacent workers (priority)
2. Hunt and pursue a lone worker (fallback)

**How It Works:**
- The Mannequin abandons all pretense and hunts aggressively
- **Priority 1:** If a worker is adjacent, attack immediately
- **Priority 2:** If no adjacent worker, chase the nearest lone target using Chebyshev distance pathfinding
- Cannot tolerate being outnumbered; retreats if the situation becomes overwhelming

**Transitions:**
- **→ Idle** — If **2+ workers** are detected within range (outnumbered; retreat to hide)
- **→ Mimic** — If **0 workers** are nearby AND **health ≤ 30%** AND it **has items** (escape via disguise to heal)
- **→ Active** — If **0 workers** are nearby AND (health > 30% OR no items) (cool down; resume scavenging)
- **Stays Berserk** — If exactly 1 worker is detected (continue the hunt)

**Entry Effect:**
- Displays as 'M'
- Resets the idle turn counter
- **Panic Effect:** Forces all **adjacent workers to drop their items** (ambush shock; workers panic and scatter their inventory)

---

### **4. Mimic State**
**Display Character:** The item's glyph (e.g., 'G' for gold coin, or '?' if empty)  
**Active Behaviors:** Heal by manipulating adjacent workers

**How It Works:**
- The Mannequin **disguises itself** as one of its carried items
- The display character changes to mimic the item (fooling observers into thinking it's harmless)
- While disguised:
  - Each turn, it checks for adjacent workers
  - If an adjacent worker is found:
    - The Mannequin heals **2 HP**
- The disguise is temporary and lasts **5 turns**

**Transitions:**
- **→ Berserk** — If it detects exactly **1 worker** nearby (disguise exposed; revert to aggression)
- **→ Idle** — If the disguise duration **expires (5 turns)** AND inventory is **empty** (nothing left to show for the effort)
- **→ Active** — If the disguise duration **expires (5 turns)** AND inventory has **items** (resume collecting)
- **Stays Mimic** — If the duration timer hasn't expired and no solo worker detected

**Entry Effect:**
- Sets disguise duration to 5 turns
- **Takes the first item from inventory**, permanently removes it, and sets the display character to that item's glyph
  - If the inventory is empty, uses a default disguise character '?'
- **Pushes all adjacent workers away** using distance heuristics to create separation
  - Workers are moved away from the Mannequin, unaware they've been manipulated
