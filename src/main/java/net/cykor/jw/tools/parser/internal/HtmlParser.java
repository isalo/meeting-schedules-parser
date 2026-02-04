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
package net.cykor.jw.tools.parser.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cykor.jw.tools.parser.model.MWBSchedule;
import net.cykor.jw.tools.parser.model.WSchedule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTML parsing utilities for extracting schedule data from parsed documents.
 *
 * <p>Internal class - not part of the public API.
 */
public final class HtmlParser {

  private static final String NON_BREAKING_SPACE = "\u00A0";

  private HtmlParser() {}

  /** Checks if the HTML document contains a valid MWB schedule. */
  public static boolean isValidMwbSchedule(Document doc) {
    if (doc == null) {
      return false;
    }
    Element h1 = doc.selectFirst("h1");
    Element h2 = doc.selectFirst("h2");

    if (h1 == null || h2 == null) {
      return false;
    }

    Elements pGroups = doc.select(".pGroup");
    Elements h3s = doc.select("h3");

    return !pGroups.isEmpty() || !h3s.isEmpty();
  }

  /** Checks if the HTML document contains a valid Watchtower schedule. */
  public static boolean isValidWSchedule(Document doc) {
    if (doc == null) {
      return false;
    }

    Elements h3s = doc.select("h3");
    return !h3s.isEmpty();
  }

  /** Extracts week date from MWB HTML. */
  public static String getMwbWeekDate(Document doc) {
    Element h1 = doc.selectFirst("h1");
    if (h1 == null) {
      return null;
    }
    return normalizeText(h1.text());
  }

  /** Extracts weekly Bible reading from MWB HTML. */
  public static String getMwbWeeklyBibleReading(Document doc) {
    Element h2 = doc.selectFirst("h2");
    if (h2 == null) {
      return null;
    }
    return normalizeText(h2.text());
  }

  /** Extracts AYF (Apply Yourself to the Field Ministry) part count. */
  public static int getMwbAyfCount(Document doc) {
    Element section3 = doc.selectFirst("#section3");

    if (section3 != null) {
      return section3.select("li").size();
    }

    Elements goldElements = doc.select(".du-color--gold-700");
    return Math.max(1, goldElements.size() - 1);
  }

  /** Extracts LC (Living as Christians) part count. */
  public static int getMwbLcCount(Document doc) {
    Element section4 = doc.selectFirst("#section4");

    if (section4 != null) {
      int liCount = section4.select("li").size();
      return liCount == 6 ? 2 : 1;
    }

    Elements maroonElements =
        doc.select(".du-color--maroon-600.du-margin-top--8.du-margin-bottom--0");
    return Math.max(1, maroonElements.size() - 1);
  }

  /** Extracts all sources from MWB HTML as a concatenated string. */
  public static String getMwbSources(Document doc) {
    StringBuilder src = new StringBuilder();

    Elements pGroups = doc.select(".pGroup");
    for (Element pGroup : pGroups) {
      Elements lis = pGroup.select("li");
      for (Element li : lis) {
        Element p = li.selectFirst("p");
        if (p != null) {
          src.append("@").append(p.text());
        }
      }
    }

    if (src.isEmpty()) {
      Elements h3s = doc.select("h3");
      int songIndex = 0;

      for (Element h3 : h3s) {
        boolean isSong = h3.hasClass("dc-icon--music") || h3.selectFirst(".dc-icon--music") != null;

        Element parent = h3.parent();
        boolean isPart = parent == null || !parent.hasClass("boxContent");

        if (isSong) {
          songIndex++;
        }

        if (isSong || isPart) {
          String data = h3.text();

          if (isSong) {
            data = data.replace("|", "@");
          }

          if (isPart) {
            Element nextSibling = h3.nextElementSibling();
            if (nextSibling != null) {
              Element nextP = nextSibling.selectFirst("p");
              if (nextP != null) {
                data += " " + nextP.text();
              }
            }
          }

          src.append("@").append(data);

          Element nextSibling = h3.nextElementSibling();
          if (isSong
              && songIndex == 2
              && nextSibling != null
              && "DIV".equalsIgnoreCase(nextSibling.tagName())
              && (nextSibling.nextElementSibling() == null
                  || !"H3".equalsIgnoreCase(nextSibling.nextElementSibling().tagName()))) {
            Element nextP = nextSibling.selectFirst("p");
            if (nextP != null) {
              src.append("@").append(nextP.text());

              Element tmpSibling = nextSibling.nextElementSibling();
              if (tmpSibling != null) {
                Element tmpP = tmpSibling.selectFirst("p");
                if (tmpP != null) {
                  src.append(" ").append(tmpP.text());
                }
              }
            }
          }
        }
      }

      int sepBeforeBR = nthIndexOf(src.toString(), "@", 5);
      if (sepBeforeBR > 0) {
        src.insert(sepBeforeBR, "@junk@junk");
      }
    }

    return normalizeText(src.toString());
  }

