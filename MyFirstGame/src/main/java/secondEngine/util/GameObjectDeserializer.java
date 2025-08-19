package secondEngine.util;

import com.google.gson.*;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name, Window.incrementAndGetCurrentObjectId());
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }

        go.transform = go.getComponent(Transform.class);
        return go;
    }
}