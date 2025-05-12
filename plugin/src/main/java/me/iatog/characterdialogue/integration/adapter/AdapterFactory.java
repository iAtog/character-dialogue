package me.iatog.characterdialogue.integration.adapter;

public interface AdapterFactory {
    NPCAdapter<?> createAdapter() throws AdapterException;
}
