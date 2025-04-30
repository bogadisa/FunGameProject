package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import secondEngine.Component;
import secondEngine.components.helpers.AnimationState;

public class StateMachine extends Component {
    private class StateTrigger {
        public String fromState;
        public String trigger;

        public StateTrigger(String state, String trigger) {
            this.fromState = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) && t2.fromState.equals(this.fromState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromState, trigger);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;

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

    public void addState(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void trigger(String trigger) {
        // Go through all triggers
        for (StateTrigger state : stateTransfers.keySet()) {
            // Checks that the trigger is applicable/possible to/for the current state
            if (state.fromState.equals(currentState.title) && state.trigger.equals(trigger)) {
                if (stateTransfers.get(state) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(state));
                    // checks if the new state is available
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
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
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
                // TODO Why do we need to set texture when the sprite already contains the texture?
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }


}
