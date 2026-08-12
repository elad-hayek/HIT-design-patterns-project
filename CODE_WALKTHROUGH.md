# Validation Library: Application-Flow and Line-by-Line Guide

## 1. What the assignment asks for

The specification (`sources/design_patterns_validation_library.md`) has these
core requirements:

1. Build a Java library named `validation`, packed into `validation.jar`.
2. Implement the **Combinator** design pattern.
3. Everything lives in package `il.ac.hit.validation`.
4. Provide `User` with a primary constructor `(username, email, password, age)`.
5. Provide `UserFactory`, implementing **Factory Method**, that turns a type
   string (`"basic"` / `"premium"` / `"platinum"`) into a `BasicUser`,
   `PremiumUser`, or `PlatinumUser`.
6. Provide `UserUtils.sort`, implementing **Template Method** via a supplied
   `Comparator<User>`.
7. Provide `ValidationResult` (with `Valid` and `Invalid` implementations).
8. Provide `UserValidation extends Function<User, ValidationResult>` with
   `and`/`or`/`xor` instance combinators, `all`/`none` static combinators,
   and eight static validation-rule factories.

This project maps those requirements as follows:

- Combinator: `UserValidation` (`and`, `or`, `xor`, `all`, `none`, and the
  eight rule factories).
- Factory Method: `UserFactory` (produces `BasicUser`, `PremiumUser`, or
  `PlatinumUser`).
- Template Method: `UserUtils.sort` (fixed algorithm, caller-supplied
  `Comparator<User>`).
- Domain object: `User`, and its three tier subclasses `BasicUser`,
  `PremiumUser`, `PlatinumUser`.
- Result type: `ValidationResult`, `Valid`, `Invalid`.

## 2. Application flow

### Building a user

1. Calling code either constructs `new User(username, email, password, age)`
   directly, or calls `UserFactory.createUser(type, username, email,
   password, age)`.
2. `UserFactory` switches on the lower-cased `type` string and delegates to
   the matching subclass constructor (`BasicUser`, `PremiumUser`, or
   `PlatinumUser`), each of which simply forwards its four arguments to
   `User`'s constructor via `super(...)`.
3. `User`'s constructor calls its own setters (`setUsername`, `setEmail`,
   `setPassword`, `setAge`) instead of assigning fields directly, so
   validation always runs, no matter whether the object is built through the
   constructor or (later) through a setter call.

### Building and combining validation rules

1. Calling code obtains individual rules from `UserValidation`'s static
   factory methods, e.g. `UserValidation.emailEndsWithIL()`. Each factory
   returns a lambda; because `UserValidation` extends `Function<User,
   ValidationResult>`, that lambda *is* a `UserValidation`.
2. Two rules are combined with `and`, `or`, or `xor`. Each of these default
   methods returns a **new** `UserValidation` lambda that, when later
   applied to a `User`, evaluates both sides and combines their outcomes.
   Because the combination is itself a `UserValidation`, it can be combined
   again — this open-ended composability is the Combinator pattern.
3. `all(UserValidation...)` and `none(UserValidation...)` extend the same
   idea to whole collections of rules in one call.

### Applying a validation

1. Calling code calls `.apply(user)` on any `UserValidation` (inherited from
   `Function`).
2. If the validation is a primitive rule, the rule's own lambda body runs
   directly against the `User`'s getters and returns `new Valid()` or
   `new Invalid(reason)`.
3. If the validation is a combination (built through `and`/`or`/`xor`/`all`/
   `none`), applying it recursively applies its inner validations and folds
   their `ValidationResult`s into one final `ValidationResult`.
4. Calling code reads `result.isValid()` and, on failure, `result.getReason()`
   to learn why.

### Sorting users

1. Calling code calls `UserUtils.sort(users, comparator)` with any
   `Comparator<User>` (e.g. `Comparator.comparingInt(User::getAge)`).
2. `UserUtils.sort` validates its arguments and delegates the actual
   reordering to `Arrays.sort`, which uses the supplied comparator for every
   element comparison — the comparator is the "variable step" of the
   Template Method.

## 3. Reading conventions

- Line numbers match current source files exactly.
- Blank lines only separate logical sections and have no runtime effect.
- A line containing only `{` starts a scope; a line containing only `}` ends
  one.
- Some Java statements span several physical lines. Those lines are
  explained together where splitting them would hide their meaning.
- A multi-line Javadoc or block comment that explains one thing is grouped
  into a single row, using a line range, rather than one row per comment
  line — the words inside the comment are prose, not separate statements.
- "Prompt link" explains why the line exists relative to the assignment
  specification or the required style guide.

---

