package com.oauthLogin.exam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauthLogin.exam.entity.KakaoProfile;
import com.oauthLogin.exam.entity.OauthToken;
import com.oauthLogin.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(String code) {
        // POST 방식으로 key=value 데이터 요청 (카카오 쪽으로)
        // Retrofit2
        // OkHttp
        // RestTemplate
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // REST API ID
        final String CLIENT_ID = "";
        // redirect uri
        final String REDIRECT_URI = "";

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);


        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음 - 토큰 받기
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OauthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("kakao_access_token: " + oauthToken.getAccess_token());

        // -------------------------------------------------
        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        // Bearer 뒤에 space 한번 해줘야 함
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(headers2);

        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음 - 토큰 받기
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );


        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // User 오브젝트 : username, password, email
        System.out.println("kakao id : " + kakaoProfile.getId());
        System.out.println("kakao email : " + kakaoProfile.getKakao_account().getEmail());

        System.out.println("blog server username : " + kakaoProfile.getKakao_account().getEmail()
        + "_" + kakaoProfile.getId());
        System.out.println("blog server email : " + kakaoProfile.getKakao_account().getEmail());

        UUID garbagePassword = UUID.randomUUID();
        System.out.println("blog server password : " + garbagePassword);

        return response2.getBody();
    }

}
