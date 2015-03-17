package evilcraft.world.gen;

import evilcraft.world.gen.structure.EvilDungeonStructure;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * WorldGenerator for Evil Dungeons.
 * @author rubensworks
 *
 */
public class EvilDungeonGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider.getDimensionId() == 0) generateSurface(world, random, chunkX * 16, chunkZ * 16);
    }
    
    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        (new EvilDungeonStructure()).generate(world, random,
                new BlockPos(chunkX + random.nextInt(16), random.nextInt(60), chunkZ + random.nextInt(16)));
    }
}