## 4. `User.java` — the validated domain object

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package for the whole library. |
| 3–12 | Class-level Javadoc: explains `User` holds username/email/password/age, and that `BasicUser`/`PremiumUser`/`PlatinumUser` only distinguish membership tier. | Style guide requires Javadoc on every public class. |
| 13 | Declares public class `User`. | Base type required by the spec. |
| 14 | Blank line separating fields from the class opening brace. | Structural. |
| 15 | Comment explaining the `username` field's purpose. | Common-rejects "COMMENTS" rule: a comment roughly every 8–10 lines. |
| 16 | Declares private field `username`. | Spec instance variable; style guide requires private fields. |
| 17 | Blank line. | Structural. |
| 18 | Comment explaining the `email` field. | Style guide comment density. |
| 19 | Declares private field `email`. | Spec instance variable. |
| 20 | Blank line. | Structural. |
| 21 | Comment explaining the `password` field. | Style guide comment density. |
| 22 | Declares private field `password`. | Spec instance variable. |
| 23 | Blank line. | Structural. |
| 24 | Comment explaining the `age` field. | Style guide comment density. |
| 25 | Declares private field `age`. | Spec instance variable. |
| 26 | Blank line before the constructor. | Structural. |
| 27–38 | Javadoc for the primary constructor: describes that it is the primary constructor and that it delegates to setters. | Style guide: "every class should include a primary constructor... other constructors delegate to it." |
| 39 | Declares the constructor `User(String username, String email, String password, int age)`. | Spec: "primary constructor that accepts values... in the exact order listed." |
| 40 | Calls `setUsername(username)`. | Style guide: "constructors should invoke the setters rather than assigning the values directly." |
| 41 | Calls `setEmail(email)`. | Same style-guide rule. |
| 42 | Calls `setPassword(password)`. | Same style-guide rule. |
| 43 | Calls `setAge(age)`. | Same style-guide rule. |
| 44 | Ends the constructor. | Structural. |
| 45 | Blank line. | Structural. |
| 46–50 | Javadoc for `getUsername()`. | Style guide requires Javadoc on public methods. |
| 51 | Declares `getUsername()`. | Encapsulated read access to the private field. |
| 52 | Returns `username`. | Getter body. |
| 53 | Ends the method. | Structural. |
| 54 | Blank line. | Structural. |
| 55–60 | Javadoc for `setUsername(String)`, documenting the `IllegalArgumentException`. | Style guide requires Javadoc, including `@throws`. |
| 61 | Declares `setUsername` as `public final`. `final` stops subclasses from overriding it. | Avoids the `this-escape` risk of calling an overridable method from the constructor (line 40), and matches the style guide's "validation lives in the setter" rule. |
| 62 | Checks whether `username` is `null` or empty. | Style guide: "every method should start with validating the values." |
| 63 | Throws `IllegalArgumentException` when the check fails. | Same style-guide rule. |
| 64 | Ends the `if` block. | Structural. |
| 65 | Assigns the validated value to the field. | Completes the setter. |
| 66 | Ends the method. | Structural. |
| 67 | Blank line. | Structural. |
| 68–72 | Javadoc for `getEmail()`. | Style guide Javadoc requirement. |
| 73 | Declares `getEmail()`. | Encapsulated read access. |
| 74 | Returns `email`. | Getter body. |
| 75 | Ends the method. | Structural. |
| 76 | Blank line. | Structural. |
| 77–82 | Javadoc for `setEmail(String)`. | Style guide Javadoc requirement. |
| 83 | Declares `setEmail` as `public final`. | Same `this-escape` / style-guide reasoning as line 61. |
| 84 | Checks whether `email` is `null` or empty. | Argument-validation rule. |
| 85 | Throws `IllegalArgumentException`. | Same rule. |
| 86 | Ends the `if` block. | Structural. |
| 87 | Assigns the validated value to the field. | Completes the setter. |
| 88 | Ends the method. | Structural. |
| 89 | Blank line. | Structural. |
| 90–94 | Javadoc for `getPassword()`. | Style guide Javadoc requirement. |
| 95 | Declares `getPassword()`. | Encapsulated read access. |
| 96 | Returns `password`. | Getter body. |
| 97 | Ends the method. | Structural. |
| 98 | Blank line. | Structural. |
| 99–104 | Javadoc for `setPassword(String)`. | Style guide Javadoc requirement. |
| 105 | Declares `setPassword` as `public final`. | Same reasoning as line 61. |
| 106 | Checks whether `password` is `null` or empty. | Argument-validation rule. |
| 107 | Throws `IllegalArgumentException`. | Same rule. |
| 108 | Ends the `if` block. | Structural. |
| 109 | Assigns the validated value to the field. | Completes the setter. |
| 110 | Ends the method. | Structural. |
| 111 | Blank line. | Structural. |
| 112–116 | Javadoc for `getAge()`. | Style guide Javadoc requirement. |
| 117 | Declares `getAge()`. | Encapsulated read access. |
| 118 | Returns `age`. | Getter body. |
| 119 | Ends the method. | Structural. |
| 120 | Blank line. | Structural. |
| 121–126 | Javadoc for `setAge(int)`. | Style guide Javadoc requirement. |
| 127 | Declares `setAge` as `public final`. | Same reasoning as line 61. |
| 128 | Checks whether `age` is negative. | Argument-validation rule. |
| 129 | Throws `IllegalArgumentException`. | Same rule. |
| 130 | Ends the `if` block. | Structural. |
| 131 | Assigns the validated value to the field. | Completes the setter. |
| 132 | Ends the method. | Structural. |
| 133 | Blank line. | Structural. |
| 134–139 | Javadoc for `toString()`. | Style guide Javadoc requirement. |
| 140 | `@Override` annotation. | Style guide: "whenever overriding a method, the `@Override` annotation must be added." |
| 141 | Declares `toString()`. | Style guide: "make sure it includes the definition of the `toString` method." |
| 142–143 | Builds and returns a string with `username`, `email`, and `age`. The `password` is intentionally left out of the output. | Human-readable representation for debugging/logging, without leaking the secret credential. |
| 144 | Ends the method. | Structural. |
| 145 | Ends the class. | Structural. |

