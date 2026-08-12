# Lotto Numbers: Application-Flow and Line-by-Line Guide

## 1. What the assignment asks for

The prompt has five main requirements:

1. Build a Java Swing application.
2. Generate six random Lottery numbers.
3. Use **Composite** for UI construction.
4. Use **Observer** for UI-event handling.
5. Use **State** for Community and Professional editions.
6. Represent the whole running application with one **Singleton** `Application` object.

This project maps those requirements as follows:

- Swing UI: `MainView`, plus Swing controls created by both state classes.
- Composite: `UIComponent`, `UILeaf`, and `UIComposite`.
- Observer: `UIEvent`, `UIEventObserver`, `UIEventPublisher`, and `MainController`.
- State: `LottoState`, `CommunityState`, and `ProfessionalState`.
- Singleton: `Application`.
- Random number logic: `LottoNumberGenerator`.
- Program entry point: `Main`.
- Verification: `LottoNumbersTest`.

## 2. Application flow

### Startup

1. Java calls `Main.main`.
2. `Main` schedules startup on Swing's Event Dispatch Thread.
3. `Application.getInstance()` returns the one Singleton application object.
4. `Application.start()` creates `MainView` and `MainController`.
5. `Application` begins in `CommunityState`.
6. `MainView.showState(...)` asks that state for its name, description, and options UI.
7. `MainView` displays the window.

### Generate button

1. User clicks **Generate**.
2. Swing's action listener tells `UIEventPublisher` to publish `UIEvent.GENERATE`.
3. `MainController`, registered as an observer, receives the event.
4. Controller asks current `LottoState` to generate numbers.
5. Current state supplies edition-specific rules:
   - Community: fixed range `1..42`, six numbers, fresh random source.
   - Professional: user range, six numbers, date-derived deterministic random seed.
6. `LottoNumberGenerator` validates, shuffles candidates, selects six unique values, sorts them, and returns them.
7. Controller sends result to `MainView`.
8. View formats and displays the numbers.

### Upgrade or switch edition

1. User clicks edition-switch button.
2. View publishes `UPGRADE` or `USE_COMMUNITY`.
3. Controller creates corresponding state and calls `Application.setState(...)`.
4. Application stores new state and tells view to redraw state-specific controls.
5. Same controller and same view keep running; behavior changes through state object. This is core State-pattern idea: smooth behavior change without replacing application.

## 3. Reading conventions

- Line numbers match current source files.
- Blank lines only separate logical sections and have no runtime effect.
- A line containing only `{` starts a scope; a line containing only `}` ends one.
- Some Java statements span several physical lines. Those lines are explained together where splitting them would hide their meaning.
- “Prompt link” explains why line exists relative to assignment.

---

## 4. `Main.java` — program entry

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports `SwingUtilities`, Swing helper used to schedule UI work safely. | Assignment requires Swing UI. |
| 3 | Declares `Main` as public and `final`; it is entry-point utility class and cannot be subclassed. | Starts whole requested app. |
| 4–5 | Private empty constructor prevents creating meaningless `Main` objects. | Only `Application` should represent running app. |
| 7 | Declares Java entry method. JVM calls this first. `args` would contain command-line arguments, though unused. | Beginning of application flow. |
| 8 | `invokeLater` schedules lambda on Swing Event Dispatch Thread. Lambda gets Singleton `Application`, then calls `start()`. | Combines correct Swing startup with Singleton requirement. |
| 9 | Ends `main`. | Structural. |
| 10 | Ends class. | Structural. |

Important: `Application.getInstance()` is called inside lambda, so UI creation happens on Swing thread.

---

## 5. `Application.java` — Singleton and State context

`Application` has two design-pattern roles:

- **Singleton:** only one instance represents whole app.
- **State context:** it stores current `LottoState` and changes it.

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports Swing top-level window class `JFrame`. | Swing UI requirement. |
| 3 | Declares non-inheritable `Application`. | Central object represents entire app. |
| 4 | Stores current behavior object through `LottoState` interface. | State pattern context field. |
| 5 | Stores window reference. `null` means app window has not started. | Manages single running UI. |
| 6 | Stores view reference so state changes can refresh UI. | Connects current State to Swing display. |
| 7 | Creates one eagerly initialized, private static `Application` object. Class loading creates it once. | Direct Singleton implementation. |
| 9 | Private constructor prevents outside code from calling `new Application()`. | Enforces Singleton. |
| 10 | Sets initial edition to `CommunityState`. | Prompt defines Community behavior; app starts there. |
| 11 | Ends constructor. | Structural. |
| 13 | Declares public static access point for Singleton. No instance needed to call it. | Required way to access sole application. |
| 14 | Returns exact same `instance` every time. | Singleton identity guarantee. |
| 15 | Ends getter. | Structural. |
| 17 | Declares getter for current state. | Controller needs active edition behavior. |
| 18 | Returns current `LottoState`. | State pattern delegates work to current state. |
| 19 | Ends getter. | Structural. |
| 21 | Declares state-transition method. Parameter may be any `LottoState` implementation. | Allows smooth edition switch. |
| 22 | Checks invalid `null` state. | Keeps State context valid. |
| 23 | Throws clear input error if caller tries to remove state. | Prevents app from having no edition behavior. |
| 24 | Ends validation block. | Structural. |
| 25 | Replaces old state with new state. | Actual State-pattern transition. |
| 26 | Checks whether view already exists. State may be set before startup. | Avoids calling UI before it is created. |
| 27 | Tells existing view to rebuild labels and controls for new state. | Makes State transition visible and smooth. |
| 28 | Ends view check. | Structural. |
| 29 | Ends setter. | Structural. |
| 31 | Declares startup operation. | Launches requested application. |
| 32 | Checks whether window already exists. | Helps ensure one Singleton does not open duplicate windows. |
| 33 | Brings existing window to front. | Repeated `start()` reuses same app window. |
| 34 | Exits method early. | Stops duplicate UI construction. |
| 35 | Ends already-started branch. | Structural. |
| 37 | Creates view containing Swing controls. | Swing requirement. |
| 38 | Creates controller with this application and view. Constructor registers controller as observer. Object is not stored because publisher holds its observer reference. | Observer event handling requirement. |
| 39 | Renders initial Community state into view. | State requirement and startup flow. |
| 41 | Creates top-level Swing window with title. | Swing requirement. |
| 42 | Makes program exit when user closes window. | Standard desktop-app lifecycle. |
| 43 | Places Composite-built view root inside frame. | Connects Swing window to Composite UI tree. |
| 44 | Sizes frame from preferred sizes of its child components. | Produces usable UI. |
| 45 | Centers frame on screen by passing `null`. | UI convenience. |
| 46 | Makes completed window visible. | Final startup step. |
| 47 | Ends `start`. | Structural. |
| 48 | Ends class. | Structural. |

