# Final Project in Design Patterns: The Validation Library in Java

> **PLEASE NOTE:** DURING THE MEETINGS TILL THE END OF THE SEMESTER CHANGES MIGHT BE INTRODUCED INTO THIS DOCUMENT. THEREFORE, MAKE SURE THAT YOUR PROJECT MEETS THE REQUIREMENTS OF THIS DOCUMENT BY THE END OF THIS SEMESTER. THE REQUIREMENTS WON’T CHANGE. THE ONLY CHANGES THAT CAN TAKE PLACE ARE SMALL IMPROVEMENTS TO THE TEXT IN ORDER TO CLARIFY THE REQUIREMENTS.
> 
> QUESTIONS REGARDING THE INTERPRETATION OF THIS DOCUMENT SHOULD BE POSTED IN THE COURSE FORUM. MAKE SURE TO FOLLOW THIS FORUM TO VERIFY THAT YOUR INTERPRETATION IS THE ACCURATE ONE.

---

## Introduction

The final project includes the development of a library in Java named **`validation`**. This library should implement the **Combinator design pattern**.

- The library must be packed in a single JAR file named **`validation.jar`**.
- The final project should support the use of **English only**.
- The interfaces and classes listed below must be part of the library. You can add more members to these interfaces and classes (ensuring the code below still works seamlessly) or add additional classes and interfaces.
- Your library will be tested using the code provided below or a similar test program.
- The validation library will assist developers with validating new users in software applications.

---

## The `il.ac.hit.validation` Package

All classes and interfaces should be contained within the package **`il.ac.hit.validation`**. At minimum, the package must include the following components:

### 1. `il.ac.hit.validation.User` Class
Defines the `User` class with the following instance variables:
- `username`
- `email`
- `password`
- `age`

Must include a **primary constructor** that accepts values assigned to these properties in the exact order listed above.

### 2. `il.ac.hit.validation.UserFactory` Class
Implements the **Factory Method** design pattern.
- The factory method accepts one of the following strings: `"basic"`, `"premium"`, or `"platinum"`.
- Returns a new object instantiated from `BasicUser`, `PremiumUser`, or `PlatinumUser` respectively.
- These three classes (`BasicUser`, `PremiumUser`, `PlatinumUser`) must extend `User`.

### 3. `il.ac.hit.validation.UserUtils` Class
Includes a static `sort` method capable of sorting an array of users.
- Implements the **Template Method** design pattern.
- **Parameters**:
  1. The user array.
  2. The comparison functionality for comparing two users. 
- *Note:* Using the `Comparator<T>` interface will be considered an implementation of the Template Method.

### 4. `il.ac.hit.validation.ValidationResult` Interface
An interface declaring two abstract methods (with no parameters):
- `boolean isValid()`
- `Optional<String> getReason()`

### 5. Concrete Result Classes
- **`il.ac.hit.validation.Valid`**: A concrete class implementing `ValidationResult`.
- **`il.ac.hit.validation.Invalid`**: A concrete class implementing `ValidationResult`.

### 6. `il.ac.hit.validation.UserValidation` Interface
Extends `Function<User, ValidationResult>`.

#### Instance Methods (Combining Conditions)
Each method takes one parameter of type `UserValidation` and returns a `UserValidation` instance:
- **`and(UserValidation other)`**: Checks whether both conditions are fulfilled.
- **`or(UserValidation other)`**: Checks whether at least one of the conditions is fulfilled.
- **`xor(UserValidation other)`**: Checks whether only one of the two conditions is fulfilled.

#### Static Methods (Varargs Combination)
Static methods returning `UserValidation` and defined with repeated parameters (varargs):
- **`all(UserValidation... validations)`**: Checks whether **all** supplied conditions are fulfilled.
- **`none(UserValidation... validations)`**: Checks whether **none** of the supplied conditions is fulfilled.

#### Static Validation Rules (Factory Methods)
Public static methods returning `UserValidation` with no parameters:
- **`emailEndsWithIL()`**: Verifies that the email address ends with `"il"`.
- **`emailLengthBiggerThan10()`**: Verifies that the email length is greater than 10.
- **`passwordLengthBiggerThan8()`**: Verifies that the password length is greater than 8.
- **`passwordIncludesLettersNumbersOnly()`**: Verifies that the password contains letters and numbers only.
- **`passwordIncludesDollarSign()`**: Verifies that the password includes the `$` character.
- **`passwordIsDifferentFromUsername()`**: Verifies that the password is different from the username.
- **`ageBiggerThan18()`**: Verifies that the age is greater than 18.
- **`usernameLengthBiggerThan8()`**: Verifies that the username length is greater than 8.

---

## Code Style

The Java code must strictly follow the style guidelines shown in the *Java Professional Style Guide* book:
- [Java Professional Style Guide on Amazon](https://www.amazon.com/Professional-Java-Style-Guide-Maintainable-ebook/dp/B0GHZL3113/ref=sr_1_1)
- Please follow the course message board regarding free access instructions.

---

## Submission Guidelines

1. **Development Environment**:
   - IDE: IntelliJ (Ultimate or Community Edition).
   - JDK: JDK 24.

2. **Video Demonstration**:
   - Create a short video (~60 seconds recommended) demonstrating how the project runs.
   - Upload to YouTube as an **Unlisted** video.
   - Must include your own voiced explanation of how the Combinator design pattern was implemented (audio, not on-screen text).

3. **Submitted Artifacts**:
   - The team manager must upload three files to Moodle:
     1. **`firstname_lastname.zip`**: Exported directly from IntelliJ (`File -> Export -> Project to Zip file`).
     2. **`validation.jar`**: The compiled JAR file.
     3. **`firstname_lastname.pdf`**: A single PDF document containing all source code.
   - File naming format: Use English letters only, lowercase, separated by underscores (e.g., `moshe_israeli.zip` and `moshe_israeli.pdf`). No spaces or special characters allowed.

4. **PDF Document Requirements**:
   - Must contain all source code files written by the team, left-aligned, properly formatted without broken lines to facilitate code review.
   - **Front Section Structure**:
     - **a.** First and last name of the Development Team Manager.
     - **b.** First name, Last name, ID, Mobile Number, and Email Address of every team member.
     - **c.** Active, clickable link to the YouTube video.
     - **d.** A short summary (up to 100 words) describing the team's use of at least 2 collaboration tools presented in class.

5. **Submission Policies**:
   - Only the **Team Manager** submits the project on Moodle.
   - Submit at least **30 minutes before the official deadline** due to server time discrepancies.
   - **Single-student submissions are not accepted** by default (if specially approved, maximum possible grade starts at 90).
   - Late submissions lose 2 points for every hour of delay.

---

## Code Sample for Testing the Project

```java
public class UserValidationV2Demo {  
    public static void main(String args[]) {
        User user = new User("admin", "admin@#yzw.co.il", "abc123", 34);

        UserValidation validation1 = UserValidation.emailLengthBiggerThan10();
        UserValidation validation2 = UserValidation.emailEndsWithIL();

        ValidationResult result = (validation1.and(validation2)).apply(user);

        if (result.isValid()) {
            System.out.println("User is valid");
        } else {
            System.out.println("User is not valid");
        }
    }
}
```