### Why setters are `final`

`javac -Xlint:all` reports a `this-escape` warning when a constructor calls
an overridable instance method, because a subclass could override that
method and run it before the subclass's own fields are initialized. `User`
is extended by `BasicUser`, `PremiumUser`, and `PlatinumUser` (see below),
so its constructor (line 39) calling `setUsername`/`setEmail`/
`setPassword`/`setAge` triggers that warning unless those methods cannot be
overridden. Marking them `final` removes the warning and keeps the
single-validation-path guarantee the style guide asks for.

---

## 5. `BasicUser.java`, `PremiumUser.java`, `PlatinumUser.java` — Factory Method products

These three files are structurally identical; only the class name and the
one sentence describing the membership tier differ. They are documented
together here instead of three times over.

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3–8 | Class-level Javadoc: names the membership tier (`"basic"` / `"premium"` / `"platinum"`) and states that `UserFactory` creates instances of this class when that type string is requested. | Style guide Javadoc requirement; documents the Factory Method contract from the caller's point of view. |
| 9 | Declares the class as `public class <Name>User extends User`. | Spec: "These three classes... must extend `User`." |
| 10 | Blank line. | Structural. |
| 11–18 | Javadoc for the constructor, describing the four inherited properties. | Style guide Javadoc requirement. |
| 19 | Declares the constructor `<Name>User(String username, String email, String password, int age)`, matching `User`'s primary constructor parameter order. | Spec: primary constructor parameter order must match `User`. |
| 20 | Calls `super(username, email, password, age)`. | Style guide: "other constructors in that class should delegate the initialization to the primary constructor" — here, the subtype has no state of its own, so it delegates entirely to `User`'s primary constructor. |
| 21 | Ends the constructor. | Structural. |
| 22 | Ends the class. | Structural. |

None of the three subclasses declares extra fields or overrides any
behavior — the entire purpose of having three distinct classes is so that
`UserFactory` (below) and any calling code that inspects the runtime type
(as `ExtendedValidationDemo.java` in the personal `test/` folder does) can
tell the tiers apart.

---

## 6. `UserFactory.java` — Factory Method

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3–11 | Class-level Javadoc: states this class implements Factory Method and that callers never need to know the concrete subclasses directly. | Spec: "Implements the Factory Method design pattern." |
| 12 | Declares `public final class UserFactory`. `final` because it is a pure static utility, never meant to be subclassed. | Common-rejects style expectations for a stateless factory. |
| 13 | Blank line. | Structural. |
| 14 | Comment explaining the `TYPE_BASIC` constant. | Style guide comment density. |
| 15 | Declares `private static final String TYPE_BASIC = "basic"`. | Spec: the factory method accepts the string `"basic"`. Naming it as a constant avoids repeating the literal. |
| 16 | Blank line. | Structural. |
| 17 | Comment explaining the `TYPE_PREMIUM` constant. | Style guide comment density. |
| 18 | Declares `private static final String TYPE_PREMIUM = "premium"`. | Spec: the factory method accepts the string `"premium"`. |
| 19 | Blank line. | Structural. |
| 20 | Comment explaining the `TYPE_PLATINUM` constant. | Style guide comment density. |
| 21 | Declares `private static final String TYPE_PLATINUM = "platinum"`. | Spec: the factory method accepts the string `"platinum"`. |
| 22 | Blank line. | Structural. |
| 23–26 | Javadoc for the private constructor. | Style guide Javadoc requirement. |
| 27 | Declares a private, empty constructor. | Common-rejects style: a static-only utility class should not be instantiable. |
| 28 | Ends the constructor. | Structural. |
| 29 | Blank line. | Structural. |
| 30–41 | Javadoc for `createUser`, documenting every parameter, the return value, and the `IllegalArgumentException`. | Style guide Javadoc requirement, including `@param`/`@return`/`@throws`. |
| 42–43 | Declares `public static User createUser(String type, String username, String email, String password, int age)`, split across two lines because the parameter list is long. | Spec: "the factory method accepts one of the following strings"; returns a `User` so callers stay decoupled from the concrete subclass. |
| 44 | Checks whether `type` is `null`. | Argument-validation rule (style guide). |
| 45 | Throws `IllegalArgumentException` when `type` is missing. | Same rule. |
| 46 | Ends the `if` block. | Structural. |
| 47 | Blank line. | Structural. |
| 48 | Comment explaining the dispatch that follows. | Style guide comment density. |
| 49 | Starts a `switch` expression on `type.toLowerCase()`, so `"Basic"`, `"BASIC"`, and `"basic"` are all accepted. | Spec accepts the three type strings; lower-casing makes matching resilient to input case. |
| 50 | `case TYPE_BASIC ->` constructs and returns `new BasicUser(...)`. | Spec: `"basic"` → `BasicUser`. |
| 51 | `case TYPE_PREMIUM ->` constructs and returns `new PremiumUser(...)`. | Spec: `"premium"` → `PremiumUser`. |
| 52 | `case TYPE_PLATINUM ->` constructs and returns `new PlatinumUser(...)`. | Spec: `"platinum"` → `PlatinumUser`. |
| 53 | `default ->` throws `IllegalArgumentException` naming the unrecognized type. | Defensive handling for any other string. |
| 54 | Ends the `switch` expression with `;`. | Structural — a switch *expression* (not a classic switch statement) is used, so its value is directly returned. |
| 55 | Ends the method. | Structural. |
| 56 | Ends the class. | Structural. |

