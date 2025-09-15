package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import secondEngine.Component;
import secondEngine.components.helpers.AnimationState;

public class AnimationStateMachine extends Component {
    private class StateTrigger {
        // TODO move to enums
        public String fromState;
        public String trigger;
        public int compositeSpriteIndex = -1;
        public boolean isColorTrigger = false;
        public boolean isSpriteTrigger = false;

        public StateTrigger(AnimationState state, String trigger) {
            this.fromState = state.title;
            this.isColorTrigger = state.isColorAnimation();
            this.isSpriteTrigger = state.isSpriteAnimation();
            this.trigger = trigger;
        }

        public StateTrigger(AnimationState state, String trigger, int index) {
            this(state, trigger);
            this.compositeSpriteIndex = index;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger) o;
            return t2.trigger.equals(this.trigger) && t2.fromState.equals(this.fromState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromState, trigger, compositeSpriteIndex);
        }
    }

    public HashMap<StateTrigger, AnimationState> stateTransfers = new HashMap<>();
    private List<AnimationState> spriteStates = new ArrayList<>();
    private List<AnimationState> colorStates = new ArrayList<>();
    private transient AnimationState currentSpriteState = null;
    private transient AnimationState currentColorState = null;
    private transient HashMap<Integer, AnimationState> compositeSpriteIndexToAnimation = new HashMap<>();

    private String defaultSpriteStateTitle = "";
    private String defaultColorStateTitle = "";

    public void refreshTextures() {
        for (AnimationState state : colorStates) {
            state.refreshTextures();
        }
    }

    private void setDefaultSpriteState(String animationTitle) {
        for (AnimationState state : spriteStates) {
            if (state.title.equals(animationTitle)) {
                defaultSpriteStateTitle = animationTitle;
                if (currentSpriteState == null) {
                    currentSpriteState = state;
                }
                return;
            }
        }

        System.out.println("Unable to find default sprite state '" + animationTitle + "'");
    }

    private void setDefaultColorState(String animationTitle) {
        for (AnimationState state : colorStates) {
            if (state.title.equals(animationTitle)) {
                defaultColorStateTitle = animationTitle;
                if (currentColorState == null) {
                    currentColorState = state;
                }
                return;
            }
        }

        System.out.println("Unable to find default color state '" + animationTitle + "'");
    }

    public void setDefaultState(AnimationState spriteState, AnimationState colorState) {
        setDefaultSpriteState(spriteState.title);
        setDefaultColorState(colorState.title);
    }

    public void addState(AnimationState state) {
        if (state.isSpriteAnimation()) this.spriteStates.add(state);
        if (state.isColorAnimation()) this.colorStates.add(state);
        state.refreshTextures();
    }

    public void addTrigger(AnimationState from, AnimationState to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addTrigger(AnimationState from, AnimationState to, String onTrigger, int compositeSpriteIndex) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger, compositeSpriteIndex), to);
    }

    public void trigger(String trigger, int compositeSpriteIndex) {
        Optional<AnimationState> newState = trigger(trigger);
        newState.ifPresent(state -> compositeSpriteIndexToAnimation.put(compositeSpriteIndex, state));
    }

    public Optional<AnimationState> trigger(String trigger) {
        Optional<AnimationState> optionalState = Optional.empty();
        for (Entry<StateTrigger, AnimationState> entrySet: stateTransfers.entrySet()) {
            StateTrigger stateTrigger = entrySet.getKey();
            if (((stateTrigger.isColorTrigger && stateTrigger.fromState.equals(currentColorState.title)) || (stateTrigger.isSpriteTrigger && stateTrigger.fromState.equals(currentSpriteState.title))) && stateTrigger.trigger.equals(trigger)) {
                AnimationState state = entrySet.getValue();
                if (state.isSpriteAnimation()) currentSpriteState = state;
                if (state.isColorAnimation()) currentColorState = state;
                optionalState = Optional.of(state);
            }
        }
        return optionalState;
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : colorStates) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    @Override
    public void start() {
        for (AnimationState state : spriteStates) {
            if (state.title.equals(defaultSpriteStateTitle)) {
                currentSpriteState = state;
                break;
            }
        }
        for (AnimationState state : colorStates) {
            if (state.title.equals(defaultColorStateTitle)) {
                currentColorState = state;
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
        if (currentSpriteState != null || currentColorState != null) {
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (currentSpriteState != null) {
                currentSpriteState.update(dt);
                if (sprite != null) {
                    
                    // TODO Why do we need to set texture when the sprite already contains the
                    // texture?
                    sprite.setSprite(currentSpriteState.getCurrentSprite());
                    sprite.setTexture(currentSpriteState.getCurrentSprite().getTexture());
                }
            }
            if (currentColorState != null) {
                currentColorState.update(dt);
                if (sprite != null) {
                    sprite.setColor(currentColorState.getCurrentColor());
                }
            }
            
        }
    }

}
