/**
 * An interface for a generic subscriber
 * @param T the type of the topic
 * @param C the type consuming logic
 * Created by Matteo Gabellini on 29/01/2018.
 */
interface Subscriber<T, C> {
    /**
     * The function to subscribe to the specified topic
     *
     * @param topic - the topic to subscribe
     * @param consumingLogic - the consuming logic applied when a message is received
     */
    fun subscribe(topic: T, consumingLogic: C)

    /**
     * The function to revoke a subscription
     * @param topic - the topic to unsubscribe
     */
    fun unsubscribe(topic: T)

    /**
     * The function to get a set of subscribed topic
     *
     * In order to change a subscription to a topic use subscribe/unsubscribe methods
     *
     * @return A set containing all the subscribed topic
     */
    fun subscribedTopics(): Set<T>
}