`TYPE_BASIC`/`TYPE_PREMIUM`/`TYPE_PLATINUM` are `static final` (compile-time
constant) `String`s, which is what makes it legal to use them as `switch`
case labels on line 50–52.

---

## 7. `UserUtils.java` — Template Method

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3 | Imports `java.util.Arrays`. | Needed for `Arrays.sort`. |
| 4 | Imports `java.util.Comparator`. | Spec: sort takes "the comparison functionality for comparing two users." |
| 5 | Blank line. | Structural. |
| 6–14 | Class-level Javadoc: explains this class implements Template Method — the sorting algorithm is fixed, the comparison step is supplied by the caller. | Spec: "Implements the Template Method design pattern... using the `Comparator<T>` interface will be considered an implementation of the Template Method." |
| 15 | Declares `public final class UserUtils`. | Stateless utility class. |
| 16 | Blank line. | Structural. |
| 17–20 | Javadoc for the private constructor. | Style guide Javadoc requirement. |
| 21 | Declares a private, empty constructor. | Prevents instantiating a static-only utility class. |
| 22 | Ends the constructor. | Structural. |
| 23 | Blank line. | Structural. |
| 24–31 | Javadoc for `sort`, documenting both parameters and the `IllegalArgumentException`. | Style guide Javadoc requirement. |
| 32 | Declares `public static void sort(User[] users, Comparator<User> comparator)`. | Spec parameters exactly: "1. The user array. 2. The comparison functionality." |
| 33 | Checks whether `users` is `null`. | Argument-validation rule. |
| 34 | Throws `IllegalArgumentException`. | Same rule. |
| 35 | Ends the `if` block. | Structural. |
| 36 | Checks whether `comparator` is `null`. | Argument-validation rule. |
| 37 | Throws `IllegalArgumentException`. | Same rule. |
| 38 | Ends the `if` block. | Structural. |
| 39 | Blank line. | Structural. |
| 40 | Comment explaining the delegation that follows. | Style guide comment density. |
| 41 | Calls `Arrays.sort(users, comparator)`, sorting the array in place. | The fixed part of the Template Method (the sorting algorithm) calls out to the variable part (the comparator) supplied by the caller. |
| 42 | Ends the method. | Structural. |
| 43 | Ends the class. | Structural. |

Because `Arrays.sort` is called with an explicit `Comparator`, the ordering
logic can change completely (by age, by username, by email length, ...)
without `UserUtils.sort` itself ever changing — exactly the behavior the
spec calls out as an accepted Template Method implementation.

---

## 8. `ValidationResult.java` — the outcome contract

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3 | Imports `java.util.Optional`. | Spec: `getReason()` returns `Optional<String>`. |
| 4 | Blank line. | Structural. |
| 5–13 | Interface-level Javadoc: explains the success/failure contract for `isValid()` and `getReason()`. | Style guide Javadoc requirement. |
| 14 | Declares `public interface ValidationResult`. | Spec: "An interface declaring two abstract methods." |
| 15 | Blank line. | Structural. |
| 16–20 | Javadoc for `isValid()`. | Style guide Javadoc requirement. |
| 21 | Declares abstract method `boolean isValid();`. | Spec: `boolean isValid()` with no parameters. |
| 22 | Blank line. | Structural. |
| 23–28 | Javadoc for `getReason()`. | Style guide Javadoc requirement. |
| 29 | Declares abstract method `Optional<String> getReason();`. | Spec: `Optional<String> getReason()` with no parameters. |
| 30 | Ends the interface. | Structural. |

`ValidationResult` is deliberately minimal: it says nothing about *how* a
result is produced, only what a caller can ask it. `Valid` and `Invalid`
(next two sections) are the two concrete answers.

---

## 9. `Valid.java` — successful outcome

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3 | Imports `java.util.Optional`. | Needed for `getReason()`'s return type. |
| 4 | Blank line. | Structural. |
| 5–7 | Class-level Javadoc: one sentence, since the class has exactly one responsibility. | Style guide Javadoc requirement. |
| 8 | Declares `public final class Valid implements ValidationResult`. | Spec: "A concrete class implementing `ValidationResult`." |
| 9 | Blank line. | Structural. |
| 10–12 | Javadoc for the no-argument constructor. | Style guide Javadoc requirement. |
| 13 | Declares `public Valid()`. | Lets calling code and the combinators write `new Valid()`. |
| 14 | Ends the constructor. | Structural. |
| 15 | Blank line. | Structural. |
| 16–20 | Javadoc for `isValid()`, stating it always returns `true`. | Style guide Javadoc requirement. |
| 21 | `@Override` annotation. | Style guide: annotate every overriding method. |
| 22 | Declares `isValid()`. | Implements the `ValidationResult` contract. |
| 23 | Returns `true`. | `Valid` represents success by definition. |
| 24 | Ends the method. | Structural. |
| 25 | Blank line. | Structural. |
| 26–31 | Javadoc for `getReason()`, stating it always returns an empty `Optional`. | Style guide Javadoc requirement. |
| 32 | `@Override` annotation. | Same rule as line 21. |
| 33 | Declares `getReason()`. | Implements the `ValidationResult` contract. |
| 34 | Returns `Optional.empty()`. | A success has nothing to explain. |
| 35 | Ends the method. | Structural. |
| 36 | Blank line. | Structural. |
| 37–41 | Javadoc for `toString()`. | Style guide Javadoc requirement. |
| 42 | `@Override` annotation. | Same rule as line 21. |
| 43 | Declares `toString()`. | Style guide: every class should define `toString`. |
| 44 | Returns the literal string `"Valid{}"`. | Debug/log-friendly representation; there is no state to include. |
| 45 | Ends the method. | Structural. |
| 46 | Ends the class. | Structural. |

