package secondEngine.objects.entities;

import secondEngine.components.PlayerControls;
import secondEngine.components.StateMachine;
import secondEngine.components.helpers.AnimationState;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.util.AssetPool;

public class Player{
    public static GameObject generate() {
        SpriteSheet playerSprites = AssetPool.getSpriteSheet("entities/player_3_3_9.png");
        GameObject player = SpriteObject.generate(playerSprites.getSprite(0), 32, 64);
        player.setName("Player");

        AnimationState runDown = new AnimationState();
        runDown.title = "Run Down";
        float defaultFrameTime = 5.0f;
        runDown.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runDown.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runDown.setLoop(true);
        
        AnimationState runSideways = new AnimationState();
        runSideways.title = "Run Sideways";
        runSideways.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(4), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runSideways.addFrame(playerSprites.getSprite(5), defaultFrameTime);
        runSideways.setLoop(true);

        AnimationState runUp = new AnimationState();
        runUp.title = "Run Up";
        runUp.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(7), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        runUp.addFrame(playerSprites.getSprite(8), defaultFrameTime);
        runUp.setLoop(true);


        AnimationState idling = new AnimationState();
        idling.title = "Idle";
        idling.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        idling.setLoop(false);

        
        // AnimationState runRight = new AnimationState();
        // runRight.title = "Run Right";
        // runRight.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        // runRight.addFrame(playerSprites.getSprite(7), defaultFrameTime);
        // runRight.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(runDown);
        stateMachine.addState(runUp);
        stateMachine.addState(runSideways);
        stateMachine.addState(idling);

        stateMachine.setDefaultState(idling.title);

        stateMachine.addState(idling.title, runUp.title, "runUp");
        stateMachine.addState(runDown.title, runUp.title, "runUp");
        stateMachine.addState(runSideways.title, runUp.title, "runUp");
        
        stateMachine.addState(idling.title, runDown.title, "runDown");
        stateMachine.addState(runUp.title, runDown.title, "runDown");
        stateMachine.addState(runSideways.title, runDown.title, "runDown");
        
        stateMachine.addState(idling.title, runSideways.title, "runSideways");
        stateMachine.addState(runDown.title, runSideways.title, "runSideways");
        stateMachine.addState(runUp.title, runSideways.title, "runSideways");

        stateMachine.addState(runSideways.title, idling.title, "idle");
        stateMachine.addState(runDown.title, idling.title, "idle");
        stateMachine.addState(runUp.title, idling.title, "idle");


        player.addComponent(stateMachine);

        PlayerControls controls = new PlayerControls();
        player.addComponent(controls);

        return player;
    }
}
