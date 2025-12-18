package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

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
            this.fromState = state.getTitle();
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

    public HashMap<StateTrigger, AnimationState> stateTransfers = new HashMap<>();
    private List<AnimationState> spriteStates = new ArrayList<>();
    private List<AnimationState> colorStates = new ArrayList<>();
    private transient AnimationState currentSpriteState = null;
    private transient AnimationState currentColorState = null;
    private transient HashMap<Integer, AnimationState[]> compositeSpriteIndexToAnimation = new HashMap<>();
    private transient boolean colorAnimation, spriteAnimation;

    private String defaultSpriteStateTitle = "";
    private String defaultColorStateTitle = "";

    public void refreshTextures() {
        for (AnimationState state : colorStates) {
            state.refreshTextures();
        }
        for (AnimationState state : spriteStates) {
            state.refreshTextures();
        }
    }

    private void setDefaultSpriteState(String animationTitle) {
        for (AnimationState state : spriteStates) {
            if (state.getTitle().equals(animationTitle)) {
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
            if (state.getTitle().equals(animationTitle)) {
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
        setDefaultSpriteState(spriteState.getTitle());
        setDefaultColorState(colorState.getTitle());
    }

    public void addState(AnimationState state) {
        if (state.isSpriteAnimation()) {
            for (AnimationState addedState : spriteStates) {
                assert !addedState.getTitle().equals(state.getTitle()) : "State already added";
            }
            this.spriteStates.add(state);
        }
        if (state.isColorAnimation()) {
            for (AnimationState addedState : colorStates) {
                assert !addedState.getTitle().equals(state.getTitle()) : "State already added";
            }
            this.colorStates.add(state);
        }
        state.refreshTextures();
    }

    public void addTrigger(AnimationState from, AnimationState to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addTrigger(AnimationState from, AnimationState to, String onTrigger, int compositeSpriteIndex) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger, compositeSpriteIndex), to);
    }

    public void trigger(String trigger, int compositeSpriteIndex) {
        Optional<AnimationState[]> newState = trigger(trigger);
        newState.ifPresent(state -> compositeSpriteIndexToAnimation.put(compositeSpriteIndex, state));
    }

    public Optional<AnimationState[]> trigger(String trigger) {
        AnimationState newColorState = currentColorState;
        AnimationState newSpriteState = currentSpriteState;
        for (Entry<StateTrigger, AnimationState> entrySet : stateTransfers.entrySet()) {
            StateTrigger stateTrigger = entrySet.getKey();

            if (stateTrigger.trigger.equals(trigger)) {
                AnimationState state = entrySet.getValue();
                if (state.isColorAnimation() && stateTrigger.isColorTrigger
                        && stateTrigger.fromState.equals(currentColorState.getTitle())) {
                    newColorState = state;
                    colorAnimation = true;
                }
                if (state.isSpriteAnimation() && stateTrigger.isSpriteTrigger
                        && stateTrigger.fromState.equals(currentSpriteState.getTitle())) {
                    newSpriteState = state;
                    spriteAnimation = true;
                }
                if (stateTrigger.fromState.equals(currentColorState.getTitle())
                        || stateTrigger.fromState.equals(currentSpriteState.getTitle())) {
                    break;
                }
            }
        }
        if (colorAnimation) {
            currentColorState = newColorState;
        }
        if (spriteAnimation) {
            currentSpriteState = newSpriteState;
        }
        AnimationState[] newState = { currentColorState, currentSpriteState };
        Optional<AnimationState[]> optionalState = Optional.of(newState);
        return optionalState;
    }

    @Override
    public void start() {
        for (AnimationState state : spriteStates) {
            if (state.getTitle().equals(defaultSpriteStateTitle)) {
                currentSpriteState = state;
                break;
            }
        }
        for (AnimationState state : colorStates) {
            if (state.getTitle().equals(defaultColorStateTitle)) {
                currentColorState = state;
                break;
            }
        }
    }

    private void updateComposite(float dt) {
        CompositeSpriteRenderer compSprite = gameObject.getComponent(CompositeSpriteRenderer.class);
        if (compSprite != null) {
            boolean[] isFinished = new boolean[compositeSpriteIndexToAnimation.size()];
            Integer[] keys = compositeSpriteIndexToAnimation.keySet().toArray(new Integer[isFinished.length]);
            int i = 0;
            for (int index : keys) {
                AnimationState[] animationState = compositeSpriteIndexToAnimation.get(index);
                AnimationState colorState = animationState[0];
                AnimationState spriteState = animationState[1];
                isFinished[i] = updateStates(dt, colorState, spriteState);
                i++;
            }
            for (int j = 0; j < keys.length; j++) {
                if (isFinished[j]) {
                    compositeSpriteIndexToAnimation.remove(keys[j]);
                }
            }
        }
    }

    private void updateRegular(float dt) {
        if (currentSpriteState != null || currentColorState != null) {
            updateStates(dt, currentColorState, currentSpriteState);
        }
    }

    private boolean updateStates(float dt, AnimationState colorState, AnimationState spriteState) {
        SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
        if (colorState != null) {
            if (colorState != null && this.colorAnimation) {
                this.colorAnimation = !colorState.update(dt);
                if (sprite != null) {
                    sprite.setColor(colorState.getCurrentColor());
                }
            }
        }
        if (spriteState != null) {
            this.spriteAnimation = !spriteState.update(dt);
            if (sprite != null && this.spriteAnimation) {
                // TODO Why do we need to set texture when the sprite already contains the
                // texture?
                sprite.setSprite(spriteState.getCurrentSprite());
                // sprite.setTexture(spriteState.getCurrentSprite().getTexture());
            }
        }
        return !(spriteAnimation || colorAnimation);
    }

    @Override
    public void update(float dt) {
        // TODO should these happen at the same time ever?
        if (!compositeSpriteIndexToAnimation.isEmpty()) {
            updateComposite(dt);
        } else if (colorAnimation || spriteAnimation) {
            updateRegular(dt);
        }
    }
}