  /** Parses a single MWB week schedule from HTML document. */
  public static MWBSchedule parseMwbSchedule(Document doc, int year, String lang) {
    boolean enhancedParsing = LanguageSupport.isEnhancedParsingAvailable(lang);

    MWBSchedule.Builder builder = MWBSchedule.builder();

    String weekDate = getMwbWeekDate(doc);
    if (enhancedParsing) {
      String enhancedDate = extractMwbDate(weekDate, year, lang);
      builder.weekDate(enhancedDate);
      builder.weekDateLocale(weekDate);
    } else {
      builder.weekDate(weekDate);
    }

    builder.weeklyBibleReading(getMwbWeeklyBibleReading(doc));

    String src = getMwbSources(doc);
    String[] splits = src.split("@", -1);

    if (splits.length > 1) {
      builder.songFirst(extractSongNumber(splits[1]));
    }

    if (splits.length > 3) {
      String tmpSrc = splits[3].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.tgwTalk(enhanced.type());
        builder.tgwTalkTitle(enhanced.fullTitle());
      } else {
        builder.tgwTalk(tmpSrc);
      }
    }

    if (splits.length > 4) {
      String tmpSrc = splits[4].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.tgwGemsTitle(enhanced.fullTitle());
      } else {
        builder.tgwGemsTitle(tmpSrc);
      }
    }

    if (splits.length > 7) {
      String tmpSrc = splits[7].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.tgwBread(enhanced.src());
        builder.tgwBreadTitle(enhanced.fullTitle());
      } else {
        builder.tgwBread(tmpSrc);
      }
    }

    int ayfCount = getMwbAyfCount(doc);
    builder.ayfCount(ayfCount);

    if (splits.length > 8) {
      String tmpSrc = splits[8].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.ayfPart1(enhanced.src());
        builder.ayfPart1Time(enhanced.time());
        builder.ayfPart1Type(enhanced.type());
        builder.ayfPart1Title(enhanced.fullTitle());
      } else {
        builder.ayfPart1(tmpSrc);
      }
    }

    if (ayfCount > 1 && splits.length > 9) {
      String tmpSrc = splits[9].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.ayfPart2(enhanced.src());
        builder.ayfPart2Time(enhanced.time());
        builder.ayfPart2Type(enhanced.type());
        builder.ayfPart2Title(enhanced.fullTitle());
      } else {
        builder.ayfPart2(tmpSrc);
      }
    }

    if (ayfCount > 2 && splits.length > 10) {
      String tmpSrc = splits[10].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.ayfPart3(enhanced.src());
        builder.ayfPart3Time(enhanced.time());
        builder.ayfPart3Type(enhanced.type());
        builder.ayfPart3Title(enhanced.fullTitle());
      } else {
        builder.ayfPart3(tmpSrc);
      }
    }

    if (ayfCount > 3 && splits.length > 11) {
      String tmpSrc = splits[11].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.ayfPart4(enhanced.src());
        builder.ayfPart4Time(enhanced.time());
        builder.ayfPart4Type(enhanced.type());
        builder.ayfPart4Title(enhanced.fullTitle());
      } else {
        builder.ayfPart4(tmpSrc);
      }
    }

    int nextIndex = ayfCount > 3 ? 12 : ayfCount > 2 ? 11 : ayfCount > 1 ? 10 : 9;
    if (splits.length > nextIndex) {
      builder.songMiddle(LanguageSupport.extractSongNumber(splits[nextIndex]));
    }

    int lcCount = getMwbLcCount(doc);
    builder.lcCount(lcCount);

    nextIndex++;
    if (splits.length > nextIndex) {
      String tmpSrc = splits[nextIndex].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.lcPart1(enhanced.type());
        builder.lcPart1Time(enhanced.time());
        builder.lcPart1Title(enhanced.fullTitle());
        if (enhanced.src() != null && !enhanced.src().isEmpty()) {
          builder.lcPart1Content(enhanced.src());
        }
      } else {
        builder.lcPart1(tmpSrc);
      }
    }

    if (lcCount == 2) {
      nextIndex++;
      if (splits.length > nextIndex) {
        String tmpSrc = splits[nextIndex].trim();
        if (enhancedParsing) {
          EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
          builder.lcPart2(enhanced.type());
          builder.lcPart2Time(enhanced.time());
          builder.lcPart2Title(enhanced.fullTitle());
          if (enhanced.src() != null && !enhanced.src().isEmpty()) {
            builder.lcPart2Content(enhanced.src());
          }
        } else {
          builder.lcPart2(tmpSrc);
        }
      }
    }

    nextIndex++;
    if (splits.length > nextIndex) {
      String tmpSrc = splits[nextIndex].trim();
      if (enhancedParsing) {
        EnhancedSource enhanced = extractSourceEnhanced(tmpSrc, lang);
        builder.lcCbs(enhanced.src());
        builder.lcCbsTitle(enhanced.fullTitle());
      } else {
        builder.lcCbs(tmpSrc);
      }
    }

    nextIndex += 2;
    if (splits.length > nextIndex) {
      builder.songConclude(LanguageSupport.extractSongNumber(splits[nextIndex].trim()));
    }

    return builder.build();
  }

  /** Extracts Watchtower study articles from TOC HTML. */
  public static Elements getWStudyArticles(Document doc) {
    return doc.select("h3");
  }

  /** Extracts study date from article element. */
  public static String getWStudyDate(Element article) {
    Element p = article.selectFirst(".desc");
    if (p != null) {
      return normalizeText(p.text());
    }
    return normalizeText(article.text());
  }

  /** Extracts study title from article element. */
  public static String getWStudyTitle(Element article) {
    Element h2 = article.selectFirst("h2");
    if (h2 != null) {
      return normalizeText(h2.text().trim());
    }

    Element nextSibling = article.nextElementSibling();
    if (nextSibling != null) {
      Element a = nextSibling.selectFirst("a");
      if (a != null) {
        return normalizeText(a.text());
      }
    }

    return null;
  }

  /** Extracts songs from Watchtower article content. */
  public static WStudySongs getWStudySongs(Document content) {
    Elements pubRefs = content.select(".pubRefs");

    Integer openingSong = null;
    Integer concludingSong = null;

    if (!pubRefs.isEmpty()) {
      openingSong = extractSongNumber(pubRefs.first().text());

      if (pubRefs.size() == 2) {
        Element blockTeach = content.selectFirst(".blockTeach");
        if (blockTeach != null) {
          Element nextSibling = blockTeach.nextElementSibling();
          if (nextSibling != null) {
            concludingSong = extractSongNumber(nextSibling.text());
          }
        }
      } else {
        concludingSong = extractSongNumber(pubRefs.last().text());
      }
    }

    return new WStudySongs(openingSong, concludingSong);
  }

  /** Parses a single Watchtower schedule from HTML. */
  public static WSchedule parseWSchedule(Element article, Document content, String lang) {
    boolean enhancedParsing = LanguageSupport.isEnhancedParsingAvailable(lang);

    WSchedule.Builder builder = WSchedule.builder();

    String studyDate = getWStudyDate(article);
    if (studyDate != null && !studyDate.isEmpty()) {
      if (enhancedParsing) {
        String enhancedDate = extractWStudyDate(studyDate, lang);
        builder.studyDate(enhancedDate);
        builder.studyDateLocale(studyDate);
      } else {
        builder.studyDate(studyDate);
      }
    }

    builder.studyTitle(getWStudyTitle(article));

    WStudySongs songs = getWStudySongs(content);
    builder.openingSong(songs.openingSong());
    builder.concludingSong(songs.concludingSong());

    return builder.build();
  }

  /** Parses multiple MWB weeks from a list of HTML documents. */
  public static List<MWBSchedule> parseMwb(List<Document> htmlDocs, int year, String lang) {
    List<MWBSchedule> schedules = new ArrayList<>();
    for (Document doc : htmlDocs) {
      schedules.add(parseMwbSchedule(doc, year, lang));
    }
    return schedules;
  }

  private static String normalizeText(String text) {
    if (text == null) {
      return null;
    }
    return text.replace(NON_BREAKING_SPACE, " ").trim();
  }

  private static Integer extractSongNumber(String text) {
    Object result = LanguageSupport.extractSongNumber(text);
    if (result instanceof Integer) {
      return (Integer) result;
    }
    return null;
  }

  private static String extractMwbDate(String weekDate, int year, String lang) {
    if (weekDate == null || lang == null) {
      return weekDate;
    }

    LanguageSupport.LanguageConfig config = LanguageSupport.getConfig(lang);
    if (config == null || config.mwbDatePattern() == null) {
      return weekDate;
    }

    Matcher matcher = config.mwbDatePattern().matcher(weekDate);
    if (!matcher.find()) {
      return weekDate;
    }

    int day;
    String monthName;

    if ("E".equalsIgnoreCase(lang)) {
      monthName = matcher.group(1);
      day = Integer.parseInt(matcher.group(2));
    } else {
      day = Integer.parseInt(matcher.group(1));
      monthName = matcher.group(3);
    }

    int month = LanguageSupport.monthNameToNumber(monthName, lang);
    if (month == 0) {
      return weekDate;
    }

    return String.format("%d/%02d/%02d", year, month, day);
  }

  private static String extractWStudyDate(String studyDate, String lang) {
    if (studyDate == null || lang == null) {
      return studyDate;
    }

    LanguageSupport.LanguageConfig config = LanguageSupport.getConfig(lang);
    if (config == null || config.wStudyDatePattern() == null) {
      return studyDate;
    }

    Matcher matcher = config.wStudyDatePattern().matcher(studyDate);
    if (!matcher.find()) {
      return studyDate;
    }

    try {
      int day;
      int month;
      int year;

      if ("E".equalsIgnoreCase(lang)) {
        String monthName = matcher.group(2);
        day = Integer.parseInt(matcher.group(3));
        year = Integer.parseInt(matcher.group(5));
        month = LanguageSupport.monthNameToNumber(monthName, lang);
      } else if ("U".equalsIgnoreCase(lang)) {
        day = Integer.parseInt(matcher.group(2));
        String monthName = matcher.group(4);
        year = Integer.parseInt(matcher.group(5));
        month = LanguageSupport.monthNameToNumber(monthName, lang);
      } else {
        day = Integer.parseInt(matcher.group(2));
        String monthName = matcher.group(4);
        year = Integer.parseInt(matcher.group(5));
        month = LanguageSupport.monthNameToNumber(monthName, lang);
      }

      if (month == 0) {
        return studyDate;
      }

      return String.format("%d/%02d/%02d", year, month, day);
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      return studyDate;
    }
  }

  private static EnhancedSource extractSourceEnhanced(String src, String lang) {
    if (src == null || src.isEmpty()) {
      return new EnhancedSource(null, null, null, src);
    }

    Integer time = LanguageSupport.extractTime(src, lang);

    Pattern typePattern = Pattern.compile("^(\\d+)\\.\\s*(.+?)(?:\\s*\\(|$)");
    Matcher typeMatcher = typePattern.matcher(src);

    String type = null;
    String fullTitle = src;
    String extractedSrc = src;

    if (typeMatcher.find()) {
      fullTitle = typeMatcher.group(1) + ". " + typeMatcher.group(2).trim();
      type = typeMatcher.group(2).trim();
    }

    int parenStart = src.indexOf('(');
    if (parenStart > 0) {
      extractedSrc = src.substring(parenStart + 1);
      int parenEnd = extractedSrc.lastIndexOf(')');
      if (parenEnd > 0) {
        extractedSrc = extractedSrc.substring(0, parenEnd);
      }
    }

    return new EnhancedSource(type, time, extractedSrc.trim(), fullTitle);
  }

  private static int nthIndexOf(String str, String substr, int n) {
    int pos = -1;
    for (int i = 0; i < n; i++) {
      pos = str.indexOf(substr, pos + 1);
      if (pos == -1) {
        return -1;
      }
    }
    return pos;
  }

  /** Enhanced source extraction result. */
  public record EnhancedSource(String type, Integer time, String src, String fullTitle) {}

  /** Watchtower study songs. */
  public record WStudySongs(Integer openingSong, Integer concludingSong) {}
}
