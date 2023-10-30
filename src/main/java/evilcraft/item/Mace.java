package evilcraft.item;

import com.google.common.collect.Multimap;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.client.particle.EntityDistortFX;
import evilcraft.client.particle.EntityPlayerTargettedBlurFX;
import evilcraft.client.particle.ExtendedEntityExplodeFX;
import evilcraft.core.config.configurable.ConfigurableDamageIndicatedItemFluidContainer;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.fluid.Blood;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * An abstract powerable mace.
 * @author rubensworks
 */
public abstract class Mace extends ConfigurableDamageIndicatedItemFluidContainer {

    private final int hitUsage;
    private final int maximumCharge;
    private final int powerLevels;
    private final float meleeDamage;

    public Mace(ExtendedConfig<ItemConfig> eConfig, int containerSize, int hitUsage, int maximumCharge, int powerLevels, float meleeDamage) {
        super(eConfig, containerSize, Blood.getInstance());
        this.hitUsage = hitUsage;
        this.maximumCharge = maximumCharge;
        this.powerLevels = powerLevels;
        this.meleeDamage = meleeDamage;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    protected boolean isUsable(ItemStack itemStack, EntityPlayer player) {
        return canConsume(1, itemStack, player);
    }

    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase attacked, EntityLivingBase attacker) {
        if(attacker instanceof EntityPlayer && isUsable(itemStack, (EntityPlayer)attacker)) {
            this.drain(itemStack, hitUsage, true);
        }
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) {
        return !isUsable(itemStack, player);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return this.maximumCharge * (this.powerLevels - getPower(itemStack));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if(ItemPowerableHelpers.onPowerableItemItemRightClick(itemStack, world, player, this.powerLevels, true)) {
            return itemStack;
        } else {
            if(isUsable(itemStack, player)) {
                player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            } else {
                if(world.isRemote) {
                    animateOutOfEnergy(world, player);
                }
            }
        }
        return itemStack;
    }

    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer player, int duration) {
        World world = player.worldObj;
        if(world.isRemote && duration % 2 == 0) {
            showUsingItemTick(world, itemStack, player, duration);
        }
        super.onUsingTick(itemStack, player, duration);
    }

    @SideOnly(Side.CLIENT)
    protected void showUsingItemTick(World world, ItemStack itemStack, EntityPlayer player, int duration) {
        int itemUsedCount = getMaxItemUseDuration(itemStack) - duration;
        double area = getArea(itemUsedCount);
        int points = (int)(Math.pow(area, 0.55)) * 2 + 1;
        int particleChance = 5 * (this.powerLevels - getPower(itemStack));
        for(double point = -points; point <= points; point++) {
            for(double pointHeight = -points; pointHeight <= points; pointHeight += 0.5F) {
                if(itemRand.nextInt(particleChance) == 0) {
                    double u = Math.PI * (point / points);
                    double v = -2 * Math.PI * (pointHeight / points);

                    double xOffset = Math.cos(u) * Math.sin(v) * area;
                    double yOffset = Math.sin(u) * area;
                    double zOffset = Math.cos(v) * area;

                    double xCoord = player.posX;
                    double yCoord = player.posY - 0.5D;
                    double zCoord = player.posZ;

                    double particleX = xCoord + xOffset - world.rand.nextFloat() * area / 4 - 0.5F;
                    double particleY = yCoord + yOffset - world.rand.nextFloat() * area / 4 - 0.5F;
                    double particleZ = zCoord + zOffset - world.rand.nextFloat() * area / 4 - 0.5F;

                    float particleMotionX = (float)(xOffset * 10);
                    float particleMotionY = (float)(yOffset * 10);
                    float particleMotionZ = (float)(zOffset * 10);

                    FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntityDistortFX(world, particleX, particleY, particleZ, particleMotionX, particleMotionY, particleMotionZ, (float)area * 3));

                    if(world.rand.nextInt(10) == 0) {
                        int spread = 10;
                        float scale2 = 0.3F - world.rand.nextFloat() * 0.2F;
                        float r = 1.0F * world.rand.nextFloat();
                        float g = 0.2F + 0.01F * world.rand.nextFloat();
                        float b = 0.1F + 0.5F * world.rand.nextFloat();
                        float ageMultiplier2 = 20;

                        double motionX = spread - world.rand.nextDouble() * 2 * spread;
                        double motionY = spread - world.rand.nextDouble() * 2 * spread;
                        double motionZ = spread - world.rand.nextDouble() * 2 * spread;

                        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntityPlayerTargettedBlurFX(world, scale2, motionX, motionY, motionZ, r, g, b, ageMultiplier2, player));
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void showUsedItemTick(World world, EntityPlayer player, int power) {
        int particles = (power + 1) * (power + 1) * (power + 1) * 10;
        for(int i = 0; i < particles; i++) {
            double x = player.posX - 0.5F + world.rand.nextDouble();
            double y = player.posY - 1F + world.rand.nextDouble();
            double z = player.posZ - 0.5F + world.rand.nextDouble();

            double particleMotionX = (-1 + world.rand.nextDouble() * 2) * (power + 1) / 2;
            double particleMotionY = (-1 + world.rand.nextDouble() * 2) * (power + 1) / 2;
            double particleMotionZ = (-1 + world.rand.nextDouble() * 2) * (power + 1) / 2;

            float r = 1.0F * world.rand.nextFloat();
            float g = 0.2F + 0.01F * world.rand.nextFloat();
            float b = 0.1F + 0.5F * world.rand.nextFloat();

            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ExtendedEntityExplodeFX(world, x, y, z, particleMotionX, particleMotionY, particleMotionZ, r, g, b, 0.3F));
        }
    }

    /**
     * The area of effect for the given in use count (counting up per tick).
     * @param itemUsedCount The amount of ticks the item was active.
     * @return The area of effect.
     */
    protected double getArea(int itemUsedCount) {
        return itemUsedCount / 5 + 2.0D;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int itemInUseCount) {
        // Actual usage length
        int itemUsedCount = getMaxItemUseDuration(itemStack) - itemInUseCount;

        // Calculate how much blood to drain
        int toDrain = itemUsedCount * getCapacity(itemStack) * (getPower(itemStack) + 1) / (getMaxItemUseDuration(itemStack) * this.powerLevels);
        FluidStack consumed = consume(toDrain, itemStack, player);
        int consumedAmount = consumed == null ? 0 : consumed.amount;

        // Recalculate the itemUsedCount depending on how much blood is available
        itemUsedCount = consumedAmount * getMaxItemUseDuration(itemStack) / getCapacity(itemStack);

        // Only do something if there is some blood left
        if(consumedAmount > 0) {
            // This will perform an effect to entities in a certain area,
            // depending on the itemUsedCount.
            use(world, player, itemUsedCount, getPower(itemStack));
            if(world.isRemote) {
                showUsedItemTick(world, player, getPower(itemStack));
            }
        } else if(world.isRemote) {
            animateOutOfEnergy(world, player);
        }
    }

    /**
     * The usage action after charging the mace.
     * @param world The world
     * @param player The using player
     * @param itemUsedCount The charge count
     * @param power The configured power level
     */
    protected abstract void use(World world, EntityPlayer player, int itemUsedCount, int power);

    @SideOnly(Side.CLIENT)
    protected void animateOutOfEnergy(World world, EntityPlayer player) {
        double xCoord = player.posX;
        double yCoord = player.posY;
        double zCoord = player.posZ;

        double particleX = xCoord;
        double particleY = yCoord;
        double particleZ = zCoord;

        float particleMotionX = world.rand.nextFloat() * 0.2F - 0.1F;
        float particleMotionY = 0.2F;
        float particleMotionZ = world.rand.nextFloat() * 0.2F - 0.1F;
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySmokeFX(world, particleX, particleY, particleZ, particleMotionX, particleMotionY, particleMotionZ));

        world.playSoundAtEntity(player, "note.bd", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public int getItemEnchantability() {
        return 15;
    }

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    public Multimap getAttributeModifiers(ItemStack itemStack) {
        Multimap multimap = super.getAttributeModifiers(itemStack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.meleeDamage, 0));
        return multimap;
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        ItemPowerableHelpers.addPreInformation(itemStack, list);
        super.addInformation(itemStack, entityPlayer, list, par4);
        ItemPowerableHelpers.addPostInformation(itemStack, list);
    }

    /**
     * Get the power level of the given ItemStack.
     * @param itemStack The item to check.
     * @return The power this Mace currently has.
     */
    public int getPower(ItemStack itemStack) {
        return ItemPowerableHelpers.getPower(itemStack);
    }
}