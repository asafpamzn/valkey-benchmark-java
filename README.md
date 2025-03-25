# Valkey Benchmark Java

Valkey Benchmark Java is a benchmark tool for Valkey. This tool allows developers to conduct performance testing in Java. It supports various testing scenarios, including throughput testing, latency measurements, and custom command benchmarking.

Key Advantages:

1. **Enhanced Java Developer Experience** Built with native Valkey clients in Java, providing authentic end-to-end performance measurements in your production environment.

2. **Advanced Cluster Testing**: Leverages the [valkey-GLIDE](https://github.com/valkey-io/valkey-glide) client to support:
   - Dynamic cluster topology updates
   - Read replica operations
  
3. **Extensible Command Framework**: 
   - Supports custom commands and complex operations
   - Test Lua scripts and multi-key transactions
   - Benchmark your specific use cases and data patterns

## Installation

1. Clone the repository:
    ```bash
    git clone <repository-url>
    cd valkey-benchmark-java
    ```

2. Build the project:
    ```bash
    ./gradlew build
    ```

## Dependencies

This tool requires the following Java dependencies:
- `glide-api`: Valkey GLIDE client library

These dependencies are managed in the `build.gradle` file.

## Basic Usage

Run a basic benchmark:
```bash
./gradlew :runBenchmark --args="-h localhost -p 6379"
```
Common usage patterns:

```bash
# Run SET benchmark with 50 parallel clients
./gradlew :runBenchmark --args="-c 50 -t set"

# Run GET benchmark with rate limiting
./gradlew :runBenchmark --args="-t get --qps 1000"

# Run benchmark for specific duration
./gradlew :runBenchmark --args="--test-duration 60"

# Run benchmark with sequential keys
./gradlew :runBenchmark --args="--sequential 1000000"

```
## Configuration Options

### Basic Options
- `-h, --host <hostname>`: Server hostname (default: "127.0.0.1")
- `-p, --port <port>`: Server port (default: 6379)
- `-c, --clients <num>`: Number of parallel connections (default: 50)
- `-n, --requests <num>`: Total number of requests (default: 100000)
- `-d, --datasize <bytes>`: Data size for SET operations (default: 3)
- `-t, --type <command>`: Command to benchmark (e.g., SET, GET)

### Advanced Options
- `--threads <num>`: Number of worker threads (default: 1)
- `--test-duration <seconds>`: Run test for specified duration
- `--sequential <keyspace>`: Use sequential keys
- `--random <keyspace>`: Use random keys from keyspace

### Rate Limiting Options
- `--qps <num>`: Limit queries per second
- `--start-qps <num>`: Starting QPS for dynamic rate
- `--end-qps <num>`: Target QPS for dynamic rate
- `--qps-change-interval <seconds>`: Interval for QPS changes
- `--qps-change <num>`: QPS change amount per interval

### Security Options
- `--tls`: Enable TLS connection

### Cluster Options
- `--cluster`: Use cluster client
- `--read-from-replica`: Read from replica nodes


### Throughput Testing
```bash

# Maximum throughput test
./gradlew :runBenchmark --args="-c 100 -n 1000000"

# Rate-limited test
./gradlew :runBenchmark --args="-c 50 --qps 5000"
```

### Latency Testing
```bash
# Low-concurrency latency test
./gradlew :runBenchmark --args="-c 1 -n 10000"

# High-concurrency latency test
./gradlew :runBenchmark --args="-c 200 -n 100000"
```

### Duration-based Testing
```bash
# Run test for 5 minutes
./gradlew :runBenchmark --args="--test-duration 300"
```

### Key Space Testing
```bash
# Sequential keys
./gradlew :runBenchmark --args="--sequential 1000000"

# Random keys
./gradlew :runBenchmark --args="-r 1000000"
```

## Output and Statistics
The benchmark tool provides real-time and final statistics including:

### Real-time Metrics
- Current throughput (requests/second)
- Overall throughput
- Average latency

### Final Report
- Total execution time
- Total requests completed
- Average throughput
- Latency percentiles (P50, P95, P99)
- Min/Max/Avg latencies

Example output:

```plaintext
[+] Total test time: 900.562427576 seconds
[+] Total requests completed: 9535699
[+] Overall throughput: 10588.604085634326 req/s

--- Latency Report (microseconds) ---
  Min: 79 us
  P50: 92 us
  P95: 106 us
  P99: 114 us
  Max: 46385 us
  Avg: 93.63646346219612 us
```

## Custom Commands
To implement custom commands, modify the `CustomCommands` class in the benchmark code:
```java
static class CustomCommand {
    static boolean execute(GlideClient client) {
        // Implement your custom command logic here
        try {
            // Example custom command
            String result = client.set("custom:key", "custom:value").get();
            return "OK".equalsIgnoreCase(result);
        } catch (Exception e) {
            log(ERROR, "glide", "Custom command error: " + e.getMessage());
            return false;
        }
    }
}
```

Run custom command benchmark:

```bash
./gradlew :runBenchmark --args="-t custom"
```

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.
