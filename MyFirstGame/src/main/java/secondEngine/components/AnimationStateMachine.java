package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import secondEngine.Component;
import secondEngine.components.helpers.AnimationState;

public class AnimationStateMachine extends Component {
    private class StateTrigger {
        // TODO move to enums
        public String fromState;
        public String trigger;
        public int compositeSpriteIndex = -1;

        public StateTrigger(String state, String trigger) {
            this.fromState = state;
            this.trigger = trigger;
        }

        public StateTrigger(String state, String trigger, int index) {
            this(state, trigger);
            this.compositeSpriteIndex = index;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class)
                return false;
            StateTrigger t2 = (StateTrigger) o;
            return t2.trigger.equals(this.trigger) && t2.fromState.equals(this.fromState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromState, trigger, compositeSpriteIndex);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private transient HashMap<Integer, AnimationState> compositeSpriteIndexToAnimation = new HashMap<>();

    private String defaultStateTitle = "";

    public void refreshTextures() {
        for (AnimationState state : states) {
            state.refreshTextures();
        }
    }

    public void setDefaultState(String animationTitle) {
        for (AnimationState state : states) {
            if (state.title.equals(animationTitle)) {
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = state;
                }
                return;
            }
        }

        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    public void addState(AnimationState state) {
        this.states.add(state);
        state.refreshTextures();
    }

    public void addTrigger(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addTrigger(String from, String to, String onTrigger, int compositeSpriteIndex) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger, compositeSpriteIndex), to);
    }

    public void trigger(String trigger, int compositeSpriteIndex) {
        AnimationState newState = trigger(trigger);
        compositeSpriteIndexToAnimation.put(compositeSpriteIndex, newState);
    }

    public AnimationState trigger(String trigger) {
        // Go through all triggers
        for (StateTrigger state : stateTransfers.keySet()) {
            // Checks that the trigger is applicable/possible to/for the current state
            if (state.fromState.equals(currentState.title) && state.trigger.equals(trigger)) {
                if (stateTransfers.get(state) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(state));
                    // checks if the new state is available
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                        return currentState;
                    }
                }
                break;
            }
        }
        return null;
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (!compositeSpriteIndexToAnimation.isEmpty()) {
            CompositeSpriteRenderer compSprite = gameObject.getComponent(CompositeSpriteRenderer.class);
            if (compSprite != null) {
                boolean[] isFinished = new boolean[compositeSpriteIndexToAnimation.size()];
                Integer[] keys = compositeSpriteIndexToAnimation.keySet().toArray(new Integer[isFinished.length]);
                int i = 0;
                for (int index : keys) {
                    AnimationState animationState = compositeSpriteIndexToAnimation.get(index);
                    boolean finished = animationState.update(dt);
                    isFinished[i] = finished;
                    i++;
                    SpriteRenderer sprite = compSprite.getSpriteRenderer(index);
                    sprite.setSprite(animationState.getCurrentSprite());
                    // TODO Why do we need to set texture when the sprite already contains the
                    // texture?
                    sprite.setTexture(animationState.getCurrentSprite().getTexture());
                    sprite.setColor(animationState.getCurrentColor());
                }
                i = 0;
                for (int index : keys) {
                    if (isFinished[i]) {
                        compositeSpriteIndexToAnimation.remove(index);
                    }
                    i++;
                }
            }

        }
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
                // TODO Why do we need to set texture when the sprite already contains the
                // texture?
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
                sprite.setColor(currentState.getCurrentColor());
            }
        }
    }

}
