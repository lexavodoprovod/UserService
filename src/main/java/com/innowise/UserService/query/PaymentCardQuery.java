package com.innowise.UserService.query;

public final class PaymentCardQuery {
    public static final String UPDATE_PAYMENT_CARD_BY_ID_NATIVE = """
            UPDATE payment_cards
            SET number = :#{#paymentCard.number},
            holder = :#{#paymentCard.holder},
            expiration_date = :#{#paymentCard.expirationDate},
            updated_at = CURRENT_TIMESTAMP
            WHERE id = :#{#paymentCard.id}
            """;

    public static final String ACTIVATE_PAYMENT_CARD_BY_ID_JPQL = """
             UPDATE PaymentCard p
             SET p.active = true, p.updatedAt = CURRENT_TIMESTAMP
             WHERE p.id = :id
            """;

    public static final String DEACTIVATE_PAYMENT_CARD_BY_ID_JPQL = """
             UPDATE PaymentCard p
             SET p.active = false, p.updatedAt = CURRENT_TIMESTAMP
             WHERE p.id = :id
            """;

    public static final String DEACTIVATE_PAYMENT_CARDS_BY_USER_ID_JPQL = """
            UPDATE PaymentCard p
            SET p.active = false
            WHERE p.user.id = :userId
            """;

    public static final String COUNT_PAYMENT_CARD_BY_USER_ID_JPQL = """
            SELECT COUNT(p)
            FROM PaymentCard p
            WHERE p.user.id = :userId
            """;

    public static final String COUNT_ACTIVE_PAYMENT_CARD_BY_USER_ID_JPQL = """
            SELECT COUNT(p)
            FROM PaymentCard p
            WHERE p.user.id = :userId AND p.active = true
            """;

    private PaymentCardQuery() {}
}
