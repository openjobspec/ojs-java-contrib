package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

/**
 * Enqueues OJS jobs after the current Spring {@code @Transactional} block commits.
 *
 * <p>If no transaction is active, the job is enqueued immediately.
 *
 * <pre>{@code
 * @Service
 * public class OrderService {
 *     private final OjsTransactionalEnqueue enqueue;
 *
 *     @Transactional
 *     public void createOrder(Order order) {
 *         orderRepo.save(order);
 *         enqueue.afterCommit("order.process", Map.of("orderId", order.id()));
 *     }
 * }
 * }</pre>
 */
public class OjsTransactionalEnqueue {

    private final OJSClient client;

    public OjsTransactionalEnqueue(OJSClient client) {
        this.client = client;
    }

    /**
     * Enqueue a job after the current transaction commits.
     * If no transaction is active, enqueues immediately.
     *
     * @param type the job type
     * @param args the job arguments
     */
    public void afterCommit(String type, Map<String, Object> args) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            client.enqueue(type, args);
                        }
                    });
        } else {
            client.enqueue(type, args);
        }
    }
}
