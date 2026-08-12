package il.ac.hit.validation;

import java.util.Optional;

/**
 * Describes the outcome of validating a {@link User}.
 *
 * <p>A {@code ValidationResult} either represents success, in which case
 * {@link #isValid()} returns {@code true} and {@link #getReason()} returns
 * an empty {@link Optional}, or it represents failure, in which case
 * {@link #isValid()} returns {@code false} and {@link #getReason()} returns
 * an {@link Optional} describing why the validation failed.</p>
 */
public interface ValidationResult {

    /**
     * Indicates whether the validation succeeded.
     *
     * @return {@code true} if the validation succeeded, {@code false} otherwise
     */
    boolean isValid();

    /**
     * Returns the reason the validation failed.
     *
     * @return an {@link Optional} describing the failure, or an empty
     * {@link Optional} when the validation succeeded
     */
    Optional<String> getReason();
}
