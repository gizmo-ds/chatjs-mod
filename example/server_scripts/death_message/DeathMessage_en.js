EntityEvents.death((event) => {
  const entity = event.entity;
  if (entity.type.toString() !== "minecraft:player") return;

  const playerName = entity.getGameProfile().getName();
  const deathMessage = event.source.getLocalizedDeathMessage(entity).getString();

  const messages = [
    {
      role: "user",
      content:
        `The player "${playerName}" has died in Minecraft, and the in-game death message is "${deathMessage}". Please write a mocking sentence in the style of Nietzsche.` +
        `You need to reply in JSON format, for example: {"content": "He who fights with monsters should look to it that he himself does not become a monster. — Nietzsche"}`,
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
