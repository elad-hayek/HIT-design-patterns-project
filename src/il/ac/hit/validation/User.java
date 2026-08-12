package il.ac.hit.validation;

/**
 * Represents an application user that can be validated by the validation
 * library. A {@code User} is described by a username, an email address, a
 * password, and an age.
 *
 * <p>Concrete subtypes ({@link BasicUser}, {@link PremiumUser}, and
 * {@link PlatinumUser}) are produced by {@link UserFactory} and only
 * distinguish the membership tier of the user; they do not add any new
 * state.</p>
 */
public class User {

    // Unique name chosen by the user during registration
    private String username;

    // Contact email address used for account related communication
    private String email;

    // Secret credential used to authenticate the user
    private String password;

    // Age of the user, expressed in whole years
    private int age;

    /**
     * Creates a new {@code User} with the supplied properties.
     *
     * <p>This is the primary constructor. It delegates every assignment to
     * the corresponding setter so that validation is applied consistently,
     * regardless of how the fields are populated later on.</p>
     *
     * @param username the username of the user
     * @param email    the email address of the user
     * @param password the password of the user
     * @param age      the age of the user, in years
     */
    public User(String username, String email, String password, int age) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setAge(age);
    }

    /**
     * Returns the username of this user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the username of this user.
     *
     * @param username the new username
     * @throws IllegalArgumentException if {@code username} is {@code null} or empty
     */
    public final void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username must not be null or empty");
        }
        this.username = username;
    }

    /**
     * Returns the email address of this user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the email address of this user.
     *
     * @param email the new email address
     * @throws IllegalArgumentException if {@code email} is {@code null} or empty
     */
    public final void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email must not be null or empty");
        }
        this.email = email;
    }

    /**
     * Returns the password of this user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the password of this user.
     *
     * @param password the new password
     * @throws IllegalArgumentException if {@code password} is {@code null} or empty
     */
    public final void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password must not be null or empty");
        }
        this.password = password;
    }

    /**
     * Returns the age of this user, in years.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Updates the age of this user.
     *
     * @param age the new age, in years
     * @throws IllegalArgumentException if {@code age} is negative
     */
    public final void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("age must not be negative");
        }
        this.age = age;
    }

    /**
     * Returns a human-readable representation of this user. Useful for
     * debugging and logging.
     *
     * @return a string describing this user
     */
    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email
                + "', age=" + age + "}";
    }
}
