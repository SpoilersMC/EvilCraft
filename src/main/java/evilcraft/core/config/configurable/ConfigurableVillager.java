package evilcraft.core.config.configurable;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.VillagerConfig;
import evilcraft.core.helper.WeightedItemStack;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Villager that can hold ExtendedConfigs
 * @author rubensworks
 */
public class ConfigurableVillager implements IConfigurable, IVillageTradeHandler {

    protected ExtendedConfig<VillagerConfig> eConfig = null;

    // The weights of the output - inputs must be less than than maxWeightDifference
    protected List<WeightedItemStack> allowedTradeInputs = new ArrayList<WeightedItemStack>();
    protected List<WeightedItemStack> allowedTradeOutputs = new ArrayList<WeightedItemStack>();
    protected int maxWeightDifference = 25;
    protected int inputMinStackSize = 1;
    protected int inputMaxStackSize = 64;
    protected int addedRecipes = 0;
    protected int requiredAddedRecipes = 10;
    private int attemptAddRecipeUpperbound = 100;
    private int attemptAddRecipe = 0;

    /**
     * Make a new instance of a villager.
     * @param eConfig The config for this villager.
     */
    @SuppressWarnings("rawtypes")
    protected ConfigurableVillager(ExtendedConfig eConfig) {
        this.setConfig(eConfig);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
        if(villager.getProfession() == eConfig.downCast().getId()) {
            addedRecipes = 0;
            attemptAddRecipe = 0;
            while(addedRecipes < requiredAddedRecipes && attemptAddRecipe < attemptAddRecipeUpperbound) {
                WeightedItemStack outputWeighted = getRandomOutput(random);
                WeightedItemStack inputAWeighted = getRandomInput(random);
                if(!tryAddingRecipe(recipeList, random, inputAWeighted, null, outputWeighted)) {
                    WeightedItemStack inputBWeighted = getRandomInput(random);
                    tryAddingRecipe(recipeList, random, inputAWeighted, inputBWeighted, outputWeighted);
                }
            }
        }
    }

    /**
     * This will try adding the recipe to the recipelist if the weights of the inputs are almost the same as the output.
     * In that case it will calculate random stack sizes for the input(s) and derive an appropriate output stack size depending on the normalized weights.
     * @param recipeList The MerchantRecipeList.
     * @param random Random instance.
     * @param inputAWeighted Weighted first input.
     * @param inputBWeighted Weighted second input, can be null.
     * @param outputWeighted Weighted output.
     * @return If the recipe could be added.
     */
    private boolean tryAddingRecipe(MerchantRecipeList recipeList, Random random, WeightedItemStack inputAWeighted, WeightedItemStack inputBWeighted, WeightedItemStack outputWeighted) {
        attemptAddRecipe++;
        int inputWeight = inputAWeighted.getWeight() * 64;
        if(inputBWeighted != null) {
            inputWeight += inputBWeighted.getWeight() * 64;
        } else {
            inputWeight *= 2;
        }

        if(outputWeighted.getWeight() - inputWeight >= maxWeightDifference) {
            if(inputBWeighted == null) {
                return false;
            }
            inputWeight -= inputBWeighted.getWeight() * 64;
            inputBWeighted = null;
            if(Math.abs(outputWeighted.getWeight() - inputWeight) >= maxWeightDifference) {
                return false;
            }
        }

        ItemStack inputA;
        ItemStack inputB;
        ItemStack output;

        int totalInputWeight = 0;

        inputA = inputAWeighted.getItemStack().copy();
        inputA.stackSize = getRandomBetween(random, inputA.stackSize, getRandomBetween(random, inputMinStackSize, inputMaxStackSize));
        if(inputA.stackSize > inputA.getMaxStackSize()) {
            inputA.stackSize = inputA.getMaxStackSize();
        }
        totalInputWeight += inputA.stackSize * inputAWeighted.getWeight() / inputAWeighted.getItemStack().stackSize;

        if(inputBWeighted != null) {
            inputB = inputAWeighted.getItemStack().copy();
            inputB.stackSize = getRandomBetween(random, inputB.stackSize, getRandomBetween(random, inputMinStackSize, inputMaxStackSize));
            if(inputB.stackSize > inputB.getMaxStackSize()) {
                inputB.stackSize = inputB.getMaxStackSize();
            }
            totalInputWeight += inputB.stackSize * inputBWeighted.getWeight() / inputBWeighted.getItemStack().stackSize;
        } else {
            inputB = null;
        }

        output = outputWeighted.getItemStack().copy();
        output.stackSize = Math.min(64, outputWeighted.getItemStack().stackSize * (int)safeDivide(totalInputWeight, outputWeighted.getWeight()));
        if(output.stackSize == 0) {
            return false;
        } else if(output.stackSize > output.getMaxStackSize()) {
            output.stackSize = output.getMaxStackSize();
        }

        recipeList.addToListWithCheck(new MerchantRecipe(inputA, inputB, output));
        addedRecipes++;
        return true;
    }

    protected float safeDivide(int i, float j) {
        return (float)i / (float)Math.max(1, j);
    }

    protected WeightedItemStack getRandomInput(Random random) {
        return WeightedItemStack.getRandomWeightedItemStack(allowedTradeInputs, random);
    }

    protected WeightedItemStack getRandomOutput(Random random) {
        return WeightedItemStack.getRandomWeightedItemStack(allowedTradeOutputs, random);
    }

    protected int getRandomBetween(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}