### Singleton detail

This is an **eager Singleton**: line 7 constructs instance when class initializes. Java class initialization is thread-safe, so no manual synchronization is needed. Private constructor plus static getter prevents normal outside construction.

---

## 6. `LottoState.java` — State contract

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports `JComponent`, common Swing base type. | Each edition supplies its UI options. |
| 2 | Imports `List`, return type for generated numbers. | Both editions generate number lists. |
| 4 | Declares State-pattern interface. | Required State abstraction. |
| 5 | Requires every state to provide edition name. | View changes edition label without knowing concrete details. |
| 7 | Requires every state to provide explanatory text. | View changes description through State interface. |
| 9 | Requires every state to build edition-specific Swing controls. | Community and Professional need different UI. |
| 11 | Requires every state to provide number-generation behavior. | Core behavior changes with state. |
| 12 | Ends interface. | Structural. |

The controller depends on `LottoState`, not on one concrete edition. This lets it call `generateNumbers()` identically in both states.

---

## 7. `CommunityState.java` — fixed Community edition

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports `JComponent` for options-panel return value. | State-specific Swing UI. |
| 2 | Imports `JLabel` for read-only Community settings. | Shows fixed rules. |
| 3 | Imports `List` for number result. | Six generated Lottery numbers. |
| 4 | Imports `Random` for fresh random draws. | Random-selection requirement. |
| 6 | Declares final Community State implementing common `LottoState`. | One of two required states. |
| 7 | Marks next method as implementation of interface method. Compiler checks signature. | State contract. |
| 8 | Starts edition-name method. | State supplies its identity. |
| 9 | Returns `"Community"`. | Community state from prompt. |
| 10 | Ends method. | Structural. |
| 12 | Marks description override. | State contract. |
| 13 | Starts description method. | Supplies state-specific UI text. |
| 14 | States exact fixed rules: six unique numbers, `1..42`. | Direct match to Community prompt. |
| 15 | Ends method. | Structural. |
| 17 | Marks options-component override. | State contract. |
| 18 | Starts UI creation method. | State owns edition-specific options UI. |
| 19 | Creates horizontal Composite with 8-pixel gaps. | Composite requirement. |
| 20 | Wraps `"Range: 1 - 42"` Swing label in `UILeaf`, then adds it to Composite. | Shows fixed Community range using Composite tree. |
| 21 | Wraps `"Numbers: 6"` label and adds second leaf. | Shows fixed requested count. |
| 22 | Builds Composite and returns resulting Swing component. | State UI becomes part of main UI. |
| 23 | Ends method. | Structural. |
| 25 | Marks generation override. | State contract. |
| 26 | Starts Community behavior. | State owns generation rule. |
| 27 | Delegates to shared generator with minimum `1`, maximum `42`, count `6`, and fresh `Random`. | Exact Community requirement. |
| 28 | Ends method. | Structural. |
| 29 | Ends class. | Structural. |

Fresh `new Random()` means repeated Community clicks normally produce different selections.

---

