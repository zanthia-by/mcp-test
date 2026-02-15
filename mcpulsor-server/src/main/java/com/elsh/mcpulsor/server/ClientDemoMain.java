package com.elsh.mcpulsor.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

public class ClientDemoMain {

    public static void main(String[] args) {
        HttpClientStreamableHttpTransport clientTransport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8091")
                .endpoint("/mcpulsor")
                .build();

        McpSyncClient client = McpClient.sync(clientTransport).build();

        client.initialize();
        client.listTools()
                .tools()
                .forEach(System.out::println);
        client.callTool(McpSchema.CallToolRequest.builder().name("bioSensor").build())
                .content()
                .forEach(System.out::println);

    }
}
