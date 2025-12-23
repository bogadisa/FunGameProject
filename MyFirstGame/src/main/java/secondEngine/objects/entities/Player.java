package secondEngine.objects.entities;

import org.joml.Vector4f;

import secondEngine.components.PlayerControls;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.helpers.AnimationState;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.util.AssetPool;

public class Player {
    public static GameObject generate() {
        SpriteSheet playerSprites = AssetPool.getSpriteSheet("entities/player_3_3_9.png");
        GameObject player = SpriteObject.generate(playerSprites.getSprite(0), 64, 96);
        player.transform.position.add(64, 80, 0);
        player.setName("Player");

        float defaultFrameTime = 5.0f;

        AnimationState runDown = new AnimationState().init("Run Down");
        runDown.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runDown.setLoop(true);

        AnimationState runSideways = new AnimationState().init("Run Sideways");
        runSideways.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(4), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(5), defaultFrameTime);
        runSideways.setLoop(true);

        AnimationState runUp = new AnimationState().init("Run Up");
        runUp.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(7), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(8), defaultFrameTime);
        runUp.setLoop(true);

        AnimationState idling = new AnimationState().init("Idle");
        idling.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        idling.setLoop(false);

        AnimationState addColor = new AnimationState().init("AddColor");
        addColor.addFrame(new Vector4f(1), defaultFrameTime);

        AnimationState removeColor = new AnimationState().init("RemoveColor");
        removeColor.addFrame(new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), defaultFrameTime);

        AnimationStateMachine stateMachine = new AnimationStateMachine();

        stateMachine.addState(runDown);
        stateMachine.addState(runUp);
        stateMachine.addState(runSideways);
        stateMachine.addState(idling);
        stateMachine.addState(addColor);
        stateMachine.addState(removeColor);

        stateMachine.setDefaultState(idling, addColor);

        stateMachine.addTrigger(idling, runUp, "runUp");
        stateMachine.addTrigger(runDown, runUp, "runUp");
        stateMachine.addTrigger(runSideways, runUp, "runUp");

        stateMachine.addTrigger(idling, runDown, "runDown");
        stateMachine.addTrigger(runUp, runDown, "runDown");
        stateMachine.addTrigger(runSideways, runDown, "runDown");

        stateMachine.addTrigger(idling, runSideways, "runSideways");
        stateMachine.addTrigger(runDown, runSideways, "runSideways");
        stateMachine.addTrigger(runUp, runSideways, "runSideways");

        stateMachine.addTrigger(runSideways, idling, "idle");
        stateMachine.addTrigger(runDown, idling, "idle");
        stateMachine.addTrigger(runUp, idling, "idle");

        stateMachine.addTrigger(removeColor, addColor, "toggleColor");
        stateMachine.addTrigger(addColor, removeColor, "toggleColor");

        player.addComponent(stateMachine);

        PlayerControls controls = new PlayerControls();
        controls.setPlayerWidth(player.transform.scale.x);
        player.addComponent(controls);

        return player;
    }
}
