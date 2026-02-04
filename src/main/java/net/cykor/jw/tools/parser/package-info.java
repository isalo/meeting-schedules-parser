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

/**
 * Meeting Schedules Parser library for parsing JW Meeting Workbook and Watchtower Study
 * publications.
 *
 * <h2>Overview</h2>
 *
 * <p>This library parses JWPUB and EPUB files containing JW meeting schedules and returns
 * structured data as Java objects or JSON.
 *
 * <h2>Main Classes</h2>
 *
 * <ul>
 *   <li>{@link net.cykor.jw.tools.parser.MeetingSchedulesParser} - Main entry point for parsing
 *   <li>{@link net.cykor.jw.tools.parser.ParseResult} - Result containing parsed schedules
 *   <li>{@link net.cykor.jw.tools.parser.model.MWBSchedule} - Meeting Workbook schedule data
 *   <li>{@link net.cykor.jw.tools.parser.model.WSchedule} - Watchtower Study schedule data
 * </ul>
 *
 * <h2>Quick Start</h2>
 *
 * <pre>{@code
 * MeetingSchedulesParser parser = MeetingSchedulesParser.create();
 * ParseResult result = parser.parse(Path.of("mwb_E_202401.jwpub"));
 *
 * // Access schedules
 * for (MWBSchedule schedule : result.getMwbSchedules()) {
 *     System.out.println(schedule.getWeekDate());
 * }
 *
 * // Get JSON output
 * String json = result.toJson();
 * }</pre>
 *
 * @see net.cykor.jw.tools.parser.MeetingSchedulesParser
 */
package net.cykor.jw.tools.parser;