## 8. `ProfessionalState.java` — configurable Professional edition

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports base Swing component type. | Method returns Professional options UI. |
| 2 | Imports labels. | Labels inputs. |
| 3 | Imports spinner input control. | User sets numbers and date. |
| 4 | Imports date model for date spinner. | Prompt requires user-provided date. |
| 5 | Imports numeric model for range spinners. | Prompt requires configurable range. |
| 6 | Imports modern date-only type `LocalDate`. | Uses day, month, year without time. |
| 7 | Imports system time-zone lookup. | Converts old Swing `Date` to `LocalDate`. |
| 8 | Imports legacy `Date`, value type used by `JSpinner` date model. | Swing date input. |
| 9 | Imports `List` for generated numbers. | Lottery output. |
| 10 | Imports `Random` for seeded shuffling. | Random generation. |
| 12 | Declares final Professional State implementing `LottoState`. | Second required state. |
| 13 | Creates minimum spinner. Defaults to `1`, allows `1..999999`, step `1`. | Lets Professional user set lower range bound. |
| 14 | Creates maximum spinner. Defaults to `42`, allows `1..1000000`, step `1`. | Lets Professional user set upper bound. |
| 15 | Creates date spinner using default date model, initially current date/time. | Lets user provide requested date. |
| 17 | Starts constructor. | Configures state-owned UI control. |
| 18 | Replaces default date editor with `dd/MM/yyyy` format. | Explicit day/month/year input from prompt. |
| 19 | Ends constructor. | Structural. |
| 21 | Marks name-method override. | State contract. |
| 22 | Starts name method. | Supplies edition identity. |
| 23 | Returns `"Professional"`. | Professional state from prompt. |
| 24 | Ends method. | Structural. |
| 26 | Marks description override. | State contract. |
| 27 | Starts description method. | Supplies UI explanation. |
| 28 | Explains configurable range/date and deterministic repeat behavior. | Extends Professional prompt with clear date behavior. |
| 29 | Ends method. | Structural. |
| 31 | Marks options-component override. | State contract. |
| 32 | Starts Professional UI construction. | Creates edition-specific controls. |
| 33 | Creates horizontal Composite with 8-pixel gaps. | Composite UI generation requirement. |
| 34 | Creates and adds `"Minimum:"` label leaf. | Labels range input. |
| 35 | Wraps existing minimum spinner in leaf and adds it. | User-configurable lower bound. |
| 36 | Creates and adds `"Maximum:"` label leaf. | Labels range input. |
| 37 | Wraps maximum spinner in leaf and adds it. | User-configurable upper bound. |
| 38 | Creates and adds `"Draw date:"` label leaf. | Labels date input. |
| 39 | Wraps date spinner in leaf and adds it. | User-provided day/month/year. |
| 40 | Builds Composite and returns Swing panel. | State-specific subtree joins main UI tree. |
| 41 | Ends method. | Structural. |
| 43 | Marks generation override. | State contract. |
| 44 | Starts Professional generation behavior. | State owns configurable algorithm. |
| 45 | Reads minimum spinner value and casts boxed `Integer` to primitive `int`. | Uses user-set range. |
| 46 | Reads maximum spinner value likewise. | Uses user-set range. |
| 47–49 | Reads spinner's legacy `Date`, converts it to an instant, applies machine's default time zone, then extracts date-only `LocalDate`. | Uses supplied day, month, and year as prompt requests. |
| 50 | Converts date to number of days since Unix epoch and uses it as initial seed. | Makes date affect generated result. |
| 51 | Mixes minimum into seed using multiplier `31`. | Makes chosen lower bound affect result. |
| 52 | Mixes maximum into seed likewise. | Makes chosen upper bound affect result. |
| 53 | Calls shared generator for six values using user range and deterministic `Random(seed)`. | Professional generation requirement. |
| 54 | Ends method. | Structural. |
| 56 | Declares package-private helper used by tests to set all inputs without clicking UI. | Enables verification of Professional rules. |
| 57 | Sets minimum spinner value. Spinner model validates its allowed bounds. | Test setup for configurable range. |
| 58 | Sets maximum spinner value. | Test setup for configurable range. |
| 59 | Sets date spinner value. | Test setup for date behavior. |
| 60 | Ends helper. | Structural. |
| 61 | Ends class. | Structural. |

### Meaning of date behavior

Prompt says generator should “work by” supplied date but does not define exact algorithm. This implementation interprets that requirement as a seed: same date plus same range produces same draw. Changing date or range changes seed and normally changes draw.

---

## 9. `LottoNumberGenerator.java` — shared generation algorithm

This class is not one of four patterns. It isolates reusable Lottery logic so both states avoid duplicating it.

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports mutable `ArrayList`. | Stores candidate and result numbers. |
| 2 | Imports collection shuffle/sort utilities. | Randomizes then orders draw. |
| 3 | Imports `List` interface. | Public result abstraction. |
| 4 | Imports `Random`. | Caller controls random strategy/seed. |
| 6 | Declares final utility class. | Shared Lottery generation. |
| 7–8 | Private empty constructor prevents instances; all behavior is static. | Generator is helper, not whole application object. |
| 10 | Declares static generator taking inclusive bounds, result count, and random source. | Supports both Community and Professional rules. |
| 11 | Tests logically invalid reversed range. | Validates Professional user input. |
| 12 | Throws user-readable exception when minimum exceeds maximum. | Controller later displays error in Swing dialog. |
| 13 | Ends first validation. | Structural. |
| 14 | Computes inclusive range size as `maximum - minimum + 1`; casts to `long` before subtraction to avoid `int` overflow; checks enough unique values exist. | Six unique numbers require at least six candidates. |
| 15 | Throws error naming requested count if range is too small. | Enforces valid Lottery draw. |
| 16 | Ends second validation. | Structural. |
| 18 | Creates empty candidate list. | Will contain every integer in inclusive range. |
| 19 | Loops from minimum through maximum, including both endpoints. | Prompt says range endpoints are included. |
| 20 | Adds current integer to candidates. | Builds pool of possible Lottery values. |
| 21 | Detects largest possible Java `int`. | Prevents `number++` overflow from wrapping to negative. |
| 22 | Stops loop safely at `Integer.MAX_VALUE`. | Defensive correctness for general generator. |
| 23 | Ends overflow guard. | Structural. |
| 24 | Ends candidate-building loop. | Structural. |
| 25 | Randomly permutes all candidates using supplied `Random`. | Random selection without duplicates. |
| 27 | Copies first `count` shuffled values into independent result list. | Selects requested number of unique values. |
| 28 | Sorts selected values ascending for readable display. | Presentation convenience; selection stays random. |
| 29 | Returns completed draw. | Sends numbers back through State → Controller → View. |
| 30 | Ends method. | Structural. |
| 31 | Ends class. | Structural. |

