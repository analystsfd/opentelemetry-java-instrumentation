/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package config.exclude;

import io.opentelemetry.instrumentation.annotations.WithSpan;

public class SomeClass implements Runnable {

  @WithSpan
  @Override
  public void run() {}

  public static class NestedClass implements Runnable {

    @WithSpan
    @Override
    public void run() {}
  }
}
