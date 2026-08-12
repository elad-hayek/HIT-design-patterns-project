package il.ac.hit.validation;

import java.util.Optional;

/**
 * A {@link ValidationResult} that represents a successful validation.
 */
public final class Valid implements ValidationResult {

    /**
     * Creates a new {@code Valid} result.
     */
    public Valid() {
    }

    /**
     * Always returns {@code true}, since this result represents success.
     *
     * @return {@code true}
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Always returns an empty {@link Optional}, since a successful
     * validation has no failure reason.
     *
     * @return an empty {@link Optional}
     */
    @Override
    public Optional<String> getReason() {
        return Optional.empty();
    }

    /**
     * Returns a human-readable representation of this result.
     *
     * @return a string describing this result
     */
    @Override
    public String toString() {
        return "Valid{}";
    }
}
