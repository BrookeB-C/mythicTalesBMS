package com.mythictales.bms.taplist.catalog.service;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;
import com.mythictales.bms.taplist.catalog.repo.BjcpStyleRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StyleImportService {
  private final BjcpStyleRepository styles;

  public StyleImportService(BjcpStyleRepository styles) { this.styles = styles; }

  @Transactional
  public List<Long> importCsv(InputStream csvStream, boolean upsert) throws Exception {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
      String line;
      boolean headerSkipped = false;
      List<Long> ids = new ArrayList<>();
      while ((line = br.readLine()) != null) {
        if (!headerSkipped) { headerSkipped = true; continue; }
        if (line.isBlank()) continue;
        // very simple CSV split; assumes no quoted commas
        String[] c = line.split(",");
        if (c.length < 5) continue; // minimally require code,name,category,subcategory,year
        String code = trim(c,0);
        String name = trim(c,1);
        String category = trim(c,2);
        String subcat = trim(c,3);
        Integer year = intOrNull(trim(c,4));
        Double ogMin = dbl(c,5), ogMax = dbl(c,6), fgMin = dbl(c,7), fgMax = dbl(c,8), ibuMin = dbl(c,9), ibuMax = dbl(c,10), abvMin = dbl(c,11), abvMax = dbl(c,12), srmMin = dbl(c,13), srmMax = dbl(c,14);
        String notes = c.length > 15 ? trim(c,15) : null;
        BjcpStyle s = styles.findFirstByCodeAndYear(code, year).orElse(null);
        if (s == null) s = new BjcpStyle();
        if (s.getId() == null || upsert) {
          s.setCode(code);
          s.setName(name);
          s.setCategory(category);
          s.setSubcategory(subcat);
          s.setYear(year);
          s.setOgMin(ogMin); s.setOgMax(ogMax);
          s.setFgMin(fgMin); s.setFgMax(fgMax);
          s.setIbuMin(ibuMin); s.setIbuMax(ibuMax);
          s.setAbvMin(abvMin); s.setAbvMax(abvMax);
          s.setSrmMin(srmMin); s.setSrmMax(srmMax);
          s.setNotes(notes);
          styles.save(s);
          ids.add(s.getId());
        }
      }
      return ids;
    }
  }

  private static String trim(String[] a, int i) { return i < a.length ? a[i].trim() : null; }
  private static Integer intOrNull(String s) { try { return s==null||s.isBlank()?null:Integer.valueOf(s); } catch(Exception e){ return null; } }
  private static Double dbl(String[] a, int i){ return dblVal(i<a.length ? a[i] : null); }
  private static Double dblVal(String s){ try { return s==null||s.isBlank()?null:Double.valueOf(s); } catch(Exception e){ return null; } }
}

