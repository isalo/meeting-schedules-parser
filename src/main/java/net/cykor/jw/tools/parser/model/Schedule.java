/*
 * Copyright 2024 Cykor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cykor.jw.tools.parser.model;

/**
 * Marker interface for all schedule types.
 *
 * <p>Implementations: {@link MWBSchedule}, {@link WSchedule}
 */
public sealed interface Schedule permits MWBScheduleWrapper, WScheduleWrapper {}

final class MWBScheduleWrapper implements Schedule {
  private final MWBSchedule schedule;

  MWBScheduleWrapper(MWBSchedule schedule) {
    this.schedule = schedule;
  }

  public MWBSchedule getSchedule() {
    return schedule;
  }
}

final class WScheduleWrapper implements Schedule {
  private final WSchedule schedule;

  WScheduleWrapper(WSchedule schedule) {
    this.schedule = schedule;
  }

  public WSchedule getSchedule() {
    return schedule;
  }
}
