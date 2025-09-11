const tools = [
    {
        type: "function",
        function: {
            description: "执行minecraft游戏指令",
            name: "exec_command",
            parameters: {
                type: "object",
                properties: {
                    command: { type: "string", description: "需要执行的minecraft指令" },
                },
                required: ["command"],
            },
        },
    },
];

function send_messages(msgs) {
    let result = openai.chat.createCompletion(JSON.stringify({ messages: msgs, tools: tools, tool_choice: "auto" }));
    result = JSON.parse(JsonIO.toString(result));
    return result.choices[0].message;
}

PlayerEvents.chat((e) => {
    if (!e.message.toLowerCase().startsWith("@ai ")) return;
    const msg = e.message.substring(4);
    const messages = [{ role: "user", content: `${e.getUsername()} Say: ${msg}` }];

    let message = send_messages(messages);
    const tool_calls = message.tool_calls;
    if (message.content !== "") {
        e.server.scheduleInTicks(1, (_) => e.server.tell(Text.of("§bAI Say: §f" + message.content)));
    } else if (!tool_calls && message.content === "") {
        e.server.scheduleInTicks(1, (_) => e.server.tell(Text.of("Failed!")));
    }

    if (tool_calls && tool_calls.length > 0) {
        messages.push(message);
        for (const tool of tool_calls) {
            switch (tool.function.name) {
                case "exec_command": {
                    messages.push({ role: "tool", tool_call_id: tool.id, content: "success" });
                    var args = JSON.parse(tool.function.arguments);
                    e.server.runCommand(args.command);
                    console.log(`AI exec: ${args.command}`);
                    break;
                }
                default:
                    console.warn(`Unknown function: ${tool.function.name} ${tool.function.arguments}`);
                    return e.cancel();
            }
        }
        message = send_messages(messages);
        if (message.content !== "") {
            e.server.scheduleInTicks(1, (_) => e.server.tell(Text.of("§bAI Say: §f" + message.content)));
        }
    }
});
