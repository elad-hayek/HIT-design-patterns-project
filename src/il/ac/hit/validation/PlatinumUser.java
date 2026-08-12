package il.ac.hit.validation;

/**
 * Represents a user registered under the "platinum" membership tier.
 *
 * <p>Instances of this class are created by {@link UserFactory} when the
 * factory method is invoked with the {@code "platinum"} type string.</p>
 */
public class PlatinumUser extends User {

    /**
     * Creates a new {@code PlatinumUser} with the supplied properties.
     *
     * @param username the username of the user
     * @param email    the email address of the user
     * @param password the password of the user
     * @param age      the age of the user, in years
     */
    public PlatinumUser(String username, String email, String password, int age) {
        super(username, email, password, age);
    }
}