Why values are unique: candidate list contains each range value once, and selection takes different list positions after shuffling.

---

## 10. `UIComponent.java` — Composite common component

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports Swing component base type. | Composite represents UI elements. |
| 3 | Declares common Composite-pattern interface. | Required Composite abstraction. |
| 4 | Requires both leaf and composite objects to expose same `build()` operation. | Client treats single widgets and widget groups uniformly. |
| 5 | Ends interface. | Structural. |

This common type is what makes `UILeaf` and `UIComposite` interchangeable inside a UI tree.

---

## 11. `UILeaf.java` — Composite leaf

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports base Swing component. | Leaf wraps real Swing widget. |
| 3 | Declares final Composite leaf implementing `UIComponent`. | Required leaf role. |
| 4 | Stores one wrapped Swing component permanently. | Leaf represents indivisible UI element. |
| 6 | Constructor accepts component to wrap. | Allows labels, buttons, spinners, and panels to become tree leaves. |
| 7 | Rejects `null`. | Keeps Composite tree valid. |
| 8 | Throws clear construction error. | Defensive UI construction. |
| 9 | Ends validation. | Structural. |
| 10 | Saves wrapped component. | Establishes leaf contents. |
| 11 | Ends constructor. | Structural. |
| 13 | Marks implementation of shared Composite operation. | Common component contract. |
| 14 | Starts `build`. | Converts pattern object to Swing object. |
| 15 | Returns wrapped widget unchanged. | Leaf has no children to build. |
| 16 | Ends method. | Structural. |
| 17 | Ends class. | Structural. |

---

## 12. `UIComposite.java` — Composite container

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports vertical `BoxLayout`. | Supports column layouts. |
| 2 | Imports base Swing component return type. | Implements common `build`. |
| 3 | Imports `JPanel`, actual Swing container. | Composite contains child widgets. |
| 4 | Imports empty padding border. | Adds column spacing. |
| 5 | Imports `BorderLayout`. | Used as initial column panel layout. |
| 6 | Imports `FlowLayout`. | Used for horizontal rows. |
| 7 | Imports generic `LayoutManager`. | Constructor accepts layout strategy. |
| 8 | Imports `ArrayList`. | Stores ordered children. |
| 9 | Imports `List`. | Declares children by interface. |
| 11 | Declares final Composite implementation. | Required Composite container role. |
| 12 | Stores Swing panel that will hold built children. | Concrete UI container. |
| 13 | Creates empty ordered list of child `UIComponent`s. Children can be leaves or composites. | Core recursive Composite tree. |
| 15 | Constructor accepts desired Swing layout. | Makes container reusable. |
| 16 | Creates panel with that layout. | Initializes concrete container. |
| 17 | Ends constructor. | Structural. |
| 19 | Declares row factory with configurable gap. | Convenient UI-tree construction. |
| 20 | Returns Composite whose `FlowLayout` centers children and uses same horizontal/vertical gap. | Builds rows of controls. |
| 21 | Ends row factory. | Structural. |
| 23 | Declares column factory with padding amount. | Convenient UI-tree construction. |
| 24 | Creates Composite initially with `BorderLayout`. | Obtains object/panel before replacing layout. |
| 25 | Replaces panel layout with vertical `BoxLayout` tied to same panel. | Stacks child UI components vertically. |
| 26 | Adds equal empty padding on all four sides. | Visually separates column contents. |
| 27 | Returns configured Composite. | Used for header and center sections. |
| 28 | Ends column factory. | Structural. |
| 30 | Declares fluent child-add method. | Builds nested tree one child at a time. |
| 31 | Rejects missing child. | Keeps tree valid. |
| 32 | Throws clear construction error. | Defensive Composite implementation. |
| 33 | Ends validation. | Structural. |
| 34 | Appends child, preserving insertion/display order. | Composite owns its child list. |
| 35 | Returns `this`, allowing chained `.add(...).add(...)`. | Makes declarative UI tree concise. |
| 36 | Ends `add`. | Structural. |
| 38 | Marks common Composite operation implementation. | Same operation as leaf. |
| 39 | Starts build. | Turns pattern tree into Swing subtree. |
| 40 | Clears panel's current Swing children. | Allows safe rebuilding without duplicates. |
| 41 | Iterates every abstract child in order. | Treats leaves/composites uniformly. |
| 42 | Calls child's own `build()` and adds returned Swing component. Recursion occurs if child is another Composite. | Essential Composite behavior. |
| 43 | Ends loop. | Structural. |
| 44 | Returns assembled Swing panel. | Gives caller usable UI component. |
| 45 | Ends method. | Structural. |
| 46 | Ends class. | Structural. |

### Composite tree used by `MainView`

