package il.ac.hit.validation;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Utility class offering sorting behavior for arrays of {@link User}.
 *
 * <p>This class implements the <b>Template Method</b> design pattern.
 * The overall sorting algorithm (iterating and reordering the array) is
 * fixed, while the comparison step that decides the relative order of two
 * users is supplied by the caller through a {@link Comparator}, which acts
 * as the variable step of the template.</p>
 */
public final class UserUtils {

    /**
     * Prevents instantiation. {@code UserUtils} exposes only static
     * utility behavior.
     */
    private UserUtils() {
    }

    /**
     * Sorts the supplied array of users in place, using {@code comparator}
     * to determine the relative order of any two users.
     *
     * @param users      the array of users to sort
     * @param comparator the comparison function used to order the users
     * @throws IllegalArgumentException if {@code users} or {@code comparator} is {@code null}
     */
    public static void sort(User[] users, Comparator<User> comparator) {
        if (users == null) {
            throw new IllegalArgumentException("users must not be null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }

        // Delegate the actual reordering to the comparator supplied by the caller
        Arrays.sort(users, comparator);
    }
}
