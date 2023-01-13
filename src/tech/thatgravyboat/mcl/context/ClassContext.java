package tech.thatgravyboat.mcl.context;

import java.util.List;
import java.util.Objects;

public record ClassContext(String pPackage, String pClass, List<String> pImports) {

    public static ClassContext ofFullQualifier(String qualifier) {
        var pPackage = qualifier.substring(0, qualifier.lastIndexOf("."));
        var pClass = qualifier.substring(qualifier.lastIndexOf(".") + 1);
        return new ClassContext(pPackage, pClass, List.of());
    }

    public String fullClass() {
        return pPackage + "." + pClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassContext other) {
            return other.pPackage.equals(pPackage) && other.pClass.equals(pClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pPackage, pClass);
    }
}
