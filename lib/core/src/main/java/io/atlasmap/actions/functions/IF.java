/**
 * Copyright (C) 2017 Red Hat, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atlasmap.actions.functions;

import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;

import io.atlasmap.api.AtlasFieldAction;
import io.atlasmap.expression.ExpressionException;
import io.atlasmap.expression.FunctionResolver;
import io.atlasmap.expression.internal.BooleanExpression;
import io.atlasmap.expression.internal.ComparisonExpression;
import io.atlasmap.expression.parser.ParseException;
import io.atlasmap.spi.AtlasActionProcessor;
import io.atlasmap.v2.Expression;

public class IF implements AtlasFieldAction {

    interface FunctionFactory {
        String getName();
        io.atlasmap.expression.Expression create(List<io.atlasmap.expression.Expression> args);
    }

    public static final HashMap<String, FunctionFactory> FUNCTIONS = new HashMap<>();

    static {
        ServiceLoader<FunctionFactory> implementations = ServiceLoader.load(FunctionFactory.class, FunctionFactory.class.getClassLoader());
        for (FunctionFactory f : implementations) {
            FUNCTIONS.put(f.getName().toUpperCase(), f);
        }
    }

    private static final FunctionResolver FUNCTION_RESOLVER = new FunctionResolver() {
        @Override
        public io.atlasmap.expression.Expression resolve(String name, List<io.atlasmap.expression.Expression> args) throws ParseException {
            name = name.toUpperCase();
            FunctionFactory f = FUNCTIONS.get(name);
            if (f != null) {
                return f.create(args);
            }
            // Todo: use a service loader to load pickup all the function impls.
            if ("LT".equals(name)) {
                if (args.size() != 2) {
                    throw new ParseException("LT expects 2 arguments.");
                }
                return ComparisonExpression.createLessThan(args.get(0), args.get(1));
            } else if ("IF".equals(name)) {
                if (args.size() != 3) {
                    throw new ParseException("IF expects 3 argument.");
                }
                BooleanExpression conditional = BooleanExpression.asBooleanExpression(args.get(0));
                io.atlasmap.expression.Expression trueExpression = args.get(1);
                io.atlasmap.expression.Expression falseExpression = args.get(2);
                return (ctx) -> {
                    if (conditional.matches(ctx)) {
                        return trueExpression.evaluate(ctx);
                    } else {
                        return falseExpression.evaluate(ctx);
                    }
                };
            } else if ("TOLOWER".equals(name)) {
                if (args.size() != 1) {
                    throw new ParseException("TOLOWER expects 1 argument.");
                }
                io.atlasmap.expression.Expression arg = args.get(0);
                return (ctx) -> {
                    Object value = arg.evaluate(ctx);
                    if (value == null) {
                        return null;
                    }
                    return value.toString().toLowerCase();
                };
            }
            throw new ParseException("Unknown function: " + name);
        }
    };

    @AtlasActionProcessor
    public static Object process(Expression action, List<Object> args) throws ExpressionException {
        io.atlasmap.expression.Expression parsedExpression = io.atlasmap.expression.Expression.parse(action.getExpession(), FUNCTION_RESOLVER);
        return parsedExpression.evaluate((index) -> {
            try {
                return args.get(Integer.parseInt(index));
            } catch (Throwable e) {
                throw new ExpressionException("Invalid varibale: " + index);
            }
        });
    }

}
