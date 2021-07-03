package com.arraybase.lang;

import com.arraybase.GBVariables;
import com.arraybase.MethodCondition;

public class BasicVariableSetConditional extends MethodCondition {
    private String variable = null;
    public BasicVariableSetConditional(String c) {
        super();
        if (c.startsWith("&&")) {
            variable = c.substring(2).trim();
        } else
            variable = c;
    }
    public boolean evalCondition() {
        if (GBVariables.get(variable) != null) {
            return true;
        } else {
            return super.evalCondition();
        }

    }
}
