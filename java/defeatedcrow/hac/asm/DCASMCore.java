package defeatedcrow.hac.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class DCASMCore extends DummyModContainer {
	public DCASMCore() {
		super(new ModMetadata());

		ModMetadata meta = super.getMetadata();
		meta.modId = "dcs_climate|asm";
		meta.name = "HeatAndClimateASM";
		meta.version = "1.1.1";
		// 以下は省略可
		meta.authorList = Arrays.asList(new String[] {
				"defeatedcrow" });
		meta.credits = "defeatedcrow";
		meta.description = "Method transformar for the climate.";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController lc) {
		bus.register(this);
		return true;
	}
}
