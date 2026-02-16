package com.elsh.mcpulsor.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class ClientDemoMain {

    public static void main(String[] args) {
        HttpClientStreamableHttpTransport clientTransport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8091")
                .endpoint("/mcpulsor")
                .build();

        // Example STDIO transport
//        ServerParameters serverParams = ServerParameters.builder("java").args("-jar /app/app.jar").build();
//        StdioClientTransport stdioTransport = new StdioClientTransport(serverParams, new JacksonMcpJsonMapper(new ObjectMapper()));
//        McpSyncClient client = McpClient.sync(stdioTransport).build();

        McpSyncClient client = McpClient.sync(clientTransport).build();

        client.initialize();
        client.listTools()
                .tools()
                .forEach(System.out::println);
        client.callTool(McpSchema.CallToolRequest
                        .builder()
                        .name("bioSensor")
                        .arguments(Map.of("days", 365))
                        .build())
                .content()
                .forEach(System.out::println);

    }
}