---

## 10. `Invalid.java` — failed outcome with a reason

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3 | Imports `java.util.Optional`. | Needed for `getReason()`'s return type. |
| 4 | Blank line. | Structural. |
| 5–8 | Class-level Javadoc. | Style guide Javadoc requirement. |
| 9 | Declares `public final class Invalid implements ValidationResult`. | Spec: "A concrete class implementing `ValidationResult`." |
| 10 | Blank line. | Structural. |
| 11 | Comment explaining the `reason` field. | Style guide comment density. |
| 12 | Declares `private final String reason`. | Unlike `Valid`, a failure needs to carry an explanation. |
| 13 | Blank line. | Structural. |
| 14–19 | Javadoc for the constructor, documenting the `IllegalArgumentException`. | Style guide Javadoc requirement. |
| 20 | Declares `public Invalid(String reason)`. | Lets the combinators and rule factories write `new Invalid("...")`. |
| 21 | Checks whether `reason` is `null` or empty. | Argument-validation rule (style guide). |
| 22 | Throws `IllegalArgumentException`. | Same rule. |
| 23 | Ends the `if` block. | Structural. |
| 24 | Assigns the validated value to the field. | Completes the constructor. |
| 25 | Ends the constructor. | Structural. |
| 26 | Blank line. | Structural. |
| 27–31 | Javadoc for `isValid()`, stating it always returns `false`. | Style guide Javadoc requirement. |
| 32 | `@Override` annotation. | Style guide: annotate every overriding method. |
| 33 | Declares `isValid()`. | Implements the `ValidationResult` contract. |
| 34 | Returns `false`. | `Invalid` represents failure by definition. |
| 35 | Ends the method. | Structural. |
| 36 | Blank line. | Structural. |
| 37–41 | Javadoc for `getReason()`. | Style guide Javadoc requirement. |
| 42 | `@Override` annotation. | Same rule as line 32. |
| 43 | Declares `getReason()`. | Implements the `ValidationResult` contract. |
| 44 | Returns `Optional.of(reason)`. | Spec: `getReason()` returns the failure reason. |
| 45 | Ends the method. | Structural. |
| 46 | Blank line. | Structural. |
| 47–51 | Javadoc for `toString()`. | Style guide Javadoc requirement. |
| 52 | `@Override` annotation. | Same rule as line 32. |
| 53 | Declares `toString()`. | Style guide: every class should define `toString`. |
| 54 | Returns a string embedding `reason`. | Debug/log-friendly representation that shows *why* validation failed. |
| 55 | Ends the method. | Structural. |
| 56 | Ends the class. | Structural. |

---

## 11. `UserValidation.java` — the Combinator itself

This is the file that carries the required design pattern. Every method on
it either **produces** a `UserValidation` or **combines** existing
`UserValidation`s into a new one; nothing here ever mutates an existing
validation.

