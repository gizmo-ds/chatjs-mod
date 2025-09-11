# Chat.JS

[![License](https://img.shields.io/github/license/gizmo-ds/chatjs-mod?style=flat-square)](./LICENSE.txt)

Access large language models from KubeJS using an OpenAI-compatible API.

## Config

Edit the `chatjs.server.toml` file to change the OpenAI compatible API you are using.

Use the `/chatjs set_apikey <your API Key>` command to set your API Key (requires OP permission).

If everything is set up correctly, executing `/chatjs models` can list all available models (requires OP permission).

## Example

### Function Calling

Enter `@ai <What you want to say to AI>` in Chat, the AI will execute game commands based on what you enter.

For example `@ai Kill all slimes`.

<details>
<summary>Click to expand</summary>

```javascript
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
```

</details>

## License

This mod is distributed under [LGPL-3.0 license](https://github.com/gizmo-ds/chatjs-mod/blob/1.20.1/LICENSE.txt)

> [!WARNING]    
> The mod files (.jar files) published on Modrinth and CurseForge contain icon files under an ARR (All Rights Reserved)
> license. You are not allowed to redistribute these files elsewhere. If you need to modify the mod, you can compile it
> yourself from the source code (the default compilation does not include ARR-licensed content).
