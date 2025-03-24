package benchmark;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static glide.api.logging.Logger.Level.ERROR;
import static glide.api.logging.Logger.log;

import glide.api.GlideClient;
import glide.api.GlideClusterClient;
import glide.api.models.configuration.GlideClientConfiguration;
import glide.api.models.configuration.GlideClusterClientConfiguration;
import glide.api.models.configuration.NodeAddress;
import glide.api.models.configuration.ReadFrom;

/**
 * Provides client implementations and factory methods for benchmark operations.
 * This class manages the creation and configuration of both standalone and cluster
 * mode clients for the Valkey benchmark suite.
 * 
 * Features:
 * - Unified interface for benchmark operations
 * - Support for standalone and cluster modes
 * - Configurable client settings
 * - Error handling and logging
 */
public class ValkeyBenchmarkClients {
    
    /**
     * Interface defining the basic operations that can be performed by benchmark clients.
     * Provides a common abstraction layer for different client implementations.
     */
    public interface BenchmarkClient {
        /**
         * Performs a SET operation.
         * 
         * @param key The key to set
         * @param value The value to set
         * @return Result of the SET operation ("OK" if successful)
         * @throws ExecutionException If the operation fails
         * @throws InterruptedException If the operation is interrupted
         */
        String set(String key, String value) throws ExecutionException, InterruptedException;

        /**
         * Performs a GET operation.
         * 
         * @param key The key to retrieve
         * @return The value associated with the key, or null if not found
         * @throws ExecutionException If the operation fails
         * @throws InterruptedException If the operation is interrupted
         */
        String get(String key) throws ExecutionException, InterruptedException;
      
    }

    /**
     * Implementation of BenchmarkClient for standalone mode operations.
     * Wraps a GlideClient instance and provides synchronous operation execution.
     */
    static class StandaloneBenchmarkClient implements BenchmarkClient {
        private final GlideClient client;

        /**
         * Creates a new standalone benchmark client.
         * 
         * @param client The GlideClient instance to wrap
         */
        public StandaloneBenchmarkClient(GlideClient client) {
            this.client = client;
        }

        @Override
        public String set(String key, String value) throws ExecutionException, InterruptedException {
            return client.set(key, value).get();
        }

        @Override
        public String get(String key) throws ExecutionException, InterruptedException {
            return client.get(key).get();
        }

        /**
         * Gets the underlying GlideClient instance.
         * 
         * @return The wrapped GlideClient
         */
        GlideClient getClient() {
            return this.client;
        }
    }

    /**
     * Implementation of BenchmarkClient for cluster mode operations.
     * Wraps a GlideClusterClient instance and provides synchronous operation execution.
     */
    static class ClusterBenchmarkClient implements BenchmarkClient {
        private final GlideClusterClient client;

        /**
         * Creates a new cluster benchmark client.
         * 
         * @param client The GlideClusterClient instance to wrap
         */
        public ClusterBenchmarkClient(GlideClusterClient client) {
            this.client = client;
        }

        @Override
        public String set(String key, String value) throws ExecutionException, InterruptedException {
            return client.set(key, value).get();
        }

        @Override
        public String get(String key) throws ExecutionException, InterruptedException {
            return client.get(key).get();
        }

  
        /**
         * Gets the underlying GlideClusterClient instance.
         * 
         * @return The wrapped GlideClusterClient
         */
        GlideClusterClient getClusterClient() {
            return this.client;
        }
    }

    /**
     * Creates a standalone client with the specified configuration.
     * 
     * @param nodeList List of nodes to connect to
     * @param config Global benchmark configuration
     * @return Configured standalone benchmark client
     * @throws CancellationException If client creation is cancelled
     * @throws ExecutionException If client creation fails
     * @throws InterruptedException If the operation is interrupted
     */
    public static BenchmarkClient createStandaloneClient(List<NodeAddress> nodeList, ValkeyBenchmarkConfig config)
            throws CancellationException, ExecutionException, InterruptedException {
        GlideClientConfiguration clientConfig =
                GlideClientConfiguration.builder()
                        .addresses(nodeList)
                        .readFrom(config.isReadFromReplica() ? ReadFrom.PREFER_REPLICA : ReadFrom.PRIMARY)
                        .clientAZ("AZ1")
                        .useTLS(config.isUseTls())
                        .build();
        try {
            return new StandaloneBenchmarkClient(GlideClient.createClient(clientConfig).get());
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            log(ERROR, "glide", "Client creation error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates a cluster client with the specified configuration.
     * 
     * @param nodeList List of nodes to connect to
     * @param config Global benchmark configuration
     * @return Configured cluster benchmark client
     * @throws CancellationException If client creation is cancelled
     * @throws ExecutionException If client creation fails
     * @throws InterruptedException If the operation is interrupted
     */
    public static BenchmarkClient createClusterClient(List<NodeAddress> nodeList, ValkeyBenchmarkConfig config)
            throws CancellationException, ExecutionException, InterruptedException {
        GlideClusterClientConfiguration clientConfig =
            GlideClusterClientConfiguration.builder()
                        .addresses(nodeList)
                        .readFrom(config.isReadFromReplica() ? ReadFrom.PREFER_REPLICA : ReadFrom.PRIMARY)
                        .clientAZ("AZ1")
                        .useTLS(config.isUseTls())
                        .build();
        try {
            return new ClusterBenchmarkClient(GlideClusterClient.createClient(clientConfig).get());
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            log(ERROR, "glide", "Client creation error: " + e.getMessage());
            throw e;
        }
    }
}
