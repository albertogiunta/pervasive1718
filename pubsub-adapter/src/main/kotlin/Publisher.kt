/**
 * An interface for a generic publisher
 * @param X the message type published
 * @param T the type of topics where the publisher sends messages
 * Created by Matteo Gabellini on 29/01/2018.
 */
interface Publisher<X, T> {
    /**
     * The function that publish a message on the specified topic
     * @param message the message published on the topic
     * @param topic the topic where the message is published
     */
    fun publish(message: X, topic: T)
}