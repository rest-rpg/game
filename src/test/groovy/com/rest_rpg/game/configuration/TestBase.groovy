package com.rest_rpg.game.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.rest_rpg.user.api.model.Role
import com.rest_rpg.user.api.model.UserLite
import com.rest_rpg.user.feign.UserInternalClient
import jakarta.servlet.http.Cookie
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.LinkedMultiValueMap
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("no-security")
class TestBase extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @SpringBean
    UserInternalClient userInternalClient = Spy()

    Random random = new Random()

    def setup() {
        userInternalClient.getUserById(_ as Long) >> {
            Long id -> new UserLite(id, 'Test name', 'testEmail@mail.com', Role.USER)
        }
        userInternalClient.getUserLiteByUsername(_ as String) >> {
            String username -> new UserLite(random.nextLong(100) + 1, username, 'testEmail@mail.com', Role.USER)
        }
        userInternalClient.getUsernameFromContext() >> { 'Test name' }
    }

    def "should call mocked method directly"() {
        setup:
        UserLite expectedUser = new UserLite(1L, "Test name", "testEmail@mail.com", Role.USER)

        when:
        UserLite actualUser = userInternalClient.getUserById(1L)

        then:
        actualUser == expectedUser
    }

    <T> Response<T> httpPost(String url, Object request, Class<T> requiredType, Map customArgs = [:]) {
        Map args = [
                parameters  : new LinkedMultiValueMap<>()
        ]
        args << customArgs

        def requestPost = post(url)
                .content(asJsonString(request))
                .params(args.parameters as LinkedMultiValueMap)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        def response = mvc.perform(requestPost).andReturn().response
        return new Response<T>(response, objectMapper, requiredType)
    }

    <T> Response<T> httpPut(String url, Object request, Class<T> requiredType, Map customArgs = [:]) {
        Map args = [
                parameters  : new LinkedMultiValueMap<>(),
                refreshToken: null,
                accessToken : null
        ]
        args << customArgs

        def requestPost = put(url)
                .content(asJsonString(request))
                .params(args.parameters as LinkedMultiValueMap)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        if (args.refreshToken) {
            requestPost.cookie(new Cookie("jwt", args.refreshToken))
        }
        if (args.accessToken) {
            requestPost.header("Authorization", "Bearer " + args.accessToken)
        }

        def response = mvc.perform(requestPost).andReturn().response
        return new Response<T>(response, objectMapper, requiredType)
    }

    <T> Response<T> httpDelete(String url, Class<T> requiredType, Map customArgs = [:]) {
        Map args = [
                parameters  : new LinkedMultiValueMap<>(),
                refreshToken: null,
                accessToken : null
        ]
        args << customArgs

        def requestGet = delete(url)
                .params(args.parameters as LinkedMultiValueMap)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        if (args.refreshToken) {
            requestGet.cookie(new Cookie("jwt", args.refreshToken))
        }
        if (args.accessToken) {
            requestGet.header("Authorization", "Bearer " + args.accessToken)
        }

        def response = mvc.perform(requestGet).andReturn().response
        return new Response<T>(response, objectMapper, requiredType)
    }

    <T> Response<T> httpGet(String url, Class<T> requiredType, Map customArgs = [:]) {
        Map args = [
                parameters  : new LinkedMultiValueMap<>(),
                refreshToken: null,
                accessToken : null
        ]
        args << customArgs

        def requestGet = get(url)
                .params(args.parameters as LinkedMultiValueMap)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        if (args.refreshToken) {
            requestGet.cookie(new Cookie("jwt", args.refreshToken))
        }
        if (args.accessToken) {
            requestGet.header("Authorization", "Bearer " + args.accessToken)
        }

        def response = mvc.perform(requestGet).andReturn().response
        return new Response<T>(response, objectMapper, requiredType)
    }

    private static asJsonString(final Object obj) {
        try {
            def mapper = new ObjectMapper()
            def jsonContent = mapper.writeValueAsString(obj)
            return jsonContent
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }
}
