package com.sparta.todo.service;

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


@Slf4j(topic = "Weather API")
@Service
public class WeatherAPIService {

    private final RestTemplate restTemplate;

    public WeatherAPIService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
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

        // JSON 응답 처리
        JSONArray weatherArray = new JSONArray(responseEntity);

        for (int i = 0; i < weatherArray.length(); i++) {
            JSONObject weatherObject = weatherArray.getJSONObject(i);
            String date = weatherObject.getString("date");
            String weather = weatherObject.getString("weather");

            // 현재 날짜와 일치하는 지 확인
            if (todayDate.equals(date)) {
                return weather;
            }
        }

        return "No weather data about date in Weather API";
    }


}
