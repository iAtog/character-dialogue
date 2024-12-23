package me.iatog.characterdialogue.dialogs;

public record Choice(int getIndex, String getMessage, Class<? extends DialogChoice> getChoiceClass, String getArgument) { }