| Line(s) | What code does | Prompt link |
|---|---|---|
| 1 | Declares the `il.ac.hit.validation` package. | Required package. |
| 2 | Blank line. | Structural. |
| 3 | Imports `java.util.Objects`. | Used for `Objects.requireNonNull` argument checks. |
| 4 | Imports `java.util.function.Function`. | Spec: "Extends `Function<User, ValidationResult>`." |
| 5 | Blank line. | Structural. |
| 6–18 | Interface-level Javadoc: explains that every `UserValidation` is a function `User -> ValidationResult`, that `and`/`or`/`xor`/`all`/`none` combine validations into new validations, and that combinations can be combined further without limit. | Directly documents the Combinator pattern the assignment requires. |
| 19 | Declares `public interface UserValidation extends Function<User, ValidationResult>`. | Spec: "The `il.ac.hit.validation.UserValidation` Interface... Extends `Function<User, ValidationResult>`." |
| 20 | Blank line. | Structural. |
| 21–28 | Javadoc for `and`, documenting the `NullPointerException`. | Style guide Javadoc requirement. |
| 29 | Declares `default UserValidation and(UserValidation other)`. | Spec: "`and(UserValidation other)`: Checks whether both conditions are fulfilled." |
| 30 | Calls `Objects.requireNonNull(other, ...)`. | Argument-validation rule; fails fast on a `null` combinator argument. |
| 31 | Blank line. | Structural. |
| 32 | Comment explaining the AND logic that follows. | Style guide comment density. |
| 33 | Starts the returned lambda `user -> { ... }`. This lambda is itself a `UserValidation`, since `UserValidation`'s single abstract method is `apply(User)`. | Combinator: `and` produces a brand-new validation instead of mutating `this`. |
| 34 | Evaluates `this` against `user` and stores the result. | Left-hand side of the AND. |
| 35 | Checks whether the left-hand result already failed. | Short-circuit: no need to evaluate `other` if `this` already failed. |
| 36 | Returns the failing left-hand result immediately. | Its `Invalid` reason explains why the AND failed. |
| 37 | Ends the `if` block. | Structural. |
| 38 | Evaluates and returns `other.apply(user)`. | If `this` passed, the AND's outcome is whatever `other` decides. |
| 39 | Ends the lambda body. | Structural. |
| 40 | Ends the `and` method. | Structural. |
| 41 | Blank line. | Structural. |
| 42–50 | Javadoc for `or`, documenting the `NullPointerException`. | Style guide Javadoc requirement. |
| 51 | Declares `default UserValidation or(UserValidation other)`. | Spec: "`or(UserValidation other)`: Checks whether at least one of the conditions is fulfilled." |
| 52 | Calls `Objects.requireNonNull(other, ...)`. | Argument-validation rule. |
| 53 | Blank line. | Structural. |
| 54 | Comment explaining the OR logic that follows. | Style guide comment density. |
| 55 | Starts the returned lambda. | Combinator: produces a new validation. |
| 56 | Evaluates `this` against `user`. | Left-hand side of the OR. |
| 57 | Checks whether the left-hand side already passed. | Short-circuit: no need to evaluate `other` if `this` already passed. |
| 58 | Returns the passing left-hand result immediately. | OR succeeds as soon as one side succeeds. |
| 59 | Ends the `if` block. | Structural. |
| 60 | Blank line. | Structural. |
| 61 | Evaluates `other` against `user`. | Right-hand side of the OR, only reached if `this` failed. |
| 62 | Checks whether the right-hand side passed. | Second (and last) chance for the OR to succeed. |
| 63 | Returns the passing right-hand result. | OR succeeds because at least one side succeeded. |
| 64 | Ends the `if` block. | Structural. |
| 65 | Blank line. | Structural. |
| 66–68 | Builds and returns a new `Invalid`, combining both sides' failure reasons into one message. | Both conditions failed, so the OR fails — with a reason that references both original failures. |
| 69 | Ends the lambda body. | Structural. |
| 70 | Ends the `or` method. | Structural. |
| 71 | Blank line. | Structural. |
| 72–80 | Javadoc for `xor`, documenting the `NullPointerException`. | Style guide Javadoc requirement. |
| 81 | Declares `default UserValidation xor(UserValidation other)`. | Spec: "`xor(UserValidation other)`: Checks whether only one of the two conditions is fulfilled." |
| 82 | Calls `Objects.requireNonNull(other, ...)`. | Argument-validation rule. |
| 83 | Blank line. | Structural. |
| 84 | Comment explaining the XOR logic that follows. | Style guide comment density. |
| 85 | Starts the returned lambda. | Combinator: produces a new validation. |
| 86 | Evaluates `this` against `user`. | Left-hand side of the XOR. |
| 87 | Evaluates `other` against `user`. | Right-hand side of the XOR. Unlike `and`/`or`, XOR always needs both sides, so there is no short-circuit here. |
| 88 | Blank line. | Structural. |
| 89 | Uses the boolean `^` (XOR) operator on the two `isValid()` results. | Directly expresses "exactly one of the two conditions." |
| 90 | Returns `new Valid()` when exactly one side passed. | XOR succeeds. |
| 91 | Ends the `if` block. | Structural. |
| 92 | Returns `new Invalid("Exactly one...")` otherwise (both passed, or both failed). | XOR fails. |
| 93 | Ends the lambda body. | Structural. |
| 94 | Ends the `xor` method. | Structural. |
| 95 | Blank line. | Structural. |
| 96–103 | Javadoc for `all`, documenting the `NullPointerException`. | Style guide Javadoc requirement. |
| 104 | Declares `static UserValidation all(UserValidation... validations)`. | Spec: "`all(UserValidation... validations)`: Checks whether all supplied conditions are fulfilled" — a static method with varargs. |
| 105 | Calls `Objects.requireNonNull(validations, ...)`. | Argument-validation rule. |
| 106 | Blank line. | Structural. |
| 107 | Comment explaining the fail-fast loop that follows. | Style guide comment density. |
| 108 | Starts the returned lambda. | Combinator: produces a new validation from the whole array. |
| 109 | Starts a `for` loop over every supplied `validation`. | Needs to check every rule to confirm they *all* pass. |
| 110 | Evaluates the current `validation` against `user`. | One rule's outcome. |
| 111 | Checks whether it failed. | Fail-fast: the whole `all` fails as soon as one rule fails. |
| 112 | Returns that failing result immediately. | Its reason explains which rule failed. |
| 113 | Ends the `if` block. | Structural. |
| 114 | Ends the `for` loop. | Structural. |
| 115 | Returns `new Valid()` after every rule has passed. | All supplied conditions were fulfilled. |
| 116 | Ends the lambda body. | Structural. |
| 117 | Ends the `all` method. | Structural. |
| 118 | Blank line. | Structural. |
| 119–126 | Javadoc for `none`, documenting the `NullPointerException`. | Style guide Javadoc requirement. |
| 127 | Declares `static UserValidation none(UserValidation... validations)`. | Spec: "`none(UserValidation... validations)`: Checks whether none of the supplied conditions is fulfilled." |
| 128 | Calls `Objects.requireNonNull(validations, ...)`. | Argument-validation rule. |
| 129 | Blank line. | Structural. |
| 130 | Comment explaining the loop that follows. | Style guide comment density. |
| 131 | Starts the returned lambda. | Combinator: produces a new validation from the whole array. |
| 132 | Starts a `for` loop over every supplied `validation`. | Needs to check every rule to confirm *none* of them pass. |
| 133 | Evaluates the current `validation` against `user`. | One rule's outcome. |
| 134 | Checks whether it unexpectedly passed. | `none` fails as soon as any rule passes. |
| 135 | Returns a new `Invalid` explaining that a condition was fulfilled. | `none` fails. |
| 136 | Ends the `if` block. | Structural. |
| 137 | Ends the `for` loop. | Structural. |
| 138 | Returns `new Valid()` after confirming every rule failed. | None of the supplied conditions were fulfilled. |
| 139 | Ends the lambda body. | Structural. |
| 140 | Ends the `none` method. | Structural. |
| 141 | Blank line. | Structural. |
| 142–147 | Javadoc for `emailEndsWithIL()`. | Style guide Javadoc requirement. |
| 148 | Declares `static UserValidation emailEndsWithIL()`. | Spec: "`emailEndsWithIL()`: Verifies that the email address ends with `"il"`." |
| 149–151 | Returns a lambda that checks `user.getEmail().endsWith("il")`, yielding `Valid`/`Invalid` accordingly. | Implements the rule. |
| 152 | Ends the method. | Structural. |
| 153 | Blank line. | Structural. |
| 154–159 | Javadoc for `emailLengthBiggerThan10()`. | Style guide Javadoc requirement. |
| 160 | Declares `static UserValidation emailLengthBiggerThan10()`. | Spec: "Verifies that the email length is greater than 10." |
| 161–163 | Returns a lambda that checks `user.getEmail().length() > 10`. | Implements the rule. |
| 164 | Ends the method. | Structural. |
| 165 | Blank line. | Structural. |
| 166–171 | Javadoc for `passwordLengthBiggerThan8()`. | Style guide Javadoc requirement. |
| 172 | Declares `static UserValidation passwordLengthBiggerThan8()`. | Spec: "Verifies that the password length is greater than 8." |
| 173–175 | Returns a lambda that checks `user.getPassword().length() > 8`. | Implements the rule. |
| 176 | Ends the method. | Structural. |
| 177 | Blank line. | Structural. |
| 178–183 | Javadoc for `passwordIncludesLettersNumbersOnly()`. | Style guide Javadoc requirement. |
| 184 | Declares `static UserValidation passwordIncludesLettersNumbersOnly()`. | Spec: "Verifies that the password contains letters and numbers only." |
| 185–187 | Returns a lambda that checks the password against the regular expression `^[a-zA-Z0-9]+$`. | Implements the rule: only ASCII letters/digits are accepted. |
| 188 | Ends the method. | Structural. |
| 189 | Blank line. | Structural. |
| 190–195 | Javadoc for `passwordIncludesDollarSign()`. | Style guide Javadoc requirement. |
| 196 | Declares `static UserValidation passwordIncludesDollarSign()`. | Spec: "Verifies that the password includes the `$` character." |
| 197–199 | Returns a lambda that checks `user.getPassword().contains("$")`. | Implements the rule. |
| 200 | Ends the method. | Structural. |
| 201 | Blank line. | Structural. |
| 202–207 | Javadoc for `passwordIsDifferentFromUsername()`. | Style guide Javadoc requirement. |
| 208 | Declares `static UserValidation passwordIsDifferentFromUsername()`. | Spec: "Verifies that the password is different from the username." |
| 209–211 | Returns a lambda that checks `!user.getPassword().equals(user.getUsername())`. | Implements the rule. |
| 212 | Ends the method. | Structural. |
| 213 | Blank line. | Structural. |
| 214–218 | Javadoc for `ageBiggerThan18()`. | Style guide Javadoc requirement. |
| 219 | Declares `static UserValidation ageBiggerThan18()`. | Spec: "`ageBiggerThan18()`: Verifies that the age is greater than 18." |
| 220–222 | Returns a lambda that checks `user.getAge() > 18`. | Implements the rule. |
| 223 | Ends the method. | Structural. |
| 224 | Blank line. | Structural. |
| 225–230 | Javadoc for `usernameLengthBiggerThan8()`. | Style guide Javadoc requirement. |
| 231 | Declares `static UserValidation usernameLengthBiggerThan8()`. | Spec: "Verifies that the username length is greater than 8." |
| 232–234 | Returns a lambda that checks `user.getUsername().length() > 8`. | Implements the rule. |
| 235 | Ends the method. | Structural. |
| 236 | Ends the interface. | Structural. |

