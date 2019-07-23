package jp.lenia23.rotationmod;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy {
    //mod instance
   /* final RotationMod _mod;

    public ClientProxy(RotationMod _mod) {
        this._mod = _mod;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        if (TickEvent.Phase.END.equals(event.phase)) {
            if(this._mod.procKey.isKeyDown()){
                this._mod.onKeyPressed(this._mod.procKey);
            }
        }
    }*/
}
