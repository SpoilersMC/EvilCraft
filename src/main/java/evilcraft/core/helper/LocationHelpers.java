package evilcraft.core.helper;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import evilcraft.api.ILocation;
import evilcraft.core.algorithm.Location;

/**
 * Helper methods involving {@link ILocation} and {@link TargetPoint}.
 * @author immortaleeb
 */
public class LocationHelpers {

    private static final Random random = new Random();

    /**
     * Creates a {@link TargetPoint} for the dimension and position of the given {@link Entity} and a given range.
     * @param entity Entity who's dimension and position will be used to create the {@link TargetPoint}.
     * @param range The range of the {@link TargetPoint}.
     * @return A {@link TargetPoint} with the position and dimension of the entity and the given range.
     */
    public static TargetPoint createTargetPointFromEntityPosition(Entity entity, int range) {
        return new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range);
    }

    /**
     * Creates a {@link TargetPoint} for the dimension of the given world and the given {@link ILocation}.
     * @param world The world from which the dimension will be used.
     * @param location The location for the target.
     * @param range The range of the {@link TargetPoint}.
     * @return A {@link TargetPoint} with the position and dimension of the entity and the given range.
     */
    public static TargetPoint createTargetPointFromLocation(World world, ILocation location, int range) {
        int[] c = validateLocation(location);
        return new TargetPoint(world.provider.dimensionId, c[0], c[1], c[2], range);
    }

    /**
     * Get the distance between the two given locations.
     * @param first The first location.
     * @param second The second location.
     * @return The Euclidian distance.
     */
    public static double getDistance(ILocation first, ILocation second) {
        int[] c1 = first.getCoordinates();
        int[] c2 = second.getCoordinates();
        if(c1.length != c2.length) {
            throw new LocationException("The locations {" + first + ", " + second + "} have different dimensions.");
        }
        int d = 0;
        for(int i = 0; i < c1.length; i++) {
            d += (c1[i] - c2[i]) * (c1[i] - c2[i]);
        }
        return Math.sqrt(d);
    }

    private static int[] validateLocation(ILocation location) throws LocationException {
        if(location == null || location.getDimensions() != 3) {
            throw new LocationException("The location '" + location + "' does not have exactly three coordinated.");
        }
        return location.getCoordinates();
    }

    /**
     * Get the block from a location in a world.
     * @param world The world.
     * @param location The location.
     * @return The block on that location.
     * @throws LocationException If the given location does not have exactly three dimensions.
     */
    public static Block getBlock(World world, ILocation location) throws LocationException {
        int[] c = validateLocation(location);
        return world.getBlock(c[0], c[1], c[2]);
    }

    /**
     * Get the block metadata from a location in a world.
     * @param world The world.
     * @param location The location.
     * @return The block on that location.
     * @throws LocationException If the given location does not have exactly three dimensions.
     */
    public static int getBlockMeta(World world, ILocation location) throws LocationException {
        int[] c = validateLocation(location);
        return world.getBlockMetadata(c[0], c[1], c[2]);
    }

    /**
     * Get the tile entity from a location in a world.
     * @param world The world.
     * @param location The location.
     * @return The tile entity on that location.
     * @throws LocationException If the given location does not have exactly three dimensions.
     */
    public static TileEntity getTile(IBlockAccess world, ILocation location) throws LocationException {
        int[] c = validateLocation(location);
        return world.getTileEntity(c[0], c[1], c[2]);
    }

    /**
     * Set the block.
     * @param world The world.
     * @param location The location.
     * @param block The block to set.
     * @throws LocationException If the given location does not have exactly three dimensions.
     */
    public static void setBlock(World world, ILocation location, Block block) throws LocationException {
        int[] c = LocationHelpers.validateLocation(location);
        world.setBlock(c[0], c[1], c[2], block);
    }

    /**
     * Set the block metadata.
     * @param world The world.
     * @param location The location.
     * @param meta The meta value to set.
     * @param notifyFlag The flag to set. See the BLOCK_NOTIFY_* fields in {@link Helpers} for more info, these can be or-ed together.
     * @throws LocationException If the given location does not have exactly three dimensions.
     */
    public static void setBlockMetadata(World world, ILocation location, int meta, int notifyFlag) throws LocationException {
        int[] c = LocationHelpers.validateLocation(location);
        world.setBlockMetadataWithNotify(c[0], c[1], c[2], meta, notifyFlag);
    }

    /**
     * Get a random point inside a sphere in an efficient way.
     * @param center The center coordinates of the sphere.
     * @param radius The radius of the sphere.
     * @return The coordinates of the random point.
     */
    public static ILocation getRandomPointInSphere(ILocation center, int radius) {
        ILocation randomPoint = null;
        while(randomPoint == null) {
            int totalDistance = 0;
            int[] coordinates = new int[center.getDimensions()];
            for(int i = 0; i < center.getDimensions(); i++) {
                coordinates[i] = center.getCoordinates()[i] - radius + random.nextInt(2 * radius);
                int d = center.getCoordinates()[i] - coordinates[i];
                totalDistance += d * d;
            }
            if((int)Math.sqrt(totalDistance) <= radius) {
                randomPoint = new Location(coordinates);
            }
        }
        return randomPoint;
    }

    /**
     * Get yaw from the start location to the end location.
     * @param start Start
     * @param end End
     * @return The yaw
     */
    public static double getYaw(ILocation start, ILocation end) {
        int[] c1 = start.getCoordinates();
        int[] c2 = end.getCoordinates();
        double dX = c1[0] - c2[0];
        double dY = c1[1] - c2[1];
        double dZ = c1[2] - c2[2];
        return Math.atan2(dZ, dX) * 180 / Math.PI;
    }

    /**
     * Get pitch from the start location to the end location.
     * @param start Start
     * @param end End
     * @return The pitch
     */
    public static double getPitch(ILocation start, ILocation end) {
        int[] c1 = start.getCoordinates();
        int[] c2 = end.getCoordinates();
        double dX = c1[0] - c2[0];
        double dY = c1[1] - c2[1];
        double dZ = c1[2] - c2[2];
        return (Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI) * 180 / Math.PI;
    }

    /**
     * Exceptions that can occur with location manipulation.
     * @author rubensworks
     */
    public static class LocationException extends RuntimeException {

        /**
         * Make a new instance.
         * @param message The message.
         */
        public LocationException(String message) {
            super(message);
        }
    }
}