### Why every rule and combinator returns a lambda, not an enum or a class

Each factory method (`emailEndsWithIL()`, `ageBiggerThan18()`, ...) and each
combinator (`and`, `or`, `xor`, `all`, `none`) returns `user -> ...`. Because
`UserValidation`'s only abstract method is `apply(User)` (inherited from
`Function`), a lambda with that shape satisfies the whole interface. This is
what lets rules and combinations be created and passed around as ordinary
values — the essence of the Combinator pattern: small building blocks (the
eight rule factories) and combining operators (`and`/`or`/`xor`/`all`/
`none`) that both take and return the same type, so they can be chained
without limit, exactly as `UserValidationV2Demo` does when it calls
`validation1.and(validation2)`.

---

## 12. How the three patterns cooperate

### Combinator: validation rules

- `UserValidation` is both the "atom" type (a single rule) and the
  "expression" type (a combination of rules), because both are just
  functions `User -> ValidationResult`.
- `and`, `or`, and `xor` are binary combinators; `all` and `none` are
  variadic combinators over the same building blocks.
- `Valid` and `Invalid` are the two possible outcomes every rule and every
  combination eventually reduces to.

Spec relationship: "This library should implement the Combinator design
pattern."

### Factory Method: user creation

- `User` defines the common shape (`username`, `email`, `password`, `age`).
- `BasicUser`, `PremiumUser`, and `PlatinumUser` are the three concrete
  products, distinguished only by type, not by extra state.
