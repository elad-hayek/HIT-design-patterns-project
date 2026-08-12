package il.ac.hit.validation;

/**
 * Factory class responsible for creating {@link User} instances.
 *
 * <p>This class implements the <b>Factory Method</b> design pattern: callers
 * ask for a user by supplying a type string, and this class decides which
 * concrete {@link User} subclass gets instantiated. Callers never need to
 * know about {@link BasicUser}, {@link PremiumUser}, or {@link PlatinumUser}
 * directly.</p>
 */
public final class UserFactory {

    // Type string that selects the "basic" membership tier
    private static final String TYPE_BASIC = "basic";

    // Type string that selects the "premium" membership tier
    private static final String TYPE_PREMIUM = "premium";

    // Type string that selects the "platinum" membership tier
    private static final String TYPE_PLATINUM = "platinum";

    /**
     * Prevents instantiation. {@code UserFactory} exposes only static
     * factory behavior.
     */
    private UserFactory() {
    }

    /**
     * Creates a new {@link User} of the concrete subtype that matches the
     * supplied membership type.
     *
     * @param type     one of {@code "basic"}, {@code "premium"}, or {@code "platinum"}
     * @param username the username of the new user
     * @param email    the email address of the new user
     * @param password the password of the new user
     * @param age      the age of the new user, in years
     * @return a new {@link BasicUser}, {@link PremiumUser}, or {@link PlatinumUser}
     * @throws IllegalArgumentException if {@code type} is {@code null} or unrecognized
     */
    public static User createUser(
            String type, String username, String email, String password, int age) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        // Dispatch to the concrete subtype that matches the requested type
        return switch (type.toLowerCase()) {
            case TYPE_BASIC -> new BasicUser(username, email, password, age);
            case TYPE_PREMIUM -> new PremiumUser(username, email, password, age);
            case TYPE_PLATINUM -> new PlatinumUser(username, email, password, age);
            default -> throw new IllegalArgumentException("Unknown user type: " + type);
        };
    }
}
