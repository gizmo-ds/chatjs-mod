package dev.aika.chatjs.api.resources;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class ModelObject {
    public String id;
    public String object = "model";
    public Integer created;
    @SerializedName("owned_by")
    public String ownedBy;
}
