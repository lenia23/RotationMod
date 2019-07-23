package jp.lenia23.rotationmod;

import ca.weblite.objc.Client;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Mod(References.MODID)
public class RotationMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private static Minecraft mc = null;

    // Capable rotation block
    public static String blockIds = "";
    static List<Object> _blockIds = null;
    // Rotation key
    public static int keyNum = GLFW.GLFW_KEY_R;
    public static KeyBinding procKey;
    private static int keyPressed = 0;

    /*private final Object _client = DistExecutor.runForDist(() -> {
        return () -> {
            return new ClientProxy(this);
        };
    }, () -> {
        return () -> {
            return null;
        };
    });*/


    public RotationMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        mc = Minecraft.getInstance();

        InputMappings.Input keyInstance = InputMappings.Type.KEYSYM.getOrMakeInput(keyNum);
        procKey = new KeyBinding("test", KeyConflictContext.IN_GAME, keyInstance, References.MODID);
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientRegistry.registerKeyBinding(procKey);

        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("rotationmod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    private void onKeyPressed(KeyBinding key) {
        //The tag/tags for rotation
        ResourceLocation stairTag = this.mc.getConnection().getTags().getBlocks().getOrCreate(new ResourceLocation("stairs")).getId();
        //To get looking block and tags
        Entity entity = this.mc.getRenderViewEntity();
        RayTraceResult rayTraceBlock = entity.rayTrace(20.0D, 0.0F, RayTraceFluidMode.NEVER);
        //block coordinates
        BlockPos blockpos = rayTraceBlock.getBlockPos();

        IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
        LOGGER.info(mc.player.getLookVec());
        Collection<ResourceLocation> resourcelocation = this.mc.getConnection().getTags().getBlocks().getOwningTags(iblockstate.getBlock());
        LOGGER.info(resourcelocation);
        LOGGER.info(stairTag);

        if(resourcelocation.contains(stairTag)) {
            LOGGER.info(String.valueOf((Object) IRegistry.field_212618_g.getKey(iblockstate.getBlock())));
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : iblockstate.getValues().entrySet()) {
                if(entry.getKey().getName().equals("facing")) {
                    BlockStairs stair = (BlockStairs) iblockstate.getBlock();
                    //stair.rotate()
                    IBlockState newblockstate = iblockstate.rotate(mc.world, blockpos, Rotation.CLOCKWISE_90);
                    Block.replaceBlock(iblockstate,newblockstate, this.mc.world, blockpos, 1);
                    //LOGGER.info(entry.getValue());
                    //EnumFacing facing = (EnumFacing) entry.getValue();
                    //facing.rotateY();
                    LOGGER.info(entry);
                }
            }
        }

    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        if (TickEvent.Phase.END.equals(event.phase)) {
            //LOGGER.info("----Tick Event----");
            if(procKey.isKeyDown() && keyPressed <= 0){
                keyPressed = 20;
                onKeyPressed(procKey);
            } else {
                --keyPressed;
            }
        }
    }


}
