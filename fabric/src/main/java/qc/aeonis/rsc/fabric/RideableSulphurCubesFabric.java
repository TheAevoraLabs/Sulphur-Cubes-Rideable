package qc.aeonis.rsc.fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RideableSulphurCubesFabric implements ModInitializer {
    public static final String MOD_ID = "rdsc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Rideable Sulphur Cubes v2.0 (Fabric) initialized by Aevora Labs");
    }
}
