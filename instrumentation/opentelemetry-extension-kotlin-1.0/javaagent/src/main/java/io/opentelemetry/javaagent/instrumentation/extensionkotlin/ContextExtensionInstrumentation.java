/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.extensionkotlin;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import application.io.opentelemetry.context.Context;
import io.opentelemetry.extension.kotlin.ContextExtensionsKt;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.opentelemetryapi.context.AgentContextStorage;
import kotlin.coroutines.CoroutineContext;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class ContextExtensionInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("application.io.opentelemetry.extension.kotlin.ContextExtensionsKt");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        named("asContextElement")
            .and(takesArgument(0, named("application.io.opentelemetry.context.Context"))),
        this.getClass().getName() + "$ContextAdvice");

    transformer.applyAdviceToMethod(
        named("asContextElement")
            .and(
                takesArgument(
                    0, named("application.io.opentelemetry.context.ImplicitContextKeyed"))),
        this.getClass().getName() + "$ImplicitContextKeyedAdvice");

    transformer.applyAdviceToMethod(
        named("getOpenTelemetryContext")
            .and(takesArgument(0, named("kotlin.coroutines.CoroutineContext"))),
        this.getClass().getName() + "$GetOpenTelemetryContextAdvice");
  }

  @SuppressWarnings("unused")
  public static class ContextAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static CoroutineContext enter(@Advice.Argument(0) Context applicationContext) {
      if (applicationContext != null) {
        io.opentelemetry.context.Context agentContext =
            AgentContextStorage.getAgentContext(applicationContext);
        return ContextExtensionsKt.asContextElement(agentContext);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(
        @Advice.Return(readOnly = false) CoroutineContext result,
        @Advice.Enter CoroutineContext coroutineContext) {
      if (coroutineContext != null) {
        result = coroutineContext;
      }
    }
  }

  @SuppressWarnings("unused")
  public static class ImplicitContextKeyedAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static CoroutineContext enter(
        @Advice.Argument(0)
            application.io.opentelemetry.context.ImplicitContextKeyed implicitContextKeyed) {
      if (implicitContextKeyed != null) {
        Context applicationContext = Context.current().with(implicitContextKeyed);
        io.opentelemetry.context.Context agentContext =
            AgentContextStorage.getAgentContext(applicationContext);
        return ContextExtensionsKt.asContextElement(agentContext);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(
        @Advice.Return(readOnly = false) CoroutineContext result,
        @Advice.Enter CoroutineContext coroutineContext) {
      if (coroutineContext != null) {
        result = coroutineContext;
      }
    }
  }

  @SuppressWarnings("unused")
  public static class GetOpenTelemetryContextAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static Context enter(@Advice.Argument(0) CoroutineContext coroutineContext) {
      if (coroutineContext != null) {
        io.opentelemetry.context.Context agentContext =
            ContextExtensionsKt.getOpenTelemetryContext(coroutineContext);
        return AgentContextStorage.toApplicationContext(agentContext);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(
        @Advice.Return(readOnly = false) Context result, @Advice.Enter Context context) {
      if (context != null) {
        result = context;
      }
    }
  }
}
