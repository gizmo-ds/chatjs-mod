EntityEvents.death((event) => {
  const entity = event.entity;
  if (entity.type.toString() !== "minecraft:player") return;

  const playerName = entity.getGameProfile().getName();
  const deathMessage = event.source.getLocalizedDeathMessage(entity).getString();

  const messages = [
    {
      role: "user",
      content:
        `玩家"${playerName}"在Minecraft中死亡了,游戏内的提示消息是"${deathMessage}",请你模仿鲁迅的风格写一句话嘲讽他.` +
        `你需要回复我JSON格式的内容,比如: {"content": "惟沉默是最高的轻蔑。——鲁迅"}`,
    },
  ];

  setTimeout(() => {
    let result = openai.chat.createCompletion(
      JSON.stringify({
        messages: messages,
        response_format: { type: "json_object" },
      })
    );
    result = JSON.parse(JsonIO.toString(result));
    const msg = JSON.parse(result.choices[0].message.content);
    entity.tell(Text.green(msg.content));
  }, 100);
});