- `UserFactory.createUser(type, ...)` is the factory method: it hides which
  concrete subclass gets built behind a single type string.

Spec relationship: "`il.ac.hit.validation.UserFactory` Class — Implements
the Factory Method design pattern."

### Template Method: sorting

- `UserUtils.sort` is the fixed algorithm (delegating to `Arrays.sort`).
- `Comparator<User>` is the variable step, supplied by the caller for each
  call.

Spec relationship: "Implements the Template Method design pattern... Using
the `Comparator<T>` interface will be considered an implementation of the
Template Method."

---

## 13. Full end-to-end example

Using the sample program from the assignment specification
(`UserValidationV2Demo`, kept locally under `test/`, never committed):

```java
User user = new User("admin", "admin@#yzw.co.il", "abc123", 34);

UserValidation validation1 = UserValidation.emailLengthBiggerThan10();
UserValidation validation2 = UserValidation.emailEndsWithIL();

ValidationResult result = (validation1.and(validation2)).apply(user);
```

1. `new User(...)` runs `User`'s constructor (`User.java` line 39), which
   calls the four setters (lines 40–43), each validating its argument.
2. `UserValidation.emailLengthBiggerThan10()` (`UserValidation.java` line
   160) returns a lambda; it is *not* evaluated yet, only created.
3. `UserValidation.emailEndsWithIL()` (line 148) likewise returns an
   unevaluated lambda.
4. `validation1.and(validation2)` (line 29) returns a **third**, new lambda
   that, when applied, will run both `validation1` and `validation2` and AND
   their results together. Nothing about `user` has been touched yet.
5. `.apply(user)` finally runs that combined lambda (lines 33–39):
   - `validation1.apply(user)` runs the lambda from line 161: `"admin@#yzw.co.il".length()` is `16`, which is `> 10`, so it returns `new Valid()`.
   - Because the left-hand result is valid, line 38 evaluates
     `validation2.apply(user)`, which runs the lambda from line 149:
     `"admin@#yzw.co.il".endsWith("il")` is `true`, so it also returns
     `new Valid()`.
   - The AND's overall result is that second `Valid()`.
6. `result.isValid()` is `true`, so the program prints `"User is valid"`.

If either check had failed, the `and` combinator would have short-circuited
at line 35–37 and returned the first `Invalid` result, carrying its
specific reason string.

---

## 14. Small but important Java ideas used

- `final class` / `final` methods: prevents subclassing or overriding where
  none is intended (see the `User` setters discussion in section 4).
- `private final field`: assigned once in the constructor, never reassigned
  (`Invalid.reason`).
- `static`: belongs to the class, not to an instance (`UserFactory`,
  `UserUtils`, and every `UserValidation` factory/combinator method).
- `interface` with `default` methods: `UserValidation` mixes abstract
  behavior (inherited `apply` from `Function`) with concrete, reusable
  behavior (`and`/`or`/`xor`).
- `interface` with varargs `static` methods: `all(UserValidation...)` and
  `none(UserValidation...)`.
- Lambda expressions such as `user -> ...`: compact implementations of the
  single-method `Function`/`UserValidation` contract.
- Method reference target `User::getAge` (used by calling code, e.g. with
  `Comparator.comparingInt`): compact reference to an existing method.
- Ternary `condition ? new Valid() : new Invalid(reason)`: selects one of
  the two `ValidationResult` outcomes.
- `switch` **expression** with arrow (`->`) syntax and no fall-through:
  `UserFactory.createUser`.
- `Optional<String>`: expresses "a reason, or none" without using `null`.
- `Objects.requireNonNull`: concise, standard-library null-argument
  validation.
- The boolean `^` (XOR) operator: expresses "exactly one of two conditions"
  directly, without nested `if`/`else`.

## 15. One-sentence responsibility map

- `User`: hold a username/email/password/age and validate them on write.
- `BasicUser` / `PremiumUser` / `PlatinumUser`: mark which membership tier a
  `User` belongs to.
- `UserFactory`: turn a type string into the right concrete `User` subtype.
- `UserUtils`: sort a `User[]` using a caller-supplied `Comparator<User>`.
- `ValidationResult`: describe whether a validation passed, and why not.
- `Valid`: the "passed" outcome.
- `Invalid`: the "failed" outcome, carrying a reason.
- `UserValidation`: define a single validation rule as `User ->
  ValidationResult`, and provide the combinators (`and`/`or`/`xor`/`all`/
  `none`) and rule factories that make it a Combinator.
