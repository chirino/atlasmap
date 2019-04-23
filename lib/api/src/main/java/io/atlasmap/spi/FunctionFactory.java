package io.atlasmap.actions.functions;

import java.util.List;

import io.atlasmap.expression.Expression;
import io.atlasmap.expression.parser.ParseException;

public abstract class FunctionFactory {
    public String getName() {
        return getClass().getSimpleName();
    }
    public abstract io.atlasmap.expression.Expression create(List<Expression> args) throws ParseException;
}