```text
root JPanel
├── header UIComposite (column)
│   ├── title UILeaf
│   ├── editionLabel UILeaf
│   └── descriptionLabel UILeaf
├── center UIComposite (column)
│   ├── stateOptions UILeaf
│   │   └── state-created UIComposite
│   └── resultLabel UILeaf
└── actions UIComposite (row)
    ├── generateButton UILeaf
    └── switchButton UILeaf
```

---

## 13. `UIEvent.java` — Observer event messages

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares enum: closed set of UI event values. | Observer notifications need clear event messages. |
| 2 | `GENERATE` means user requested number draw. | Core button action. |
| 3 | `UPGRADE` means switch from Community to Professional. | Smooth State upgrade. |
| 4 | `USE_COMMUNITY` means switch back to Community. | Demonstrates reversible State transition. |
| 5 | Ends enum. | Structural. |

Using enum avoids fragile string event names.

---

## 14. `UIEventObserver.java` — Observer contract

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares observer interface. | Required Observer abstraction. |
| 2 | Requires observer callback receiving published `UIEvent`. | Publisher can notify event handler without knowing controller internals. |
| 3 | Ends interface. | Structural. |

`MainController` is concrete observer.

---

## 15. `UIEventPublisher.java` — Observer subject/publisher

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports mutable `ArrayList`. | Stores registered observers and makes notification snapshot. |
| 2 | Imports `List` interface. | Observer collection type. |
| 4 | Declares final event publisher. | Observer subject role. |
| 5 | Creates empty private observer list. | Publisher tracks subscribers. |
| 7 | Declares subscription method. | Observer registration. |
| 8 | Accepts only non-null observer not already registered. | Prevents invalid entries and duplicate notifications. |
| 9 | Adds observer. | Completes subscription. |
| 10 | Ends condition. | Structural. |
| 11 | Ends method. | Structural. |
| 13 | Declares unsubscription method. | Observer removal. |
| 14 | Removes matching observer if present; does nothing if absent. | Stops future event delivery. |
| 15 | Ends method. | Structural. |
| 17 | Declares event-publication method. | View uses it for button events. |
| 18 | Copies observer list, then iterates copy. Snapshot avoids modification problems if observer list changes during callback. | Robust Observer notification. |
| 19 | Calls each observer's callback with same event. | Core Observer behavior. |
| 20 | Ends loop. | Structural. |
| 21 | Ends method. | Structural. |
| 22 | Ends class. | Structural. |

Publisher knows only `UIEventObserver`, not `MainController`. That loose coupling is main benefit of Observer.

---

## 16. `MainView.java` — Swing display, Composite client, Observer publisher

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports border factory. | Styles root Swing panel. |
| 2 | Imports buttons. | Generate and edition-switch actions. |
| 3 | Imports base Swing component. | Root return type. |
| 4 | Imports labels. | Title, state info, and result display. |
| 5 | Imports dialog helper. | Shows validation errors. |
| 6 | Imports panel container. | Root and replaceable state-options areas. |
| 7 | Imports alignment constants. | Centers label text. |
| 8 | Imports `BorderLayout`. | Arranges main screen and state panel. |
| 9 | Imports `Dimension`. | Sets preferred window content size. |
| 10 | Imports `Font`. | Styles headings/result. |
| 11 | Imports `List`. | Accepts generated results. |
| 12 | Imports stream collector utility. | Joins values into display text. |
| 14 | Declares final view class. | Swing presentation layer. |
| 15 | Creates publisher owned by view. | UI events handled with Observer pattern. |
| 16 | Creates root panel using `BorderLayout` with 10-pixel gaps. | Swing main layout. |
| 17 | Creates panel where current State's controls are inserted. | State-specific UI can change smoothly. |
| 18 | Creates centered, initially empty edition label. | Displays Community/Professional name. |
| 19 | Creates centered, initially empty description label. | Displays State explanation. |
| 20 | Creates centered result label with initial instruction. | Displays Lottery output. |
| 21 | Creates Generate button. | User triggers random selection. |
| 22 | Creates switch button; label is chosen by current State later. | User upgrades/switches state. |
| 23 | Initially says switch button should publish `UPGRADE`. | Initial state is Community. |
| 25 | Starts view constructor. | Builds screen. |
| 26 | Adds 16-pixel padding around root. | UI presentation. |
| 27 | Requests root size `680 × 300`; frame later packs around it. | Makes UI comfortably sized. |
| 29 | Creates centered title label. | Identifies app. |
| 30 | Derives bold 24-point title font. | Visual hierarchy. |
| 31 | Makes edition name bold 16-point. | Highlights active State. |
| 32 | Makes generated numbers bold 28-point. | Highlights core result. |
| 34–37 | Creates vertical header Composite and adds three `UILeaf`s: title, edition label, description. | Composite generates UI as prompt requests. |
| 39–41 | Creates vertical center Composite with state-options panel and result label leaves. | Composite plus replaceable State UI. |
| 43–45 | Creates horizontal actions Composite with Generate and switch button leaves. | Composite constructs action UI. |
| 47 | Builds header Composite and places panel in root's north region. | Adds first Composite subtree. |
| 48 | Builds center Composite and places panel in center. | Adds second Composite subtree. |
| 49 | Builds actions Composite and places panel in south. | Adds third Composite subtree. |
| 51 | Registers Swing listener. On click, ignored event-details parameter is unused; lambda publishes `GENERATE` to observers. | Converts Swing event into custom Observer event. |
| 52 | Registers switch listener. It publishes current `switchEvent`, which depends on active State. | Observer handles edition transition. |
| 53 | Ends constructor. | Structural. |
| 55 | Declares root getter. | `Application` needs view content for frame. |
| 56 | Returns root panel as general `JComponent`. | Connects view to window. |
| 57 | Ends getter. | Structural. |
| 59 | Declares observer-registration facade. | Controller subscribes without accessing publisher directly. |
| 60 | Delegates subscription to `UIEventPublisher`. | Observer wiring. |
| 61 | Ends method. | Structural. |
| 63 | Declares method that renders any `LottoState`. | State-driven UI update. |
| 64 | Gets state name and displays it with `" edition"`. | Same view supports both states. |
| 65 | Gets state-specific description. | Behavior/UI delegated through State interface. |
| 66 | Removes controls belonging to previous state. | Prepares smooth transition. |
| 67 | Asks new state to create options UI and places it in center. | State supplies edition-specific Composite UI. |
| 68 | Checks whether current object is Community State. | Determines direction/text of switch action. |
| 69 | Chooses `UPGRADE` in Community, otherwise `USE_COMMUNITY`. | Links view action to State transition. |
| 70 | Chooses matching button text. | Makes transition understandable to user. |
| 71 | Resets old draw display after state change. | Avoids showing result from previous edition. |
| 72 | Re-runs Swing layout calculations after controls change. | Required for dynamic State UI update. |
| 73 | Requests visual repaint. | Shows new State immediately. |
| 74 | Ends method. | Structural. |
| 76 | Declares result-display method. | Controller sends generated values here. |
| 77–79 | Streams numbers, converts each integer to text, joins them with three spaces, and puts text in result label. | Displays Lottery draw. |
| 80 | Ends method. | Structural. |
| 82 | Declares error-display method. | Handles invalid Professional input. |
| 83–84 | Opens modal error dialog attached to root, with supplied message, fixed title, and error icon/type. | Gives UI feedback when generation rules fail. |
| 85 | Ends method. | Structural. |
| 86 | Ends class. | Structural. |

