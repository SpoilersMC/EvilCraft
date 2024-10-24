package evilcraft.core.degradation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import evilcraft.api.degradation.IDegradationEffect;
import evilcraft.api.degradation.IDegradationRegistry;

/**
 * Registry for all the {@link IDegradationEffect}.
 * @author rubensworks
 */
public class DegradationRegistry implements IDegradationRegistry {

    private static final Set<IDegradationEffect> DEGRADATION_EFFECTS = new LinkedHashSet<IDegradationEffect>();
    private static final List<IDegradationEffect> WEIGHTED_LIST = new ArrayList<IDegradationEffect>();
    private static final Random random = new Random();

    @Override
    public void registerDegradationEffect(String nameID, IDegradationEffect degradationEffect, int weight) {
        DEGRADATION_EFFECTS.add(degradationEffect);
        for(int i = 0; i < weight; i++) {
            WEIGHTED_LIST.add(degradationEffect);
        }
    }

    @Override
    public Set<IDegradationEffect> getDegradationEffects() {
        return DEGRADATION_EFFECTS;
    }

    @Override
    public IDegradationEffect getRandomDegradationEffect() {
        int index = random.nextInt(WEIGHTED_LIST.size());
        return WEIGHTED_LIST.get(index);
    }
}