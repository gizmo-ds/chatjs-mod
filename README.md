# Chat.JS

[![License](https://img.shields.io/github/license/gizmo-ds/chatjs-mod?style=flat-square)](./LICENSE.txt)

Access large language models from KubeJS using an OpenAI-compatible API.

## Config

Edit the `chatjs.server.toml` file to change the OpenAI compatible API you are using.

Use the `/chatjs set_apikey <your API Key>` command to set your API Key (requires OP permission).

If everything is set up correctly, executing `/chatjs models` can list all available models (requires OP permission).

## Example

### Death message

Send a message after the player dies.

[example/server_scripts/death_message](example/server_scripts/death_message)

### Function Calling

Enter `@ai <What you want to say to AI>` in Chat, the AI will execute game commands based on what you enter.

For example `@ai Kill all slimes`.

[example/server_scripts/function_calling](example/server_scripts/function_calling)

## License

This mod is distributed under [LGPL-3.0 license](https://github.com/gizmo-ds/chatjs-mod/blob/1.20.1/LICENSE.txt)

> [!WARNING]    
> The mod files (.jar files) published on Modrinth and CurseForge contain icon files under an ARR (All Rights Reserved)
> license. You are not allowed to redistribute these files elsewhere. If you need to modify the mod, you can compile it
> yourself from the source code (the default compilation does not include ARR-licensed content).
