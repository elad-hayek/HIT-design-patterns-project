package il.ac.hit.validation;

import java.util.Optional;

/**
 * A {@link ValidationResult} that represents a failed validation, together
 * with a human-readable reason describing what went wrong.
 */
public final class Invalid implements ValidationResult {

    // Explanation of why the validation failed
    private final String reason;

    /**
     * Creates a new {@code Invalid} result carrying the supplied reason.
     *
     * @param reason a human-readable description of the failure
     * @throws IllegalArgumentException if {@code reason} is {@code null} or empty
     */
    public Invalid(String reason) {
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("reason must not be null or empty");
        }
        this.reason = reason;
    }

    /**
     * Always returns {@code false}, since this result represents failure.
     *
     * @return {@code false}
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * Returns the reason this validation failed.
     *
     * @return an {@link Optional} containing the failure reason
     */
    @Override
    public Optional<String> getReason() {
        return Optional.of(reason);
    }

    /**
     * Returns a human-readable representation of this result.
     *
     * @return a string describing this result
     */
    @Override
    public String toString() {
        return "Invalid{reason='" + reason + "'}";
    }
}
