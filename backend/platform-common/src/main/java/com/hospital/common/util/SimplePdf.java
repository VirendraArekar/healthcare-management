package com.hospital.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class SimplePdf {
  private SimplePdf() {}

  public static byte[] render(String title, List<String> lines) {
    try {
      StringBuilder text =
          new StringBuilder("BT /F1 16 Tf 50 790 Td (")
              .append(escape(title))
              .append(") Tj /F1 10 Tf");
      for (String line : lines.stream().limit(45).toList())
        text.append(" 0 -16 Td (").append(escape(line)).append(") Tj");
      text.append(" ET");
      byte[] stream = text.toString().getBytes(StandardCharsets.US_ASCII);
      var objects =
          List.of(
              "<< /Type /Catalog /Pages 2 0 R >>",
              "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
              "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0"
                  + " R >> >> /Contents 5 0 R >>",
              "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
              "<< /Length " + stream.length + " >>\nstream\n" + text + "\nendstream");
      var out = new ByteArrayOutputStream();
      out.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));
      var offsets = new ArrayList<Integer>();
      for (int i = 0; i < objects.size(); i++) {
        offsets.add(out.size());
        out.write(
            ((i + 1) + " 0 obj\n" + objects.get(i) + "\nendobj\n")
                .getBytes(StandardCharsets.US_ASCII));
      }
      int xref = out.size();
      out.write(("xref\n0 6\n0000000000 65535 f \n").getBytes(StandardCharsets.US_ASCII));
      for (int off : offsets)
        out.write(
            String.format(Locale.ROOT, "%010d 00000 n \n", off)
                .getBytes(StandardCharsets.US_ASCII));
      out.write(
          ("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF")
              .getBytes(StandardCharsets.US_ASCII));
      return out.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String escape(String value) {
    String safe = value.replaceAll("[^\\x20-\\x7E]", "?");
    safe = safe.substring(0, Math.min(safe.length(), 140));
    return safe.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
  }
}
