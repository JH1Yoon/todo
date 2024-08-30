package com.sparta.todo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Slf4j(topic = "Weather API")
@Service
public class WeatherAPIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherAPIService(RestTemplateBuilder builder, ObjectMapper objectMapper) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
    }

    public String getWeatherToday() {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://f-api.github.io/f-api/weather.json")
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        return fromsontoString(responseEntity.getBody());
    }

    private String fromsontoString(String responseEntity) {
        // 현재 날짜 가져오기
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));

        try {
            // Json 배열을 List<Map>으로 변환
            List<Map<String, String>> weatherList = objectMapper.readValue(responseEntity, new TypeReference<>() {});

            // 날시 정보 확인
            for (Map<String, String> weatherMap : weatherList) {
                String date = weatherMap.get("date");
                String weather = weatherMap.get("weather");

                // 현재 날짜와 일치하는 지 확인
                if (todayDate.equals(date)) {
                    return weather;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing JSON response", e);
        }

        return "No weather data about date in Weather API";
    }


}
