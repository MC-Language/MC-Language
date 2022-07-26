package tech.thatgravyboat.mcl.lang;

import java.util.Collection;

public record Package(String id, Collection<McFunction> functions) {}
