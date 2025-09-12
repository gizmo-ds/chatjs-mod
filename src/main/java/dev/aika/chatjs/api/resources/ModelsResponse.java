package dev.aika.chatjs.api.resources;

import java.util.List;

public record ModelsResponse(String object, List<ModelObject> data) {
}
