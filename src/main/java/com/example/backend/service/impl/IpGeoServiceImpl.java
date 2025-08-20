package com.example.backend.service.impl;

import com.example.backend.service.IpGeoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;

@Service
public class IpGeoServiceImpl implements IpGeoService {

    @Value("${blog.ipgeo.api-url:https://ipapi.co}")
    private String apiBase;

    @Override
    public String resolveLocation(String ipAddress) {
        try {
            if (!StringUtils.hasText(ipAddress)) return null;
            // 使用公共IP接口 ipapi.co 简单查询城市与国家（也可替换为企业自建库）
            String url = apiBase + "/" + URLEncoder.encode(ipAddress, "UTF-8") + "/json/";
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) return null;
            try (java.io.InputStream is = conn.getInputStream()) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder sbBody = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sbBody.append(line);
                }
                String body = sbBody.toString();
                com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
                String country = text(node, "country_name");
                String region = text(node, "region");
                String city = text(node, "city");
                StringBuilder sb = new StringBuilder();
                if (StringUtils.hasText(country)) sb.append(country);
                if (StringUtils.hasText(region)) sb.append("-" + region);
                if (StringUtils.hasText(city)) sb.append("-" + city);
                return sb.length() == 0 ? null : sb.toString();
            }
        } catch (Exception ignore) {
            return null;
        }
    }

    private String text(com.fasterxml.jackson.databind.JsonNode n, String k) {
        if (n == null) return null;
        com.fasterxml.jackson.databind.JsonNode v = n.get(k);
        return v == null || v.isNull() ? null : v.asText();
    }
}


