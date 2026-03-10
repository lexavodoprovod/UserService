package com.innowise.UserService.model.query;

public final class UserQuery {

    public static final String UPDATE_USER_BY_ID_NATIVE = """
            UPDATE users
            SET name = :#{#user.name},
            surname = :#{#user.surname},
            email = :#{#user.email},
            birth_date = :#{#user.birthDate},
            updated_at = CURRENT_TIMESTAMP
            WHERE id = :#{#user.id}
     """;

    public static final String ACTIVATE_USER_BY_ID_JPQL = """
              UPDATE User u
              SET u.active = true, u.updatedAt = CURRENT_TIMESTAMP
              WHERE u.id = :id
            """;

    public static final String DEACTIVATE_USER_BY_ID_JPQL = """
            UPDATE User u
            SET u.active = false, u.updatedAt = CURRENT_TIMESTAMP
            WHERE u.id = :id
            """;


    private UserQuery() {}
}
