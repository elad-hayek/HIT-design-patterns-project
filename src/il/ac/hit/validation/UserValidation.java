package il.ac.hit.validation;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a single validation rule that can be applied to a {@link User}.
 *
 * <p>This interface is the heart of the <b>Combinator</b> design pattern
 * implemented by this library. Every {@code UserValidation} is a function
 * from {@link User} to {@link ValidationResult}. Small validations, created
 * through the static factory methods below, can be combined into bigger
 * validations using {@link #and(UserValidation)}, {@link #or(UserValidation)},
 * {@link #xor(UserValidation)}, {@link #all(UserValidation...)}, and
 * {@link #none(UserValidation...)}. Because combining validations yields
 * another {@code UserValidation}, combinations can themselves be combined
 * further, without limit.</p>
 */
public interface UserValidation extends Function<User, ValidationResult> {

    /**
     * Combines this validation with {@code other}, producing a validation
     * that succeeds only when both this validation and {@code other} succeed.
     *
     * @param other the other validation to combine with this one
     * @return a validation representing the logical AND of this and {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default UserValidation and(UserValidation other) {
        Objects.requireNonNull(other, "other must not be null");

        // Both validations must pass for the combined validation to pass
        return user -> {
            ValidationResult thisResult = this.apply(user);
            if (!thisResult.isValid()) {
                return thisResult;
            }
            return other.apply(user);
        };
    }

    /**
     * Combines this validation with {@code other}, producing a validation
     * that succeeds when at least one of this validation and {@code other}
     * succeeds.
     *
     * @param other the other validation to combine with this one
     * @return a validation representing the logical OR of this and {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default UserValidation or(UserValidation other) {
        Objects.requireNonNull(other, "other must not be null");

        // At least one of the two validations must pass
        return user -> {
            ValidationResult thisResult = this.apply(user);
            if (thisResult.isValid()) {
                return thisResult;
            }

            ValidationResult otherResult = other.apply(user);
            if (otherResult.isValid()) {
                return otherResult;
            }

            return new Invalid("Neither condition was fulfilled: "
                    + thisResult.getReason().orElse("")
                    + "; " + otherResult.getReason().orElse(""));
        };
    }

    /**
     * Combines this validation with {@code other}, producing a validation
     * that succeeds only when exactly one of this validation and
     * {@code other} succeeds.
     *
     * @param other the other validation to combine with this one
     * @return a validation representing the logical XOR of this and {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default UserValidation xor(UserValidation other) {
        Objects.requireNonNull(other, "other must not be null");

        // Exactly one of the two validations must pass, not both and not neither
        return user -> {
            ValidationResult thisResult = this.apply(user);
            ValidationResult otherResult = other.apply(user);

            if (thisResult.isValid() ^ otherResult.isValid()) {
                return new Valid();
            }
            return new Invalid("Exactly one of the two conditions must be fulfilled");
        };
    }

    /**
     * Combines the supplied validations into a single validation that
     * succeeds only when every one of them succeeds.
     *
     * @param validations the validations to combine
     * @return a validation that succeeds when all supplied validations succeed
     * @throws NullPointerException if {@code validations} is {@code null}
     */
    static UserValidation all(UserValidation... validations) {
        Objects.requireNonNull(validations, "validations must not be null");

        // Fail fast on the first validation that does not pass
        return user -> {
            for (UserValidation validation : validations) {
                ValidationResult result = validation.apply(user);
                if (!result.isValid()) {
                    return result;
                }
            }
            return new Valid();
        };
    }

    /**
     * Combines the supplied validations into a single validation that
     * succeeds only when none of them succeed.
     *
     * @param validations the validations to combine
     * @return a validation that succeeds when no supplied validation succeeds
     * @throws NullPointerException if {@code validations} is {@code null}
     */
    static UserValidation none(UserValidation... validations) {
        Objects.requireNonNull(validations, "validations must not be null");

        // Fail as soon as any validation unexpectedly passes
        return user -> {
            for (UserValidation validation : validations) {
                ValidationResult result = validation.apply(user);
                if (result.isValid()) {
                    return new Invalid("At least one of the supplied conditions was fulfilled");
                }
            }
            return new Valid();
        };
    }

    /**
     * Creates a validation verifying that the user's email address ends
     * with {@code "il"}.
     *
     * @return the validation rule
     */
    static UserValidation emailEndsWithIL() {
        return user -> user.getEmail().endsWith("il")
                ? new Valid()
                : new Invalid("Email does not end with \"il\"");
    }

    /**
     * Creates a validation verifying that the user's email address is
     * longer than 10 characters.
     *
     * @return the validation rule
     */
    static UserValidation emailLengthBiggerThan10() {
        return user -> user.getEmail().length() > 10
                ? new Valid()
                : new Invalid("Email length is not bigger than 10");
    }

    /**
     * Creates a validation verifying that the user's password is longer
     * than 8 characters.
     *
     * @return the validation rule
     */
    static UserValidation passwordLengthBiggerThan8() {
        return user -> user.getPassword().length() > 8
                ? new Valid()
                : new Invalid("Password length is not bigger than 8");
    }

    /**
     * Creates a validation verifying that the user's password contains
     * letters and digits only.
     *
     * @return the validation rule
     */
    static UserValidation passwordIncludesLettersNumbersOnly() {
        return user -> user.getPassword().matches("^[a-zA-Z0-9]+$")
                ? new Valid()
                : new Invalid("Password includes characters other than letters and numbers");
    }

    /**
     * Creates a validation verifying that the user's password includes the
     * {@code $} character.
     *
     * @return the validation rule
     */
    static UserValidation passwordIncludesDollarSign() {
        return user -> user.getPassword().contains("$")
                ? new Valid()
                : new Invalid("Password does not include the '$' character");
    }

    /**
     * Creates a validation verifying that the user's password is different
     * from the user's username.
     *
     * @return the validation rule
     */
    static UserValidation passwordIsDifferentFromUsername() {
        return user -> !user.getPassword().equals(user.getUsername())
                ? new Valid()
                : new Invalid("Password must be different from username");
    }

    /**
     * Creates a validation verifying that the user's age is bigger than 18.
     *
     * @return the validation rule
     */
    static UserValidation ageBiggerThan18() {
        return user -> user.getAge() > 18
                ? new Valid()
                : new Invalid("Age is not bigger than 18");
    }

    /**
     * Creates a validation verifying that the user's username is longer
     * than 8 characters.
     *
     * @return the validation rule
     */
    static UserValidation usernameLengthBiggerThan8() {
        return user -> user.getUsername().length() > 8
                ? new Valid()
                : new Invalid("Username length is not bigger than 8");
    }
}
