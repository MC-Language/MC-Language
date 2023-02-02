package tech.thatgravyboat.mcl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class SimpleJson {

    public static final class DefaultValue implements Value {

        private final String value;

        public DefaultValue(java.lang.Object value) {
            this.value = value instanceof String str ? "\"" + str +"\"" : String.valueOf(value);
        }

        @Override
        public String output() {
            return value;
        }
    }

    public static final class Array extends ArrayList<Value> implements Value {

        @Override
        public String output() {
            return "[" + stream().map(Value::output).collect(Collectors.joining(",")) + "]";
        }
    }

    public static final class Object extends HashMap<String, Value> implements Value {

        @Override
        public String output() {
            var joined = new StringJoiner(",", "{", "}").setEmptyValue("{}");
            for (var entry : entrySet()) {
                joined.add("\"" + entry.getKey() + "\":" + entry.getValue().output());
            }
            return joined.toString();
        }
    }

    public interface Value {

        String output();
    }
}