The view publishes events but does not decide how to generate numbers or change state. Those decisions belong to controller/application/state.

---

## 17. `MainController.java` — concrete Observer and flow coordinator

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares final controller implementing `UIEventObserver`. | Concrete Observer handling UI events. |
| 2 | Stores application reference. | Needs current State and transitions. |
| 3 | Stores view reference. | Needs to display results/errors. |
| 5 | Constructor receives dependencies. | Wires application flow. |
| 6 | Saves application. | Controller can access State context. |
| 7 | Saves view. | Controller can update UI. |
| 8 | Registers itself with view's publisher. | Essential Observer subscription. |
| 9 | Ends constructor. | Structural. |
| 11 | Marks implementation of observer callback. | Observer contract. |
| 12 | Receives one published UI event. | Starts event-handling flow. |
| 13 | Starts `try` so user-input exceptions can become dialogs. | Graceful Swing handling. |
| 14 | Branches on enum event. | Selects requested action. |
| 15 | Starts Generate case. | Lottery action. |
| 16 | Gets current State, delegates generation to it, then tells view to display returned list. | State + Observer cooperate. |
| 17 | Stops switch from falling into next case. | Java switch control flow. |
| 18 | Starts Upgrade case. | State transition action. |
| 19 | Creates Professional State and installs it in Singleton application. | Smooth Community → Professional upgrade. |
| 20 | Stops switch fall-through. | Java control flow. |
| 21 | Starts Use Community case. | Reverse State transition. |
| 22 | Creates Community State and installs it. | Switches behavior/UI back. |
| 23 | Stops switch fall-through. | Java control flow. |
| 24 | Handles any future enum not explicitly supported. | Defensive event handling. |
| 25 | Throws error including unknown event. | Prevents silent ignored UI actions. |
| 26 | Ends switch. | Structural. |
| 27 | Catches validation errors from state/generator. | Invalid ranges do not crash UI. |
| 28 | Sends error text to dialog. | User-facing feedback. |
| 29 | Ends catch. | Structural. |
| 30 | Ends callback. | Structural. |
| 31 | Ends class. | Structural. |

Observer chain:

```text
JButton ActionListener
    → UIEventPublisher.notifyObservers(...)
        → MainController.onEvent(...)
            → Application / current LottoState / MainView
```

---

## 18. `LottoNumbersTest.java` — executable verification

