package evilcraft.core.helper;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

/**
 * Helpers for entities.
 * @author rubensworks
 */
public class EntityHelpers {

    /**
     * The NBT tag name that is used for storing the unique name id for an entity.
     */
    public static final String NBTTAG_ID = "id";

    /**
     * This should by called when custom entities collide.
     * It will call the correct method in {@link Block#onEntityCollidedWithBlock(World, int, int, int, Entity)}.
     * @param world The world
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param entity The entity that collides.
     */
    public static void onEntityCollided(World world, int x, int y, int z, Entity entity) {
        Block block = world.getBlock(x, y, z);
        if(block != null)
            block.onEntityCollidedWithBlock(world, x, y, z, entity);
    }

    /**
     * Get the list of entities within a certain area.
     * @param world The world to look in.
     * @param x The center X coordinate.
     * @param y The center Y coordinate.
     * @param z The center Z coordinate.
     * @param area The radius of the area.
     * @return The list of entities in that area.
     */
    @SuppressWarnings("unchecked")
    public static List<Entity> getEntitiesInArea(World world, int x, int y, int z, int area) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(area, area, area);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
        return entities;
    }

    /**
     * Spawn the entity in the world.
     * @param world The world.
     * @param entityLiving The entity to spawn.
     * @return If the entity was spawned.
     */
    public static boolean spawnEntity(World world, EntityLiving entityLiving) {
        Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float)entityLiving.posX, (float)entityLiving.posY, (float)entityLiving.posZ);
        if(canSpawn == Result.ALLOW || canSpawn == Result.DEFAULT) { // && entityliving.getCanSpawnHere()
            if(!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float)entityLiving.posX, (float)entityLiving.posY, (float)entityLiving.posZ)) {
                world.spawnEntityInWorld(entityLiving);
                entityLiving.onSpawnWithEgg(null);
                return true;
            }
        }
        return false;
    }

    /**
     * Spawn xp orbs at the given player.
     * @param world The world.
     * @param player The player.
     * @param xp The amount of experience to spawn.
     */
    public static void spawnXpAtPlayer(World world, EntityPlayer player, int xp) {
        if(!world.isRemote) {
            while(xp > 0) {
                int current;
                current = EntityXPOrb.getXPSplit(xp);
                xp -= current;
                world.spawnEntityInWorld(new EntityXPOrb(world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, current));
            }
        }
    }
}