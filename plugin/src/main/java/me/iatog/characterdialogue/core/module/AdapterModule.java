package me.iatog.characterdialogue.core.module;

import me.iatog.characterdialogue.api.NPCAdapterInfo;
import me.iatog.characterdialogue.core.plugin.CorePlugin;
import me.iatog.characterdialogue.integration.adapter.AdapterFactory;
import me.iatog.characterdialogue.integration.adapter.AdapterRegistry;
import me.iatog.characterdialogue.integration.adapter.citizens.CitizensAdapterFactory;

import java.util.ServiceLoader;

public class AdapterModule implements Module {

    private final CorePlugin core;
    private final AdapterRegistry registry;

    public AdapterModule(CorePlugin core) {
        this.core = core;
        this.registry = new AdapterRegistry();
    }

    @Override
    public void load() {
        core.useRegistry(registry);
        registry.registerAdapterFactory("Citizens", new CitizensAdapterFactory());

        ServiceLoader<AdapterFactory> serviceLoader = ServiceLoader.load(AdapterFactory.class);
        for (AdapterFactory factory : serviceLoader) {
            if (factory.getClass().isAnnotationPresent(NPCAdapterInfo.class)) {
                NPCAdapterInfo info = factory.getClass().getAnnotation(NPCAdapterInfo.class);
                registry.registerAdapterFactory(info.pluginName(), factory);
            }
        }

        registry.loadAvailableAdapters();
    }

}
