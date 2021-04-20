package ro.ggez.containerprotect.protection;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Entity;
import ro.ggez.containerprotect.PluginMain;

public class EntityProtection extends Protection {

    private final Entity entity;

    public EntityProtection(PluginMain plugin, Entity entity) {
        super(plugin);

        this.entity = entity;
        this.dataContainer = this.entity.getPersistentDataContainer();
    }

    @Override
    public boolean canBeProtected() {
        return entity != null;
    }

    @Override
    public String getDisplayName() {
        return WordUtils.capitalizeFully(this.entity.getType().toString().replace("_", " "));
    }

}