This is a small test harness without JUnit. `main` runs checks and throws `AssertionError` when a requirement fails.

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Imports `LocalDate` for fixed Professional test date. | Tests date-based behavior. |
| 2 | Imports time zone for date conversion. | Builds value compatible with spinner. |
| 3 | Imports legacy `Date`. | `ProfessionalState.setOptions` expects it. |
| 4 | Imports `HashSet`. | Tests uniqueness by removing duplicates. |
| 5 | Imports `List`. | Stores draws. |
| 6 | Imports seeded `Random`. | Makes generator test predictable. |
| 7 | Imports thread-safe integer wrapper. | Counts Observer callbacks inside lambda. |
| 9 | Declares final test utility class. | Verification only. |
| 10–11 | Private constructor prevents creating test objects. | Tests run through static methods. |
| 13 | Declares test-program entry point. | Allows `java LottoNumbersTest`. |
| 14 | Runs Singleton test. | Verifies Singleton prompt requirement. |
| 15 | Runs core generator test. | Verifies Lottery rules. |
| 16 | Runs Community State test. | Verifies Community prompt requirement. |
| 17 | Runs Professional State test. | Verifies Professional prompt requirement. |
| 18 | Runs Observer test. | Verifies Observer prompt requirement. |
| 19 | Prints success only after every earlier test returns. | Simple pass report. |
| 20 | Ends test entry point. | Structural. |
| 22 | Starts Singleton test method. | Tests one Application object. |
| 23–24 | Calls getter twice and uses `==` identity comparison; failure message states invariant. | Confirms exact same Singleton instance. |
| 25 | Ends test. | Structural. |
| 27 | Starts general generator test. | Tests shared number logic. |
| 28 | Generates six values from exactly six candidates `5..10` with fixed seed. | Controlled generation test. |
| 29 | Checks requested count is six. | Lottery result size. |
| 30 | Converts result to set and checks size remains six. | Confirms uniqueness. |
| 31 | Because output is sorted, checks first is at least 5 and last at most 10. | Confirms inclusive range. |
| 33 | Expects failure when range has only five values but count is six. | Validates uniqueness feasibility. |
| 34 | Expects failure when minimum exceeds maximum. | Validates range order. |
| 35 | Ends generator test. | Structural. |
| 37 | Starts Community test. | Tests fixed edition. |
| 38 | Creates Community state and generates one draw. | Exercises State behavior directly. |
| 39 | Checks exactly six values. | Direct Community requirement. |
| 40–41 | Checks sorted result endpoints stay inside `1..42`. | Direct Community requirement. |
| 42 | Ends Community test. | Structural. |
| 44 | Starts Professional test. | Tests configurable edition. |
| 45 | Creates Professional state with its Swing inputs. | Concrete State under test. |
| 46–47 | Creates legacy `Date` representing local start of 15 June 2030. | Fixed day/month/year input. |
| 48 | Sets range `20..50` and fixed date. | Professional configuration. |
| 49 | Generates first draw. | First deterministic result. |
| 50 | Generates again without changing inputs. | Repetition check. |
| 51 | Checks both lists equal. | Confirms same date/range reproduces same seeded draw. |
| 52–53 | Checks sorted endpoints stay inside selected `20..50` range. | Confirms Professional range is used. |
| 55 | Changes range to only `1..5`. | Creates invalid range for six unique numbers. |
| 56 | Expects state generation to fail. Method reference means “call `state.generateNumbers()` later.” | Confirms validation reaches Professional UI flow. |
| 57 | Ends Professional test. | Structural. |
| 59 | Starts Observer test. | Tests UI event pattern separately. |
| 60 | Creates publisher. | Observer subject under test. |
| 61 | Creates mutable callback counter initialized to zero. `AtomicInteger` can be changed inside lambda. | Measures notifications. |
| 62 | Creates observer lambda that ignores event and increments counter. | Concrete test observer. |
| 63 | Subscribes observer. | Observer registration. |
| 64 | Publishes first Generate event. Counter becomes one. | Observer notification. |
| 65 | Unsubscribes observer. | Observer removal. |
| 66 | Publishes again. Counter should remain one. | Ensures removal works. |
| 67 | Checks callback happened exactly once. | Confirms observer receives events only while registered. |
| 68 | Ends Observer test. | Structural. |
| 70 | Declares helper expecting an action to throw `IllegalArgumentException`. | Reuses validation-test logic. |
| 71 | Starts protected execution. | Needed to catch expected exception. |
| 72 | Runs supplied lambda/method reference. | Executes invalid operation. |
| 73 | Throws `AssertionError` if action unexpectedly succeeds. | Makes missing validation fail test. |
| 74 | Catches expected exception type. | Passing path for invalid input. |
| 75 | Comment says exception is intentional. Comment has no runtime effect. | Documents test intention. |
| 76 | Ends catch. | Structural. |
| 77 | Ends helper. | Structural. |
| 79 | Declares generic assertion helper with condition and failure message. | Supports all requirement checks. |
| 80 | Tests whether condition is false. | Detects failed requirement. |
| 81 | Throws `AssertionError` with useful message. | Stops suite and reports failure. |
| 82 | Ends condition. | Structural. |
| 83 | Ends helper. | Structural. |
| 84 | Ends class. | Structural. |

### What tests do not cover

- Composite classes are used by both states and view but have no dedicated assertions.
- Controller event-to-state transitions are not directly tested.
- Swing window rendering is not visually tested.
- Professional date changes are not explicitly compared against different dates.

These are coverage observations, not evidence that code is wrong.

---

## 19. `README.md` — project usage documentation

