package evilcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.Achievements;
import evilcraft.Configs;
import evilcraft.core.IInformationProvider;
import evilcraft.core.config.configurable.ConfigurableBlock;
import evilcraft.core.config.extendedconfig.BlockConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.L10NHelpers;
import evilcraft.item.DarkGem;
import evilcraft.item.DarkGemConfig;
import evilcraft.item.DarkGemCrushedConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ore that drops {@link DarkGem}.
 * @author rubensworks
 */
public class DarkOre extends ConfigurableBlock implements IInformationProvider {

    private static DarkOre _instance = null;
    private static final int MINIMUM_DROPS = 1; // Minimum amount of drops when mining this block
    private static final int INCREASE_DROPS = 3; // Amount that can be increased at random for drops
    private static final int INCREASE_XP = 5; // Amount of XP that can be gained from mining this block
    private static final int CRUSHEDCHANCE = 4; // The chance on a crushed dark gem with no fortune.

    private static final int GLOWINGMETA = 1;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<BlockConfig> eConfig) {
        if(_instance == null)
            _instance = new DarkOre(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static DarkOre getInstance() {
        return _instance;
    }

    private DarkOre(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.rock);
        this.setTickRandomly(true);
        this.setHardness(3.0F);
        this.setStepSound(soundTypeStone);
        this.setHarvestLevel("pickaxe", 2); // Iron tier
    }

    @Override
    public Item getItemDropped(int meta, Random random, int zero) {
        if(Configs.isEnabled(DarkGemConfig.class))
            return DarkGem.getInstance();
        else
            return null;
    }

    @Override
    public int quantityDroppedWithBonus(int amount, Random random) {
        return this.quantityDropped(random) + random.nextInt(amount / 4 + 1);
    }

    @Override
    public int quantityDropped(Random random) {
        return MINIMUM_DROPS + random.nextInt(INCREASE_DROPS);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float dropchance, int fortune) {
        super.dropBlockAsItemWithChance(world, x, y, z, meta, dropchance, fortune);

        if(this.getItemDropped(meta, world.rand, fortune) != Item.getItemFromBlock(this)) {
            int xp = 1 + world.rand.nextInt(INCREASE_XP);
            this.dropXpOnBlockBreak(world, x, y, z, xp);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);
        if((fortune > 0 || world.rand.nextInt(CRUSHEDCHANCE) == 0) && Configs.isEnabled(DarkGemCrushedConfig.class)) {
            drops.add(new ItemStack(DarkGemCrushedConfig._instance.getItemInstance(), world.rand.nextInt(fortune / 3 + 1) + 1));
        }
        return drops;
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(DarkOre._instance);
    }

    @Override
    public int tickRate(World world) {
        return 60;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        player.addStat(Achievements.FIRST_AGE, 1);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.glow(world, x, y, z);
        super.onBlockClicked(world, x, y, z, player);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        this.glow(world, x, y, z);
        super.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float motionX, float motionY, float motionZ) {
        this.glow(world, x, y, z);
        return super.onBlockActivated(world, x, y, z, player, meta, motionX, motionY, motionZ);
    }

    private boolean isGlowing(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == GLOWINGMETA;
    }

    private void glow(World world, int x, int y, int z) {
        this.sparkle(world, x, y, z);
        if(!world.isRemote && !isGlowing(world, x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, GLOWINGMETA, 2);// Flag=2 causes client update
            world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(isGlowing(world, x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);// Flag=2 causes client update
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if(isGlowing(world, x, y, z)) {
            this.sparkle(world, x, y, z);
        }
    }

    private void sparkle(World world, int x, int y, int z) {
        if(!world.isRemote)
            return;

        Random random = world.rand;
        double offset = 0.0625D;

        for(int l = 0; l < 6; ++l) {
            double sparkX = (double)((float)x + random.nextFloat());
            double sparkY = (double)((float)y + random.nextFloat());
            double sparkZ = (double)((float)z + random.nextFloat());

            if(l == 0 && !world.getBlock(x, y + 1, z).isBlockNormalCube()) {
                sparkY = (double)(y + 1) + offset;
            }

            if(l == 1 && !world.getBlock(x, y - 1, z).isBlockNormalCube()) {
                sparkY = (double)(y + 0) - offset;
            }

            if(l == 2 && !world.getBlock(x, y, z + 1).isBlockNormalCube()) {
                sparkZ = (double)(z + 1) + offset;
            }

            if(l == 3 && !world.getBlock(x, y, z - 1).isBlockNormalCube()) {
                sparkZ = (double)(z + 0) - offset;
            }

            if(l == 4 && !world.getBlock(x + 1, y, z).isBlockNormalCube()) {
                sparkX = (double)(x + 1) + offset;
            }

            if(l == 5 && !world.getBlock(x - 1, y, z).isBlockNormalCube()) {
                sparkX = (double)(x + 0) - offset;
            }

            if(sparkX < (double)x
                    || sparkX > (double)(x + 1)
                    || sparkY < 0.0D
                    || sparkY > (double)(y + 1)
                    || sparkZ < (double)z
                    || sparkZ > (double)(z + 1)) {
                world.spawnParticle("smoke", sparkX, sparkY, sparkZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public int getRenderPasses() {
        return 2;
    }

    @Override @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta, int renderPass) {
        if(renderPass == 1) {
            return this.blockIcon;
        } else {
            return Blocks.stone.getIcon(side, meta);
        }
    }

    @Override
    public String getInfo(ItemStack itemStack) {
        return IInformationProvider.INFO_PREFIX + L10NHelpers.localize(this.getUnlocalizedName() + ".info.custom", DarkOreConfig.startY, DarkOreConfig.endY);
    }

    @Override @SuppressWarnings("rawtypes")
    public void provideInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
    }
}