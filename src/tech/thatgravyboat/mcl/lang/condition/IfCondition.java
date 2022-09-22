package tech.thatgravyboat.mcl.lang.condition;

public record IfCondition(String value) {

    public IfCondition(String variable, String operator, String value) {
        this(variable + " " + operator + " " + value);
        //TODO: implement
    }
}