| Line(s) | What text does | Prompt link |
|---|---|---|
| 1 | Names project. | Assignment identity. |
| 3 | States this Swing app demonstrates four patterns. | Summarizes architecture. |
| 5 | Names Composite participants and nested UI purpose. | Composite requirement. |
| 6 | Names Observer publisher/controller relationship. | Observer requirement. |
| 7 | Names State implementations and their responsibilities. | State requirement. |
| 8 | Names Singleton access method. | Singleton requirement. |
| 10–12 | Summarizes Community rules and Professional deterministic date/range behavior. | Functional requirements. |
| 14 | Starts run section. | User instructions. |
| 16 | States Java requirement: JDK 8+. | Build prerequisite. |
| 18 | Starts PowerShell code block. | Formatting only. |
| 19 | Compiles all Java sources in folder. | Prepares app. |
| 20 | Runs `Main`. | Starts flow explained above. |
| 21 | Ends code block. | Formatting only. |
| 23 | Starts test section. | Verification instructions. |
| 25 | Starts PowerShell code block. | Formatting only. |
| 26 | Recompiles all sources. | Prepares tests. |
| 27 | Runs tests in headless AWT mode so no display is required. | Checks requirements without opening window. |
| 28 | Ends code block. | Formatting only. |

---

## 19A. `.class` files — compiled copies, not additional source

Files such as `Application.class`, `MainView.class`, and `CommunityState.class` are Java bytecode produced by `javac` from corresponding `.java` files. They contain machine-oriented JVM instructions rather than separate handwritten application logic. Their behavior is already explained through source lines above.

Two names deserve special mention:

- `MainController$1.class` is compiler-generated support for enum `switch` logic in `MainController`.
- `Application$Holder.class` appears to be an older compiled artifact from a previous `Application` implementation, because current `Application.java` has no nested `Holder` class. Running `javac *.java` creates or replaces needed classes but does not automatically delete obsolete `.class` files.

These binary files are build output, not extra classes that should be studied line by line. Source of truth is `.java`.

---

## 20. How four patterns cooperate

### Composite: UI structure

- `UIComponent` defines one operation.
- `UILeaf` wraps one Swing widget.
- `UIComposite` owns child `UIComponent`s and builds a Swing panel.
- `MainView`, `CommunityState`, and `ProfessionalState` build nested structures without treating a single label differently from a group.

Prompt relationship: “code responsible for generating UI will implement Composite.”

### Observer: UI events

- `UIEventPublisher` is subject/publisher.
- `UIEventObserver` is observer interface.
- `MainController` is concrete observer.
- `MainView` owns publisher and emits enum events from Swing listeners.

Prompt relationship: “code for handling UI events will implement Observer.”

### State: Community and Professional behavior

- `LottoState` is state interface.
- `CommunityState` supplies fixed UI/rules.
- `ProfessionalState` supplies configurable UI/rules.
- `Application` is context holding current state.
- `MainController` requests transitions.

Prompt relationship: “allow a smooth upgrade from community to professional.”

### Singleton: whole app identity

- `Application` has private constructor.
- Static field creates one instance.
- `getInstance()` returns it.
- `Main` starts that instance.

Prompt relationship: “entire Lotto Numbers application should be represented using an object instantiated from Application; Application should implement Singleton.”

---

## 21. Full end-to-end example

Assume app is in Community:

1. `resultLabel` says **Press Generate**.
2. User clicks Generate.
3. `MainView` publishes `GENERATE`.
4. `UIEventPublisher` calls `MainController.onEvent(GENERATE)`.
5. Controller gets `Application`'s current `CommunityState`.
6. `CommunityState.generateNumbers()` calls generator with `(1, 42, 6, new Random())`.
7. Generator creates values 1 through 42, shuffles them, copies first six, sorts them.
8. Controller gives result to `MainView.showNumbers`.
9. View joins values and updates label.

Now user clicks **Upgrade to Professional**:

1. View publishes `UPGRADE`.
2. Controller calls `application.setState(new ProfessionalState())`.
3. Application stores Professional state.
4. Application calls `view.showState(state)`.
5. View replaces fixed Community labels with minimum, maximum, and date spinners.
6. Next Generate event follows same Observer route, but current State now reads Professional inputs.

No special “professional controller” is needed. Changing state object changes both options UI and number-generation behavior.

## 22. Small but important Java ideas used

- `final class`: prevents subclassing.
- `private final field`: reference is assigned once and hidden from other classes.
- `static`: belongs to class rather than an instance.
- `interface`: defines required behavior without implementation.
- `implements`: class promises to provide interface methods.
- `@Override`: compiler-verifiable marker for an implemented/overridden method.
- Lambda such as `ignored -> ...`: compact implementation of one-method interface.
- Method reference such as `String::valueOf`: compact reference to existing method.
- `instanceof`: checks concrete runtime type.
- Ternary `condition ? first : second`: selects one of two values.
- Enum: fixed, type-safe set of named constants.
- Exception: signals invalid operation up call stack.
- Swing Event Dispatch Thread: dedicated thread where Swing UI should be created and updated.

## 23. One-sentence responsibility map

- `Main`: enter program safely on Swing thread.
- `Application`: own one running app and current edition.
- `MainView`: construct/display Swing UI and publish user events.
- `MainController`: observe events and coordinate actions.
- `LottoState`: define what every edition must provide.
- `CommunityState`: fixed `1..42` behavior.
- `ProfessionalState`: user range/date behavior.
- `LottoNumberGenerator`: validate and create unique sorted draw.
- `UIComponent`: unify UI tree nodes.
- `UILeaf`: represent one Swing widget.
- `UIComposite`: represent group of UI nodes.
- `UIEvent`: name possible user actions.
- `UIEventObserver`: define event callback.
- `UIEventPublisher`: manage and notify observers.
- `LottoNumbersTest`: verify key prompt requirements.
