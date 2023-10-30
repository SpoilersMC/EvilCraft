package evilcraft.core.degradation;

import java.util.Random;

import evilcraft.api.degradation.IDegradable;
import evilcraft.api.degradation.IDegradationEffect;
import evilcraft.core.config.configurable.ConfigurableDegradationEffect;
import evilcraft.core.config.extendedconfig.DegradationEffectConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;

/**
 * A {@link IDegradationEffect} that can be executed with a certain chance.
 * It will take into account the current degradation factor and the higher this is, the higher the chance of the execution of this effect.
 * An optional chance can also be given for a chance next to the degradation chance.
 * @author rubensworks
 */
public abstract class StochasticDegradationEffect extends ConfigurableDegradationEffect {

    private double chance;

    /**
     * Make a new instance.
     * @param eConfig The config.
     * @param chance The chance on occurring. A value between 0 and 1.
     */
    public StochasticDegradationEffect(ExtendedConfig<DegradationEffectConfig> eConfig, double chance) {
        super(eConfig);
        this.chance = chance;
    }

    /**
     * Make a new instance.
     * @param eConfig The config.
     */
    public StochasticDegradationEffect(ExtendedConfig<DegradationEffectConfig> eConfig) {
        this(eConfig, 1.0D);
    }

    @Override
    public boolean canRun(IDegradable degradable) {
        Random random = degradable.getWorld().rand;
        return degradable.getDegradation() * getChance() > random.nextDouble();
    }

    /**
     * Get the configured chance on occurring.
     * @return The chance.
     */
    public double getChance() {
        return this.chance;
    }
}