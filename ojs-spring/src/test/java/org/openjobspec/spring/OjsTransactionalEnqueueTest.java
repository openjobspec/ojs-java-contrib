package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsTransactionalEnqueueTest {

    @Mock
    OJSClient client;

    @Test
    void enqueuesImmediatelyWithoutTransaction() {
        var enqueue = new OjsTransactionalEnqueue(client);
        enqueue.afterCommit("email.send", Map.of("to", "user@example.com"));
        verify(client).enqueue("email.send", Map.of("to", "user@example.com"));
    }

    @Test
    void defersEnqueueUntilTransactionCommit() {
        var enqueue = new OjsTransactionalEnqueue(client);

        TransactionSynchronizationManager.initSynchronization();
        try {
            enqueue.afterCommit("email.send", Map.of("to", "user@example.com"));

            // Not yet enqueued
            verify(client, never()).enqueue(anyString(), anyMap());

            // Simulate commit
            var syncs = TransactionSynchronizationManager.getSynchronizations();
            assertEquals(1, syncs.size());
            syncs.getFirst().afterCommit();

            verify(client).enqueue("email.send", Map.of("to", "user@example.com"));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
