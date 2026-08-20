package il.ac.hit.validation;

/**
 * Represents a user registered under the "premium" membership tier.
 *
 * <p>Instances of this class are created by {@link UserFactory} when the
 * factory method is invoked with the {@code "premium"} type string.</p>
 */
public class PremiumUser extends User {

    /**
     * Creates a new {@code PremiumUser} with the supplied properties.
     *
     * @param username the username of the user
     * @param email    the email address of the user
     * @param password the password of the user
     * @param age      the age of the user, in years
     */
    public PremiumUser(String username, String email, String password, int age) {
        super(username, email, password, age);
    }

    /**
     * Returns a human-readable representation of this user, including the
     * membership tier.
     *
     * @return a string describing this user
     */
    @Override
    public String toString() {
        return "PremiumUser{username='" + getUsername() + "', email='" + getEmail()
                + "', age=" + getAge() + "}";
    }
}
