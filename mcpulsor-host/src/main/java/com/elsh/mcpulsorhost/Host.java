package com.elsh.mcpulsorhost;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Host {

    @Autowired
    private ChatClient chatClient;

    private String systemPrompt;
    private McpSyncClient client;

    @PostConstruct
    public void init() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8091")
                .endpoint("/mcpulsor")
                .build();
        client = McpClient.sync(transport).build();
        client.initialize();
        McpSchema.ListToolsResult toolsResult = client.listTools();
        systemPrompt = SystemPromptFactory.withTools(toolsResult);
    }

    public void printAnswerToUser(String question) {
        AssistantMessage assistantMessage = chatClient
                .prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .chatResponse()
                .getResult()
                .getOutput();

        if (CallToolUtil.isToolRequired(assistantMessage.getText())) {
            McpSchema.CallToolRequest callToolRequest = CallToolUtil.getRequiredTool(assistantMessage.getText());
            String toolResponse = CallToolUtil.wrapResponse(client.callTool(callToolRequest).content().getFirst().toString());

            UserMessage userMessage = new UserMessage(question);
            UserMessage toolMessage = new UserMessage(toolResponse);

            assistantMessage = chatClient
                    .prompt()
                    .system(systemPrompt)
                    .messages(List.of(
                            userMessage,
                            assistantMessage,
                            toolMessage
                    ))
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput();

        }
        System.out.println(assistantMessage.getText());
    }

}
