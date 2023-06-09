/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.ratpack;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesBuilder;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesConfigurer;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;

@AutoService(IgnoredTypesConfigurer.class)
public class RatpackIgnoredTypesConfigurer implements IgnoredTypesConfigurer {

  @Override
  public void configure(IgnoredTypesBuilder builder, ConfigProperties config) {
    // Context is passed through Netty channels in Ratpack as executor instrumentation is
    // not suitable. As the context that would be propagated via executor would be
    // incorrect, skip the propagation. Not checking for concrete class names as this covers
    // anonymous classes from ratpack.exec.internal.DefaultExecution and
    // ratpack.exec.internal.DefaultExecController.
    builder.ignoreTaskClass("ratpack.exec.internal.");
  }
}
