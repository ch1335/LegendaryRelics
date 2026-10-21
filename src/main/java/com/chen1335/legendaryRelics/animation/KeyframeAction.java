package com.chen1335.legendaryRelics.animation;

import java.util.function.Consumer;

public abstract class KeyframeAction {
    public boolean trigger = false;
    abstract void trigger(PlayerAnimationState state);

    public void rest(){
        trigger = false;
    }

    public static class Run extends KeyframeAction {
        private final Consumer<PlayerAnimationState> consumer;

        public Run(Consumer<PlayerAnimationState> consumer) {
            this.consumer = consumer;
        }

        @Override
        void trigger(PlayerAnimationState state) {
            consumer.accept(state);
        }
    }
}
